import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class TitleScreen {
    private JPanel p;
    private GameScreen gameScreen;
    private boolean showInstructions = true;
    private Clip  backgroundClip;
    private ArrayList<Star> stars;

    public TitleScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        initComponents();
        startBlinking();
        playBackgroundMusic("title_background.wav");
        stars = new ArrayList<>();
        initStars(); // Initialize stars
        startStarAnimation();
    }

    public JPanel getPanel(){
        return p;
    }

    private void initStars() {
        for (int i = 0; i < 20; i++) {
            int x = (int) (Math.random() * 800);
            int y = (int) (Math.random() * 600);
            int speed = 1 + (int) (Math.random() * 3); // Random speed between 1 and 3
            stars.add(new Star(x, y, speed));
        }
    }

    private void initComponents() {
        p = new JPanel(){
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

                // 과제 제목
                g2.setFont(new Font("Arial", Font.PLAIN, 48));
                String titleLine1 = "Java Programming";
                String titleLine2 = "Homework #5";
                FontMetrics fm = g2.getFontMetrics();
                int titleLine1Width = fm.stringWidth(titleLine1);
                int titleLine2Width = fm.stringWidth(titleLine2);

                g2.setColor(Color.BLACK);
                g2.drawString(titleLine1, getWidth()/2 - titleLine1Width/2 + 3,167 + 3 );
                g2.drawString(titleLine2, getWidth()/2 - titleLine2Width/2 + 3, 217 + 3);

                GradientPaint titleGradient = new GradientPaint(getWidth() / 2, 100, new Color(153, 153, 153), getWidth()/2, 155, Color.WHITE);
                g2.setPaint(titleGradient);
                g2.drawString(titleLine1, getWidth()/2 - titleLine1Width/2,167 );
                g2.drawString(titleLine2, getWidth()/2 - titleLine2Width/2, 217);

                // 게임 제목
                g2.setFont(new Font("Serif", Font.BOLD, 84));
                String subtitle = "Block Breaker";
                int subtitleWidth = g2.getFontMetrics().stringWidth(subtitle);

                g2.setColor(new Color(22, 79, 49));
                g2.setFont(new Font("Serif", Font.PLAIN, 84));
                g2.drawString(subtitle, (getWidth() - subtitleWidth) / 2 + 6, 326 + 6);

                g2.setColor(new Color(226, 211, 182));
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        if (i != 0 || j != 0) {
                            g2.drawString(subtitle, (getWidth() - subtitleWidth) / 2 + i, 326  + j);
                        }
                    }
                }

                g2.setColor(new Color(174, 34, 37));
                g2.drawString(subtitle, (getWidth() - subtitleWidth) / 2, 326);

                // 설명 text
                if (showInstructions) {
                    g.setFont(new Font("Arial", Font.PLAIN, 24));
                    String instructions = "Press Spacebar to play!";
                    int instructionsWidth = g2.getFontMetrics().stringWidth(instructions);

                    g2.setColor(Color.BLACK);
                    g2.drawString(instructions, (getWidth() - instructionsWidth) / 2 + 2, 438 + 2);
                    g2.setColor(new Color(243, 223, 45));
                    g2.drawString(instructions, (getWidth() - instructionsWidth) / 2, 438);
                }

                drawTreeRow(g2, getWidth(), getHeight() - 230);
                //drawStar(g2);
            }
        };
        p.setFocusable(true);
        p.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    stopBackgroundMusic();
                    gameScreen.showGameScreen();
                }
            }
        });
    }

    private void startBlinking() {
        Timer timer = new Timer(300, e -> {
            showInstructions = !showInstructions;
            p.repaint();
        });
        timer.start();
    }

    private void playBackgroundMusic(String fileName) {
        try {
            URL url = getClass().getClassLoader().getResource(fileName);
            if (url == null) {
                throw new RuntimeException("Audio file not found: " + fileName);
            }
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audio);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
            backgroundClip.close();
        }
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

    private void startStarAnimation() {
        Timer timer = new Timer(50, e -> {
            for (Star star : stars) {
                star.move();
            }
            p.repaint();
        });
        timer.start();
    }
}

class Star {
    private int x, y, speed;

    public Star(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }

    public void move() {
        y += speed;
        if (y > 600) { // Reset position if it moves off-screen
            y = 0;
            x = new Random().nextInt(800);
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(240, 235, 192));
        g.fillOval(x, y, 5, 5);
    }
}