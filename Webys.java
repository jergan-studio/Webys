import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Webys - Jergan Studio */
public class Webys {
    private static final String HOME = "https://duckduckgo.com/";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Webys::start);
    }

    private static void start() {
        JFrame frame = new JFrame("Webys — Jergan Studio");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);

        JTextField address = new JTextField(HOME);
        JButton back = new JButton("←");
        JButton forward = new JButton("→");
        JButton reload = new JButton("↻");
        JButton home = new JButton("Home");
        JButton go = new JButton("Go");
        JLabel status = new JLabel("Webys by Jergan Studio — DuckDuckGo");

        Runnable open = () -> {
            String value = address.getText().trim();
            if (value.isEmpty()) value = HOME;
            if (!value.matches("^[a-zA-Z][a-zA-Z0-9+.-]*://.*$")) {
                value = value.contains(".") && !value.contains(" ")
                        ? "https://" + value
                        : "https://duckduckgo.com/?q=" + encode(value);
            }
            address.setText(value);
            browse(value, status);
        };

        go.addActionListener(e -> open.run());
        address.addActionListener(e -> open.run());
        home.addActionListener(e -> { address.setText(HOME); open.run(); });
        reload.addActionListener(e -> browse(address.getText(), status));
        back.addActionListener(e -> status.setText("Back is handled by your default browser."));
        forward.addActionListener(e -> status.setText("Forward is handled by your default browser."));

        JPanel bar = new JPanel(new BorderLayout(6, 6));
        bar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        buttons.add(back);
        buttons.add(forward);
        buttons.add(reload);
        buttons.add(home);
        bar.add(buttons, BorderLayout.WEST);
        bar.add(address, BorderLayout.CENTER);
        bar.add(go, BorderLayout.EAST);

        JPanel root = new JPanel(new BorderLayout());
        root.add(bar, BorderLayout.NORTH);
        root.add(status, BorderLayout.SOUTH);
        frame.setContentPane(root);
        frame.setVisible(true);
    }

    private static void browse(String value, JLabel status) {
        try {
            Desktop.getDesktop().browse(URI.create(value));
            status.setText("Opened: " + value);
        } catch (Exception e) {
            status.setText("Could not open: " + e.getMessage());
        }
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
