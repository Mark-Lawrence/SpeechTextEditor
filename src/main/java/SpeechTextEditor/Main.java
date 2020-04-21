package SpeechTextEditor;

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;
import com.google.cloud.speech.v1p1beta1.SpeechClient;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Date;

public class Main {
    // V.A.L.E == Voice Activated Language Editor
    static String wakeWord = "okay vale";
    static int SLEEP_TIME = 10 * 1000;
    static Date startTime = new Date();
    static boolean botIsActive = false;
    static boolean visitedInitial = false;

    static boolean initialRecording = true;
    static String userText = "";
    private static JLabel headerLabel;
    private static JButton recordButton;

    public static boolean isRecording = true;

    static boolean startInitialRecording = true;
    static boolean startRecodingAudio = false;
    static int i = 1;
    static int outputFileIndex = 0;

    public static void main(String[] args) {

        System.out.println("Initializing....");
        
        
        JFrame f = new JFrame("Voice Text Editor");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new GridLayout(2, 1));
        f.setSize(400, 400);
        headerLabel = new JLabel("What do you want to say", JLabel.CENTER);

        recordButton = new JButton("Dictate");
        recordButton.setSize(100, 70);
        recordButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isRecording) {
                    System.out.println("Stop capturing");
                    isRecording = false;
                    recordButton.setText("Make Modification");
                } else {
                    isRecording = true;
                    recordButton.setText("Stop");
                    if (initialRecording) {
                        System.out.println("Start initial recording");
                        initialRecording = false;
                        startInitialRecording = true;
                    } else {
                        System.out.println("Start audio recording");
                        startRecodingAudio = true;
                    }
                }
            }
        });

        // f.show();
        f.add(headerLabel);
        // f.add(recordButton);
        f.setVisible(true);
        while (true) {
            if (startInitialRecording) {
            	System.out.println("Waiting for wake word (start)");
            	try {
        			InfiniteStreamRecognize.infiniteStreamingRecognize("en-US");
        		} catch (Exception e1) {
        			// TODO Auto-generated catch block
        			e1.printStackTrace();
        		}
            	botIsActive = true;
                System.out.println("Wake word said");
                
                startInitialRecording = false;
                try {
					TextToSpeech.speak("What would you like to say?",-1);
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
                
                System.out.println("Recording inital message");
                userText = recordInitialMessage();

                headerLabel.setText(userText);
                try {
                    TextToSpeech.speak("Your message says: "+ userText+". Make any modifications you would like, or send.", outputFileIndex);
                    outputFileIndex += 1;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                startRecodingAudio = true;
            }

            if (startRecodingAudio) {
                System.out.println("Capture modification");
                // startRecodingAudio = false;
                recordAudio();
                headerLabel.setText(userText);
                try {
                    System.out.println("HERE");
                    TextToSpeech.speak("It now says: "+ userText, outputFileIndex);
                    outputFileIndex += 1;
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

            }
        }

    }

    
    public static String recordInitialMessage() {
    	SpeechClient speechClient = null;
        try {
            speechClient = SpeechClient.create();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    	JavaSoundRecorder.captureSpeech();
        return SpeechToText.sampleRecognize(speechClient);
    }


    public static String recordAudio() {
        SessionsClient sessionsClient = null;
        try {
            sessionsClient = SessionsClient.create();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        // userEdit = input.nextLine();
        JavaSoundRecorder.captureSpeech();

        try {
            Modifier newModifier = DetectIntentAudio.detectIntentAudio("speech-text-editor-jhsbcd",
                    System.getProperty("user.dir") + "\\AudioRecording.wav", "123456789", "en-US", userText,
                    sessionsClient);

            // SpeechTextEditor.Modifier newModifier =
            // detectIntentTexts("texteditor-vvvhmi", text, "123456789", "en-US", userText);
            if (newModifier != null) {
                userText = newModifier.doModification();
                // System.out.println(userText);

            } else {
                System.out.println("Input not understood");
                return recordAudio();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return userText;
    }
}
