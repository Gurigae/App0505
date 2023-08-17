package com.example.app0505;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;

import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

//BLE 디바이스 스캔을 위한 블루투스 기능이 포함된 클래스
public class BluetoothHelper {
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    //위치 권한 설정
    private Context context;
    //context
    private BluetoothManager bluetoothManager;
    //블루투스 매니저
    private BluetoothAdapter bluetoothAdapter;
    //블루투스 어댑터
    private Activity activity;
    //액티비티
    private static ArrayList<Integer> rssiResult1,rssiResult2,rssiResult3;
    //rssi 값을 저장 할 ArrayList
    private String mac1,mac2,mac3;
    //ScanFilter에 사용할 MAC주소가 담긴 String mac 1,2,3
    private int countSize;
    //스캔할 개수를 설정

    // Bluetooth를 사용하기 위한 초기 설정 함수.
    // 블루투스 매니저와 어댑터를 가져오고, 권한을 요청
    public BluetoothHelper(Context context, Activity activity, ArrayList<Integer> rssiResult1,ArrayList<Integer> rssiResult2,ArrayList<Integer> rssiResult3) {
        this.activity = activity;
        this.context = context;
        this.rssiResult1=rssiResult1;
        this.rssiResult2=rssiResult2;
        this.rssiResult3=rssiResult3;

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        // 블루투스의 권한 요청
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
    }

    //countsize를 설정
    public void setcountSize(int countSize){
        this.countSize=countSize;
    }

    //countsize를 반환
    public int getCountSize(){
        return countSize;
    }

    // 블루투스 매니저, 어댑터 호출 메소드
    public void bleCall() {

        bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();

        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        if (bluetoothAdapter == null)
        {
            Toast.makeText(activity, "블루투스를 지원하지 않는 장비입니다.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent intent;
            if (!bluetoothAdapter.isEnabled())
            {
                // 블루투스가 비활성화된 경우, 사용자에게 블루투스 활성화를 요청합니다.
                intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivity(intent);
            }
            else
            {
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    // 권한이 없는 경우 권한 요청
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                }
            }
        }
    }



    // LOW LATENCY 방식의 장치 스캔을 시작합니다.
    public void startScan(List<ScanFilter> scanFilters) {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                // LOW_LATENCY 설정
                ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

                // 스캔필터를 따라 LOW_LATENCY 기반 스캔
                bluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettingsBuilder.build(), (ScanCallback) leScanCallback);

                mac1=scanFilters.get(0).getDeviceAddress();
                mac2=scanFilters.get(1).getDeviceAddress();
                mac3=scanFilters.get(2).getDeviceAddress();

                Toast.makeText(activity, "스캔 시작", Toast.LENGTH_SHORT).show();

            }
        }
    }

    // 스캔 중지 메소드
    public void stopScan() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            // BLE 장치 스캔 중지
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothAdapter.getBluetoothLeScanner().stopScan((ScanCallback) leScanCallback);
            Toast.makeText(activity, "스캔 중지되었습니다.", Toast.LENGTH_SHORT).show();
            Log.d("SIZE","countsize:"+countSize);
        }
    }

    // BLE 스캔 콜백 함수
    public android.bluetooth.le.ScanCallback leScanCallback = new android.bluetooth.le.ScanCallback() {
        public void onScanResult(int callbackType, ScanResult result) {
            // 스캔 결과에서 BLE 장치 정보를 가져옵니다.
            BluetoothDevice device = result.getDevice();
            int rssi = result.getRssi();
            byte[] scanRecord = result.getScanRecord() != null ? result.getScanRecord().getBytes() : null;
            String deviceAddress = device.getAddress();

            Log.d("DEBUG", "countsize from getCountSize(): " + countSize);

            if(deviceAddress.equals(mac1)) {
                rssiResult1.add(rssi);
            }
            else if (deviceAddress.equals(mac2)) {
                rssiResult2.add(rssi);
            }
            else if (deviceAddress.equals(mac3)) {
                rssiResult3.add(rssi);
            }

            Log.d("BLE_CLCT",rssiResult1.size()+", "+rssiResult2.size()+", "+rssiResult3.size());
            Log.d("ARRAY VALUES",rssiResult1+","+rssiResult2+","+rssiResult3);

            isFull();
        }

        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Toast.makeText(activity, "스캔에 실패하였습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    public void isFull(){
        if ((rssiResult1.size()>=countSize)&&(rssiResult2.size()>=countSize)&&(rssiResult3.size()>=countSize))
        {
            stopScan();
            Log.d("ARRAY SIZE",rssiResult1.size()+", "+rssiResult2.size()+", "+rssiResult3.size());
            Log.d("ARRAY VALUES",rssiResult1+","+rssiResult2+","+rssiResult3);
        }
    }
}
