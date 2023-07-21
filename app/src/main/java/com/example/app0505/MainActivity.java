package com.example.app0505;

import android.Manifest;
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
import android.os.Bundle;

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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    //블루투스가 켜지면 자동으로  대략적인 위치를 가져오는 퍼미션을 요청.
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    //블루투스 스캔을 위한 BluetoothManager, bluetoothAdapter
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    //리스트뷰의 아이템과 매인 액티비티에서 수행한 스캔 결과를 연동해 리스트뷰에 연결해주는 ListAdapter
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private EditText editMac1,editMac2,editMac3,Count,Area;
    private Button startButton,stopButton,saveButton,setButton;

    private CSVHelper csvhelper;
    private BluetoothHelper bluetoothhelper;

    //Count에서 입력받은 사이즈를 정수형으로 저장할 변수 countsize
    private int countsize;

    //스캔시작, 스캔중지, 저장 버튼


    //editMac에 입력할 mac주소를 스트링형태로 저장한 mac1,2,3
    private String mac1, mac2, mac3;

    //스캔한 데이터를 저장할 ArrayList
    ArrayList<Integer> beacon1 = new ArrayList<>();
    ArrayList<Integer> beacon2 = new ArrayList<>();
    ArrayList<Integer> beacon3 = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        csvhelper = new CSVHelper(this);
        bluetoothhelper = new BluetoothHelper(this,this);
        bluetoothhelper.bleCheck(bluetoothAdapter);

        // 위치 권한 요청
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

        // BluetoothAdapter 가져오기
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        //블루투스 지원여부 확인
        bleCheck(bluetoothAdapter);

        startButton=findViewById(R.id.startbutton);
        stopButton=findViewById(R.id.stopbutton);
        saveButton=findViewById(R.id.savebutton);
        setButton=findViewById(R.id.setbutton);
        editMac1=findViewById(R.id.mac1);
        editMac2=findViewById(R.id.mac2);
        editMac3=findViewById(R.id.mac3);
        Count=findViewById(R.id.count);
        Area=findViewById(R.id.area);

        //기존에 설정한 맥주소, 카운트 개수를 자동 입력
        setButton.setOnClickListener(new View.OnClickListener()
        {
            //@Override
            public void onClick(View v) {
                firstSetting();
            }
        });
        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                preparingScan();
                startScan();

                mLeDeviceListAdapter.clear();
                mLeDeviceListAdapter.notifyDataSetChanged();
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                stopScan();
                Log.d("STOP_BUTTON", "STOP 버튼이 클릭되었습니다.");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
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

    //스캔 초기 세팅 2
    private void preparingScan()
    {
        mac1 = editMac1.getText().toString();
        mac2 = editMac2.getText().toString();
        mac3 = editMac3.getText().toString();
        countsize = Integer.parseInt(Count.getText().toString());

        beacon1.clear();
        beacon2.clear();
        beacon3.clear();
    }

    //초기 세팅 함수. 실험을 위해 준비된 비콘의 MAC주소, 카운트 갯수를 미리 지정한다.
    private void firstSetting()
    {
        //입력받은 MAC주소 가져오기
        editMac1.setText("D0:39:72:A4:B3:95");
        editMac2.setText("D0:39:72:A4:B4:A9");
        editMac3.setText("D0:39:72:A4:9E:AB");
        Count.setText("10");
    }

    //블루투스 지원 여부
    private void bleCheck(BluetoothAdapter bluetoothAdapter)
    {

        if (bluetoothAdapter == null)
        {
            // Bluetooth is not supported, turn off the device
            Toast.makeText(this, "블루투스를 지원하지 않는 장치입니다.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            // If Bluetooth is not enabled, request to turn it on
            if (!bluetoothAdapter.isEnabled())
            {
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

    //스캔한 데이터를 CSV로 전환 후 내부 저장소에 저장는 함수
    private void saveDataToCSV()
    {
        File directory = new File(getExternalFilesDir(null) + getPackageName());
        if (!directory.exists())
        {
            directory.mkdirs(); // 폴더가 존재하지 않으면 생성
        }

        String areaStr = Area.getText().toString();
        if (areaStr == null || areaStr.isEmpty())
        {
            Toast.makeText(this, "영역 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        int area = Integer.parseInt(areaStr);

        String csvFileName = area + "_" + countsize + ".csv"; // 파일 이름 설정
        File file = new File(directory, csvFileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file)))
        {
            for (int i = 0; i < countsize; i++)
            {
                int b1, b2, b3;
                b1 = beacon1.get(i);
                b2 = beacon2.get(i);
                b3 = beacon3.get(i);
                String str = (b1 + "," + b2 + "," + b3 + "\n");

                writer.write(str);
            }
            Toast.makeText(this, "데이터가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
            Log.d("CSV_SAVE", "CSV 파일이 성공적으로 저장되었습니다. 경로: " + file.getAbsolutePath());
        }
        catch (IOException e)
        {
            Toast.makeText(this, "데이터 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.e("CSV_SAVE", "CSV 파일 저장 중 오류가 발생했습니다.");
        }
    }

    //스캔 시작 함수
    private void startScan()
    {
        if (mac1.isEmpty() || mac2.isEmpty() || mac3.isEmpty() || Count.getText().toString().isEmpty()) {
            Toast.makeText(this, "Mac 주소와 count를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
            {
                // Start scanning for BLE devices
                mLeDeviceListAdapter.clear();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                }
                else
                {
                    // 스캔 모드를 "Low Latency"로 설정
                    ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);

                    // 스캔 필터
                    List<ScanFilter> scanFilters = new ArrayList<>();

                    // ScanFilter 생성 및 추가
                    ScanFilter scanFilter1 = new ScanFilter.Builder().setDeviceAddress(mac1).build();
                    scanFilters.add(scanFilter1);

                    ScanFilter scanFilter2 = new ScanFilter.Builder().setDeviceAddress(mac2).build();
                    scanFilters.add(scanFilter2);

                    ScanFilter scanFilter3 = new ScanFilter.Builder().setDeviceAddress(mac3).build();
                    scanFilters.add(scanFilter3);

                    // 스캔 시작
                    bluetoothAdapter.getBluetoothLeScanner().startScan(scanFilters, scanSettingsBuilder.build(), (ScanCallback) leScanCallback);
                    Toast.makeText(this, "스캔 시작", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "블루투스를 활성화해주세요.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //스캔 중지 함수
    private void stopScan()
    {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled())
        {
            // Stop scanning for BLE devices
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothAdapter.getBluetoothLeScanner().stopScan((ScanCallback) leScanCallback);
            Toast.makeText(this, "스캔 중지되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //BLE 스캔 콜백
    public android.bluetooth.le.ScanCallback leScanCallback = new android.bluetooth.le.ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            BluetoothDevice device = result.getDevice();
            int rssi = result.getRssi();
            byte[] scanRecord = result.getScanRecord() != null ? result.getScanRecord().getBytes() : null;

            String deviceAddress = device.getAddress();

            // mac1, mac2, mac3와 스캔한 디바이스의 MAC 주소가 일치하면 각 beacon1, beacon2, beacon3에 RSSI 추가
            if (deviceAddress.equals(mac1))
            {
                mLeDeviceListAdapter.addDevice(device, rssi, scanRecord);
                mLeDeviceListAdapter.notifyDataSetChanged();
                beacon1.add(rssi);
            }
            else if (deviceAddress.equals(mac2))
            {
                mLeDeviceListAdapter.addDevice(device, rssi, scanRecord);
                mLeDeviceListAdapter.notifyDataSetChanged();
                beacon2.add(rssi);
            }
            else if (deviceAddress.equals(mac3))
            {
                mLeDeviceListAdapter.addDevice(device, rssi, scanRecord);
                mLeDeviceListAdapter.notifyDataSetChanged();
                beacon3.add(rssi);
            }

            // 수집 완료 시 중지
            isFull();

            Log.d("BLE_SCAN", "SIZE: " + beacon1.size() + ", " + beacon2.size() + ", " + beacon3.size());
        }

        // 스캔 실패 시 처리할 내용을 추가할 수 있습니다.
        @Override
        public void onScanFailed(int errorCode)
        {
            super.onScanFailed(errorCode);

        }

        // 수집하고자하는 디바이스들의 RSSI 값이 countsize보다 같거나 많으면 스캔 중지
        public void isFull()
        {
            if (beacon1.size() >= countsize && beacon2.size() >= countsize && beacon3.size() >= countsize)
            {
                stopScan();
                saveButton.setTextColor(Color.RED);
            }
        }
    };
}