//package com.example.demogui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

public class ArduinoAndroidInterface extends Activity{

//	package arduino.android.bluetooth;
//
//
//	public class ArduinoAndroidInterface {
	        
    private static final int REQUEST_ENABLE_BT = 1; //has to be a value > 0
	public static int ERROR_OK = 0;
    public static String MACaddr = "00:07:80:49:8B:61";  //MAC address of the Arduino BT-board
    public static UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Bluetooth UUID for an outgoing BT-Socket
    public static BluetoothAdapter bluetoothAdapter = null;
    public static BluetoothDevice bluetoothDevice = null;
    public static BluetoothSocket bluetoothSocket = null;
	public static BluetoothSocket tmpSocket = null;
	public static OutputStream bluetoothOutputStream = null;
    
	//necessary?
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); //change with activity name
		
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		bluetoothDevice = bluetoothAdapter.getRemoteDevice(MACaddr);
	}
    
    public static int initBluetoothConnection() {
            
        /**
         * Es gilt zu klären, wann wir das machen und ob wir was aus 
         * https://github.com/Catrobat/Catroid/tree/master/catroid/src/org/catrobat/catroid/bluetooth
         * verwenden könnnen.
         */
    	
    	///////////////////////////////////////////////////////////////////////////////////////////
    	//we must check if this implementation is correct, or if the code should be anywhere else//
        ///////////////////////////////////////////////////////////////////////////////////////////    
    	
    	//check if the Bluetooth-Adapter was available on the device
    	if(bluetoothAdapter == null) {
    		ERROR_OK = -1;
    		return ERROR_OK;
    	}
    		
    	//check if the Arduino Board is on the bonded devices list
    	if(bluetoothDevice == null) {
    		ERROR_OK = -2;
    		return ERROR_OK;
    	}
    	
    	//enable the Bluetooth adapter
    	bluetoothAdapter.enable();
    	if(!bluetoothAdapter.isEnabled())
    		ERROR_OK = -3;
    	
    	//create an outgoing Bluetooth Socket
    	try {
    		tmpSocket = bluetoothDevice.createRfcommSocketToServiceRecord(myUUID);
    		ERROR_OK = 1;
		} catch (IOException e) {
			return -4;
		}
    	
    	bluetoothSocket = tmpSocket;
    	bluetoothAdapter.cancelDiscovery();
    	
    	try {
    		bluetoothSocket.connect();
    		ERROR_OK = 2;
		} catch (IOException e) {
			try {
				bluetoothSocket.close();
			} catch (IOException e1) {
				return -5;
			}
		}
    	
    	return ERROR_OK;
    }
    
    /* 
     * Sollen wir mit Fehlercodes oder mit Exceptions arbeiten?
     */
    public static int setDigitalPin(int pinNumber, boolean value) {
    	
    	if(bluetoothSocket == null)
    		return -5;
            
        System.err.println("TODO: Implement this method!");
        
        return ERROR_OK;
    }
    
    
    /* 
     * Sollen wir mit Fehlercodes oder mit Exceptions arbeiten?
     */
    public static int readDigitalPin(int pinNumber) {
    	
    	if(bluetoothSocket == null)
    		return -5;
            
        System.err.println("TODO: Implement this method!");
        
        return 1;
    }
    
    /* 
     * Sollen wir mit Fehlercodes oder mit Exceptions arbeiten?
     */
    public static int setAnalogPin(int pinNumber, int value) {
    	
    	if(bluetoothSocket == null)
    		return -5;
    	    
        System.err.println("TODO: Implement this method!");
        
        return ERROR_OK;
    }
    
    /* 
     * Sollen wir mit Fehlercodes oder mit Exceptions arbeiten?
     */
    public static int readAnalogPin(int pinNumber) {
    	
    	if(bluetoothSocket == null)
    		return -5;
            
        System.err.println("TODO: Implement this method!");
        
        return 123;
    }
    
    public static int sendDataViaBluetoothOutputstream(char outputData) {
    	
    	if(bluetoothSocket == null)
    		return -5;
		
		
		try {
			bluetoothOutputStream = bluetoothSocket.getOutputStream();
		} catch (IOException e) {
			return -6;
		}
    	
    	try {
    		bluetoothOutputStream.write(outputData);
			ERROR_OK = 3;
		} catch (IOException e) {
			return -7;
		}
    	
    	return ERROR_OK;
    }
    
    public static void checkERROR_OK(int ERROR_OK) {
    	switch(ERROR_OK)
    	{
    	case 3:
    		//Outgoing Data send successfully
    		break;
    	case 2:
    		//Bluetooth Socket connected
    		break;
    	case 1:
    		//Bluetooth Socket created
    		break;
    	case 0:
    		//everything was ok
    		break;
    	case -1:
    		//Error, no default Bluetooth-Adapter found
    		break;
    	case -2:
    		//Error, no device found on the bonded devices list
    		break;
    	case -3:
    		//Error, Bluetooth-Adapter not enabled
    		break;
    	case -4:
    		//Error, Bluetooth Socket not created
    		break;
    	case -5:
    		//Error, Bluetooth Socket not connected
    		break;
    	case -6:
    		//Error, no Bluetooth Outputstream
    		break;
    	case -7:
    		//Error, no Data was send via Outputstream
    		break;
    	}
    }
    
}


