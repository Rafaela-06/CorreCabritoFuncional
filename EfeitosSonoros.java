import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
public class EfeitosSonoros {
    public static void tocar(String audio){
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(audio));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
   
