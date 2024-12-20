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

    public GameScreenPanel(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        setFocusable(true);
        addKeyListener(this);
        setSize(BlockBreaker.FRAME_WIDTH,BlockBreaker.FRAME_HEIGHT);
        requestFocusInWindow();
        initGameObjects();
    }

    private void initGameObjects() {
        balls = new ArrayList<>();
        balls.add(new Ball(BlockBreaker.FRAME_WIDTH/2-5, BlockBreaker.FRAME_HEIGHT-40-30-5, -2, -3));
        racket = new Racket(BlockBreaker.FRAME_WIDTH/2-75, BlockBreaker.FRAME_HEIGHT-40-30);

       generateBlocks();    // 블럭 생성
    }

    private void generateBlocks(){
        blocks = new ArrayList<>();
        int rows = level * 3;
        int cols = level * 3;

        int padding = 6;
        int blockWidth = (BlockBreaker.FRAME_WIDTH - cols * padding - 40 )/cols;
        int blockHeight = ((BlockBreaker.FRAME_HEIGHT / 3) - 2 * padding)/rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int x = 20 + padding + col * (blockWidth + padding);
                int y = 20 + row * (blockHeight + padding) + padding;
                boolean isYellow = new Random().nextBoolean();
                blocks.add(new Block(x, y, blockWidth, blockHeight, isYellow));
            }
        }

        for (Ball ball : balls){
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
                        gameScreen.showGameOverScreen(score, highScore);
                    }
                }
            }

            if (blocks.isEmpty()){
                level++;
                generateBlocks();
                balls.clear();
                balls.add(new Ball(BlockBreaker.FRAME_WIDTH/2-5, BlockBreaker.FRAME_HEIGHT-40-30-5, -2, -3));
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

        // 배경 그라데이션
        GradientPaint background = new GradientPaint(0, 0, new Color(11, 12, 32), 0, getHeight(), new Color(108, 111, 135));
        g2.setPaint(background);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 좌측 벽 그리기
        g.setColor(new Color(66, 25, 11));
        g.fillRect(0, 0, 20, getHeight());
        g2.setStroke(new BasicStroke(3));
        g2.setPaint(new GradientPaint(0, 0, new Color(249, 198, 181), 20, getHeight(), new Color(61, 59, 59)));
        g2.drawRect(0, 0, 20, getHeight());

        // 우측 벽 그리기
        g.setColor(new Color(66, 25, 11));
        g.fillRect(getWidth() - 20, 0, 20, getHeight());
        g2.setStroke(new BasicStroke(3));
        g2.setPaint(new GradientPaint(0, 0, new Color(249, 198, 181), 20, getHeight(), new Color(61, 59, 59)));
        g2.drawRect(getWidth() - 20, 0, 20, getHeight());

        // 상단 벽 그리기
        g.setColor(new Color(66, 25, 11));
        g.fillRect(0, 0, getWidth(), 20);
        g2.setStroke(new BasicStroke(3));
        g2.setPaint(new GradientPaint(0, 0, new Color(249, 198, 181), 20, getHeight(), new Color(61, 59, 59)));
        g2.drawRect(0, 0, getWidth(), 20);

        // 벽돌 그리기
        for (Ball ball : balls){
            ball.draw(g);
        }

        // 패들 그리기
        racket.draw((Graphics2D) g);

        // 블럭 그리기
        for (Block block : blocks){
            block.draw((Graphics2D) g);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            System.out.println("LEFT");
            racket.moveLeft();
        }else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            System.out.println("RIGHT");
            racket.moveRight();
        }
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}

class Ball{
    private int x, y, dx, dy, size = 10;

    public Ball(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
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

        if (x < 20 || x > BlockBreaker.FRAME_WIDTH - size -20) dx = -dx;
        if(y < 20 ) dy = -dy;
    }

    public void checkCollision(Racket racket, ArrayList<Block> blocks, ArrayList<Ball> balls){
        Rectangle ballRect = new Rectangle(x, y, size, size);
        if (ballRect.intersects(racket.getBounds())) {
            dy = -Math.abs(dy); // Always bounce upward
        }

        for (int i = blocks.size() - 1; i >= 0; i--){
            Block block = blocks.get(i);
            if(ballRect.intersects(blocks.get(i).getBounds())){
                blocks.remove(i);
                dy = -dy;
                GameScreenPanel.score += 10;    // 점수 10점 추가
                if(block.isYellow()){
                    splitBall(balls);
                }
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
}

class Racket {
    private int x, y, width = 150, height = 30;

    public Racket(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void moveLeft(){
        x = Math.max(20, x - 20);
    }

    public void moveRight(){
        x = Math.min(BlockBreaker.FRAME_WIDTH - width - 20, x + 20);
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

class Block{
    private int x, y, width, height;
    private  boolean isYellow;

    public Block(int x, int y, int width, int height, boolean isYellow ){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isYellow = isYellow;
    }

    public boolean isYellow(){
        return isYellow;
    }

    public void draw(Graphics2D g) {
        // Draw paddle
        g.setColor(isYellow ? new Color(179, 96, 58) : new Color(158, 56, 38));
        g.fillRect(x, y, width, height);

        // Draw gradient border
        GradientPaint gradient = new GradientPaint(x + width/2, y, new Color(255,141, 117), x + width/2, y + height, new Color(123,23, 7));
        g.setPaint(gradient);
        g.setStroke(new BasicStroke(3));
        g.drawRect(x, y, width, height);
    }

    public Rectangle getBounds(){
        return new Rectangle(x, y, width, height);
    }
}