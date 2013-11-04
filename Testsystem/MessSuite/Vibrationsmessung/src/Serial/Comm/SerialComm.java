package Serial.Comm;

import gnu.io.*;
import Maths.GraphicTool;

import com.csvreader.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.URI;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

public class SerialComm implements SerialPortEventListener {
	
	public SerialPort serialPort;
	public CsvWriter csvLight, csvVibration;
	
	private List<Integer> lDataList;
	private List<Integer> vDataList;

	/**
	* A BufferedReader which will be fed by a InputStreamReader 
	* converting the bytes into characters 
	* making the displayed results codepage independent
	*/
	private BufferedReader input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 2000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 9600;

	public boolean startRecord = false;
	String dirPath;
	
	public SerialComm() {
		
		lDataList = new LinkedList();
		vDataList = new LinkedList();
		
		long theTime = System.currentTimeMillis();
		dirPath = "data\\" + Long.toString(theTime);
		
		File dir = new File(dirPath);
		dir.mkdir();
		
		csvLight 		= new CsvWriter(dirPath + "\\lightData.csv");
		csvVibration	= new CsvWriter(dirPath + "\\vibrationData.csv");
	
		
	}
	
	public static DataContainer readInitialVals(int durInSecs, boolean drawDiagram) {
		
		DataExchangeThread t = new DataExchangeThread(durInSecs, true, drawDiagram);
		t.start();
		
		try {
			Thread.sleep((durInSecs + 4) * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return t.dataContainer;
	}
	
	public static DataContainer readTestVals(DataContainer initData, int durInSecs, boolean drawDiagram) {
		
		DataExchangeThread t = new DataExchangeThread(initData, durInSecs, false, drawDiagram);
		t.start();
		
		try {
			Thread.sleep((durInSecs + 4) * 1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return t.dataContainer;
	}
	

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Thread t=new Thread() {
			public void run() {
				//the following line will keep this app alive for 1000 seconds,
				//waiting for events to occur and responding to them (printing incoming messages to console).
				try {
					//init phase
					System.out.println("Program started");
					
					SerialComm comm = new SerialComm();
					
					try {
						comm.findPort();
					} catch (PortInUseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					comm.initializePort();
					
					Thread.sleep(1500);
					
					//read data
					comm.startRecord = true;
					comm.writeToDev("#");
					Thread.sleep(14000);
					comm.startRecord = false;
					comm.writeToDev("!");
					
					//stop reading data and just wait
					Thread.sleep(1000);
					System.out.println("Thread Stop");
					comm.close();
					
					//create plot
					comm.createPlot();
					
					
					
				} catch (InterruptedException ie) {}
			}
		};
		t.start();

		System.out.println("Finished successfully");
	}
	
	public void writeToDev(String msg) {
		
		System.out.println(msg);
		byte[] bytesToDev = msg.getBytes();
		try {
			output.write(bytesToDev);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void initializePort() {

		if (serialPort == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			
		
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	
	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
		
		csvLight.close();
		csvVibration.close();
		copyFiles();
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				String inputLine=input.readLine();
				
				if(inputLine.length() == 0)
					return;
				
				if(startRecord == true) {

					String[] parts = inputLine.split(" ");
					
					long ts = System.currentTimeMillis();
					try {
						vDataList.add(Integer.parseInt(parts[1]));
						lDataList.add(Integer.parseInt(parts[5]));
						csvLight.writeRecord(new String[]{Long.toString(ts), parts[5]});
						csvVibration.writeRecord(new String[]{Long.toString(ts), parts[1]});
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				System.out.println(inputLine);
			} catch (Exception e) {
				System.err.println(e.toString());
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
	
	public void findPort() throws PortInUseException {
		//System.out.println(java.library.path);
	    CommPortIdentifier serialPortId;
	    //static CommPortIdentifier sSerialPortId;
	    Enumeration enumComm;
	    //SerialPort serialPort;


	    enumComm = CommPortIdentifier.getPortIdentifiers();
	    while (enumComm.hasMoreElements()) {
	     	serialPortId = (CommPortIdentifier) enumComm.nextElement();
	     	if(serialPortId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
	    		System.out.println("Found Available Port at: " + serialPortId.getName());
	    		serialPort = (SerialPort)serialPortId.open(this.getClass().getName(), TIME_OUT);
	    	}
	    }
	}
	
	public void copyFiles() {
		
		Path vibPathSrc = FileSystems.getDefault().getPath(dirPath, "vibrationData.csv");
		Path vibPathDst = FileSystems.getDefault().getPath("data\\cur", "vibrationData.csv");
		
		Path lightPathSrc = FileSystems.getDefault().getPath(dirPath, "lightData.csv");
		Path lightPathDst = FileSystems.getDefault().getPath("data\\cur", "lightData.csv");
		
		try {
			Files.copy(vibPathSrc, 	 vibPathDst,   StandardCopyOption.REPLACE_EXISTING);
			Files.copy(lightPathSrc, lightPathDst, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public DataContainer collectInitVals(boolean drawDiagram) {
		
		int[] lArr = new int[lDataList.size()];
		for(int i = 0; i < lDataList.size(); i++)
			lArr[i] = lDataList.get(i);
		
		int[] vArr = new int[vDataList.size()];
		for(int i = 0; i < vDataList.size(); i++)
			vArr[i] = vDataList.get(i);
		
		lDataList.clear();
		vDataList.clear();
		
		if(drawDiagram == true) {
			JFrame f = new JFrame();
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        f.add(new GraphicTool(lArr,vArr));
	        f.setSize(800,650);
	        f.setLocation(50,50);
	        f.setVisible(true);
		}
        
        return new DataContainer(vArr, lArr, null, null);
	}
	
	public DataContainer collectTestVals(DataContainer initData, boolean drawDiagram) {
		
		int[] lArr = new int[lDataList.size()];
		for(int i = 0; i < lDataList.size(); i++)
			lArr[i] = lDataList.get(i);
		
		int[] vArr = new int[vDataList.size()];
		for(int i = 0; i < vDataList.size(); i++)
			vArr[i] = vDataList.get(i);
			
		if(drawDiagram == true) {
			JFrame f = new JFrame();
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        f.add(new GraphicTool(lArr,vArr));
	        f.setSize(800,650);
	        f.setLocation(50,50);
	        f.setVisible(true);
		}
        
		initData.lightData = lArr;
		initData.vibData = vArr;
		
        return initData;
	}

	
	public void createPlot() {
		
		int[] lArr = new int[lDataList.size()];
		for(int i = 0; i < lDataList.size(); i++)
			lArr[i] = lDataList.get(i);
		
		int[] vArr = new int[vDataList.size()];
		for(int i = 0; i < vDataList.size(); i++)
			vArr[i] = vDataList.get(i);
		
		createPlot(lArr, vArr);
        
	}
	
	public static void createPlot(int[] lArr, int[] vArr) {
		
		JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new GraphicTool(lArr,vArr));
        f.setSize(800,650);
        f.setLocation(50,50);
        f.setVisible(true);
        
	}

	
}
