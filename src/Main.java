import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    static int FRAME_WIDTH = 800;
    static int FRAME_HEIGHT = 800;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameScreen::new);
    }
}

class GameScreen extends JFrame{

    private TitleScreen titleScreen;
    private GameScreenPanel gameScreenPanel;
    private GameOverScreen gameOverScreen;

    public GameScreen() {
        setTitle("BlockBreaker Game");
        setSize(Main.FRAME_WIDTH, Main.FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
        setLocationRelativeTo(null);
        showTitleScreen();
    }

    public void showTitleScreen(){
        titleScreen = new TitleScreen(this);
        setContentPane(titleScreen.getPanel());
        revalidate();
        repaint();

        titleScreen.getPanel().requestFocusInWindow();
    }

    public void showGameScreen(){
        gameScreenPanel = new GameScreenPanel(this);
        setContentPane(gameScreenPanel);
        repaint();
        gameScreenPanel.startGame();

        gameScreenPanel.requestFocusInWindow();
    }

    public void showGameOverScreen(int score, int highScore){
        gameOverScreen = new GameOverScreen(score, highScore, this);
        setContentPane(gameOverScreen);
        revalidate();
        repaint();
        gameOverScreen.requestFocusInWindow();
    }
}

 class GameScreenPanel extends JPanel implements KeyListener, Runnable {
    private GameScreen gameScreen;
    private ArrayList<Ball> balls;
    private Racket racket;
    private ArrayList<Block> blocks;
    private boolean running;
    private int level = 1;
    static int score = 0;
    private static int highScore = 0;
    private  boolean leftPressed = false;
    private  boolean rightPressed = false;
    private RenderManager renderManager;

    public GameScreenPanel(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        renderManager = new RenderManager(Main.FRAME_WIDTH, Main.FRAME_HEIGHT);
        setFocusable(true);
        addKeyListener(this);
        setSize(Main.FRAME_WIDTH, Main.FRAME_HEIGHT);
        requestFocusInWindow();
        initGameObjects();
        SoundManager.init();
    }

    private void initGameObjects() {
        balls = new ArrayList<>();
        balls.add(new Ball(Main.FRAME_WIDTH/2-5, Main.FRAME_HEIGHT-40-30-15, -2, -3));
        racket = new Racket(Main.FRAME_WIDTH/2-75, Main.FRAME_HEIGHT-70-30);

        playSoundEffect("game_start.wav");
        generateBlocks();    // 블럭 생성
    }

    private void generateBlocks() {
        blocks = new ArrayList<>();
        int rows = level * 3;
        int cols = level * 3;
        int totalBlocks = rows * cols;
        int maxSpecialBlocks = totalBlocks / 3;  // 특수 블록(Splitter, Metal) 최대 개수
        int currentSpecialBlocks = 0;
        Random random = new Random();

        int padding = 6;
        int blockWidth = (Main.FRAME_WIDTH - cols * padding - 40) / cols;
        int blockHeight = ((Main.FRAME_HEIGHT / 3) - 2 * padding) / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = 20 + padding + col * (blockWidth + padding);
                int y = 20 + row * (blockHeight + padding) + padding;

                Block block;
                if (currentSpecialBlocks < maxSpecialBlocks && random.nextFloat() < 0.3) {
                    // 30% 확률로 특수 블록 생성
                    if (random.nextBoolean()) {
                        block = new SplitterBlock(x, y, blockWidth, blockHeight);
                    } else {
                        block = new MetalBlock(x, y, blockWidth, blockHeight);
                    }
                    currentSpecialBlocks++;
                } else {
                    block = new NormalBlock(x, y, blockWidth, blockHeight);
                }
                blocks.add(block);
            }
        }

