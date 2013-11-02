package Serial.Comm;

import gnu.io.PortInUseException;

public class DataExchangeThread extends Thread{

	private SerialComm serialComm;
	private boolean drawDiagram;
	private boolean initMode;
	
	public DataContainer dataContainer;
	
	private int durationInSecs;
	
	public DataExchangeThread(int durationInSecs, boolean initMode, boolean drawDiagram) {
		
		super();
		this.durationInSecs = durationInSecs;
		this.drawDiagram = drawDiagram;
		this.initMode = initMode;
				
		this.dataContainer = null;
				
		serialComm = new SerialComm();
		
	}
	
	public DataExchangeThread(DataContainer initData, int durationInSecs, boolean initMode, boolean drawDiagram) {
		
		super();
		this.durationInSecs = durationInSecs;
		this.drawDiagram = drawDiagram;
		this.initMode = initMode;
		
		this.dataContainer = initData;
		
		serialComm = new SerialComm();
		
	}
	
	public void run() {
		
		//the following line will keep this app alive for 1000 seconds,
		//waiting for events to occur and responding to them (printing incoming messages to console).
		try {
			//init phase
			System.out.println("Program started");

			try {
				serialComm.findPort();
			} catch (PortInUseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			serialComm.initializePort();
			
			Thread.sleep(1500);
			
			//read data
			serialComm.startRecord = true;
			serialComm.writeToDev("#");
			Thread.sleep(durationInSecs * 1000);
			serialComm.startRecord = false;
			serialComm.writeToDev("!");
			
			//stop reading data and just wait
			Thread.sleep(1000);
			System.out.println("Thread Stop");
			serialComm.close();
			
			//collect Data
			if(initMode)
				dataContainer = serialComm.collectInitVals(drawDiagram);
			else
				dataContainer = serialComm.collectTestVals(dataContainer, drawDiagram);

			
		} catch (InterruptedException ie) {}
		
	}
	
	
}
