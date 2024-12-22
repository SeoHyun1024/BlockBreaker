import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class GameScreenPanel extends JPanel implements KeyListener, Runnable {
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
        racket = new Racket(Main.FRAME_WIDTH/2-75, Main.FRAME_HEIGHT-40-30);

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

    public void increaseSpeed(int level){
        // 단계가 높아질 수록 속도 증가
        int speedIncrease = Math.min(level, 5); // 속도 증가량 제한
        dx = dx > 0 ? 2 + speedIncrease :  -(2 + speedIncrease);
        dy = dy > 0 ? 3 + speedIncrease :  -(3 + speedIncrease);
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

    private void splitBall(ArrayList<Ball> balls){
        balls.add(new Ball(x, y, -dx, dy));
        balls.add(new Ball(x, y, dx, -dy));
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

//class Block{
//    private int x, y, width, height;
//    private  boolean isYellow;
//
//    public Block(int x, int y, int width, int height, boolean isYellow ){
//        this.x = x;
//        this.y = y;
//        this.width = width;
//        this.height = height;
//        this.isYellow = isYellow;
//    }
//
//    public boolean isYellow(){
//        return isYellow;
//    }
//
//    public void draw(Graphics2D g) {
//        // Draw paddle
//        g.setColor(isYellow ? new Color(179, 96, 58) : new Color(158, 56, 38));
//        g.fillRect(x, y, width, height);
//
//        // Draw gradient border
//        GradientPaint gradient = new GradientPaint(x + width/2, y, new Color(255,141, 117), x + width/2, y + height, new Color(123,23, 7));
//        g.setPaint(gradient);
//        g.setStroke(new BasicStroke(3));
//        g.drawRect(x, y, width, height);
//    }
//
//    public Rectangle getBounds(){
//        return new Rectangle(x, y, width, height);
//    }
//}