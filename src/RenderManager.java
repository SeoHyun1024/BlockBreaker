import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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