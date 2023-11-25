package ie.tcd.scss.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JList;

import ie.tcd.scss.gui.annotations.ActionListenerFor;
import ie.tcd.scss.gui.annotations.ActionListenerInstaller;
import ie.tcd.scss.network.Endpoint;

public class Host extends JFrame {

    public Endpoint host;

    public JList<Integer> availableHosts;
    private JButton sendButton;
    private JTextField inputField;
    private JTextArea hostLogs;

    public Host(Endpoint host) {
        this.host = host;
        availableHosts = new JList<>(new DefaultListModel<Integer>());

        setTitle("Endpoint Interface");
        host.requestEndpointList();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setVisible(true);
        setJMenuBar(topMenu(host.senderAddress));
        add(new JScrollPane(availableHosts), BorderLayout.WEST);
        add(commandArea(), BorderLayout.CENTER);
        add(inputFields(), BorderLayout.SOUTH);

        availableHosts.addListSelectionListener(e -> {
            if(e.getValueIsAdjusting()) return;
            final int selected = availableHosts.getSelectedValue();
            new PrivateMessageWindow(this, selected);
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                host.disconnect();
            }
        });

        ActionListenerInstaller.processAnnotations(this);
    }

    private JMenuBar topMenu(int addr) {
        var container = new JMenuBar();
        var menu = new JMenu("Your address: " + addr);
        container.add(menu);
        return container;
    }

    private JScrollPane commandArea() {
        hostLogs = new JTextArea();
        hostLogs.setEditable(false);
        return new JScrollPane(hostLogs);
    }

    private JPanel inputFields() {
        var container = new JPanel();
        inputField = new JTextField(30);
        sendButton = new JButton("send");
        container.add(inputField, BorderLayout.CENTER);
        container.add(sendButton, BorderLayout.EAST);
        this.getRootPane().setDefaultButton(sendButton);
        return container;
    }

    public void receiveMessage(String msg) {
        hostLogs.setText("%s\nreceived message: %s".formatted(hostLogs.getText(), msg));
    }

    public void sendMessage(String msg, int dst) {
        hostLogs.setText("%s\nto %d: %s".formatted(hostLogs.getText(), dst, msg));
    }

    @ActionListenerFor(source = "sendButton")
    public void send() {
        final String input = inputField.getText();
        final String cmd = hostLogs.getText();
        inputField.setText("");
        hostLogs.setText(cmd + "\n" + input);
    }
}
