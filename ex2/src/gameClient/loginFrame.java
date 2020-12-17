package gameClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * This class extends JFrame Class that creates the frame for the login.
 * It is the main class that includes the function loginFrame.
 */
public class loginFrame extends JFrame {

	/**
	 * Constructor that creates the login screen on the frame.
	 */
    public loginFrame() {
        setTitle("I Wanna Be The Very Best, Like No One Ever Was.");
        setSize(400, 100);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);
        setVisible(true);
        setLayout(new BorderLayout());

        JLabel user = new JLabel("User ID: ");
        JTextField userID = new JTextField(20);
        JPanel panelTop = new JPanel();
        panelTop.add(user);
        panelTop.add(userID);

        JLabel scenario = new JLabel("Scenario Number: ");
        JTextField scenarioField = new JTextField(14);

        JPanel panelMiddle = new JPanel();
        panelMiddle.add(scenario);
        panelMiddle.add(scenarioField);

        JButton start = new JButton("Start");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(start);

        start.addActionListener(l -> Ex2.loginID = Integer.parseInt(userID.getText()));
        start.addActionListener(l -> Ex2.scenarioLevel = Integer.parseInt(scenarioField.getText()));
        start.addActionListener(l -> Ex2.server.start());
        start.addActionListener(l -> setVisible(false));

        add(panelTop, BorderLayout.NORTH);
        add(panelMiddle, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }
}