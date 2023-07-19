package com.example.app0505;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;

import androidx.core.app.ActivityCompat;

//블루투스의 권한을 요청하는 클래스
public class BluetoothHelper {
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private  Context context;
    private BluetoothManager bluetoothmanager;
    private BluetoothAdapter bluetoothadapter;
    Activity activity;

    public BluetoothHelper(Context context) {
        this.context = context;
        bluetoothmanager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothadapter = bluetoothmanager.getAdapter();
    }

    public void permissionhelper(){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
    }

}
