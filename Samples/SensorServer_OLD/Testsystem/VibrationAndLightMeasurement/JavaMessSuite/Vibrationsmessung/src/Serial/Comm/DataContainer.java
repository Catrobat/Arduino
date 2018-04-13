package Serial.Comm;

public class DataContainer {

	public int[] initVibData;
	public int[] vibData;
	
	public int[] initLightData;
	public int[] lightData;
	
	public DataContainer(int[] initVibData, int[] initLightData, int[] vibData, int[] lightData) {
		
		this.initVibData = initVibData;
		this.vibData = vibData;
		
		this.initLightData = initLightData;
		this.lightData = lightData;
	}
}
