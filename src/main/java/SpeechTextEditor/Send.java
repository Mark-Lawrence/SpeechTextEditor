package SpeechTextEditor;

public class Send extends Modifier{


    public Send(String originalText) {
        super(originalText);
    }


    public String doModification() {
        return "SEND";
    }

}