import javax.swing.*;

public class Main {
    static int FRAME_WIDTH = 800;
    static int FRAME_HEIGHT = 800;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameScreen::new);
    }
}
