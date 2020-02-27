
public class Change extends Modifier{
	private String originalPart;
	private String newPart;
	
	
	public Change(String originalPart, String newPart, String originalText) {
		super(originalText);
		this.originalPart = originalPart;
		this.newPart = newPart;
	}

	
	public String doModification() {
		if (originalPart != null && newPart != null) {
			return changeWords();
		}
		
	
		return getOriginalText();
	}
	
	private String changeWords() {
		String newText = "";
		String originalText = getOriginalText();
		if (originalText.contains(originalPart)){
			newText = originalText.replaceFirst(originalPart, newPart);
		}
		
		return newText;
	}
}
