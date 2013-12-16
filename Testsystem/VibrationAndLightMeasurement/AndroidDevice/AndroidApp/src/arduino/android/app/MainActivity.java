package arduino.android.app;

import java.util.ArrayList;
import java.util.List;

import arduino.android.app.R;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
//import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	public final static int VIBRATION_DURATION = 120000; //let the phone maximal vibrate for 2 Minutes
	
	private boolean lightOn = false;
	private boolean vibrationOn = false;
	
	private Camera camera = null;
	private Vibrator vibrator = null;
	
	//##########################################################################
	
	/**
	 * Called when the activity is first created. This is where you should do 
	 * all of your normal static set up — create views, bind data to lists, 
	 * and so on. This method is passed a Bundle object containing the 
	 * activity's previous state, if that state was captured
	 */
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Check if device has flash support
		boolean hasCamera = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
		boolean hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		
		if(!hasFlash && !hasCamera) {
			CheckBox lightCheckbox = (CheckBox) findViewById(R.id.cb_light);
			lightCheckbox.setClickable(false);
			
			TextView textView = (TextView) findViewById(R.id.light_status);
			textView.setText("no light support");
		}
		//
		
		// Check if device has vibrator
		turnLightOff();
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		
	}
	
	
	public void changeToDebugMode(View view) {
		
		Intent myIntent = new Intent(view.getContext(), ManualDebug.class);
        startActivityForResult(myIntent, 0);
        
        peep();
       try {
		Thread.sleep(200);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
        peep();

	}
	
	/**
	 * Called just before the activity starts interacting with the user. 
	 * At this point the activity is at the top of the activity stack, 
	 * with user input going to it.
	 */
	@Override protected void onResume() {
		super.onResume();
		
//		Toast.makeText(this, "resume", Toast.LENGTH_SHORT).show();
		
		if(lightOn) {
			turnLightOn();
		}
	}
	
	
	/**
	 * Called when the system is about to start resuming another activity.
	 * This method is typically used to commit unsaved changes to persistent
	 * data, stop animations and other things that may be consuming CPU,
	 * and so on. It should do whatever it does very quickly, because the next
	 * activity will not be resumed until it returns.
	 */
	@Override protected void onPause() {
		super.onPause();
		
//		Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show();
		
		if(lightOn) {
			turnLightOff();
		}
	}
	
	/**
	 * Called before the activity is destroyed. This is the final call that
	 * the activity will receive. It could be called either because the activity
	 * is finishing (someone called finish() on it), or because the system is
	 * temporarily destroying this instance of the activity to save space. 
	 */
	@Override protected void onDestroy() {
		super.onDestroy();
		
//		Toast.makeText(this, "destroy", Toast.LENGTH_SHORT).show();
		
		if(lightOn) {
			turnLightOff();
		}
	}
	
	//##########################################################################
	//##########################################################################
	//##########################################################################
	
	/**
	 * called when the user clicks the send button
	 * @param view View that was clicked, hence the send button
	 */
	public void sendMessage(View view) {
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		
		EditText editText = (EditText)findViewById(R.id.edit_message);
		String message = editText.getText().toString();
		
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
	
	//##########################################################################
	//##########################################################################
	//##########################################################################

	/**
	 * 
	 */
	public void performAction(View view) {
		CheckBox lightCheckbox = (CheckBox) findViewById(R.id.cb_light);
		CheckBox vibrationCheckbox = (CheckBox) findViewById(R.id.cb_vibration);
		
		List<Thread> threads = new ArrayList<Thread>();
		
		if(lightCheckbox.isChecked()) {
			
//			toggleLightUI();
			
			Thread lightThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					final int statusTextId = toggleLight();
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							TextView textView = (TextView) findViewById(R.id.light_status);
							textView.setText(statusTextId);
						}
					});
				}
			});
			
			threads.add(lightThread);
			
		}
		
		if(vibrationCheckbox.isChecked()) {
			
//			vibrate(VIBRATION_DURATION);
			
			Thread vibrationThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					if(!vibrationOn) {
						startVibrate();
						vibrationOn = true;
					} else {
						stopVibrate();
						vibrationOn = false;
					}
				}
			});
			
			threads.add(vibrationThread);
		} 
		
		for(Thread thread : threads) {
			thread.start();
		}
		
	}
	
	/**
	 * 
	 */
	private void toggleLightUI() {
		TextView textView = (TextView) findViewById(R.id.light_status);
		
		if(lightOn) {
			textView.setText(R.string.light_off);
			turnLightOff();
		}
		else {
			textView.setText(R.string.light_on);
			turnLightOn();
		}
		
		lightOn = !lightOn;
	}
	
	/**
	 * 
	 */
	private synchronized int toggleLight() {
		
		int statusTextId;
		
		if(lightOn) {
			statusTextId = R.string.light_off;
			turnLightOff();
		}
		else {
			statusTextId = R.string.light_on;
			turnLightOn();
		}
		
		lightOn = !lightOn;
		
		return statusTextId;
	}
	
	/**
	 * 
	 * @param duration
	 */
	private synchronized void startVibrate() {
		vibrator.vibrate(VIBRATION_DURATION);
	}
	
	
	private synchronized void stopVibrate() {
		vibrator.cancel();
	}
	
	
	//##########################################################################
	
	private void turnLightOn() {
		
		camera = Camera.open();
		
		if(camera != null) {
			Parameters params = camera.getParameters();
			params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			
			try {
				camera.setParameters(params);
				camera.startPreview();
			} catch (Exception e) {
				Toast.makeText(this, "turn light on exception", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "no camera", Toast.LENGTH_SHORT).show();
		}
	}
	
	//##########################################################################
	
	private void turnLightOff() {
		if(camera != null) {
			camera.stopPreview();
			camera.release();
			camera = null;
		} else {
			Toast.makeText(this, "no camera", Toast.LENGTH_SHORT).show();
		}
	}
	
	//##########################################################################
	
	private void peep() {
		
		try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {}
	}
}
