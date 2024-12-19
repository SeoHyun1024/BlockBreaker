import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameOverScreen extends JPanel{
    private JPanel p;
    private GameScreen gameScreen;

    public GameOverScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        initcomponents();
    }

    public JPanel getPanel() {
        return p;
    }

    public void initcomponents(){
        p = new JPanel(){
          @Override
          protected void paintComponent(Graphics g) {
              super.paintComponent(g);
              g.setColor(Color.BLACK);
              g.fillRect(0, 0, getWidth(), getHeight());
              g.setColor(Color.RED);
              g.setFont(new Font("Arial", Font.BOLD, 36));
              g.drawString("Game Over", 150, 200);
              g.setFont(new Font("Arial", Font.PLAIN, 24));
              g.drawString("Press Spacebar to Restart!", 150, 250);
          }
        };
        p.setFocusable(true);
        p.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE){
                    gameScreen.showTitleScreen();
                }
            }
        });
    }
}
