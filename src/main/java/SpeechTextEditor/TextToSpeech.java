package SpeechTextEditor;

//Imports the Google Cloud client library
import com.google.cloud.texttospeech.v1.AudioConfig;
import com.google.cloud.texttospeech.v1.AudioEncoding;
import com.google.cloud.texttospeech.v1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1.SynthesisInput;
import com.google.cloud.texttospeech.v1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;



/**
* Google Cloud TextToSpeech API sample application. Example usage: mvn package exec:java
* -Dexec.mainClass='com.example.texttospeech.QuickstartSample'
*/
public class TextToSpeech {
	
	private final static int BUFFER_SIZE = 128000;
    private static File soundFile;
    private static AudioInputStream audioStream;
    private static AudioFormat audioFormat;
    private static SourceDataLine sourceLine;


/** Demonstrates using the Text-to-Speech API. */
public static void speak(String textToSpeak) throws Exception {
 // Instantiates a client
 try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
   // Set the text input to be synthesized
   SynthesisInput input = SynthesisInput.newBuilder().setText(textToSpeak).build();

   // Build the voice request, select the language code ("en-US") and the ssml voice gender
   // ("neutral")
   VoiceSelectionParams voice =
       VoiceSelectionParams.newBuilder()
           .setLanguageCode("en-US")
           .setSsmlGender(SsmlVoiceGender.NEUTRAL)
           .build();

   // Select the type of audio file you want returned
   AudioConfig audioConfig =
       AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build();

   // Perform the text-to-speech request on the text input with the selected voice parameters and
   // audio file type
   SynthesizeSpeechResponse response =
       textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

   // Get the audio contents from the response
   ByteString audioContents = response.getAudioContent();
   
    
   //audioContents.writeTo(out);
   // Write the response to the output file.
   try (OutputStream out = new FileOutputStream("output.wav")) {
     out.write(audioContents.toByteArray());
     System.out.println("Audio content written to file \"output.wav\"");
     playSound(System.getProperty("user.dir")+"\\output.wav");
   }
 }
}
public static void playSound(String filename){

    String strFilename = filename;

    try {
        soundFile = new File(strFilename);
    } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
    }

    try {
        audioStream = AudioSystem.getAudioInputStream(soundFile);
    } catch (Exception e){
        e.printStackTrace();
        System.exit(1);
    }

    audioFormat = audioStream.getFormat();

    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
    try {
        sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceLine.open(audioFormat);
    } catch (LineUnavailableException e) {
        e.printStackTrace();
        System.exit(1);
    } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
    }

    sourceLine.start();

    int nBytesRead = 0;
    byte[] abData = new byte[BUFFER_SIZE];
    while (nBytesRead != -1) {
        try {
            nBytesRead = audioStream.read(abData, 0, abData.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (nBytesRead >= 0) {
            @SuppressWarnings("unused")
            int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
        }
    }

    sourceLine.drain();
    sourceLine.close();
}
}