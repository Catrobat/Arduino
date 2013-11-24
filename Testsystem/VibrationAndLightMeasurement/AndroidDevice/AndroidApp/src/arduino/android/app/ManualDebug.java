package arduino.android.app;

import arduino.android.app.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class ManualDebug extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual_debug);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manual_debug, menu);
		return true;
	}
	
	public void backToMainAct(View view) {
		
		Intent myIntent = new Intent(view.getContext(), MainActivity.class);
        startActivityForResult(myIntent, 0);
	}
	
	public void startManualTest(View view) {
		
		EditText text;
		CheckBox lightBox;
		CheckBox vibBox;
		
		//read Vals:
		
		//--> init Phase:
		text = (EditText) findViewById(R.id.initTime);
		int durInit = Integer.parseInt(text.getText().toString());
		
		lightBox = (CheckBox) findViewById(R.id.InitalLightOn);
		boolean dur0LightOn = lightBox.isChecked();
		vibBox = (CheckBox) findViewById(R.id.InitalVibOn);
		boolean dur0VibOn = vibBox.isChecked();
		
		//--> dur1 Phase:
		text = (EditText) findViewById(R.id.dur1Time);
		int dur1 = Integer.parseInt(text.getText().toString());
		
		lightBox = (CheckBox) findViewById(R.id.dur1LightOn);
		boolean dur1LightOn = lightBox.isChecked();
		vibBox = (CheckBox) findViewById(R.id.dur1VibOn);
		boolean dur1VibOn = vibBox.isChecked();
		
		//--> dur2 Phase:
		text = (EditText) findViewById(R.id.dur2Time);
		int dur2 = Integer.parseInt(text.getText().toString());
		
		lightBox = (CheckBox) findViewById(R.id.dur2LightOn);
		boolean dur2LightOn = lightBox.isChecked();
		vibBox = (CheckBox) findViewById(R.id.dur2VibOn);
		boolean dur2VibOn = vibBox.isChecked();
		
		
	}

}
