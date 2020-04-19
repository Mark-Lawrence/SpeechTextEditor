package SpeechTextEditor;

public class Add extends Modifier{
    private String newPart;
    private String locationReference;
    private String wordReference;
    private String numberReference;
  


    public Add(String newPart, String locationReference, String wordReference, String numberReference, String originalText) {
        super(originalText);
        this.newPart = newPart;
        this.locationReference = locationReference;
        this.wordReference = wordReference;
        this.numberReference = numberReference;
    }

    public String doModification() {
        if (newPart != "" && wordReference != "" && locationReference != "") {
//			System.out.println("New part, word reference, location reference");
//			System.out.println("insert desk to the right of computer");
            return addWordAtLocation();
        } else if (newPart != "" && locationReference != "" && numberReference != "") {
//			System.out.println("New part, location reference, number reference");
//			System.out.println("SpeechTextEditor.Add happy after the fifth word");
            return addWordByNumber();
        } else if (newPart != "" && locationReference != "") {
//			System.out.println("New part, location reference");
//			System.out.println("SpeechTextEditor.Add PDQ to the end of the sentence");
            return addBeginningOrEnd();
        } else if (newPart != "") {
            return (getOriginalText() +" "+ newPart);
        }
        return getOriginalText();
    }

    private String addWordAtLocation() {
        String originalText = getOriginalText();
        String newText = originalText;
        originalText = originalText.toLowerCase(); 
        if (originalText.contains(wordReference.toLowerCase())){
        	int index = originalText.indexOf(wordReference.toLowerCase());
            String[] addBetween = new String[wordReference.length()]; 
            if (locationReference.equals("after")) {
                if (originalText.length() > (index+1+wordReference.length())) {
                    return originalText.substring(0, index+wordReference.length())+" "+newPart+" "+originalText.substring(index+1+wordReference.length(), originalText.length());
                } else {
                    return originalText.substring(0, index+wordReference.length())+" "+newPart+" ";
                }
            } else if (locationReference.equals("before")) {
                if ((index-1) > 0) {
                    return originalText.substring(0, index-1)+" "+newPart+" "+originalText.substring(index, originalText.length());
                } else {
                    return newPart+" "+originalText.substring(index, originalText.length());
                }
            } else if (locationReference.equals("between")) {
            	addBetween = wordReference.split(" ", 2);
            	return originalText.substring(0, index+addBetween[0].length()) + " " + newPart + " " + originalText.substring(index+1+addBetween[0].length(), originalText.length());
            } 
        }
        return newText;
    }

    private String addWordByNumber() {
        String originalText = getOriginalText();
        String newText = originalText;

        int changeIndex = 0;
        try {
            changeIndex = ((int) Double.parseDouble(numberReference))-1;
            String[] splitOriginal = originalText.split(" ");
            newText = "";
            if (locationReference.equals("after")) {
                changeIndex += 1;
            }
            for (int i = 0; i < splitOriginal.length; i++) {

                if (i == changeIndex) {
                    newText += newPart+ " ";
                }
                newText += splitOriginal[i];
                newText += " ";

            }
        }
        catch (NumberFormatException e)
        {
            if (numberReference.equalsIgnoreCase("last")) {
                newText = originalText+" "+newPart +" ";
            } else {
                System.out.println("FAILED");
                return newText;
            }

        }

        //Removing the last char, it is going to be a space
        newText = newText.substring(0, newText.length() - 1);
        return newText;
    }

    private String addBeginningOrEnd() {
        String originalText = getOriginalText();
        String newText = originalText;
        if (locationReference.equals("start")){
            newText = newPart +" "+ newText;

        } else if (locationReference.equals("end")) {
            newText = newText +" "+ newPart;
        }
        return newText;
    }
}
