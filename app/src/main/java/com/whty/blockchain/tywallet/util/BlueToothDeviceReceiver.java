package com.whty.blockchain.tywallet.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.whty.bluetooth.manage.util.BluetoothStruct;

import java.util.ArrayList;
import java.util.Locale;

public class BlueToothDeviceReceiver extends BroadcastReceiver {
    public static ArrayList<BluetoothStruct> items;
    public static ArrayList<String> itemsNames;
    private Handler handler;
    private final String TAG = BlueToothDeviceReceiver.class.getName();

    public BlueToothDeviceReceiver(Handler mHandler) {
        this.handler = mHandler;
        items = new ArrayList<BluetoothStruct>();
        itemsNames = new ArrayList<String>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Bundle b = intent.getExtras();
        Object[] lstName = b.keySet().toArray();

        for (int i = 0; i < lstName.length; i++) {
            String keyName = lstName[i].toString();
            Log.d(keyName, String.valueOf(b.get(keyName)));
        }

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {

            BluetoothDevice bluetoothDevice = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int index = findBluetoothDevice(bluetoothDevice.getAddress(), items);
            String bluetoothDeviceName = bluetoothDevice.getName();
            if (index < 0 && bluetoothDeviceName != null) {
                //过滤设备
                if (!bluetoothDeviceName.toUpperCase(Locale.getDefault()).startsWith("BTTEST")
                        && !bluetoothDeviceName.toUpperCase(Locale.getDefault()).startsWith("BW10")) {
                    return;
                }
                items.add(new BluetoothStruct(bluetoothDevice.getName(),
                        bluetoothDevice.getAddress(), bluetoothDevice));
                itemsNames.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                System.out.println(bluetoothDevice.getName() + "  " + bluetoothDevice.getAddress());
                handler.obtainMessage(SharedMSG.Device_Found, bluetoothDevice)
                        .sendToTarget();
            }

        } else if (BluetoothDevice.ACTION_NAME_CHANGED.equals(action)) {

            BluetoothDevice bluetoothDevice = intent
                    .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            int index = findBluetoothDevice(bluetoothDevice.getAddress(), items);
            if (index >= 0) {
                items.remove(index);
                items.add(new BluetoothStruct(bluetoothDevice.getName(),
                        bluetoothDevice.getAddress(), bluetoothDevice));
                itemsNames.remove(index);
                itemsNames.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                handler.obtainMessage(SharedMSG.Device_Found, bluetoothDevice)
                        .sendToTarget();
            }

        } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
            Log.d(TAG, "收到连接蓝牙的广播");
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
            Log.d(TAG, "收到蓝牙连接断开的广播");
            handler.obtainMessage(SharedMSG.Device_Disconnected).sendToTarget();
        } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
            Log.d(TAG, "蓝牙适配器状态改变,state:" + state);
            if (state == BluetoothAdapter.STATE_OFF) {
                Log.d(TAG, "蓝牙适配器关闭");
            } else if (state == BluetoothAdapter.STATE_ON) {
                Log.d(TAG, "蓝牙适配器开启");
                // BluetoothAdapter.getDefaultAdapter().startDiscovery();
            }
        } else if (BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED
                .equals(action)) {
            int state = intent.getIntExtra(
                    BluetoothAdapter.EXTRA_CONNECTION_STATE, -1);
            Log.d(TAG, "蓝牙适配器连接状态改变,state:" + state);
            BluetoothAdapter.getDefaultAdapter().enable();
        }
    }

    private int findBluetoothDevice(String mac,
                                    ArrayList<BluetoothStruct> deviceList) {
        for (int i = 0; i < deviceList.size(); i++) {
            if (((BluetoothStruct) deviceList.get(i)).getMac().equals(mac))
                return i;
        }
        return -1;
    }

}
