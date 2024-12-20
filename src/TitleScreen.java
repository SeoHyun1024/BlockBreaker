import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TitleScreen {
    private JPanel p;
    private GameScreen gameScreen;
    private boolean showInstructions = true;

    public TitleScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        initComponents();
        startBlinking();
    }

    public JPanel getPanel(){
        return p;
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

                // 과제 제목
                g2.setFont(new Font("Arial", Font.PLAIN, 48));
                String titleLine1 = "Java Programming";
                String titleLine2 = "Homework #5";
                FontMetrics fm = g2.getFontMetrics();
                int titleLine1Width = fm.stringWidth(titleLine1);
                int titleLine2Width = fm.stringWidth(titleLine2);

                g2.setColor(Color.BLACK);
                g2.drawString(titleLine1, getWidth()/2 - titleLine1Width/2 + 3,140 + 3 );
                g2.drawString(titleLine2, getWidth()/2 - titleLine2Width/2 + 3, 200 + 3);

                GradientPaint titleGradient = new GradientPaint(getWidth() / 2, 100, new Color(153, 153, 153), getWidth()/2, 155, Color.WHITE);
                g2.setPaint(titleGradient);
                g2.drawString(titleLine1, getWidth()/2 - titleLine1Width/2,140 );
                g2.drawString(titleLine2, getWidth()/2 - titleLine2Width/2, 200);

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
                    g2.drawString(instructions, (getWidth() - instructionsWidth) / 2 + 2, 468 + 2);
                    g2.setColor(new Color(243, 223, 45));
                    g2.drawString(instructions, (getWidth() - instructionsWidth) / 2, 468);
                }

            }
        };
        p.setFocusable(true);
        p.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    gameScreen.showGameScreen();
                }
            }
        });
    }

    private void startBlinking() {
        Timer timer = new Timer(600, e -> {
            showInstructions = !showInstructions;
            p.repaint();
        });
        timer.start();
    }
}
