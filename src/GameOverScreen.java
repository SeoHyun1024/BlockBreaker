import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;

public class GameOverScreen extends JPanel{
    private int score;
    private int highScore;
    private GameScreen gameScreen;
    private boolean showInstructions = true;
    private ArrayList<Star> stars;

    public GameOverScreen(int score, int highScore, GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.score = score;
        this.highScore = highScore;

        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new KeyAdapter() { // KeyListener 추가
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameScreen.showTitleScreen(); // 스페이스바를 누르면 타이틀 화면으로
                }
            }
        });

        playSoundEffect("game_over.wav");
        startBlinking();
        stars = new ArrayList<>();
        initStars(); // Initialize stars
        startStarAnimation();
    }

    private void initStars() {
        for (int i = 0; i < 20; i++) {
            int x = (int) (Math.random() * 800);
            int y = (int) (Math.random() * 600);
            int speed = 1 + (int) (Math.random() * 3); // Random speed between 1 and 3
            stars.add(new Star(x, y, speed));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;

      // 배경 그라데이션
      GradientPaint background = new GradientPaint(0, 0, new Color(11, 12, 32), 0, getHeight(), new Color(108, 111, 135));
      g2.setPaint(background);
      g.fillRect(0, 0, getWidth(), getHeight());

      // 별 그리기
      for (Star star : stars) {
          star.draw(g2);
      }

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
      if (showInstructions) {
          g.setFont(new Font("Arial", Font.PLAIN, 24));
          String instructions = "Press Spacebar to Restart!";
          int instructionsWidth = g2.getFontMetrics().stringWidth(instructions);

          g2.setColor(Color.BLACK);
          g2.drawString(instructions, (getWidth() - instructionsWidth) / 2 + 2, 438 + 2);
          g2.setColor(new Color(243, 223, 45));
          g2.drawString(instructions, (getWidth() - instructionsWidth) / 2, 438);

      }

      drawTreeRow(g2, getWidth(), getHeight() - 230);
    }

    private void startBlinking() {
        Timer timer = new Timer(600, e -> {
            showInstructions = !showInstructions;
            repaint();
        });
        timer.start();
    }

    private  void drawTree(Graphics2D g2, int x, int y, int treeHeightOffset){
        int triangleHeight = 48;
        int triangleWidth = 70;

        // 삼각형 그리기
        for (int i = 0; i < 4; i++){
            int[] xPoints = {x, x - triangleWidth /2 - i*i*5, x + triangleWidth / 2 + i*i*5};
            int[] yPoints = {y + (i * triangleHeight ) - 40 - treeHeightOffset, y + (i * triangleHeight + triangleHeight) - treeHeightOffset, y + (i * triangleHeight +triangleHeight - treeHeightOffset )};
            Polygon triangle = new Polygon(xPoints, yPoints, xPoints.length);
            g2.setColor(Color.WHITE);
            g2.fillPolygon(triangle);
        }

        // 나무 기둥 그리기
        int trunkWidth = 20;
        int trunkHeight = 60;
        g2.setColor(Color.WHITE);
        g2.fillRect(x - trunkWidth / 2, y + triangleHeight*4 - 60, trunkWidth, trunkHeight + 40);
    }

    private void drawTreeRow(Graphics2D g2, int panelWidth, int y){
        int numTrees = 10;
        int treeWidth = 60;
        int[] spacing = {25, 25, 20, 30, 20, 30, 20, 25, 20, 25 };
        int[] heightOffset = {5, 40, 10, 30, 0, 35, 0, 55, 10, 20};


        for (int i = 0; i < numTrees; i++) {
            int totalWidth = numTrees * treeWidth + (numTrees - 1) * spacing[i];
            int startX = (panelWidth - totalWidth) / 2;
            int treeX = startX + i * (treeWidth + spacing[i]);
            drawTree(g2, treeX, y, heightOffset[i]);
        }
    }

    private void playSoundEffect(String fileName) {
        try {
            URL url = getClass().getClassLoader().getResource(fileName);
            if (url == null) {
                throw new RuntimeException("Audio file not found: " + fileName);
            }
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startStarAnimation() {
        Timer timer = new Timer(50, e -> {
            for (Star star : stars) {
                star.move();
            }
            repaint();
        });
        timer.start();
    }
}
