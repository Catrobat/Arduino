package com.example.demogui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;

public class MainActivity extends Activity {
	
	//variable declaration
    String MACaddr = "00:07:80:49:8B:61";  //MAC of the Arduino BT-board
    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	BluetoothDevice btDevice = btAdapter.getRemoteDevice(MACaddr);
	BluetoothSocket btSocket = null;
	BluetoothSocket tmpSocket = null;
	BluetoothSocket btIncomingSocket = null;
	OutputStream btoutput = null;
	InputStream btinput = null;
	BluetoothServerSocket tmpServerSocket = null;
	BluetoothServerSocket btServerSocket = null;
	UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	int bytes = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@SuppressLint("NewApi")
	public void onoff(View view) {
		
		EditText statusField = (EditText) findViewById(R.id.status_field);
		ToggleButton onOffBtn = (ToggleButton) findViewById(R.id.btn_onoff);
		
		if(btAdapter == null)
		{
			statusField.setText("Error, no Bluetooth Adapter");
			return;
		}

		if(onOffBtn.isChecked())
			on();
		else
			off();
		
	}
	
	public void on() {
		
		EditText statusField = (EditText) findViewById(R.id.status_field);
		ToggleButton btnConnectDisconnect = (ToggleButton) findViewById(R.id.btn_conndisconn);
		
		if(!btAdapter.isEnabled())
			btAdapter.enable();
		else
			statusField.setText("Bluetooth Adapter was enabled");
		
		statusField.setText("Bluetooth enabled");
		btnConnectDisconnect.setEnabled(true);
	}
	
	public void off() {
		
		EditText statusField = (EditText) findViewById(R.id.status_field);
		ToggleButton btnConnectDisconnect = (ToggleButton) findViewById(R.id.btn_conndisconn);
		
		if(btAdapter.isEnabled())
			btAdapter.disable();
		else
			statusField.setText("Bluetooth Adapter was disabled");
		
		statusField.setText("Bluetooth disabled");
		btnConnectDisconnect.setEnabled(false);
	}
	
	@SuppressLint("NewApi")
	public void conndiscon(View view) {
	
		EditText statusField = (EditText) findViewById(R.id.status_field);
		ToggleButton conDisconBtn = (ToggleButton) findViewById(R.id.btn_conndisconn);
		
		if(btAdapter == null)
		{
			statusField.setText("Error, no Bluetooth Adapter Object");
			return;
		}

		if(conDisconBtn.isChecked())
			connect();
		else
			disconnect();
		
	}
	
	public void connect() {
		
		EditText statusField = (EditText) findViewById(R.id.status_field);
		ToggleButton btnLigtOnOff = (ToggleButton) findViewById(R.id.btn_light_onoff);
		
    	try {
    		tmpSocket = btDevice.createRfcommSocketToServiceRecord(myUUID);
    		statusField.setText("Bluetooth Socket created");
		} catch (IOException e) {
			statusField.setText("Error, Bluetooth Socket not created");
			return;
		}
    	
    	btSocket = tmpSocket;
    	btAdapter.cancelDiscovery();
    	
    	try {
			btSocket.connect();
			btnLigtOnOff.setEnabled(true);
			statusField.setText("Bluetooth Socket connected");
		} catch (IOException e) {
			try {
				btSocket.close();
			} catch (IOException e1) {
				statusField.setText("Error, Bluetooth Socket closed unexpectly");
				return;
			}
		}
	}
	
	public void disconnect() {
		
		EditText statusField = (EditText) findViewById(R.id.status_field);
		ToggleButton btnLigtOnOff = (ToggleButton) findViewById(R.id.btn_light_onoff);
		
		if(btSocket == null)
		{
			statusField.setText("Error, no Bluetooth Socket");
			return;
		}
		
    	try {
			btSocket.close();
			btnLigtOnOff.setEnabled(false);
			statusField.setText("Bluetooth Socket closed");
		} catch (IOException e) {
			statusField.setText("Error, Bluetooth disabled");
		}
	}
	
	@SuppressLint("NewApi")
	public void lightOnOff(View view) {
	
		EditText statusField = (EditText) findViewById(R.id.status_field);
		ToggleButton lightOnOff = (ToggleButton) findViewById(R.id.btn_light_onoff);
		
		if(btAdapter == null)
		{
			statusField.setText("Error, no Bluetooth Adapter");
			return;
		}

		if(lightOnOff.isChecked())
			ligthOn();
		else
			lightOff();
		
	}
	
	public void ligthOn() {
		
		EditText statusField = (EditText) findViewById(R.id.status_field);
		EditText receivedField = (EditText) findViewById(R.id.received_message);
		
		if(btSocket == null)
		{
			statusField.setText("Error, no Bluetooth Socket");
			return;
		}
		
		//////////////////////////outgoing//////////////////////////
		
		try {
			btoutput = btSocket.getOutputStream();
		} catch (IOException e) {
			statusField.setText("Error, no outputstream");
		}
    	
    	try {
			btoutput.write('A');
			statusField.setText("sent [activate]");
		} catch (IOException e) {
			statusField.setText("Error, didn't sent data");
		}
    	
		//////////////////////////incoming//////////////////////////
		    	
       	try {
    			btinput = btSocket.getInputStream();	
    		} catch (IOException e) {
    			statusField.setText("Error, no inputstream");
    		}
        	
            try {
            	bytes = btinput.read();
            	if(bytes == 72)
            		receivedField.setText("L");
            	else if(bytes == 76)
            		receivedField.setText("H");
            	else
            		statusField.setText("Unknown Command");
            } catch (IOException e) {
            	statusField.setText("Nothing received");
            }
		
	}
	
public void lightOff() {
		
		EditText statusField = (EditText) findViewById(R.id.status_field);
		EditText receivedField = (EditText) findViewById(R.id.received_message);
		
		if(btSocket == null)
		{
			statusField.setText("Error, no Bluetooth Socket");
			return;
		}
		
		//////////////////////////outgoing//////////////////////////
		
		try {
			btoutput = btSocket.getOutputStream();
		} catch (IOException e) {
			statusField.setText("Error, no outputstream");
		}
		
		try {
			btoutput.write('D');
			statusField.setText("sent [deactivated]");
		} catch (IOException e) {
			statusField.setText("Error, didn't sent data");
		}
		
		//////////////////////////incoming//////////////////////////
		    	
	   	try {
				btinput = btSocket.getInputStream();	
			} catch (IOException e) {
				statusField.setText("Error, no inputstream");
			}
	    	
	        try {
	        	bytes = btinput.read();
	        	if(bytes == 72)
	        		receivedField.setText("L");
	        	else if(bytes == 76)
	        		receivedField.setText("H");
	        	else
	        		statusField.setText("Unknown Command");
	        } catch (IOException e) {
	        	statusField.setText("Nothing received");
	        }
		
	}

}
