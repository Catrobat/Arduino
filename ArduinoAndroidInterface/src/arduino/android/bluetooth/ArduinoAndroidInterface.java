package arduino.android.bluetooth;

public class ArduinoAndroidInterface {
	
	public static final int ERROR_OK = 0;
	
	
	public static int initBluetoothConnection() {
		
		System.err.println("TODO: Implement this method!");
		
		/**
		 * Es gilt zu klären, wann wir das machen und ob wir was aus 
		 * https://github.com/Catrobat/Catroid/tree/master/catroid/src/org/catrobat/catroid/bluetooth
		 * verwenden könnnen.
		 */
		
		
		return ERROR_OK;
	}
	
	/* 
	 * Sollen wir mit Fehlercodes oder mit Exceptions arbeiten?
	 */
	public static int setDigitalPin(int pinNumber, boolean value) {
		
		System.err.println("TODO: Implement this method!");
		
		return ERROR_OK;
	}
	
	
	/* 
	 * Sollen wir mit Fehlercodes oder mit Exceptions arbeiten?
	 */
	public static int readDigitalPin(int pinNumber) {
		
		System.err.println("TODO: Implement this method!");
		
		return 1;
	}
	
	/* 
	 * Sollen wir mit Fehlercodes oder mit Exceptions arbeiten?
	 */
	public static int setAnalogPin(int pinNumber, int value) {
		
		System.err.println("TODO: Implement this method!");
		
		return ERROR_OK;
	}
	
	/* 
	 * Sollen wir mit Fehlercodes oder mit Exceptions arbeiten?
	 */
	public static int readAnalogPin(int pinNumber) {
		
		System.err.println("TODO: Implement this method!");
		
		return 123;
	}
	

}
