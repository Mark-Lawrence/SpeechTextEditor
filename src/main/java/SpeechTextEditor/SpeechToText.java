package SpeechTextEditor;

import com.google.cloud.speech.v1p1beta1.RecognitionAudio;
import com.google.cloud.speech.v1p1beta1.RecognitionConfig;
import com.google.cloud.speech.v1p1beta1.RecognizeRequest;
import com.google.cloud.speech.v1p1beta1.RecognizeResponse;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1p1beta1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class SpeechToText {
    public static String sampleRecognize(SpeechClient speechClient) {
        // TODO(developer): Replace these variables before running the sample.
//        String localFilePath = "C:\\Users\\marklawrence\\Desktop\\test1.wav";
        String localFilePath = "C:\\Users\\jhana\\OneDrive\\Desktop\\test1.wav";
        return sampleRecognize(localFilePath, speechClient);
    }

    /**
     * Transcribe a short audio file using synchronous speech recognition
     *
     * @param localFilePath Path to local audio file, e.g. /path/audio.wav
     */
    public static String sampleRecognize(String localFilePath, SpeechClient speechClient) {
        System.out.println("Processing...");
        // The language of the supplied audio
        String languageCode = "en-US";

        // Sample rate in Hertz of the audio data sent
        int sampleRateHertz = 16000;

        // Encoding of audio data sent. This sample sets this explicitly.
        // This field is optional for FLAC and WAV audio formats.
        RecognitionConfig.AudioEncoding encoding = RecognitionConfig.AudioEncoding.LINEAR16;


        RecognitionConfig config =
                RecognitionConfig.newBuilder()
                        .setLanguageCode(languageCode)
                        .setSampleRateHertz(sampleRateHertz)
                        .setEncoding(encoding)
                        .setAudioChannelCount(1)
                        .build();
        Path path = Paths.get(localFilePath);
        byte[] data = null;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        ByteString content = ByteString.copyFrom(data);
        RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(content).build();
        RecognizeRequest request =
                RecognizeRequest.newBuilder().setConfig(config).setAudio(audio).build();
        RecognizeResponse response = speechClient.recognize(request);
        for (SpeechRecognitionResult result : response.getResultsList()) {
            // First alternative is the most probable result
            SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
            //System.out.printf("Transcript: %s\n", alternative.getTranscript());
            return alternative.getTranscript();
        }
        return "FAILED";
    }


}