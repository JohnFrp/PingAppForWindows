package com.peace.app;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PingApp extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField txtHost;
    private JLabel lblOutput;
    private JButton btnPing, btnStop;
    private PingService pingService;

    private static final Font FONT_LARGE = new Font("TimeNewRoman", Font.BOLD, 130);
    private static final Font FONT_MEDIUM = new Font("TimeNewRoman", Font.BOLD, 20);
    private static final Font FONT_SMALL = new Font("TimeNewRoman", Font.BOLD, 14);

    public PingApp() {
        initUI();
        pingService = new PingService(this::updateOutput);

        getRootPane().setDefaultButton(btnPing);
        setupEventListeners();
    }

    private void initUI() {
        setTitle("Ping App");
        setSize(450, 250);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        Image icon = new ImageIcon(this.getClass().getResource("cmd.png")).getImage();
        setIconImage(icon);

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.SOUTH);

        lblOutput = createOutputLabel();
        add(lblOutput, BorderLayout.CENTER);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new FlowLayout());

        JLabel lblHost = new JLabel("Enter IP:");
        lblHost.setFont(FONT_MEDIUM);
        lblHost.setForeground(Color.BLUE);

        txtHost = new JTextField(" 8.8.8.8", 10);
        txtHost.setFont(FONT_MEDIUM);
        txtHost.setForeground(Color.RED);

        btnPing = createButton("Start", FONT_SMALL, Color.BLUE);
        btnStop = createButton("Stop", FONT_SMALL, Color.BLUE);
        btnStop.setEnabled(false);

        inputPanel.add(lblHost);
        inputPanel.add(txtHost);
        inputPanel.add(btnPing);
        inputPanel.add(btnStop);

        return inputPanel;
    }

    private JLabel createOutputLabel() {
        JLabel outputLabel = new JLabel("START", JLabel.CENTER);
        outputLabel.setFont(FONT_LARGE);
        outputLabel.setForeground(Color.GREEN);
        outputLabel.setBackground(Color.BLACK);
        outputLabel.setOpaque(true);
        return outputLabel;
    }

    private JButton createButton(String text, Font font, Color color) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setForeground(color);
        button.setFocusPainted(false);
        return button;
    }

    private void setupEventListeners() {
        ActionListener pingAction = e -> {
            String host = txtHost.getText().trim();
            if (isValidHost(host)) {
                pingService.startPing(host);
                toggleButtons(false);
            } else {
                showErrorMessage("Please enter a valid host or IP address.");
            }
        };

        ActionListener stopAction = e -> {
            pingService.stopPing();
            toggleButtons(true);
        };

        btnPing.addActionListener(pingAction);
        btnStop.addActionListener(stopAction);
    }

    private void toggleButtons(boolean startEnabled) {
        btnPing.setEnabled(startEnabled);
        btnStop.setEnabled(!startEnabled);
    }

    private void updateOutput(String text) {
        lblOutput.setText(text);
    }

    private boolean isValidHost(String host) {
        return PingService.isValidIP(host) || PingService.isValidDomain(host);
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
        toggleButtons(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PingApp().setVisible(true));
    }
}
