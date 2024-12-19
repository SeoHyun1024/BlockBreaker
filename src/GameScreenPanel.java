import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

public class GameScreenPanel extends JPanel implements KeyListener, Runnable {
    private GameScreen gameScreen;
    private Ball ball;
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
        ball = new Ball(BlockBreaker.FRAME_WIDTH/2-5, BlockBreaker.FRAME_HEIGHT-40-30-5, -2, -3);
        paddle = new Paddle(BlockBreaker.FRAME_WIDTH/2-75, BlockBreaker.FRAME_HEIGHT-40-30);
        blocks = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 10; j++) {
                blocks.add(new Block(5 + j * 55, 5 + i * 25));
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
            ball.move();
            ball.checkCollision(paddle, blocks);

            if (ball.getY() > getHeight()){
                System.out.println("getHeight() :" + getHeight());
                running = false;
                gameScreen.showGameOverScreen();
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

        ball.draw(g);
        paddle.draw(g);

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

        if (x < 0 || x > BlockBreaker.FRAME_WIDTH - size) dx = -dx;
        if(y < 0 ) dy = -dy;
    }

    public void checkCollision(Paddle paddle, ArrayList<Block> blocks){
        Rectangle ballRect = new Rectangle(x, y, size, size);
        if (ballRect.intersects(paddle.getBounds())) {
            dy = -Math.abs(dy); // Always bounce upward
        }

        for (int i = blocks.size() - 1; i >= 0; i--){
            if(ballRect.intersects(blocks.get(i).getBounds())){
                blocks.remove(i);
                dy = -dy;
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

    public Block(int x, int y){
        this.x = x;
        this.y = y;
    }

    public void draw(Graphics g){
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, width, height);
    }

    public Rectangle getBounds(){
        return new Rectangle(x, y, width, height);
    }
}