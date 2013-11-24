package arduino.android.app.tests;


import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
	public void test_0_PreConditions() {
		assertNotNull(lightCheckBox);
		assertNotNull(vibrationCheckBox);
		assertNotNull(actionButton);
		
		assertFalse(lightCheckBox.isChecked());
		assertFalse(vibrationCheckBox.isChecked());
		
		//sync java and android
		try {
			Thread.sleep(7500);
			peep();
			Thread.sleep(200);
			peep();
		} catch (InterruptedException e) {}
		
		
		//============================================================
		//now start initial Light check:
		//============================================================
		
		try {
			Thread.sleep(2000 + 2000); //2000ms wait between tests and 2000ms to set up serial port on host
			peep();
			//check light checkbox
			TouchUtils.clickView(this, lightCheckBox);
			assertTrue(lightCheckBox.isChecked());
			Thread.sleep(3000);
		} catch (InterruptedException e) {}
		
		
		//turn on light
		TouchUtils.clickView(this, actionButton);
		
		//wait 2s
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			
		}
		
		//turn off light
		TouchUtils.clickView(this, actionButton);
		
		//uncheck light checkbox
		TouchUtils.clickView(this, lightCheckBox);
		assertFalse(lightCheckBox.isChecked());
		
		//wait 2s
				try {
					Thread.sleep(5000);
					peep();
					Thread.sleep(200);
					peep();
				} catch (InterruptedException e) {}
				
	}
	
	//##########################################################################
	
	public void _test_1_Light() {
		
		//check light checkbox
		TouchUtils.clickView(this, lightCheckBox);
		assertTrue(lightCheckBox.isChecked());
		
		try {
			Thread.sleep(2000 + 2000); //2000ms wait between tests and 2000ms to set up serial port on host
			peep();
			Thread.sleep(3000);
		} catch (InterruptedException e) {}
		
		
		//turn on light
		TouchUtils.clickView(this, actionButton);
		
		//wait 2s
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			
		}
		
		//turn off light
		TouchUtils.clickView(this, actionButton);
		
		//uncheck light checkbox
		TouchUtils.clickView(this, lightCheckBox);
		assertFalse(lightCheckBox.isChecked());
		
		//wait 2s
				try {
					Thread.sleep(5000);
					peep();
					Thread.sleep(200);
					peep();
				} catch (InterruptedException e) {}
		
	}
	
	public void _test_2_Vibrate() {
		
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
	
	public void _test_3_LightAndVibrate() {
		
		
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
	
	private void peep() {
		
		try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {}
	}
	
}
