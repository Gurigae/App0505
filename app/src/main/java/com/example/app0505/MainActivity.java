package com.example.app0505;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private EditText editMac1,editMac2,editMac3,Count;

    private Button startButton,stopButton;
    private String mac1,mac2,mac3;

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
        editMac1 = findViewById(R.id.mac1);
        editMac2 = findViewById(R.id.mac2);
        editMac3 = findViewById(R.id.mac3);
        Count=findViewById(R.id.count);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //입력받은 MAC주소 가져오기
                editMac1.setText("D0:39:72:A4:B3:95");
                editMac2.setText("D0:39:72:A4:B4:A9");
                editMac3.setText("D0:39:72:A4:9E:AB");

                mac1 = editMac1.getText().toString();
                mac2 = editMac1.getText().toString();
                mac3 = editMac1.getText().toString();

                startScan();

                mLeDeviceListAdapter.clear();
                mLeDeviceListAdapter.notifyDataSetChanged();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopScan();
            }
        });

        // 장치 리스트뷰
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        ListView listView = findViewById(R.id.listview);
        listView.setAdapter(mLeDeviceListAdapter);

    }

    //블루투스 지원 여부a
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

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        private int countsize=Integer.parseInt(Count.getText().toString());
        private ArrayList<BluetoothDevice> BLE1=new ArrayList<>();
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            //String deviceName = device.getName();
            String deviceAddress = device.getAddress();
            Log.d("BLE_SCAN", "SCAN: [" + deviceAddress + "]");
            /*if(device != null && device.getName() != null && device.getName().equals("pebBLE"){*/
            String filterDevices = mac1 +"|"+ mac2+ "|"+ mac3;
            boolean filter = deviceAddress.matches(filterDevices);
            //filter = deviceAddress.matches("?");
            if (filter) {
                Log.e("BLE_SCAN", "SCAN_FILTER: " + deviceAddress);
                mLeDeviceListAdapter.addDevice(device, rssi, scanRecord);
                mLeDeviceListAdapter.notifyDataSetChanged();
                if (deviceAddress.equals(mac1)) {
                    BLE1.add(device);
                }
                for (int i=0;i<countsize;i++) {

                }

            }
        }

          /* runOnUiThread(new Runnable() {
                public void run() {

                }
            });
        }
        private void processBeaconData(List<BluetoothDevice> devices){

        }*/
    };
}
