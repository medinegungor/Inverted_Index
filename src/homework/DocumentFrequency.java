package homework;
public class DocumentFrequency {
	private String fileName;
    private int frequency;

    public DocumentFrequency(String fileName, int frequency) {
        this.fileName = fileName;
        this.frequency = frequency;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void incrementFrequency() {
        this.frequency++;
    }

    @Override
    public String toString() {
        return frequency + "-" + fileName;
    }
}
