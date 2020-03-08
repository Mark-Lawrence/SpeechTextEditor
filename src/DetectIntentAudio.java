// Imports the Google Cloud client library

import com.google.cloud.dialogflow.v2.AudioEncoding;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.InputAudioConfig;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.QueryResult;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.protobuf.ByteString;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;


/**
 * DialogFlow API Detect Intent sample with audio files.
 */
public class DetectIntentAudio {
  // [START dialogflow_detect_intent_audio]

  /**
   * Returns the result of detect intent with an audio file as input.
   *
   * Using the same `session_id` between requests allows continuation of the conversation.
   *
   * @param projectId     Project/Agent Id.
   * @param audioFilePath Path to the audio file.
   * @param sessionId     Identifier of the DetectIntent session.
   * @param languageCode  Language code of the query.
   * @return QueryResult for the request.
   */
  public static Modifier detectIntentAudio(
      String projectId,
      String audioFilePath,
      String sessionId,
      String languageCode,
      String originalText,
      SessionsClient sessionsClient)
      throws Exception {
    // Instantiates a client
	Modifier newModifier = null;
   
      // Set the session name using the sessionId (UUID) and projectID (my-project-id)
      SessionName session = SessionName.of(projectId, sessionId);
      //System.out.println("Session Path: " + session.toString());

      // Note: hard coding audioEncoding and sampleRateHertz for simplicity.
      // Audio encoding of the audio content sent in the query request.
      AudioEncoding audioEncoding = AudioEncoding.AUDIO_ENCODING_LINEAR_16;
      int sampleRateHertz = 16000;

      // Instructs the speech recognizer how to process the audio content.
      InputAudioConfig inputAudioConfig = InputAudioConfig.newBuilder()
          .setAudioEncoding(audioEncoding) // audioEncoding = AudioEncoding.AUDIO_ENCODING_LINEAR_16
          .setLanguageCode(languageCode) // languageCode = "en-US"
          .setSampleRateHertz(sampleRateHertz) // sampleRateHertz = 16000
          .build();

      // Build the query with the InputAudioConfig
      QueryInput queryInput = QueryInput.newBuilder().setAudioConfig(inputAudioConfig).build();

      // Read the bytes from the audio file
      byte[] inputAudio = Files.readAllBytes(Paths.get(audioFilePath));

      // Build the DetectIntentRequest
      DetectIntentRequest request = DetectIntentRequest.newBuilder()
          .setSession(session.toString())
          .setQueryInput(queryInput)
          .setInputAudio(ByteString.copyFrom(inputAudio))
          .build();

      // Performs the detect intent request
      DetectIntentResponse response = sessionsClient.detectIntent(request);

      // Display the query result
      QueryResult queryResult = response.getQueryResult();
//      System.out.println("====================");
      //System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
      //System.out.format("Detected Intent: %s (confidence: %f)\n",
          //queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
      //System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());
      
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

      return newModifier;
    
  }
  // [END dialogflow_detect_intent_audio]
}