import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundManager {
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