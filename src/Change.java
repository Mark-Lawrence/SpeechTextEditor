
public class Change extends Modifier{
	
	private String originalPart;
	private String newPart;
	private String wordLocation;
	
	
	public Change(String originalPart, String newPart, String wordLocation, String originalText) {
		super(originalText);
		this.originalPart = originalPart;
		this.newPart = newPart;
		this.wordLocation = wordLocation;
	}

	
	public String doModification() {
		if (originalPart != "" && newPart != "") {
			return changeWords();
		} else if (wordLocation != "" && newPart != "") {
			return changeWordAtLocation();
		}
		
	
		return getOriginalText();
	}
	
	private String changeWords() {
		String originalText = getOriginalText();
		String newText = originalText;
		if (originalText.contains(originalPart)){
			newText = originalText.replaceFirst(originalPart, newPart);
		}
		
		return newText;
	}
	
	private String changeWordAtLocation() {
		String originalText = getOriginalText();
		String newText = originalText;
		int changeIndex = 0;
		try {
			changeIndex = ((int) Double.parseDouble(wordLocation))-1;
			String[] splitOriginal = originalText.split(" ");
			newText = "";

			for (int i = 0; i < splitOriginal.length; i++) {
				if (i == changeIndex) {
					newText += newPart;
				} else {
					newText += splitOriginal[i];
				}
				if (i+1 <splitOriginal.length) {
					newText += " ";
				}
			}
			}
			catch (NumberFormatException e)
			{
				if (wordLocation.equalsIgnoreCase("last")) {
					int startOfWordIndex = originalText.lastIndexOf(" ")+1;
					newText = originalText.substring(0, startOfWordIndex)+newPart;
				} else {
					System.out.println("FAILED");
					return newText;
				}
			}		
		
		return newText;
		
	}
}
