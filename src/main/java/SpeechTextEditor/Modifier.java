package SpeechTextEditor;

public abstract class Modifier {

    private String originalText;

    public Modifier(String originalText) {
        this.originalText = originalText;
    }

    public String getOriginalText(){
        return originalText;
    }

    public abstract String doModification();

}
