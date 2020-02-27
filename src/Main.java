//Imports the Google Cloud client library

import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.cloud.dialogflow.v2.TextInput.Builder;
import com.google.common.collect.Maps;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Main {
	public static void main(String[] args){
		Scanner input = new Scanner(System.in);
	
		System.out.println("Enter inital text");
		String userText = input.nextLine();
		//String userText = "How are you doing today?";
		//System.out.println(userText);
		
		
		String userEdit = "";
		do {
		System.out.println("What edits do you want to make?");
		userEdit = input.nextLine();
		//userEdit = "Change the first word to happy";
		//System.out.println(userEdit);
		
		List<String> text = new ArrayList<>();
		text.add(userEdit);
		try {
			Modifier newModifier = detectIntentTexts("texteditor-vvvhmi", text, "123456789", "en-US", userText);
			if (newModifier != null) {
				userText = newModifier.doModification();
				System.out.println(userText);
			} else {
				System.out.println("Input not understood");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//userEdit = "Done";
		} while (!userEdit.equals("Done"));
		

	}

	

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
	  Map<String, QueryResult> queryResults = Maps.newHashMap();
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
	      QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

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
	      
	      
	      queryResults.put(text, queryResult);
	    }
	  }
	  return newModifier;
	}	
}


