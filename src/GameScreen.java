import javax.swing.*;

public class GameScreen extends JFrame{

    private TitleScreen titleScreen;
    private GameScreenPanel gameScreenPanel;
    private GameOverScreen gameOverScreen;

    public GameScreen() {
        setTitle("BlockBreaker Game");
        setSize(BlockBreaker.FRAME_WIDTH, BlockBreaker.FRAME_HEIGHT);
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
    }

    public void showGameScreen(){
        gameScreenPanel = new GameScreenPanel(this);
        setContentPane(gameScreenPanel);
        repaint();
        gameScreenPanel.startGame();
    }

    public void showGameOverScreen(){
        gameOverScreen = new GameOverScreen(this);
        setContentPane(gameOverScreen.getPanel());
        revalidate();
        repaint();
    }
}
