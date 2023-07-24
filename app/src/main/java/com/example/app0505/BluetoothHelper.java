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

import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

//BLE 디바이스 스캔을 위한 블루투스 기능이 포함된 클래스
public class BluetoothHelper {
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private  Context context;
    private BluetoothManager bluetoothmanager;
    private BluetoothAdapter bluetoothadapter;
    LeDeviceListAdapter adapter;
    Activity activity;
    ArrayList<BeaconDTO> scanResults;
    int scanCount=0;
    int targetSize;

    //Bluetooth를 사용하기 위한 초기 설정 함수.
    //시스템 서비스에서 블루투스 서비스를 가져와 블루투스 매니저에 저장

    //BluetoothManager의 장치의 기본 어댑터를 가져와 bluetoothadapter에 저장.
    public BluetoothHelper(Context context,Activity activity,int targetSize)
    {
        this.activity = activity;
        this.context = context;
        this.targetSize=targetSize;
        bluetoothmanager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothadapter = bluetoothmanager.getAdapter();
        adapter = new LeDeviceListAdapter(context);

        //블루투스의 권한 요청
        //Activity
        ActivityCompat.requestPermissions(activity,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
    }

    public void bleCall()
    {
        bluetoothmanager=(BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothadapter=bluetoothmanager.getAdapter();
    }
    //
    public void bleCheck(BluetoothAdapter bluetoothadapter){
        if (bluetoothadapter==null)
        {
            Toast.makeText(activity,"블루투스를 지원하지 않는 장비입니다.",Toast.LENGTH_SHORT);
        }
        else
        {
            Intent intent;
            if(!bluetoothadapter.isEnabled())
            {
                intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivity(intent);
            }
            else
            {
                if (ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    // 권한이 없는 경우 권한 요청
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                }
            }

        }
    }
    //LOW LATENCY 방식의 장치 스캔. 메소드를 사용하려는 곳에서 스캔 필터를 따로 작성해야 함
    //TODO: 수집용 액티비티, 테스트용 액티비티를 위한 startScan() 별도 작성해야 함.
    public void startScan(List<ScanFilter> scanFilters){
        bleCheck(bluetoothadapter);
        if (bluetoothadapter != null && bluetoothadapter.isEnabled())
        {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            }
            else
            {
                //  scanfilters 작성
                ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
                //bluetoothadapter.getBluetoothLeScanner().startScan(leScanCallback); //일반 스캔
                //List<ScanFilter> scanFilters = new ArrayList<>();
                bluetoothadapter.getBluetoothLeScanner().startScan(scanFilters, scanSettingsBuilder.build(),(ScanCallback) leScanCallback);
                Toast.makeText(activity,"스캔 시작",Toast.LENGTH_SHORT).show();
                //스캔필터를 따라 LOW_LATENCY 기반 스캔
            }
        }
    }

    public List<ScanFilter> scanFilter(String mac1, String mac2, String mac3){
        List<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter scanFilter1 = new ScanFilter.Builder().setDeviceAddress(mac1).build();
        scanFilters.add(scanFilter1);

        ScanFilter scanFilter2 = new ScanFilter.Builder().setDeviceAddress(mac2).build();
        scanFilters.add(scanFilter2);

        ScanFilter scanFilter3 = new ScanFilter.Builder().setDeviceAddress(mac3).build();
        scanFilters.add(scanFilter3);

        return scanFilters;

    }

    public void stopScan(){
        if (bluetoothadapter != null && bluetoothadapter.isEnabled())
        {
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

    //지정한 개수만큼 스캔의 완료 여부 판단 함수.
    //TODO: DTO를 만들어 파라미터를 간소화해야함
    public boolean isFull(ArrayList<Integer> beacon1,ArrayList<Integer> beacon2, ArrayList<Integer> beacon3, int countsize)
    {
        if ((beacon1.size() == countsize) && (beacon2.size() == countsize) && (beacon3.size() == countsize))
        {
            stopScan();
            Toast.makeText(activity,"스캔을 완료하였습니다.",Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    public void notifyDataSetChanged(){
        if(adapter!=null)
        {
            adapter.notifyDataSetChanged();
        }
    }

    //BLE 스캔 콜백 함수
    public android.bluetooth.le.ScanCallback leScanCallback = new android.bluetooth.le.ScanCallback()
    {
        public void onScanResult(int callbackType, ScanResult result)
        {
            BluetoothDevice device = result.getDevice();
            int rssi=result.getRssi();
            byte[] ScanRecord = result.getScanRecord() != null ? result.getScanRecord().getBytes() : null;
            String deviceAddress = device.getAddress();
            Log.d("device",deviceAddress+" 수집");

            //scanResults.add(result);
            scanCount++;
            if(scanCount>=targetSize){
                stopScan();
            }

            adapter.addDevice(device,rssi,ScanRecord);
            adapter.notifyDataSetChanged();
        }

        public void onScanFailed(int errorCode)
        {
            super.onScanFailed(errorCode);
            Toast.makeText(activity,"스캔에 실패하였습니다.",Toast.LENGTH_SHORT).show();
        }
    };

}
