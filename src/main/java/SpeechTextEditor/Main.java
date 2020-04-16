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

public class Main {
	
	
	static boolean initialRecording = true;
	static String userText = "";
	private static JLabel headerLabel;
	private static JButton recordButton;
	
	public static boolean isRecording = false;
	
	static boolean startInitialRecording = false;
	static boolean startRecodingAudio = false;
	static int outputFileIndex = 0;
	
    public static void main(String[] args){
    	
    	System.out.println("Initilizing....");
    
   
    	JFrame f = new JFrame("Voice Text Editor");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new GridLayout(2, 1));
        f.setSize(400,400);
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
        
        
        //f.show();
        f.add(headerLabel);
        f.add(recordButton);
        f.setVisible(true);
        do {
			System.out.print("");
        	if (startInitialRecording) {
        		System.out.println("Starting NOW");
        		startInitialRecording = false;
        		userText = recordInitialMessage();
        		System.out.println("BAD");
        		headerLabel.setText(userText);
        		try {
        			TextToSpeech test = new TextToSpeech();
        			test.speak("Your message says: "+ userText, outputFileIndex);
        			outputFileIndex += 1;
        		} catch (Exception e1) {
        			e1.printStackTrace();
        		}
        	}
        	
        	if (startRecodingAudio) {
        		System.out.println("Starting NOW 2");
        		startRecodingAudio = false;
        		recordAudio();
        		System.out.println("TESTING");
        		try {
        			System.out.println("HERE");
        			TextToSpeech test = new TextToSpeech();
        			test.speak("It now says: "+ userText, outputFileIndex);
        			outputFileIndex += 1;
        		} catch (Exception e1) {
        			e1.printStackTrace();
        		}
        		System.out.println("TREFDDFDG");
        		headerLabel.setText(userText);
        		
        	}
        }while(true);
        
//        Scanner input = new Scanner(System.in);
//        
//
//        
//        
//        
//        System.out.println("Enter inital text");
//        //input.nextLine();
//        
//        //String userText = recordInitialMessage(speechClient);
//        String userText = "Do you want to go to the park";
//        //String userText = input.nextLine();
//       // System.out.println(userText);
//
//        do {
//           // System.out.println("What edits do you want to make?");
//            //input.nextLine();
//            //recordAudio();
//            
//        } while (true);


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
    	//userEdit = input.nextLine();
        JavaSoundRecorder.captureSpeech();

        try {
            Modifier newModifier = DetectIntentAudio.detectIntentAudio("speech-text-editor-jhsbcd",  System.getProperty("user.dir")+"\\AudioRecording.wav", "123456789", "en-US", userText, sessionsClient);

            //SpeechTextEditor.Modifier newModifier = detectIntentTexts("texteditor-vvvhmi", text, "123456789", "en-US", userText);
            if (newModifier != null) {
                userText = newModifier.doModification();
                //System.out.println(userText);
  
            } else {
                System.out.println("Input not understood");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return userText;
    	
    }
 
}
