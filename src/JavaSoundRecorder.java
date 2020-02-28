import javax.sound.sampled.*;
import java.io.*;
import java.security.Permission;


public class JavaSoundRecorder {
    static final long RECORD_TIME = 10000;  // 10 minute
    File wavFile = new File("/Users/marklawrence/Desktop/test1.wav");
    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    TargetDataLine line;
	
    
    public static void captureSpeech() {
    	checkPermissions();
        final JavaSoundRecorder recorder = new JavaSoundRecorder();
 
        // creates a new thread that waits for a specified
        // of time before stopping
        Thread stopper = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(RECORD_TIME);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                recorder.finish();
            }
        });
 
        stopper.start();
 
        // start recording
        recorder.start();
    }
    
    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                                             channels, signed, bigEndian);
        return format;
    }
    
    /**
     * Captures the sound and record into a WAV file
     */
    private void start() {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
 
            // checks if system supports the data line
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                System.exit(0);
            }
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();   // start capturing
 
            System.out.println("Start capturing...");
 
            AudioInputStream ais = new AudioInputStream(line);
 
            System.out.println("Start recording...");
 
            // start recording
            AudioSystem.write(ais, fileType, wavFile);
 
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    
    private void finish() {
        line.stop();
        line.close();
        System.out.println("Finished");
    }
	
    
    private static void checkPermissions() {
      SecurityManager sm = System.getSecurityManager();
      
      
      if (sm != null)
        {
    	  System.out.println("Getting permission");
          String perm = null;
          perm = "record";
//          switch (permission)
//            {
//            case PLAY:
//              perm = "play";
//              break;
//
//            case RECORD:
//              perm = "record";
//              break;
//
//            case ALL: default:
//              perm = "*";
//              break;
//            }

          sm.checkPermission(new AudioPermission(perm));
        }
    }
	
	
}