package com.example.app0505;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//블루투스의 권한을 요청하는 클래스
public class BluetoothHelper {
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private  Context context;
    private BluetoothManager bluetoothmanager;
    private BluetoothAdapter bluetoothadapter;
    Activity activity;

    private BluetoothHelper(Context context) {
        this.context = context;
        bluetoothmanager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothadapter = bluetoothmanager.getAdapter();
    }

    private void permissionhelper(){
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
    }

    private void bleCheck(BluetoothAdapter bluetoothadapter){
        if (bluetoothadapter==null){
            Toast.makeText(activity,"블루투스를 지원하지 않는 장비입니다.",Toast.LENGTH_SHORT);
            activity.finish();
        }
        else{
            Intent intent;
            if(!bluetoothadapter.isEnabled()){
                intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivity(intent);
            }
            else{
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // 권한이 없는 경우 권한 요청
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                }
            }

        }
    }

    private void startScan(){
        bleCheck(bluetoothadapter);

    }

    private void stopScan(){
        if (bluetoothadapter != null && bluetoothadapter.isEnabled()) {
            // Stop scanning for BLE devices
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothadapter.getBluetoothLeScanner().stopScan((ScanCallback) leScanCallback);
            Toast.makeText(activity, "스캔 중지되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveDataTOCSV(String areaStr,int countsize)
    {
        File directory = new File(getExternalFilesDir(null) + getPackageName());

        if (!directory.exists())
        {
            directory.mkdirs(); // 폴더가 존재하지 않으면 생성
        }
        String areaStr= String.getText().toString();

        if (areaStr == null || areaStr.isEmpty())
        {
            Toast.makeText(activity, "영역 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        int areaInteger = Integer.parseInt(areaStr);
        String csvFileName = areaInteger + "_" + countsize + ".csv"; // 파일 이름 설정
        File file = new File(directory, csvFileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            for (int i=0; i<countsize; i++)
            {
                int b1,b2,b3;

            }
        } catch (IOException e)
        {
            Toast.makeText(activity, "데이터 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.e("CSV_SAVE", "CSV 파일 저장 중 오류가 발생했습니다.");
        }
    }
    public android.bluetooth.le.ScanCallback leScanCallback = new android.bluetooth.le.ScanCallback() {
        public void onScanResult(int callbackType, ScanResult result)
        {
            BluetoothDevice device = result.getDevice();
            int rssi=result.getRssi();
            byte[] ScanRecord = result.getScanRecord() != null ? result.getScanRecord().getBytes() : null;
            String deviceAddress = device.getAddress();
        }

        public void onScanFailed(int errorCode)
        {
            super.onScanFailed(errorCode);
        }
    };

}
