package AndroidTesting;

import Serial.Comm.DataContainer;
import Serial.Comm.SerialComm;
import junit.framework.TestCase;

public class AndroidTestingTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testReadInitVals() {

		DataContainer data = SerialComm.readInitialVals(5, true);	
		
		assertTrue("Data is null!\n", data != null);
		
		
	}
	
	public void testMain2() {

		TestWithAndroid test = new TestWithAndroid();
		
		boolean result = test.main();
		
		assertTrue("and now it don't Works", result);
		
	}

}
