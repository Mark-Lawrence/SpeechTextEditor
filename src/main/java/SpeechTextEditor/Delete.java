package SpeechTextEditor;

public class Delete extends Modifier{

    private String originalPart;
    private String numberOfDeletes;
    private String numberReference;


    public Delete(String originalPart, String numberOfDeletes, String numberReference, String originalText) {
        super(originalText);
        this.originalPart = originalPart;
        this.numberOfDeletes = numberOfDeletes;
        this.numberReference = numberReference;
    }


    public String doModification() {
        if (originalPart != "") {
            return deleteWord();
        } else if (numberReference != "" && numberOfDeletes != "") {
            //return deleteXWords();t
        } else if (numberReference != "") {
            return deleteWordAtLocation();
        }
        return getOriginalText();
    }

    private String deleteWord() {
        String originalText = getOriginalText();
        String newText = originalText;
        originalText = originalText.toLowerCase();
        if (originalText.contains(originalPart.toLowerCase())){
            int index = originalText.indexOf(originalPart.toLowerCase());
            if ((index+1+originalPart.length()) < originalText.length()) {
                return originalText.substring(0, index)+originalText.substring(index+1+originalPart.length(), originalText.length());
            } else {
                return originalText.substring(0, index-1);
            }
        }
        return newText;
    }

    private String deleteWordAtLocation() {
        String originalText = getOriginalText();
        String newText = originalText;
        int changeIndex = 0;
        try {
            changeIndex = ((int) Double.parseDouble(numberReference))-1;
            String[] splitOriginal = originalText.split(" ");
            newText = "";

            for (int i = 0; i < splitOriginal.length; i++) {
                if (i != changeIndex) {
                    newText += splitOriginal[i];
                    newText += " ";

                } else if (i+1 <splitOriginal.length) {
                    //newText += " ";
                }
            }
        }
        catch (NumberFormatException e)
        {
            if (numberReference.equalsIgnoreCase("last")) {
                int startOfWordIndex = originalText.lastIndexOf(" ")+1;
                newText = originalText.substring(0, startOfWordIndex-1);
            } else {
                System.out.println("FAILED");
                return newText;
            }
        }
        return newText;
    }

}