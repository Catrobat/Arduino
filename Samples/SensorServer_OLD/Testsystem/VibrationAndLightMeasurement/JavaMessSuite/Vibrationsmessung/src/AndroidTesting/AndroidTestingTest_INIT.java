package AndroidTesting;

import junit.framework.TestCase;
import Serial.Comm.SerialComm;

public class AndroidTestingTest_INIT extends TestCase {
	
	private boolean testState;
	
	protected void setUp() throws Exception {
		super.setUp();
		testState = false;
	}
	

	public void test_ReadInitVals() {

		testState = SerialComm.readInitialVals(5, true);	
		
		assertTrue("==> ERROR in test_ReadInitVals\n", testState);
		
		testState = SerialComm.readTestVals(null, 10, true);	
		
		assertTrue("==> ERROR in test_ReadInitVals\n", testState);
		
		
	}
	
	

}