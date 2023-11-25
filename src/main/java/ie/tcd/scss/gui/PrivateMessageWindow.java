package ie.tcd.scss.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ie.tcd.scss.gui.annotations.ActionListenerFor;
import ie.tcd.scss.gui.annotations.ActionListenerInstaller;

public class PrivateMessageWindow extends JDialog {

    private Host parent;
    private JTextArea textArea;
    private JButton sendButton;
    private JButton cancelButton;
    private JScrollPane scrollPane;
    private JPanel buttonPanel;
    private int addr;

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 120;

    public PrivateMessageWindow(Host parent, int addr) {
        super(parent, "Private message to: " + addr);
        this.addr = addr;
        this.parent = parent;
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        pack();

        textArea = new JTextArea();
        Insets margin = new Insets(10, 10, 10, 10);
        textArea.setMargin(margin);

        scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        sendButton = new JButton("send");
        cancelButton = new JButton("cancel");
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

        buttonPanel.add(sendButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(parent);
        setVisible(true);

        ActionListenerInstaller.processAnnotations(this);
    }

    @ActionListenerFor(source = "sendButton")
    public void send() {
        final String input = textArea.getText();
        textArea.setText("");
        parent.host.prepareMessage(addr, input);
        parent.host.findPath(addr);
        parent.sendMessage(input, addr);
        this.dispose();
    }

    @ActionListenerFor(source = "cancelButton")
    public void cancel() {
        this.dispose();
    }
}
