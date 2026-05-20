import java.io.Serializable;

public class Message implements Serializable {
    private String sender;
    private String text;
    private byte[] imageData;

    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }

    public Message(String sender, byte[] imageData) {
        this.sender = sender;
        this.imageData = imageData;
    }

    public String getSender() { return sender; }
    public String getText() { return text; }
    public byte[] getImageData() { return imageData; }
    public boolean isImage() { return imageData != null; }
}