/**
 * Group Class. Stores group info and linked-list of messages.
 */
public class Group {
	public String name;
	public int numMessages;
	public Message next;
	public Message last;
	
	public Group(String name) {
		this.name = name;
		this.numMessages = 0;
		this.next = null;
		this.last = null;
	}
}
