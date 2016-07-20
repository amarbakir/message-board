import java.io.*;
import java.net.Socket;

/**
 * Get Client.
 */
public class Get {

	/**
	 * @param args
	 */
	static String hostname;
	static int portnum;
	static String groupname;
	public static String DEFAULT_HOST = "localhost";
	public static int DEFAULT_PORT = 11111;
	static String userName;
	static String message;
	
	public static void main(String[] args) throws Exception  {
		if(isValid(args)){
			String result;	// user input
			Socket sock = new Socket(hostname, portnum);	// connect to the server
			System.out.println("connected!\n");
			DataOutputStream toServer = new DataOutputStream(sock.getOutputStream());
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			System.out.println("To server: get " + groupname + "\n");
			toServer.writeBytes("get " + groupname + "\n");	// send the line to the server
			result = fromServer.readLine();
			//System.out.println(result + "\n");
			System.out.println("From server: " + result + "\n");
			String tokens[] = result.split(" ");
			
			if(tokens[0].equals("error:")){
				System.out.println("Client exiting...");
				toServer.close();
				fromServer.close();
				sock.close();
				System.out.println("Socket closed.");
				System.exit(1);
			}else{
				while((result = fromServer.readLine()) != null){
					System.out.println(result);	// print it
				}
			}
			toServer.close();
			fromServer.close();
			sock.close();
		}else{
			System.out.println("error: invalid arguments\n");
		}
			
	}
	
	/**
	 * Main method. Read user input, submits get request to server, and prints response.
	 */
	public static boolean isValid(String[] args){
		int size = args.length;
		if (size == 1) {
			groupname = args[0];
			hostname = DEFAULT_HOST;
			portnum = DEFAULT_PORT;
			
		}else if (size == 3){
			if(args[0].equals("-h")){
				hostname = args[1];
				portnum = DEFAULT_PORT;
				groupname = args[2];
			}else if(args[0].equals("-p")){
				portnum = Integer.parseInt(args[1]);
				hostname = DEFAULT_HOST;
				groupname = args[2];
			}else{
				System.out.println("error: invalid command\n");
				return false;
			}
			
		}else if (size == 5){
			if(args[0].equals("-h")){
				hostname = args[1];
			}else{
				System.out.println("error: invalid command\n");	
			}
			if(args[2].equals("-p")){
				portnum = Integer.parseInt(args[3]);
			}else{
				System.out.println("error: invalid command\n");
				return false;
			}
			groupname = args[4];
			
		}else{
			System.out.println("error: invalid command\n");
			return false;
		}
		if (groupname.indexOf(' ') != -1){
			System.out.println("error: invalid groupname\n");
			return false;
		}
		if(!(portnum >= 1024 && portnum <= 65535)){
			System.out.println("error: invalid command\n");
			return false;
		}
		return true;
	}
}