package AndroidTesting;

import Serial.Comm.DataContainer;
import Serial.Comm.SerialComm;
import junit.framework.TestCase;

public class AndroidTestingTest extends TestCase {
	
	private boolean testState;
	
	protected void setUp() throws Exception {
		super.setUp();
		testState = false;
	}
	
	public void test_ReadLightVals() {

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		testState = SerialComm.readTestVals(null, 10, true);	
		
		assertTrue("==> ERROR in test_ReadLightVals\n", testState);
		
	}

}
