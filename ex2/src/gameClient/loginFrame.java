package gameClient;

import javax.swing.*;
import java.awt.*;


public class loginFrame extends JFrame {

    public loginFrame() {
        setTitle("I Wanna Be The Very Best, Like No One Ever Was.");
        setSize(400, 200);
        setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        setResizable(true);
        setVisible(true);

        setLayout(new BorderLayout());

        JLabel user = new JLabel("User ID: ");
        JTextField userID = new JTextField(21);
        JPanel panelTop = new JPanel();
        panelTop.add(user);
        panelTop.add(userID);

        JLabel scenario = new JLabel("Scenario Number: ");
        JTextField scenarioField = new JTextField(21);


        JPanel panelMiddle = new JPanel();
        panelMiddle.add(scenario);
        panelMiddle.add(scenarioField);

        JButton start = new JButton("Start");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(start);

        start.addActionListener(e -> Ex2.loginID = Integer.parseInt(userID.getText()));
        start.addActionListener(e -> Ex2.scenarioLevel = Integer.parseInt(scenarioField.getText()));
        start.addActionListener(e -> Ex2.server.start());
        start.addActionListener(e -> setVisible(false));

        add(panelTop, BorderLayout.NORTH);
        add(panelMiddle, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
    }


}
