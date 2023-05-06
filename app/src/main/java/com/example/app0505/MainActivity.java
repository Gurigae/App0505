package com.example.app0505;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;

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

        // 버튼리스너
        Button startButton = findViewById(R.id.startbutton);
        Button stopButton = findViewById(R.id.stopbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startScan();
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
            mLeDeviceListAdapter.clear();
            mLeDeviceListAdapter.notifyDataSetChanged();
        }
    }

    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                public void run() {
                    String deviceName = device.getName();
                    /*if(device != null && device.getName() != null && device.getName().equals("pebBLE"){*/
                    if (deviceName != null && !deviceName.equals("unknown device")) { // unknow device 검색x 필터링
                        mLeDeviceListAdapter.addDevice(device, rssi, scanRecord);
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    };
}
