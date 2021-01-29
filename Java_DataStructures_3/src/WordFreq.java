
public class WordFreq {

	private String word;
	private int freq;
	
	public WordFreq(String word, int freq) {
		this.word = word;
		this.freq = freq;
	}

	@Override
	public String toString() {
		return "WordFreq [word=" + word + ", freq=" + freq + "]\n";
	}
	
	public String key() {
		return word;
	}
	
	public int freq() {
		return freq;
	}
	
	public void addFreq() {
		++freq;
	}
	
}
