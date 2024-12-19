import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TitleScreen {
    private JPanel p;
    private GameScreen gameScreen;

    public TitleScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        initComponents();
    }

    public JPanel getPanel(){
        return p;
    }

    private void initComponents() {
        p = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                g.drawString("Block Breaker Game", 500, 300);

                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.PLAIN, 24));
                g.drawString("Press Spacebar to play!!", 150, 250);
            }
        };
        p.setFocusable(true);
        p.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameScreen.showGameScreen();
                }
            }
        });
    }
}
