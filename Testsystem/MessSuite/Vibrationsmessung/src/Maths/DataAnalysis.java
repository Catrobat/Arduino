package Maths;

import Serial.Comm.DataContainer;

public class DataAnalysis {

	public double min;
	public double max;
	public double mean;
	public int len;
	
	public DataAnalysis(int[] rawData) {
		
		max = -Integer.MAX_VALUE;
    	min = Integer.MAX_VALUE;
    	mean = 0;
    	len = rawData.length;
    	
    	double sum = 0;
        
        
        for(int i = 0; i < rawData.length; i++) {
        	
        	sum += rawData[i];
        	
        	if(rawData[i] < min)
                min = rawData[i];
        	
            if(rawData[i] > max)
                max = rawData[i];
        }
        
        mean = sum / len;
	}
	
	
	public static int[] baselineComputationVibration(DataContainer data) {
		
		DataAnalysis initVals = new DataAnalysis(data.initVibData);
		DataAnalysis testVals = new DataAnalysis(data.vibData);
		
		
		System.out.print("____________________________\nStart baseline computation:\n");
		
		
		int[] resData = new int[testVals.len];
		
		for(int i = 0; i < testVals.len; i++) {
			
			double dataPnt = data.vibData[i] - initVals.mean;
			
			if(dataPnt < 0)
				dataPnt *= -1;
			
			resData[i] = (int)dataPnt;
		}
		
		return resData;
	}
	
	public static int[] minMaxComputationVibration(DataContainer data) {
		
		DataAnalysis initVals = new DataAnalysis(data.initVibData);
		DataAnalysis testVals = new DataAnalysis(data.vibData);
		
		
		System.out.print("____________________________\nStart min-max computation:\n");
		
		
		int[] resData = new int[testVals.len];
		
		for(int i = 0; i < testVals.len; i++) {
			
			if(data.vibData[i] > initVals.max || data.vibData[i] < initVals.min || data.vibData[i] == 0)
				resData[i] = 1;
			else
				resData[i] = 0;
			
		}
		
		return resData;
	}
	
	
	
}
