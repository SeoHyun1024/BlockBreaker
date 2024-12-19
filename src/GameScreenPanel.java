import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class GameScreenPanel extends JPanel implements KeyListener, Runnable {
    private GameScreen gameScreen;
    private ArrayList<Ball> balls;
    private Paddle paddle;
    private ArrayList<Block> blocks;
    private boolean running;

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
        paddle = new Paddle(BlockBreaker.FRAME_WIDTH/2-75, BlockBreaker.FRAME_HEIGHT-40-30);

        blocks = new ArrayList<>();

        int blockWidth = 50;
        int blockHeight = 20;
        int padding = 5;

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 10; col++) {
                int x = 20 + padding + col * (blockWidth + padding);
                int y = 50 + row * (blockHeight + padding);
                blocks.add(new Block(x, y, new Random().nextBoolean()));
            }
        }
    }

    public void startGame(){
        running = true;
        requestFocus();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (running){
            for (Ball ball : new ArrayList<>(balls)){
                ball.move();
                ball.checkCollision(paddle, blocks, balls);

                if (ball.getY() > getHeight()){
                    balls.remove(ball);
                    if(balls.isEmpty()){
                        running = false;
                        gameScreen.showGameOverScreen();
                    }
                }
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

        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 좌우 벽 그리기
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, 20, getHeight());  // 좌측 벽
        g.fillRect(getWidth() - 20, 0, 20, getHeight());    // 우측 벽

        // 상단 벽 그리기
        g.fillRect(0, 0, getWidth(), 20);

        // 벽돌 그리기
        for (Ball ball : balls){
            ball.draw(g);
        }

        // 패들 그리기
        paddle.draw(g);

        // 블럭 그리기
        for (Block block : blocks){
            block.draw(g);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_LEFT){
            System.out.println("LEFT");
            paddle.moveLeft();
        }else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
            System.out.println("RIGHT");
            paddle.moveRight();
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

    public void move(){
        x += dx;
        y += dy;

        if (x < 20 || x > BlockBreaker.FRAME_WIDTH - size -20) dx = -dx;
        if(y < 20 ) dy = -dy;
    }

    public void checkCollision(Paddle paddle, ArrayList<Block> blocks, ArrayList<Ball> balls){
        Rectangle ballRect = new Rectangle(x, y, size, size);
        if (ballRect.intersects(paddle.getBounds())) {
            dy = -Math.abs(dy); // Always bounce upward
        }

        for (int i = blocks.size() - 1; i >= 0; i--){
            Block block = blocks.get(i);
            if(ballRect.intersects(blocks.get(i).getBounds())){
                blocks.remove(i);
                dy = -dy;
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

class Paddle{
    private int x, y, width = 150, height = 30;

    public Paddle(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void moveLeft(){
        x = Math.max(0, x - 20);
    }

    public void moveRight(){
        x = Math.min(500, x + 20);
    }

    public void draw(Graphics g){
        g.setColor(Color.PINK);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds(){
        return new Rectangle(x, y, width, height);
    }
}

class Block{
    private int x, y, width = 50, height = 20;
    private  boolean isYellow;

    public Block(int x, int y, boolean isYellow ){
        this.x = x;
        this.y = y;
        this.isYellow = isYellow;
    }

    public boolean isYellow(){
        return isYellow;
    }

    public void draw(Graphics g){
        g.setColor(isYellow ? Color.YELLOW : Color.MAGENTA);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds(){
        return new Rectangle(x, y, width, height);
    }
}