package arduino.ethernet.client;

import java.io.*;
import java.net.*;

public class SimpleClient {

	//Ip Adress and Port, where the Arduino Server is running on
    private static final String serverIP="10.0.0.111";
    private static final int serverPort=23;
 
     public static void main(String argv[]) throws Exception
     {
          String msgToServer;//Message that will be sent to Arduino
          String msgFromServer;//recieved message will be stored here
 
          Socket clientSocket = new Socket(serverIP, serverPort);//making the socket connection
          System.out.println("Connected to:"+serverIP+" on port:"+serverPort);//debug
          //OutputStream to Arduino-Server
          DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
          //BufferedReader from Arduino-Server
          BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//
 
          
          for(int i = 0; i < 3; i++) {
        	  
        	  if(i==2)
        		  outToServer.writeBytes(Integer.toHexString(3));//sending the message
        	  else
        		  outToServer.writeBytes(Integer.toHexString(1));//sending the message
        	  
              System.out.println("sending to Arduino-Server: " + Integer.toHexString(1));//debug
              
        	  msgFromServer = inFromServer.readLine();//recieving the answer
        	  if(msgFromServer != null)
        		  System.out.println("recieved from Arduino-Server: " + msgFromServer);//print the answer
        	  
        	  Thread.sleep(1000);
        	  
          }
 
          clientSocket.close();//close the socket
          //don't do this if you want to keep the connection
     }
     
}
