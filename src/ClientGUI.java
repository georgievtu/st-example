import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientGUI {
    public ClientGUI(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void showGUI() {
        frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        JPanel top = new JPanel();
        top.add(new JLabel("Name:"));

        nameField = new JTextField(10);
        top.add(nameField);

        connectBtn = new JButton("Connect");
        top.add(connectBtn);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        input = new JTextField();
        input.setEnabled(false);
        frame.getContentPane().add(top, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.getContentPane().add(input, BorderLayout.SOUTH);

        connectBtn.addActionListener(e -> doConnect());
        input.addActionListener(e -> sendMessage());
        frame.setVisible(true);
    }

    private void doConnect() {
        String name = nameField.getText();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Enter name");
            return;
        }
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            // Send name
            out.println("USER:" + name);

            // Read first response
            String first = in.readLine();
            if (first != null && first.startsWith("ERROR:")) {
                JOptionPane.showMessageDialog(frame, first);
                socket.close();
                return;
            } else if (first != null) {
                chatArea.append(MessageAdapter.fromRaw(first) + "\n");
            }

            input.setEnabled(true);
            connectBtn.setEnabled(false);
            nameField.setEnabled(false);

            // Read broadcasts
            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        Message m = MessageAdapter.fromRaw(line);
                        if (m != null)
                            chatArea.append(m + "\n");
                        else
                            chatArea.append(line + "\n");
                    }
                } catch (IOException ex) {
                    chatArea.append("Disconnected.\n");
                }
            }).start();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, "Unable to connect: " + ex.getMessage());
        }
    }

    private void sendMessage() {
        String text = input.getText();
        if (text.isEmpty())
            return;

        String name = nameField.getText();

        Message m = new Message(name, text);

        out.println(MessageAdapter.toRaw(m));
        input.setText("");
    }

    private final String host;
    private final int port;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField input;
    private JTextField nameField;
    private JButton connectBtn;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
}
