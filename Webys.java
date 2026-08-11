import javax.swing.*;
import java.awt.*;
import java.net.URI;

/** Webys - Jergan Studio */
public class Webys {
    private static final String HOME = "https://www.google.com";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Webys::start);
    }

    private static void start() {
        JFrame frame = new JFrame("Webys — Jergan Studio");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);

        JTextField address = new JTextField(HOME);
        JButton go = new JButton("Go");
        JButton home = new JButton("Home");
        JLabel status = new JLabel("Webys by Jergan Studio");

        Runnable open = () -> {
            String value = address.getText().trim();
            if (value.isEmpty()) value = HOME;
            if (!value.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$")) {
                value = value.contains(".") && !value.contains(" ")
                        ? "https://" + value
                        : "https://www.google.com/search?q=" + encode(value);
            }
            address.setText(value);
            try {
                Desktop.getDesktop().browse(URI.create(value));
                status.setText("Opened: " + value);
            } catch (Exception e) {
                status.setText("Could not open: " + value);
            }
        };

        go.addActionListener(e -> open.run());
        home.addActionListener(e -> { address.setText(HOME); open.run(); });
        address.addActionListener(e -> open.run());

        JPanel bar = new JPanel(new BorderLayout(6, 6));
        bar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        bar.add(home, BorderLayout.WEST);
        bar.add(address, BorderLayout.CENTER);
        bar.add(go, BorderLayout.EAST);

        JPanel root = new JPanel(new BorderLayout());
        root.add(bar, BorderLayout.NORTH);
        root.add(status, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private static String encode(String value) {
        return java.net.URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8);
    }
}
