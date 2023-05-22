package com.example.app0505;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private EditText editMac1, editMac2, editMac3, Count;
    private int countsize;
    private Button startButton, stopButton, saveButton;
    private String mac1, mac2, mac3;
    private ArrayList<ArrayList<Integer>> rssiData = new ArrayList<>();

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


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //입력받은 MAC주소 가져오기
                editMac1.setText("D0:39:72:A4:B3:95");
                editMac2.setText("D0:39:72:A4:B4:A9");
                editMac3.setText("D0:39:72:A4:9E:AB");
                Count.setText("10");

                mac1 = editMac1.getText().toString();
                mac2 = editMac2.getText().toString();
                mac3 = editMac3.getText().toString();
                countsize = Integer.parseInt(Count.getText().toString());

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
                saveDataToCSV(rssiData);
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

    private void saveDataToCSV(ArrayList<ArrayList<Integer>> rssiData) {
        File directory = new File(getExternalFilesDir(null) + getPackageName());
        if (!directory.exists()) {
            directory.mkdirs(); // 폴더가 존재하지 않으면 생성
        }
        String csvFileName = "test1.csv"; // 파일 이름 설정
        File file = new File(directory, csvFileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (ArrayList<Integer> rssiList : rssiData) {
                // 각 ArrayList의 요소들을 CSV 형식으로 변환하여 파일에 쓰기
                StringBuilder csvLine = new StringBuilder();
                for (Integer rssi : rssiList) {
                    csvLine.append(rssi).append(","); // 요소와 요소 사이에 쉼표(,) 추가
                }
                csvLine.deleteCharAt(csvLine.length() - 1); // 마지막 쉼표(,) 제거
                writer.write(csvLine.toString());
                writer.newLine();
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
        }
    }

    public BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        private ArrayList<BluetoothDevice> BLE1 = new ArrayList<>();
        private ArrayList<BluetoothDevice> BLE2 = new ArrayList<>();
        private ArrayList<BluetoothDevice> BLE3 = new ArrayList<>();
        //private ArrayList<ArrayList<Integer>> rssiData = new ArrayList<>();
        private Logger logger;

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            String deviceAddress = device.getAddress();

            mLeDeviceListAdapter.addDevice(device, rssi, scanRecord);
            mLeDeviceListAdapter.notifyDataSetChanged();
            if (deviceAddress.equals(mac1)) {
                BLE1.add(device);
                updateRSSIData(0, rssi); // 0번 인덱스에 RSSI 값 추가
            } else if (deviceAddress.equals(mac2)) {
                BLE2.add(device);
                updateRSSIData(1, rssi); // 1번 인덱스에 RSSI 값 추가
            } else if (deviceAddress.equals(mac3)) {
                BLE3.add(device);
                updateRSSIData(2, rssi); // 2번 인덱스에 RSSI 값 추가
            }
            Log.d("BLE_SCAN", "SCAN: [" + deviceAddress + "]");
            Log.e("BLE_SCAN", "SCAN_FILTER: " + deviceAddress);

        }

        public void updateRSSIData(int beaconIndex, int rssi) {
            ArrayList<Integer> rssiList;
            if (beaconIndex >= rssiData.size()) {
                // 해당 비콘의 첫 번째 측정 값
                rssiList = new ArrayList<>();
                rssiData.add(rssiList);
            } else {
                rssiList = rssiData.get(beaconIndex);
            }

            rssiList.add(rssi);

            if (rssiList.size() >= countsize) {
                // countsize를 초과하는 경우, 가장 오래된 값을 제거하여 유지
                //Toast.makeText(this, "스캔이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                stopScan();
                //rssiList.remove(0);
            }

            if ((rssiData.get(0).size()>=countsize)&&(rssiData.get(1).size()>=countsize)&&(rssiData.get(2).size()>=countsize)) {
                // BLE1의 RSSI 데이터가 countsize만큼 수집된 경우
                // 이곳에서 원하는 처리를 수행할 수 있습니다.
                // 예: BLE1에 저장된 RSSI 값 출력
                Log.d("BLE_SCAN", "BLE1 RSSI values: " + rssiData.get(0).toString());
                Log.d("BLE_SCAN", "BLE2 RSSI values: " + rssiData.get(1).toString());
                Log.d("BLE_SCAN", "BLE3 RSSI values: " + rssiData.get(2).toString());
            }
        }
    };
}
