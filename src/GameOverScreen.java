import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameOverScreen extends JPanel{
    private JPanel p;
    private GameScreen gameScreen;

    public GameOverScreen(int score, int highScore, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        initComponents(score, highScore);
    }

    public JPanel getPanel() {
        return p;
    }

    public void initComponents(int score, int highScore) {
        p = new JPanel(){
          @Override
          protected void paintComponent(Graphics g) {
              super.paintComponent(g);
              Graphics2D g2 = (Graphics2D) g;

              // 배경 그라데이션
              GradientPaint background = new GradientPaint(0, 0, new Color(11, 12, 32), 0, getHeight(), new Color(108, 111, 135));
              g2.setPaint(background);
              g.fillRect(0, 0, getWidth(), getHeight());

              // 게임 오버 문구
              g.setFont(new Font("Arial", Font.PLAIN, 80));
              String title = "Game Over";
              FontMetrics fm = g2.getFontMetrics();
              int titleWidth = fm.stringWidth(title);

              g.setColor(Color.WHITE);
              g2.drawString(title, (getWidth() - titleWidth) / 2, 196);

              // 점수 표시
              g.setFont(new Font("Arial", Font.PLAIN, 38));
              String scoreText = "Your Score: " + score;
              int scoreWidth = g2.getFontMetrics().stringWidth(scoreText);
              String highScoreText = "High Score: " + highScore;
              int highScoreWidth = g2.getFontMetrics().stringWidth(highScoreText);

              g2.setColor(new Color(255, 0, 0));
              g2.drawString(highScoreText, (getWidth() - highScoreWidth) / 2 + 3, 336 + 3);
              g2.drawString(scoreText, (getWidth() - scoreWidth) / 2 + 3, 386 + 3);

              g2.setColor(new Color(226, 211, 180));
              g2.drawString(highScoreText, (getWidth() - highScoreWidth) / 2, 336);
              g2.drawString(scoreText, (getWidth() - scoreWidth) / 2, 386);

              // 재시작 문구
              g.setFont(new Font("Arial", Font.PLAIN, 24));
              String instructions = "Press Spacebar to Restart!";
              int instructionsWidth = g2.getFontMetrics().stringWidth(instructions);

              g2.setColor(Color.BLACK);
              g2.drawString(instructions, (getWidth() - instructionsWidth) / 2 + 2, 438 + 2);
              g2.setColor(new Color(243, 223, 45));
              g2.drawString(instructions, (getWidth() - instructionsWidth) / 2, 438);
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
