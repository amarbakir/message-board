import java.net.*;
import java.util.HashMap;
import java.io.*;

/**
 * Server.
 */
public class Server {

	public static int DEFAULT_PORT = 11111;
	public static ServerSocket serverSocket;
	public static HashMap<String, Group> groupsDB;
	
	Server (int port) throws IOException {
		Server.serverSocket = new ServerSocket(port, 10);
		Server.groupsDB = new HashMap<String, Group>();
	}
	
	/**
	 * Main method. Wait to accept client connection requests and spins off worker threads to deal with them.
	 */
	public static void main(String[] args) throws IOException {
		int serverPort = DEFAULT_PORT;
		if (args.length > 2) {
			System.err.println("Usage: java Server <-p port>");
			return;
		}
		if (args.length == 0) {
			serverPort = DEFAULT_PORT;
		} else if (args.length == 2) {
			if (args[0].equals("-p")) {
				serverPort = Integer.parseInt(args[1]);
			}
		}
		Server myServer = new Server(serverPort);
		while (true) {
			Socket clientSocket = serverSocket.accept();
			System.out.println("New thread created!");
			new Thread(myServer.new serverThread(clientSocket)).start();
		}
	}

	public class serverThread implements Runnable {
		private Socket socket;
		
		/**
		 * ServerThread constructor.
		 */
		public serverThread(Socket socket) {
			this.socket = socket;
		}
		
		/**
		 * Method to write a new message to the server, given a groupname and the message.
		 */
		public synchronized void updateDB(Message message, String group) {
			if (!groupsDB.containsKey(group)) {
				Group newGroup = new Group(group);
				groupsDB.put(group, newGroup);
				groupsDB.get(group).next = message;
				groupsDB.get(group).last = message;
			} else {
				groupsDB.get(group).last.next = message;
				groupsDB.get(group).last = message;
			}
			groupsDB.get(group).numMessages++;
		}
		
		/**
		 * Method to read a group's messages from the server, given the groupname.
		 */
		public String readDB(String group) {
			StringBuilder sb = new StringBuilder();
			Group thisGroup = groupsDB.get(group);
			Message curr = null;
			if (thisGroup == null) {
				return null;
			}
			curr = thisGroup.next;
			sb.append(thisGroup.numMessages + " messages\n");
			while (curr != null) {
				sb.append("From " + curr.username + " " + curr.address + " " + curr.timeStamp + '\n');
				sb.append('\n');
				sb.append(curr.message + '\n');
				if (curr.next != null) {
					sb.append('\n');
				}
				curr = curr.next;
			}
			return sb.toString();
		}
		
		/**
		 * Run method that the thread executes. All work is done here, from validating requests to responding to them.
		 */
		@Override
		public void run() {
			BufferedReader fromClient = null;
			try {
				fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			} catch (IOException e) {
				System.err.println("error: cannot create fromClient BufferedReader");
			}
			DataOutputStream toClient = null;
			try {
				toClient = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
				System.err.println("error: cannot create toClient DataOutputStream");
			}
			
			String line = null;
			String groupName = null;
			String PostOrGet = null;
			String userName = null;
			
			try {
				if ((line = fromClient.readLine()) != null) {
					System.out.println("From client: " + line + "\n");
					String tokens[] = line.split(" +");
					if (tokens.length != 2 || (!tokens[0].equals("post") && !tokens[0].equals("get"))) {
						System.out.println("To client: error: invalid command\n");
						toClient.writeBytes("error: invalid command\n");
						toClient.close();
						fromClient.close();
						socket.close();
						System.out.println("Connection closed.");
						return;
					} else {
						PostOrGet = tokens[0];
						for (int i = 0; i < tokens[1].length(); i++) {
							if (Character.isWhitespace(tokens[1].charAt(i)) || Character.isISOControl(tokens[1].charAt(i))) {
								System.out.println("To client: error: invalid group name\n");
								toClient.writeBytes("error: invalid group name\n");
								toClient.close();
								fromClient.close();
								socket.close();
								System.out.println("Connection closed.");
								return;
							}
						}
						groupName = tokens[1];
						if (PostOrGet.equals("get") && ((groupsDB.size() == 0) || !groupsDB.containsKey(groupName))) {
							System.out.println("To client: 'error: invalid group name' for "+ groupName + "\n");
							toClient.writeBytes("error: invalid group name\n");
							toClient.close();
							fromClient.close();
							socket.close();
							System.out.println("Connection closed.");
							return;
						}
						System.out.println("To client: 'ok groupname: " + groupName + "'\n");
						toClient.writeBytes("ok groupname: " + groupName + "\n");
						if (PostOrGet.equals("get")) {
							String readDB = readDB(groupName);
							//System.out.println("To client:\n" + readDB + "\n");
							toClient.writeBytes(readDB);
							toClient.close();
							fromClient.close();
							socket.close();
							System.out.println("Connection closed.");
							return;
						}
					}
					
					line = fromClient.readLine();
					System.out.println("From client: " + line + "\n");
					String tokens2[] = line.split(" +");
					if (tokens2.length != 2 || !tokens2[0].equals("id")) {
						toClient.writeBytes("error: invalid command\n");
						toClient.close();
						fromClient.close();
						socket.close();
						System.out.println("Connection closed.");
						return;
					} else {
						for (int i = 0; i < tokens2[1].length(); i++) {
							if (Character.isWhitespace(tokens2[1].charAt(i)) || Character.isISOControl(tokens2[1].charAt(i))) {
								//System.out.println("To client: 'error: invalid user name' for failing control character test\n");
								toClient.writeBytes("error: invalid user name\n");
								toClient.close();
								fromClient.close();
								socket.close();
								System.out.println("Connection closed.");
								return;
							}
						}
						userName = tokens2[1];
						System.out.println("To client: 'ok username: " + userName + "'\n");
						toClient.writeBytes("ok username: " + userName + "\n");
					}
				
					String RSA = socket.getRemoteSocketAddress().toString();
					StringBuilder sb = new StringBuilder();
					while ((line = fromClient.readLine()) != null) {
						sb.append(line);
					}
					//System.out.println(sb.toString() + "\n");
					Message msg = new Message(userName, RSA, sb.toString());
					System.out.println("New message entry:\n\n" + msg.formattedMsg);
					updateDB(msg, groupName);
					toClient.close();
					fromClient.close();
					socket.close();
					System.out.println("Connection closed.");
					return;
				}
			} catch (IOException e) {
				System.out.println("error: failure to obtain input from client");
			}			
		}
	}
}
