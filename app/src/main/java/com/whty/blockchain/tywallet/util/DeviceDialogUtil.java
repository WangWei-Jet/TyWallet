package com.whty.blockchain.tywallet.util;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.ArrayAdapter;

import com.whty.bluetooth.manage.util.BlueToothConfig;
import com.whty.bluetooth.manage.util.BlueToothUtil;
import com.whty.bluetooth.manage.util.BluetoothStruct;

public class DeviceDialogUtil {

	private Handler handler;
	private Dialog mDialog;
	private ArrayAdapter<BluetoothStruct> adapter = null;
//	private ArrayAdapter<String> adapter = null;
	private BluetoothDevice device;
	private BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	private int No_Device_Selected = 95;
	private int Device_Ensured = 99;
	
	public DeviceDialogUtil(Handler handler) 
	{		
		super();
		this.handler = handler;
		device = null;
	}		
	
	private void createDialog(final Context context) 
	{
		device = null;
		adapter = new ArrayAdapter<BluetoothStruct>(context, android.R.layout.select_dialog_singlechoice, BlueToothDeviceReceiver.items);
//		adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice, BlueToothDeviceReceiver.itemsNames);
		mDialog = new AlertDialog.Builder(context).setTitle(UIMessage.device_list_title).setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener()
		{
			public void onClick(final DialogInterface dialog, final int whichButton) 
			{				
						BlueToothConfig.cancelDiscovery();
						System.out.println("whichButton:" + whichButton);
						//System.out.println("keys[whichButton]:" + BlueToothUtil.items.get(whichButton).getName());
						System.out.println("keys[whichButton]:" + BlueToothDeviceReceiver.items.get(whichButton).getName());
						//device = BlueToothUtil.items.get(whichButton).getDevice();
						device = BlueToothDeviceReceiver.items.get(whichButton).getDevice();
						System.out.println("device:" + device);			
			
			}
		}).setPositiveButton(UIMessage.device_list_positive, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton)
			{
				btAdapter.cancelDiscovery();
				//BlueToothUtil.items.clear();
				BlueToothDeviceReceiver.items.clear();
				BlueToothDeviceReceiver.itemsNames.clear();
				adapter.notifyDataSetChanged();
				btAdapter.startDiscovery();
				dismissDialog(mDialog, false);
			}
		}).setNegativeButton(UIMessage.device_list_negative, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{			
				if(device != null)
				{
					handler.obtainMessage(Device_Ensured, device).sendToTarget();
					dismissDialog(mDialog, true);
					//BlueToothUtil.items.clear();
					BlueToothDeviceReceiver.items.clear();
					BlueToothDeviceReceiver.itemsNames.clear();
					mDialog = null;
					btAdapter.cancelDiscovery();
					
				}
				else
				{
					dismissDialog(mDialog, false);
					handler.obtainMessage(No_Device_Selected).sendToTarget();
				}
			}
		}).create();
		mDialog.setCancelable(false);
		mDialog.show();		


		mDialog.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK)
				{
					dismissDialog(mDialog, true);
					btAdapter.cancelDiscovery();
					//BlueToothUtil.items.clear();
					BlueToothDeviceReceiver.items.clear();
					BlueToothDeviceReceiver.itemsNames.clear();
					mDialog = null;
					return true;
				}
				return false;
			}
		});
	}


	private void dismissDialog(Dialog dialog, boolean flag) {
		try {
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, flag);
			dialog.dismiss();
		} catch (Exception e) {

		}
	}


	
	public void listDevice(final Context context)
	{
		if (mDialog == null)
		{
			createDialog(context);
		}
		adapter.notifyDataSetChanged();
	}


}
