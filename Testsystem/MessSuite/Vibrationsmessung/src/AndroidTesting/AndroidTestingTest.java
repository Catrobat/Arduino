package AndroidTesting;

import junit.framework.TestCase;

public class AndroidTestingTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testMain() {

		TestWithAndroid test = new TestWithAndroid();
		
		boolean result = test.main();
		
		assertTrue("It Works", result);
		
		//assertTrue("and now it don't Works", !result);
		
	}
	
	public void testMain2() {

		TestWithAndroid test = new TestWithAndroid();
		
		boolean result = test.main();
		
		assertTrue("and now it don't Works", result);
		
	}

}
