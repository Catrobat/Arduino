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

	private Activity mainActivity = null;
	
	private CheckBox lightCheckBox;
	private CheckBox vibrationCheckBox;
	
	private Button actionButton;
	
	private Socket clientSocket;
	private DataOutputStream outToServer;
	private BufferedReader inFromServer;
	
	private static final int VIBRATION_VALUE = 1;
	private static final int LIGHT_VALUE = 2;
	
	private static final int HEX_RADIX = 16;
	
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
		
		if(mainActivity == null)
			mainActivity = getActivity(); // This call also starts the activity if it is not already running
		
		lightCheckBox = (CheckBox) mainActivity.findViewById(arduino.android.app.R.id.cb_light);
		vibrationCheckBox = (CheckBox) mainActivity.findViewById(arduino.android.app.R.id.cb_vibration);
		
		actionButton = (Button) mainActivity.findViewById(arduino.android.app.R.id.go);
	}
	
	//##########################################################################
	
	
@Override protected void tearDown() throws Exception {

		clientSocket.close();//close the socket
        //don't do this if you want to keep the connection
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		
	}



//=========================================================================
//							LIGHT TESTS
//=========================================================================


	public void test_Light_SimpleOnOffTest() {
		
		//first check initial state:
		_test_PreConditions();
		
		//now let's start the test
		
		try {
			peep();
			//check light checkbox
			TouchUtils.clickView(this, lightCheckBox);
			assertTrue("Enabling light checkbox failed", lightCheckBox.isChecked());
			//turn on light
			TouchUtils.clickView(this, actionButton);
			
			Thread.sleep(200);		//give Android some time to apply now configuration
		} catch (InterruptedException e) {}
		
		
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
		
		try {
			Thread.sleep(200);
			peep();
			Thread.sleep(200);
			peep();
		} catch (InterruptedException e) {}
				
	}

//=========================================================================
	
	public void test_Light_AdvancedOnOffTest() {
		
		//first check initial state:
		_test_PreConditions();
		
		int numberOfIterations = 5;
		
		//now let's start the test
		peep();
		//Light test: enable light checkbox 
		TouchUtils.clickView(this, lightCheckBox);
		assertTrue("Enabling light checkbox failed", lightCheckBox.isChecked());
		
		for(int iteration = 0; iteration < numberOfIterations; iteration++) {
			
			//The light should (still) be off:
			checkLightSensorValue(false);
			
			//wait 1s
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			
			//now turn it on:
			TouchUtils.clickView(this, actionButton);
			
			//The light should be on:
			checkLightSensorValue(true);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			
			//The light should still be on:
			checkLightSensorValue(true);
			
			
			//now turn it off again:
			TouchUtils.clickView(this, actionButton);
			
			//The light should be off:
			checkLightSensorValue(false);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			
			
		}
				
		//FINISHED test: uncheck light checkbox
		TouchUtils.clickView(this, lightCheckBox);
		assertFalse(lightCheckBox.isChecked());

		try {
			Thread.sleep(200);
			peep();
			Thread.sleep(200);
			peep();
		} catch (InterruptedException e) {}
				
	}
	
	
//=========================================================================
//								VIBRATION TESTS
//=========================================================================


	
public void _test_Vibration_SimpleOnOffTest() {
		
		//first check initial state:
		_test_PreConditions();
		DataAnalysis initialVibrationData = getVibrationSensorValues();
		
		//now let's start the test
		peep();

		//check vibration checkbox
		TouchUtils.clickView(this, vibrationCheckBox);
		assertTrue(vibrationCheckBox.isChecked());
		
		//activate vibration
		TouchUtils.clickView(this, actionButton);
		
		//wait 1s: Arduino MUST read the new sensor vals! 
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		//now get the sensor values
		DataAnalysis test_ON_VibrationData = getVibrationSensorValues();
		assertTrue("Vibration pattern not detected!", DataAnalysis.evaluateVibrationData(initialVibrationData, test_ON_VibrationData));
		
		
		
		
		//wait 2s: now stop vibration
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {}
		
		
		//stop vibration
		TouchUtils.clickView(this, actionButton);
		//uncheck vibration checkbox
		TouchUtils.clickView(this, vibrationCheckBox);
		assertFalse(vibrationCheckBox.isChecked());
		
		//wait 1s: Arduino MUST read the new sensor vals! 
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		//now get the sensor values again
		DataAnalysis test_OFF_VibrationData = getVibrationSensorValues();
		assertFalse("Error: Vibration pattern detected!", DataAnalysis.evaluateVibrationData(initialVibrationData, test_OFF_VibrationData));
		
		try {
			Thread.sleep(200);
			peep();
			Thread.sleep(200);
			peep();
		} catch (InterruptedException e) {}
	}	
	
	//##########################################################################
	
	private DataAnalysis getVibrationSensorValues() {
		
		DataAnalysis dataSet = null;
		
		try {
			outToServer.writeBytes(Integer.toHexString(VIBRATION_VALUE));
			String msgFromServer = inFromServer.readLine();				//Receiving the answer
			
			//String looks like: AA;ED;ED;1E;...;VIBRATION_END
			
			assertTrue("Wrong data received!", msgFromServer.contains("VIBRATION_END"));
			msgFromServer = msgFromServer.replaceAll("VIBRATION_END", "");
			
			String[] dataStringArray = msgFromServer.split(";");
			int[] dataIntArray = new int[dataStringArray.length];
			
			for(int i = 0; i < dataStringArray.length; i++)
				dataIntArray[i] = Integer.parseInt(dataStringArray[i], HEX_RADIX);
			
			dataSet = new DataAnalysis(dataIntArray);
			
			System.out.println(msgFromServer);
			
		} catch (IOException e1) {
			throw new AssertionFailedError("Dataexchanged failed! Check Server-Client-Connection");
		}
		
		return dataSet;
		
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
			String msgFromServer = inFromServer.readLine();				//Receiving the answer
			
			assertFalse("Wrong Command!", msgFromServer.contains("ERROR"));
			assertTrue("Wrong data received!", msgFromServer.contains("LIGHT_END"));
			assertTrue(assertString, msgFromServer.charAt(0) == expectedValueChar);
			
			
		} catch (IOException e1) {
			throw new AssertionFailedError("Dataexchange failed! Check Server-Client-Connection");
		}
		
	}
	
	/**
	 * Verifies if the application under test is initialized correctly
	 */
	private void _test_PreConditions() {
		assertNotNull(lightCheckBox);
		assertNotNull(vibrationCheckBox);
		assertNotNull(actionButton);
		
		assertFalse(lightCheckBox.isChecked());
		assertFalse(vibrationCheckBox.isChecked());
		
		//wait 3s
		try {
			Thread.sleep(3000); // give arduino enough time to fill the vibration data array
		} catch (InterruptedException e) {}
		
		
		//============================================================
		//now start initial Light check on hardware:
		//============================================================
		
		//Initially light should be off:
		checkLightSensorValue(false);

	}
	
	private void peep() {
		
		try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {}
	}

}
