import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameOverScreen extends JPanel{
    private int score;
    private int highScore;
    private GameScreen gameScreen;
    private boolean showInstructions = true;

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

        startBlinking();
    }

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
              drawStar(g2);
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

    private  void drawStar(Graphics2D g2){
        // 별 찍기
        g2.setColor(new Color(240, 235, 192));
        g2.fillOval(108, 73, 5, 5);
        g2.fillOval(215, 108, 5, 5);
        g2.fillOval(379, 68, 5, 5);
        g2.fillOval(486, 103, 5, 5);
        g2.fillOval(569, 33, 5, 5);
        g2.fillOval(676, 68, 5, 5);
        g2.fillOval(34, 219, 5, 5);
        g2.fillOval(188, 255, 5, 5);
        g2.fillOval(295, 290, 5, 5);
        g2.fillOval(463, 252, 5, 5);
        g2.fillOval(305, 214, 5, 5);
        g2.fillOval(627, 212, 5, 5);
        g2.fillOval(595, 319, 5, 5);
        g2.fillOval(734, 247, 5, 5);
    }
}