        for (Ball ball : balls) {
            ball.increaseSpeed(level);
        }
    }

    public void startGame(){
        running = true;
        score = 0;
        requestFocus();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (running){
            for (Ball ball : new ArrayList<>(balls)){
                ball.move();
                ball.checkCollision(racket, blocks, balls);

                if (ball.getY() > getHeight()){
                    balls.remove(ball);
                    if(balls.isEmpty()){
                        running = false;
                        highScore = Math.max(highScore,score);
                        playSoundEffect("ball_explode.wav");
                        gameScreen.showGameOverScreen(score, highScore);
                    }
                }
            }

            if (blocks.isEmpty()){
                level++;
                try{
                    Thread.sleep(600);
                }catch (Exception e){
                    e.printStackTrace();
                }
                generateBlocks();
                balls.clear();
                balls.add(new Ball(Main.FRAME_WIDTH/2-5, Main.FRAME_HEIGHT-40-30-5, -2, -3));
                playSoundEffect("game_start.wav");
            }

            if(leftPressed){
                racket.moveLeft();
            }
            if(rightPressed){
                racket.moveRight();
            }

            repaint();

            try{
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected  void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        renderManager.render(g2, balls, blocks, racket);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            leftPressed = true;
        }else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            rightPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            leftPressed = false;
        }else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            rightPressed = false;
        }
    }

    private void playSoundEffect(String fileName) {
        SoundManager.playSound(fileName);
    }
}

class Ball{
    private int x, y, dx, dy, size = 10;

    public Ball(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public int getX() {
        return x;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public void increaseSpeed(int level) {
        // 단계가 높아질수록 속도 증가
        int baseSpeed = 1;
        int speedMultiplier = Math.max(1, level * 2); // 속도 증가량을 단계별로 크게 증가
        dx = dx > 0 ? baseSpeed + speedMultiplier : -(baseSpeed + speedMultiplier);
        dy = dy > 0 ? baseSpeed + speedMultiplier : -(baseSpeed + speedMultiplier);
        System.out.println(speedMultiplier);
    }

    public void move(){
        x += dx;
        y += dy;

        if (x < 20 || x > Main.FRAME_WIDTH - size -20) dx = -dx;
        if(y < 20 ) dy = -dy;
    }

    public void checkCollision(Racket racket, ArrayList<Block> blocks, ArrayList<Ball> balls) {
        // 라켓과의 충돌 검사
        Rectangle ballRect = new Rectangle(x, y, size, size);
        if (ballRect.intersects(racket.getBounds())) {
            playSoundEffect("racket.wav");
            dy = -Math.abs(dy); // 항상 위쪽으로 튕기도록 설정
        }

        // 블록과의 충돌 검사
        for (int i = blocks.size() - 1; i >= 0; i--) {
            Block block = (Block) blocks.get(i);
            if (ballRect.intersects(block.getBounds())) {
                // 블록의 고유한 충돌 처리 로직 실행
                block.handleCollision(balls);

                // 점수 추가
                GameScreenPanel.score += block.getPoints();

                // 공의 방향 전환
                dy = -dy;

                // 블록이 파괴되었다면 제거
                if (block.isDestroyed()) {
                    blocks.remove(i);
                }

                // 첫 번째 충돌 후 루프 종료
                break;
            }
        }
    }

    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.fillOval(x, y, size, size);
    }

    public int getY(){
        return y;
    }

    private void playSoundEffect(String fileName) {
        SoundManager.playSound(fileName);
    }
}

class Racket {
    private int x, y, width = 150, height = 30;

    public Racket(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void moveLeft(){
        x = Math.max(20, x - 5);
    }

    public void moveRight(){
        x = Math.min(Main.FRAME_WIDTH - width - 20, x + 5);
    }

    public void draw(Graphics2D g){
        g.setColor(new Color(113, 160, 95));
        g.fillRect(x, y, width, height);

        GradientPaint gradient = new GradientPaint(x + width/2, y, new Color(175,232, 208), x + width/2, y + height, new Color(22,84, 58));
        g.setPaint(gradient);
        g.setStroke(new BasicStroke(3));
        g.drawRect(x, y, width, height);
    }

    public Rectangle getBounds(){
        return new Rectangle(x, y, width, height);
    }
}

class TitleScreen {
    private JPanel p;
    private GameScreen gameScreen;
    private boolean showInstructions = true;
    private Clip backgroundClip;
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

