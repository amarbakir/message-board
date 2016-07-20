import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Message Class. Stores message info and linked-list data.
 */
public class Message {
	public String formattedMsg;
	public String username;
	public String address;
	public String timeStamp;
	public String message;
	public Message next;
	
	public Message(String username, String address, String message) {
		this.username = username;
		this.address = address;
		this.timeStamp = DateFormat.getDateTimeInstance(
                DateFormat.LONG, 
                DateFormat.LONG, 
                Locale.US).format(new Date());
		this.message = message;
		this.next = null;
		formattedMsg = this.toString();
	}
	
	/**
	 * Overloaded toString method for custom object printing.
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("From " + this.username + " " + this.address + " " + this.timeStamp + '\n');
		sb.append('\n');
		sb.append(this.message + '\n');
		return sb.toString();
	}
}
