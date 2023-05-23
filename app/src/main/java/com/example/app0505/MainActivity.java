package com.example.app0505;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private EditText editMac1, editMac2, editMac3, Count, Area;
    private int countsize;
    private Button startButton, stopButton, saveButton;
    private String mac1, mac2, mac3;
    ArrayList<Integer> beacon1 = new ArrayList<>();
    ArrayList<Integer> beacon2 = new ArrayList<>();
    ArrayList<Integer> beacon3 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위치 권한 요청
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        // Bluetoothadapter 가져오기
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bleCheck(bluetoothAdapter);

        // Button, EditText 릿느ㅓ
        startButton = findViewById(R.id.startbutton);
        stopButton = findViewById(R.id.stopbutton);
        saveButton = findViewById(R.id.savebutton);
        editMac1 = findViewById(R.id.mac1);
        editMac2 = findViewById(R.id.mac2);
        editMac3 = findViewById(R.id.mac3);
        Count = findViewById(R.id.count);
        Area = findViewById(R.id.area);

        //입력받은 MAC주소 가져오기
        editMac1.setText("D0:39:72:A4:B3:95");
        editMac2.setText("D0:39:72:A4:B4:A9");
        editMac3.setText("D0:39:72:A4:9E:AB");
        Count.setText("10");

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mac1 = editMac1.getText().toString();
                mac2 = editMac2.getText().toString();
                mac3 = editMac3.getText().toString();
                countsize = Integer.parseInt(Count.getText().toString());

                beacon1.clear();
                beacon2.clear();
                beacon3.clear();

                startScan();

                mLeDeviceListAdapter.clear();
                mLeDeviceListAdapter.notifyDataSetChanged();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopScan();
                Log.d("STOP_BUTTON", "STOP 버튼이 클릭되었습니다.");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataToCSV();
                saveButton.setTextColor(Color.WHITE);
                Log.d("SAVE_BUTTON", "SAVE 버튼이 클릭되었습니다..");
            }
        });

        // 장치 리스트뷰
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(mLeDeviceListAdapter);

    }

    //블루투스 지원 여부
    private void bleCheck(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported, turn off the device
            Toast.makeText(this, "블루투스를 지원하지 않는 장치입니다.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // If Bluetooth is not enabled, request to turn it on
            if (!bluetoothAdapter.isEnabled()) {
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(i);
            }
        }
    }

    private void saveDataToCSV() {
        File directory = new File(getExternalFilesDir(null) + getPackageName());
        if (!directory.exists()) {
            directory.mkdirs(); // 폴더가 존재하지 않으면 생성
        }

        String areaStr = Area.getText().toString();
        if(areaStr == null || areaStr.isEmpty()){
            Toast.makeText(this, "영역 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        int area = Integer.parseInt(areaStr);

        String csvFileName = area+"_"+countsize+".csv"; // 파일 이름 설정
        File file = new File(directory, csvFileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i=0; i<countsize; i++) {
                int b1, b2, b3;
                b1 = beacon1.get(i);
                b2 = beacon2.get(i);
                b3 = beacon3.get(i);
                String str = (b1+","+b2+","+b3+"\n");

                writer.write(str);
            }
            Toast.makeText(this, "데이터가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
            Log.d("CSV_SAVE", "CSV 파일이 성공적으로 저장되었습니다. 경로: " + file.getAbsolutePath());
        } catch (IOException e) {
            Toast.makeText(this, "데이터 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.e("CSV_SAVE", "CSV 파일 저장 중 오류가 발생했습니다.");
        }
    }

    //스캔 함수
    private void startScan() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            // Start scanning for BLE devices
            mLeDeviceListAdapter.clear();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            } else {
                bluetoothAdapter.startLeScan(leScanCallback);
                Toast.makeText(this, "스캔 시작", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "블루투스를 활성화해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopScan() {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            // Stop scanning for BLE devices
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothAdapter.stopLeScan(leScanCallback);

            Toast.makeText(this, "스캔 중지되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    public BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            String deviceAddress = device.getAddress();

            //mac1,mac2,mac3와 스캔한 디바이스의 mac이 일치하면 각 beacon1,2,3에 rssi를 추가하기
            //
            if (deviceAddress.equals(mac1)) {
                //리스트뷰에 스캔한 디바이스 정보를 추가한다.
                mLeDeviceListAdapter.addDevice(device, rssi, scanRecord);
                mLeDeviceListAdapter.notifyDataSetChanged();
                beacon1.add(rssi);
            } else if (deviceAddress.equals(mac2)) {
                //리스트뷰에 스캔한 디바이스 정보를 추가한다.
                mLeDeviceListAdapter.addDevice(device, rssi, scanRecord);
                mLeDeviceListAdapter.notifyDataSetChanged();
                beacon2.add(rssi);
            } else if (deviceAddress.equals(mac3)) {
                //리스트뷰에 스캔한 디바이스 정보를 추가한다.
                mLeDeviceListAdapter.addDevice(device, rssi, scanRecord);
                mLeDeviceListAdapter.notifyDataSetChanged();
                beacon3.add(rssi);
            }

            //수집이 완료되면 스캔을 중지한다.
            isFull();

            Log.d("BLE_SCAN", "SIZE"+beacon1.size()+","+beacon2.size()+","+beacon3.size());

            //Log.d("BLE_SCAN", "SCAN: [" + deviceAddress + "]");
            //Log.e("BLE_SCAN", "SCAN_FILTER: " + deviceAddress);
        }
        public void isFull(){
            //수집하고자하는 디바이스들의 rssi값들이 countsize보다 같거나 많으면 스캔을 중지한다.
            if(beacon1.size()>=countsize && beacon2.size()>=countsize && beacon3.size()>=countsize){
                stopScan();
                saveButton.setTextColor(Color.RED);
            }
        }

    };
}
