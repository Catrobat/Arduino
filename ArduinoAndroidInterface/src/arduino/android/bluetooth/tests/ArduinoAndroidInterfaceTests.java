package arduino.android.bluetooth.tests;

import arduino.android.bluetooth.ArduinoAndroidInterface;
import junit.framework.TestCase;

public class ArduinoAndroidInterfaceTests extends TestCase {

	protected void setUp() throws Exception {
		
		//...
		
		super.setUp();
	}
	
	public void test_EXAMPLE() {
		
		
		int err = ArduinoAndroidInterface.initBluetoothConnection();
		
		assertEquals(ArduinoAndroidInterface.ERROR_OK, err);
		
		//=============================
		
		err = ArduinoAndroidInterface.setDigitalPin(0, true);
		
		assertEquals(ArduinoAndroidInterface.ERROR_OK, err);
		
		//=============================
		
		err = ArduinoAndroidInterface.setAnalogPin(0, 123);
		
		assertEquals(ArduinoAndroidInterface.ERROR_OK, err);
		
		//=============================
		
		int val = ArduinoAndroidInterface.readDigitalPin(0);
		
		assertEquals(1, val);
		
		//=============================
		
		val = ArduinoAndroidInterface.readAnalogPin(0);
		
		assertEquals(123, val);
	}
	
	
}
