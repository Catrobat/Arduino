package AndroidTesting;

import Serial.Comm.DataContainer;
import Serial.Comm.SerialComm;
import junit.framework.TestCase;

public class AndroidTestingTest extends TestCase {

	private DataContainer testData = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		testData = null;
	}

	public void test_0_ReadInitVals() {

		testData = SerialComm.readInitialVals(5, true);	
		
		assertTrue("Data is null!\n", testData != null);
		
		
	}
	
	public void test_1_ReadLightVals() {

		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		testData = SerialComm.readTestVals(testData, 10, true);	
		
		assertTrue("Data is null!\n", testData != null);
		
	}

}
