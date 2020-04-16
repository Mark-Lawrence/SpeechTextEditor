package SpeechTextEditor;

import javax.sound.sampled.*;
import java.io.*;
import java.util.Date;
import java.util.Scanner;

public class JavaSoundRecorder {
    static final long RECORD_TIME = 700; // 10 minute (7000)
    // File wavFile = new File("/Users/marklawrence/Desktop/test1.wav");
    File wavFile = new File(System.getProperty("user.dir") + "\\AudioRecording.wav");

    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    static boolean stopped = false;
    TargetDataLine line;

    public static void captureSpeech() {
        stopped = false;
        final JavaSoundRecorder recorder = new JavaSoundRecorder();

        // start recording
        try {
            recorder.start();
        } catch (LineUnavailableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        return format;
    }

    private void start() throws LineUnavailableException {
        AudioFormat format = getAudioFormat();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format); // format is an AudioFormat object

        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);

        int numBytesRead = 0;
        byte[] data = new byte[line.getBufferSize() / 5];

        // Begin audio capture.
        line.start();
        System.out.println("Capturing....");
        long counter = 0;
        WaitForUserStopRecording thread = new WaitForUserStopRecording();

        // A short timer at first (for conitinuous recording) and then a longer timer
        // (or wait for a pause) after data changes to a value larger than 0 or less
        // than -1
        while (thread.waitForUser.isAlive()) {
            // Read the next chunk of data from the TargetDataLine.
            numBytesRead = line.read(data, 0, data.length);
            // Save this chunk of data.
            counter += numBytesRead;
            // System.out.println("DATA: " + data[0]);
            out.write(data, 0, numBytesRead);
        }
        line.stop();
        line.close();
        System.out.println("Done capturing...");
        byte[] fileData = out.toByteArray();
        InputStream inputStreamFile = new ByteArrayInputStream(fileData);

        AudioInputStream imputStream = new AudioInputStream(inputStreamFile, format, counter);

        try {
            AudioSystem.write(imputStream, fileType, wavFile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("FAILED TO SAVE TO FLE");
            e.printStackTrace();
        }

    }
}

class WaitForUserStopRecording implements Runnable {
    Thread waitForUser;

    WaitForUserStopRecording() {
        waitForUser = new Thread(this, "Stop recording thread");
        waitForUser.start();
    }

    public void run() {
        // int i = 0;
        Date startTime = new Date();
        float elapsedTime = new Date().getTime() - startTime.getTime();
        do {
            System.out.print("");
            elapsedTime = new Date().getTime() - startTime.getTime();
            // i++;

        } while (Main.isRecording && elapsedTime < 10 * 1000);
    }
}