import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatClientGUI extends JFrame {
    private JTextArea messageArea;  // Area to display chat messages
    private JTextField textField;  // Input field for sending messages
    private JButton sendButton;  // Button to send messages
    private JButton clearButton;  // Button to clear chat history
    private JButton exitButton;  // Button to exit the chat
    private JPanel userPanel;  // Panel to display online users
    private ChatClient client;  // Client instance for server communication

    public ChatClientGUI() {
        super("Chat Application");  // Set window title
        setSize(600, 500);  // Set window size
        setDefaultCloseOperation(EXIT_ON_CLOSE);  // Close operation

        // Dark theme colors
        Color backgroundColor = new Color(45, 45, 45);  // Background color
        Color buttonColor = new Color(70, 70, 70);  // Button color
        Color textColor = new Color(200, 200, 200);  // Text color
        Color messageAreaColor = new Color(30, 30, 30);  // Message area color
        Font textFont = new Font("Arial", Font.PLAIN, 14);  // Font for text
        Font buttonFont = new Font("Arial", Font.BOLD, 12);  // Font for buttons

        // Message area setup
        messageArea = new JTextArea();  // Initialize message display area
        messageArea.setEditable(false);  // Make message area read-only
        messageArea.setBackground(messageAreaColor);  // Set background color
        messageArea.setForeground(textColor);  // Set text color
        messageArea.setFont(textFont);  // Set font
        JScrollPane scrollPane = new JScrollPane(messageArea);  // Add scrolling to message area
        add(scrollPane, BorderLayout.CENTER);  // Add message area to the frame's center

        // Prompt user to enter their name
        String name = JOptionPane.showInputDialog(this, "Enter your name:", "Name Entry", JOptionPane.PLAIN_MESSAGE);
        this.setTitle("Chat Application - " + name);  // Set window title with user's name



        // Bottom panel for text field and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());  // Panel for text field and buttons
        bottomPanel.setBackground(backgroundColor);  // Set panel background color

        // Text field setup
        textField = new JTextField();  // Initialize text field for typing messages
        textField.setFont(textFont);  // Set font
        textField.setForeground(textColor);  // Set text color
        textField.setBackground(backgroundColor);  // Set background color
        textField.setCaretColor(textColor);  // Set caret color (text cursor)
        textField.addActionListener(e -> sendMessage());  // Send message when Enter key is pressed
        bottomPanel.add(textField, BorderLayout.CENTER);  // Add text field to panel

        // Send button setup
        sendButton = new JButton("Send");
        sendButton.setFont(buttonFont);
        sendButton.setBackground(buttonColor);
        sendButton.setForeground(Color.WHITE);
        sendButton.addActionListener(e -> sendMessage());  // Send message on button click
        bottomPanel.add(sendButton, BorderLayout.EAST);  // Add send button to panel

        // Clear button setup
        clearButton = new JButton("Clear");
        clearButton.setFont(buttonFont);
        clearButton.setBackground(buttonColor);
        clearButton.setForeground(Color.WHITE);
        clearButton.addActionListener(e -> clearChat());  // Clear chat history on button click
        bottomPanel.add(clearButton, BorderLayout.WEST);  // Add clear button to panel

        // Exit button setup
        exitButton = new JButton("Exit");
        exitButton.setFont(buttonFont);
        exitButton.setBackground(buttonColor);
        exitButton.setForeground(Color.WHITE);
        exitButton.addActionListener(e -> exitChat());  // Exit chat on button click
        bottomPanel.add(exitButton, BorderLayout.SOUTH);  // Add exit button to panel

        add(bottomPanel, BorderLayout.SOUTH);  // Add bottom panel to the frame's south

        try {
            // Create a ChatClient instance to connect to the server
            this.client = new ChatClient("127.0.0.1", 5000, this::onMessageReceived);
            client.startClient();  // Start client to listen for messages from server
            client.sendMessage(name + " has joined the chat.");  // Send join message to server
        } catch (IOException e) {
            e.printStackTrace();  // Print connection error
            JOptionPane.showMessageDialog(this, "Error connecting to the server", "Connection error",
                    JOptionPane.ERROR_MESSAGE);  // Show error dialog
            System.exit(1);  // Exit application on connection error
        }
    }

    // Method to send message to server
    private void sendMessage() {
        String message = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] "
                + this.getTitle().replace("Chat Application - ", "") + ": " + textField.getText();
        client.sendMessage(message);  // Send message to server
        textField.setText("");  // Clear the input field after sending
    }

    // Method to clear chat area
    private void clearChat() {
        messageArea.setText("");  // Clear message area
    }

    // Method to handle incoming messages received from the server
    private void onMessageReceived(String message) {
        // Append the received message to the message area
        SwingUtilities.invokeLater(() -> messageArea.append(message + "\n"));
    }

    // Method to exit the chat application
    private void exitChat() {
        String departureMessage = this.getTitle().replace("Chat Application - ", "") + " has left the chat.";
        client.sendMessage(departureMessage);  // Send departure message to server
        try {
            Thread.sleep(1000);  // Pause for 1 second
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
        System.exit(0);  // Exit the application
    }

    // Method to update user list
    private void updateUserList(String[] users) {
        userPanel.removeAll();  // Clear existing user list
        for (String user : users) {
            JLabel userLabel = new JLabel(user);
            userLabel.setForeground(Color.WHITE);
            userLabel.setBorder(new EmptyBorder(5, 10, 5, 10));
            userPanel.add(userLabel);  // Add user label to panel
        }
        revalidate();  // Refresh GUI layout
        repaint();  // Repaint GUI
    }

    // Main method to start the GUI application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ChatClientGUI().setVisible(true);  // Create and display the GUI
        });
    }
}
