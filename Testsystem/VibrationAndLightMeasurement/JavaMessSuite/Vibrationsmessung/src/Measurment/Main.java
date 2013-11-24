package Measurment;

import Maths.DataAnalysis;
import Serial.Comm.DataContainer;
import Serial.Comm.SerialComm;

public class Main {

	
	
	
	public static void main(String[] args) {
		
		int initDuration = 5;
		int testDuration = 10;
			
//		DataContainer data = SerialComm.readInitialVals(initDuration, true);			
//
//		data = SerialComm.readTestVals(data, testDuration, true);

		
		System.out.print("CHECK FIRST: Finished Tests!\n");
		
		//SerialComm.createPlot(DataAnalysis.baselineComputationVibration(data), DataAnalysis.minMaxComputationVibration(data));
		
		
		
	}
	
	public static void startTest() {
		
		System.out.print("YEAHHHHH\n");
	
	}
	


}
