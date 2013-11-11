package arduino.android.app.tests;


import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.CheckBox;
import arduino.android.app.MainActivity;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private Activity mainActivity;
	
	private CheckBox lightCheckBox;
	private CheckBox vibrationCheckBox;
	
	private Button actionButton;
	
	//##########################################################################
	
	public MainActivityTest() {
		super(MainActivity.class);
	}

	//##########################################################################
	
	/**
	 * Method is invoked before every test, used to initialize variables and
	 * clean up from previous tests (see also tearDown())
	 */
	@Override protected void setUp() throws Exception {
		
		
		
		// Is required by JUnit
		super.setUp();
		
//		Measurment.Main.startTest();
		
		// Turns off touch mode, if you send key events to the application
		// you have to turn off touch mode before you start any activities
		setActivityInitialTouchMode(false);
		
		mainActivity = getActivity(); // This call also starts the activity if it is not already running
		
		lightCheckBox = (CheckBox) mainActivity.findViewById(arduino.android.app.R.id.cb_light);
		vibrationCheckBox = (CheckBox) mainActivity.findViewById(arduino.android.app.R.id.cb_vibration);
		
		actionButton = (Button) mainActivity.findViewById(arduino.android.app.R.id.go);
	}
	
	//##########################################################################
	
	/**
	 * Verifies if the application under test is initialized correctly
	 */
	public void testPreConditions() {
		assertNotNull(lightCheckBox);
		assertNotNull(vibrationCheckBox);
		assertNotNull(actionButton);
		
		assertFalse(lightCheckBox.isChecked());
		assertFalse(vibrationCheckBox.isChecked());
	}
	
	//##########################################################################
	
	public void testLight() {
		
		//check light checkbox
		TouchUtils.clickView(this, lightCheckBox);
		assertTrue(lightCheckBox.isChecked());
		
		//turn on light
		TouchUtils.clickView(this, actionButton);
		
		//wait 2s
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			
		}
		
		//turn off light
		TouchUtils.clickView(this, actionButton);
		
		//uncheck light checkbox
		TouchUtils.clickView(this, lightCheckBox);
		assertFalse(lightCheckBox.isChecked());
		
	}
	
	public void testVibrate() {
		
		//check vibration checkbox
		TouchUtils.clickView(this, vibrationCheckBox);
		assertTrue(vibrationCheckBox.isChecked());
		
		//activate vibration
		TouchUtils.clickView(this, actionButton);
		
		//wait 2s
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			
		}
		
		//uncheck vibration checkbox
		TouchUtils.clickView(this, vibrationCheckBox);
		assertFalse(vibrationCheckBox.isChecked());
		
	}
	
	public void testLightAndVibrate() {
		
		
		//check light and vibration checkbox
		TouchUtils.clickView(this, lightCheckBox);
		TouchUtils.clickView(this, vibrationCheckBox);
		
		//start light and vibration
		TouchUtils.clickView(this, actionButton);
		
		//wait 1s
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
		}
		
		//uncheck light and vibration checkbox
		TouchUtils.clickView(this, lightCheckBox);
		TouchUtils.clickView(this, vibrationCheckBox);
		
	}
	
}
