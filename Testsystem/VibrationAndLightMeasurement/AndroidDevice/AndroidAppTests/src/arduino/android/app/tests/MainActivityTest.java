package arduino.android.app.tests;


import android.app.Activity;


import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.test.AssertionFailedError;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.CheckBox;
import arduino.android.app.MainActivity;


import java.io.*;
import java.net.*;



public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

	private Activity mainActivity;
	
	private CheckBox lightCheckBox;
	private CheckBox vibrationCheckBox;
	
	private Button actionButton;
	
	private Socket clientSocket;
	private DataOutputStream outToServer;
	private BufferedReader inFromServer;
	
	
	private static final int LIGHT_VALUE = 2;
	
	//Ip Adress and Port, where the Arduino Server is running on
    private static final String serverIP="10.0.0.111";
    private static final int serverPort=6789;
	
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
		
		
		//=========================================================================
		//								CLIENT
		//=========================================================================
		
		String msgToServer;//Message that will be sent to Arduino
        String msgFromServer;//recieved message will be stored here

        clientSocket = new Socket(serverIP, serverPort);//making the socket connection
        System.out.println("Connected to:"+serverIP+" on port:"+serverPort);//debug
        //OutputStream to Arduino-Server
        outToServer = new DataOutputStream(clientSocket.getOutputStream());
        //BufferedReader from Arduino-Server
        inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));//

		
		//=========================================================================
		//=========================================================================
		
		
		
		
		
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
	private void _test_PreConditions() {
		assertNotNull(lightCheckBox);
		assertNotNull(vibrationCheckBox);
		assertNotNull(actionButton);
		
		assertFalse(lightCheckBox.isChecked());
		assertFalse(vibrationCheckBox.isChecked());
		
		//wait 2s
		try {
			Thread.sleep(7500);
			peep();
			Thread.sleep(200);
			peep();
		} catch (InterruptedException e) {}
		
		
		//============================================================
		//now start initial Light check on hardware:
		//============================================================
		
		//Initially light should be off:
		checkLightSensorValue(false);
		
		
	}
	
	
	
	private void checkLightSensorValue(boolean expectedLightValue) {
		
		char expectedValueChar;
		String assertString;
		
		if(expectedLightValue) {
			expectedValueChar = '1';
			assertString = "Error: Light is turned off!";
		} else {
			expectedValueChar = '0';
			assertString = "Error: Light is turned on!";
		}
		
		try {
			outToServer.writeBytes(Integer.toHexString(LIGHT_VALUE));
			String msgFromServer = inFromServer.readLine();				//recieving the answer
			
			assertFalse("Wrong Command!", msgFromServer.contains("ERROR"));
			assertTrue("Wrong data received!", msgFromServer.contains("LIGHT_END"));
			assertTrue(assertString, msgFromServer.charAt(0) == expectedValueChar);
			
			
		} catch (IOException e1) {
			throw new AssertionFailedError("Dataexchanged failed! Check Server-Client-Connection");
		}
		
	}
	
	public void test_SimpleOnOffTest() {
		
		//first check initial state:
		_test_PreConditions();
		
		//now let's start the test
		
		try {
			peep();
			//check light checkbox
			TouchUtils.clickView(this, lightCheckBox);
			assertTrue("Turning on the light failed", lightCheckBox.isChecked());
			//turn on light
			TouchUtils.clickView(this, actionButton);
			
			Thread.sleep(200);		//give Android some time to apply now configuration
		} catch (InterruptedException e) {}
		
		
		//============================================================
		//now start Light check on hardware:
		//============================================================
		
		//The light should be on:
		checkLightSensorValue(true);

		//wait 4s
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {}
		
		//The light should still be on:
		checkLightSensorValue(true);
		
		//turn off light
		TouchUtils.clickView(this, actionButton);
		try {
			Thread.sleep(200); //give Android some time to apply now configuration
		} catch (InterruptedException e1) {}		
		
		//The light should now be off:
		checkLightSensorValue(false);
		
		
		//uncheck light checkbox
		TouchUtils.clickView(this, lightCheckBox);
		assertFalse(lightCheckBox.isChecked());
		
		//wait 2s
				try {
					Thread.sleep(200);
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
	
	@Override protected void tearDown() throws Exception {
		
		clientSocket.close();//close the socket
        //don't do this if you want to keep the connection
		
	}
	
}
