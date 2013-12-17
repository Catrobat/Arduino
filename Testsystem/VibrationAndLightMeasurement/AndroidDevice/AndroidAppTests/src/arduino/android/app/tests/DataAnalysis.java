package arduino.android.app.tests;

public class DataAnalysis {

	public double min;
	public double max;
	public double mean;
	public int pseudoSum;
	public double sum;
	public int len;
	public int[] rawData;
	public double stdev;
	
	public DataAnalysis(int[] rawData) {
		
		max = -Integer.MAX_VALUE;
    	min = Integer.MAX_VALUE;
    	mean = 0;
    	len = rawData.length;
    	this.rawData = rawData;
    	
    	sum = 0;
    	pseudoSum = 0;
        
        for(int i = 0; i < rawData.length; i++) {
        	
        	sum += rawData[i];
        	
        	if(rawData[i] < min)
                min = rawData[i];
        	
            if(rawData[i] > max)
                max = rawData[i];
        }
        
        mean = sum / len;
        
        double stdTmp = 0;
        
        for(int i = 0; i < rawData.length; i++) {      	
        	if(rawData[i] > 1.25 * mean)
        		pseudoSum += rawData[i];
        	
        	stdTmp += (mean - (double)rawData[i]) * (mean - (double)rawData[i]);
        }
        
        stdev = Math.sqrt(stdTmp - (double)len);
	}
	
	
	public static DataAnalysis baselineComputationVibration(DataAnalysis initVals, DataAnalysis testVals) {
		
		int[] resData = new int[testVals.len];
		
		for(int i = 0; i < testVals.len; i++) {
			
			double dataPnt = testVals.rawData[i] - initVals.mean;
			
			if(dataPnt < 0)
				dataPnt *= -1;
			
			resData[i] = (int)dataPnt;
		}
		
		return new DataAnalysis(resData);
	}
	
	//return true if vibration is detected; false otherwise
	public static boolean evaluateVibrationData(DataAnalysis initData, DataAnalysis testData) {
		
		//this is quite hard with the currently used vibration sensor; 
		//3 different criteria are used to decide whether a vibration is detected or not
		
		int criteriaCount = 0;
		
		if(initData.stdev * 1.15 < testData.stdev)
			criteriaCount++;

		if(initData.max * 1.35 < testData.max)
			criteriaCount++;
		
		DataAnalysis baseline_Init_VibrationData = DataAnalysis.baselineComputationVibration(initData, initData);
		DataAnalysis baseline_Test_VibrationData = DataAnalysis.baselineComputationVibration(initData, testData);
		
		if(baseline_Init_VibrationData.pseudoSum * 1.35 < baseline_Test_VibrationData.pseudoSum)
			criteriaCount++;
		
		if(criteriaCount >= 2)
			return true;
		
		return false;
	}
		
		
	
//	public static int[] minMaxComputationVibration(DataContainer data) {
//		
//		DataAnalysis initVals = new DataAnalysis(data.initVibData);
//		DataAnalysis testVals = new DataAnalysis(data.vibData);
//		
//		
//		System.out.print("____________________________\nStart min-max computation:\n");
//		
//		
//		int[] resData = new int[testVals.len];
//		
//		for(int i = 0; i < testVals.len; i++) {
//			
//			if(data.vibData[i] > initVals.max || data.vibData[i] < initVals.min || data.vibData[i] == 0)
//				resData[i] = 1;
//			else
//				resData[i] = 0;
//			
//		}
//		
//		return resData;
//	}
	
	
	
}
