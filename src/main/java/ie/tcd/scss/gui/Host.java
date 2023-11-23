package ie.tcd.scss.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
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

    private Endpoint host;

    public JList<Integer> availableHosts;

    private JButton sendButton;

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
            createPrivateMessageWindow(availableHosts.getSelectedValue());
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
        var textArea = new JTextArea();
        textArea.setEditable(false);
        return new JScrollPane(textArea);
    }

    private JPanel inputFields() {
        var container = new JPanel();
        var inputField = new JTextField(30);
        sendButton = new JButton("send");
        container.add(inputField, BorderLayout.CENTER);
        container.add(sendButton, BorderLayout.EAST);
        return container;
    }

    private void createPrivateMessageWindow(int addr) {
        JDialog dialog = new JDialog(this, "Private Message to: " + addr, true);
        dialog.setPreferredSize(new Dimension(400, 120));
        dialog.pack();
        JTextArea textArea = new JTextArea();
        Insets margin = new Insets(10, 10, 10, 10);
        textArea.setMargin(margin);
        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> {
            String message = textArea.getText();
            System.out.println("Sending private message to " + addr + ": " + message);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // @ActionListenerFor(source = "refreshButton")
    public void updateHosts() {
        System.out.println("heya");
    }
}
