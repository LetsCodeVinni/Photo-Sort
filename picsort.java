import javax.swing.*;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.io.IOException;
import java.awt.*;
import java.awt.event.*;

public class picsort extends JFrame {

    private final JLabel imageLabel = new JLabel("ERROR", SwingConstants.CENTER);
    private final List<Path> photos = new ArrayList<>();
    private final JLabel statusLabel = new JLabel("", SwingConstants.CENTER);
    private final Path folderA = Paths.get("photos", "A");
    private final Path folderB = Paths.get("photos", "B");
    private int index = 0;

    public picsort() {
        setTitle("Foto Sortierer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        try {
            Files.createDirectories(folderA);
            Files.createDirectories(folderB);
            loadPhotos(Paths.get("photos"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Gefundene Fotos: " + photos.size());

        setLayout(new BorderLayout());
        add(imageLabel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        showCurrentPhoto();
        setupKeyBindings();
        setVisible(true);
    }

    public static void main(String[] args) throws InterruptedException {
        SwingUtilities.invokeLater(() -> new picsort());
    }

    public void loadPhotos(Path sourceFolder) throws IOException {
        System.out.println("Suche in: " + sourceFolder.toAbsolutePath());
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(sourceFolder)) {
            for (Path p : stream) {
                if (Files.isRegularFile(p)) {
                    photos.add(p);
                }
            }
        }
        Collections.sort(photos);
    }

    private void showCurrentPhoto() {
        if (index >= photos.size()) {
            imageLabel.setIcon(null);
            imageLabel.setText("Fertig! Keine Fotos mehr übrig.");
            statusLabel.setText("");
            return;
        }

        Path current = photos.get(index);
        ImageIcon icon = new ImageIcon(current.toString());

        Image scaled = icon.getImage().getScaledInstance(
                getWidth() - 40, getHeight() - 100, Image.SCALE_SMOOTH);

        imageLabel.setText("");
        imageLabel.setIcon(new ImageIcon(scaled));
        statusLabel.setText((index + 1) + " / " + photos.size() + "   –   " + current.getFileName());
    }

    private void setupKeyBindings() {
        InputMap im = imageLabel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = imageLabel.getActionMap();

        im.put(KeyStroke.getKeyStroke("LEFT"), "moveToA");
        im.put(KeyStroke.getKeyStroke("RIGHT"), "moveToB");

        am.put("moveToA", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try { moveCurrentTo(folderA); } catch (IOException ex) { ex.printStackTrace(); }
            }
        });

        am.put("moveToB", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                try { moveCurrentTo(folderB); } catch (IOException ex) { ex.printStackTrace(); }
            }
        });
    }

    private void moveCurrentTo(Path targetFolder) throws IOException {
        if (index >= photos.size()) return;

        Path current = photos.get(index);
        Path target = targetFolder.resolve(current.getFileName());
        Files.move(current, target, StandardCopyOption.REPLACE_EXISTING);

        index++;
        showCurrentPhoto();
    }
}