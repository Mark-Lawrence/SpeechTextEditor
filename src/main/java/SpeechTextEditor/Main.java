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
        	System.out.println("");
        	if (startInitialRecording) {
        		System.out.println("Starting NOW");
        		startInitialRecording = false;
        		userText = recordInitialMessage();
        		headerLabel.setText(userText);
        	}
        	
        	if (startRecodingAudio) {
        		System.out.println("Starting NOW 2");
        		startRecodingAudio = false;
        		recordAudio();
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
    


    //The code below isn't used anymore... only if typing instead of talking
    /**
     * Returns the result of detect intent with texts as inputs.
     *
     * Using the same `session_id` between requests allows continuation of the conversation.
     *
     * @param projectId    Project/Agent Id.
     * @param texts        The text intents to be detected based on what a user says.
     * @param sessionId    Identifier of the DetectIntent session.
     * @param languageCode Language code of the query.
     * @return The QueryResult for each input text.
     */
    public static Modifier detectIntentTexts(
            String projectId,
            List<String> texts,
            String sessionId,
            String languageCode,
            String originalText) throws Exception {

        Modifier newModifier = null;
        // Instantiates a client
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            //System.out.println("Session Path: " + session.toString());

            // Detect intents for each text input
            for (String text : texts) {
                // Set the text (hello) and language code (en-US) for the query
                Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(languageCode);

                // Build the query with the TextInput

                //CHANGE THIS TO SOMETHING LIKE THIS: https://cloud.google.com/dialogflow/docs/detect-intent-stream
                QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();
                //QueryInput = waveInput =QueryInput.newBuilder().set

                // Performs the detect intent request
                DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

                // Display the query result
                QueryResult queryResult = response.getQueryResult();

//	      System.out.println("====================");
//	      System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
//	      System.out.format("Detected Intent: %s (confidence: %f)\n",
//	          queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
//	      System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());
//	      System.out.format("Variables: '%s'\n", queryResult.getParameters());

                Struct parameters = queryResult.getParameters();

                Map<String, Value> fields = parameters.getFieldsMap();

                if (fields.containsKey("modifiers")) {
                    String modifier = fields.get("modifiers").getStringValue();
                    if (modifier.equals("change")) {
                        String originalPart = fields.get("originalPart").getStringValue();
                        String newPart = fields.get("newPart").getStringValue();
                        String wordLocation = Double.toString(fields.get("ordinal").getNumberValue());
                        if (wordLocation.equals("0.0")) {
                            wordLocation = fields.get("ordinal").getStringValue();
                        }
                        newModifier = new Change(originalPart, newPart, wordLocation, originalText);
                    }

                    else if (modifier.equals("add")) {
                        String newPart = fields.get("newPart").getStringValue();
                        String locationReference = fields.get("locationReference").getStringValue();
                        String wordReference = fields.get("wordReference").getStringValue();
                        String numberReference = Double.toString(fields.get("ordinal").getNumberValue());
                        if (numberReference.equals("0.0")) {
                            numberReference = fields.get("ordinal").getStringValue();
                        }
                        newModifier = new Add(newPart, locationReference, wordReference, numberReference, originalText);
                    }
                    else if (modifier.equals("delete")) {
                        String originalPart = fields.get("originalPart").getStringValue();
                        String numberOfDeletes = fields.get("numberOfDeletes").getStringValue();
                        String numberReference = Double.toString(fields.get("ordinal").getNumberValue());
                        if (numberReference.equals("0.0")) {
                            numberReference = fields.get("ordinal").getStringValue();
                        }
                        newModifier = new Delete(originalPart,numberOfDeletes, numberReference, originalText);
                    }
                }
            }
        }
        return newModifier;
    }
}
