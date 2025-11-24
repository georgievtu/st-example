import javax.swing.SwingUtilities;

public class ClientMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientGUI("localhost", 1151).showGUI());
    }
}
