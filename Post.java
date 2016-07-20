import java.io.*;
import java.net.Socket;

/**
 * Post Client.
 */
public class Post {

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
	
	/**
	 * Main method. Read user input, submits post request to server, and prints response.
	 */
	public static void main(String[] args)throws Exception  {
		if(isValid(args)){
			String line;	// user input
			BufferedReader userdata = new BufferedReader(new InputStreamReader(System.in));
			Socket sock = new Socket(hostname, portnum);	// connect to the server
			System.out.println("Connected to server!\n");
			DataOutputStream toServer = new DataOutputStream(sock.getOutputStream());
			BufferedReader fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			userName = System.getProperty("user.name");
			
			System.out.println("To server: post " + groupname + "\n");
			toServer.writeBytes("post " + groupname + '\n');	// send the line to the server
			String result = fromServer.readLine();
			System.out.println("From server: " + result + "\n");
			String tokens[] = result.split(" ");
			
			if(tokens[0].equals("error:")){
				System.out.println("Client exiting...");
				toServer.close();
				fromServer.close();
				sock.close();
				System.out.println("Socket closed.");
				System.exit(1);
				//break;
			}else{
				System.out.println("To server: id " + userName + "\n");
				toServer.writeBytes("id " + userName + "\n");
				String result2 = fromServer.readLine();
				System.out.println("From server: " + result2 + "\n");
				String tokens2[] = result.split(" ");
				
				if(tokens2[0].equals("error:")){
					System.out.println("Client exiting...");
					toServer.close();
					fromServer.close();
					sock.close();
					System.out.println("Socket closed.");
					System.exit(1);
				}else{
					while((line = userdata.readLine()) != null){
						toServer.writeBytes(line);
					}
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
	 * Method to confirm that the user's request is valid.
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