 class GameOverScreen extends JPanel{
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

abstract class Block {
    protected int x, y, width, height;
    protected Color fillColor;
    protected Color borderColor1;
    protected Color borderColor2;
    protected int points;
    protected boolean destroyed;

    public Block(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.destroyed = false;
        initializeColors();
        setPoints();
    }

    protected abstract void initializeColors();
    protected abstract void setPoints();
    protected abstract void handleCollision(ArrayList<Ball> balls);

    public void draw(Graphics2D g) {
        g.setColor(fillColor);
        g.fillRect(x, y, width, height);

        GradientPaint gradient = new GradientPaint(
                x + width/2, y, borderColor1,
                x + width/2, y + height,
                borderColor2
        );
        g.setPaint(gradient);
        g.setStroke(new BasicStroke(3));
        g.drawRect(x, y, width, height);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getPoints() {
        return points;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void destroy() {
        this.destroyed = true;
    }
}

class NormalBlock extends Block {
    public NormalBlock(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    protected void initializeColors() {
        fillColor = new Color(158, 56, 38);
        borderColor1 = new Color(255, 141, 117);
        borderColor2 =  new Color(123,23, 7);
    }

    @Override
    protected void setPoints() {
        points = 10;
    }

    @Override
    protected void handleCollision(ArrayList<Ball> balls) {
        destroy();
        SoundManager.playSound("block.wav");
    }
}

class SplitterBlock extends Block {
    public SplitterBlock(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    protected void initializeColors() {
        fillColor = new Color(179, 96, 58);
        borderColor1 = new Color(255, 141, 117);
        borderColor2 =  new Color(123, 23, 7);
    }

    @Override
    protected void setPoints() {
        points = 30;
    }

    @Override
    protected void handleCollision(ArrayList<Ball> balls) {
        destroy();
        SoundManager.playSound("block_yellow.wav");

        // Get the colliding ball's properties
        Ball collidingBall = balls.get(0);  // Assuming first ball caused collision
        splitBall(collidingBall, balls);
    }

    private void splitBall(Ball originalBall, ArrayList<Ball> balls) {
        int x = originalBall.getX();
        int y = originalBall.getY();
        int dx = originalBall.getDx();
        int dy = originalBall.getDy();

        balls.add(new Ball(x, y, -dx, dy));
        balls.add(new Ball(x, y, dx, -dy));
    }
}

class MetalBlock extends Block {
    private int hitCount;
    private final int requiredHits;

    public MetalBlock(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.hitCount = 0;
        this.requiredHits = 3;
    }

    @Override
    protected void initializeColors() {
        fillColor = new Color(128, 128, 128);
        borderColor1 = new Color(192, 192, 192);
        borderColor2 =  new Color(54, 52, 52);
    }

    @Override
    protected void setPoints() {
        points = 50;
    }

    @Override
    protected void handleCollision(ArrayList<Ball> balls) {
        hitCount++;
        if (hitCount >= requiredHits) {
            destroy();
            SoundManager.playSound("block.wav");
        } else {
            SoundManager.playSound("metal.wav");
            // Darken the block color to show damage
            fillColor = fillColor.darker();
        }
    }
}

class RenderManager {
    private BufferedImage backgroundBuffer;
    private BufferedImage blockBuffer;
    private BufferedImage racketBuffer;
    private final int width;
    private final int height;

    public RenderManager(int width, int height) {
        this.width = width;
        this.height = height;
        initializeBuffers();
    }

    private void initializeBuffers() {
        backgroundBuffer = createBuffer();
        blockBuffer = createBuffer();
        racketBuffer = createBuffer();

        // 배경 프리렌더링
        Graphics2D g2d = backgroundBuffer.createGraphics();
        setupGraphics(g2d);
        renderBackground(g2d);
        g2d.dispose();
    }

    private BufferedImage createBuffer() {
        BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        return buffer;
    }

    private void setupGraphics(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
    }

    private void renderBackground(Graphics2D g2d) {
        // 배경 그라데이션
        GradientPaint background = new GradientPaint(0, 0, new Color(11, 12, 32),
                0, height, new Color(108, 111, 135));
        g2d.setPaint(background);
        g2d.fillRect(0, 0, width, height);

        // 벽 렌더링
        renderWalls(g2d);
    }

    private void renderWalls(Graphics2D g2d) {
        // 벽 색상 및 그라데이션 설정
        Color wallFill = new Color(66, 25, 11);
        GradientPaint wallGradient = new GradientPaint(0, 0, new Color(249, 198, 181),
                20, height, new Color(61, 59, 59));

        // 왼쪽 벽
        g2d.setColor(wallFill);
        g2d.fillRect(0, 0, 20, height);
        g2d.setPaint(wallGradient);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(0, 0, 20, height);

        // 오른쪽 벽
        g2d.setColor(wallFill);
        g2d.fillRect(width - 20, 0, 20, height);
        g2d.setPaint(wallGradient);
        g2d.drawRect(width - 20, 0, 20, height);

        // 상단 벽
        g2d.setColor(wallFill);
        g2d.fillRect(0, 0, width, 20);
        g2d.setPaint(wallGradient);
        g2d.drawRect(0, 0, width, 20);
    }

    public void renderBlocks(Graphics2D g2d, ArrayList<Block> blocks) {
        Graphics2D bufferG2d = blockBuffer.createGraphics();
        setupGraphics(bufferG2d);
        bufferG2d.setBackground(new Color(0, 0, 0, 0));
        bufferG2d.clearRect(0, 0, width, height);

        for (Block block : blocks) {
            block.draw(bufferG2d);
        }

        bufferG2d.dispose();
        g2d.drawImage(blockBuffer, 0, 0, null);
    }

    public void renderRacket(Graphics2D g2d, Racket racket) {
        Graphics2D bufferG2d = racketBuffer.createGraphics();
        setupGraphics(bufferG2d);
        bufferG2d.setBackground(new Color(0, 0, 0, 0));
        bufferG2d.clearRect(0, 0, width, height);

        racket.draw(bufferG2d);

        bufferG2d.dispose();
        g2d.drawImage(racketBuffer, 0, 0, null);
    }

    public void render(Graphics2D g2d, ArrayList<Ball> balls, ArrayList<Block> blocks, Racket racket) {
        // 배경 렌더링
        g2d.drawImage(backgroundBuffer, 0, 0, null);

        // 블록 렌더링
        renderBlocks(g2d, blocks);

        // 공 렌더링
        for (Ball ball : balls) {
            ball.draw(g2d);
        }

        // 라켓 렌더링
        renderRacket(g2d, racket);
    }
}

class SoundManager {
    private static final Map<String, Clip> clips = new HashMap<>();
    private static final ExecutorService soundExecutor = Executors.newFixedThreadPool(4);

    public static void init() {
        loadSound("game_start.wav");
        loadSound("ball_explode.wav");
        loadSound("racket.wav");
        loadSound("block.wav");
        loadSound("block_yellow.wav");
        loadSound("metal.wav");
    }

    private static synchronized void loadSound(String fileName) {
        try {
            URL url = SoundManager.class.getClassLoader().getResource(fileName);
            if (url == null) {
                System.err.println("Could not find audio file: " + fileName);
                return;
            }

            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            clips.put(fileName, clip);
        } catch (Exception e) {
            System.err.println("Error loading sound " + fileName + ": " + e.getMessage());
        }
    }

    public static void playSound(String fileName) {
        soundExecutor.execute(() -> {
            try {
                Clip clip = clips.get(fileName);
                if (clip != null) {
                    synchronized (clip) {
                        if (clip.isRunning()) {
                            clip.stop();
                        }
                        clip.setFramePosition(0);
                        clip.start();
                    }
                }
            } catch (Exception e) {
                System.err.println("Error playing sound " + fileName + ": " + e.getMessage());
            }
        });
    }
}