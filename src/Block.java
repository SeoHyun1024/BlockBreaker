import java.awt.*;
import java.util.ArrayList;

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