package com.example.app0505;
//TODO:코드간결하게, 변수, 함수 위치 규칙적이게 정리
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.le.ScanFilter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity
{
    private Button startBtn_collection, stopBtn_collection, sendBtn_collection, saveBtn_collection, backBtn_collection, setBtn_collection, clearBtn_collection;
    //스캔시작, 스캔중지, 파일전송, 스캔 결과 저장,editText에 초기 값 설정, editText의 값 삭제
    private EditText MAC1_collection, MAC2_collection, MAC3_collection, Area_collection, Size_collection;
    //스캔할 장치 1,2,3의 MAC 주소, 스캔한 장소의 영역을 나타내고 파일 저장 시 이름으로 활용, 스캔할 rssi의 개수
    private ListView listView;
    private TextView Txtv_collectedSize_collection;
    private ArrayList<Integer> rssiResult1,rssiResult2,rssiResult3;
    //rssi를 저장할 ArrayList
    private int countSize_collection,collectedSize;
    //ArrayList의 크기를 결정
    private String AreaStr;
    //영역 번호
    private LeDeviceListAdapter deviceListAdapter;
    //리스트뷰와 결과를 연동 할 어댑터
    private BluetoothHelper btHelper;
    //블루투스 관련 서비스를 내포한 클래스
    private  SettingHelper stHelper;
    //스캔을 위한 데이터를 처리할 클래스
    private CSVHelper csvHelper;
    //스캔한 결과를 CSV로 변환 할 클래스




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        Log.d("collection_activity", "화면전환");

        // 레이아웃에서 버튼들과 에디트텍스트, 리스트 뷰를 찾아서 연결
        backBtn_collection = findViewById(R.id.back_collectionpage);
        setBtn_collection = findViewById(R.id.set_collectionpage);
        startBtn_collection = findViewById(R.id.start_collectionpage);
        stopBtn_collection = findViewById(R.id.stop_collectionpage);
        sendBtn_collection = findViewById(R.id.send_collectionpage);
        clearBtn_collection = findViewById(R.id.clear_collectionpage);
        saveBtn_collection= findViewById(R.id.save_collectionpage);

        Txtv_collectedSize_collection=findViewById(R.id.collectedSize_collection);

        listView=findViewById(R.id.listview);

        MAC1_collection = findViewById(R.id.mac1_collectionpage);
        MAC2_collection = findViewById(R.id.mac2_collectionpage);
        MAC3_collection = findViewById(R.id.mac3_collectionpage);
        Size_collection = findViewById(R.id.size_collectionpage);
        Area_collection = findViewById(R.id.area_collectionpage);

        //rssiResult1,2,3의 객체를 생성
        rssiResult1=new ArrayList<>();
        rssiResult2=new ArrayList<>();
        rssiResult3=new ArrayList<>();

        //TODO: Beacon DTO 생성
        //BluetoothHelper, SettingHelper, CSVHelper, LeDeviceListAdapter의 객체 생성
        stHelper = new SettingHelper(MAC1_collection, MAC2_collection, MAC3_collection, Size_collection);
        btHelper = new BluetoothHelper(this, this,rssiResult1,rssiResult2,rssiResult3);
        csvHelper=new CSVHelper(this);
        deviceListAdapter=new LeDeviceListAdapter(this);

        // 블루투스 매니저, 어댑터를 호출
        btHelper.bleCall();

        //listView와 deivceListAdpater를 연결
        listView.setAdapter(deviceListAdapter);

        
        //start 버튼을 눌렀을 때 이벤트 핸들러 정의
        startBtn_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //editText에 입력한 countsize를 정수로 변환
                stHelper.convertType();
                //변환한 countsize를 SetHelper의 get 함수를 통해 초기화.
                countSize_collection = stHelper.getCountSize();
                //BluetoothHelper에서 스캔 결과를 처리하기 위해 countsiz를 넘겨줌
                btHelper.setcountSize(countSize_collection);

                /*stHelper.SetScanFilters(stHelper.mac1,stHelper.mac2,stHelper.mac3);
                //startScan이 필요한 ScanFilter의 객체 생성
                scanFilters=stHelper.SetScanFilters(stHelper.mac1,stHelper.mac2,stHelper.mac3);*/

                List<ScanFilter> scanFilters = new ArrayList<>();
              // 사용자가 사용할 스캔 필터를 정의. String으로 변환된 값을 ScanFilter로 지정
                ScanFilter scanFilter1 = new ScanFilter.Builder().setDeviceAddress(stHelper.mac1).build();
                scanFilters.add(scanFilter1);

                ScanFilter scanFilter2 = new ScanFilter.Builder().setDeviceAddress(stHelper.mac2).build();
                scanFilters.add(scanFilter2);

                ScanFilter scanFilter3 = new ScanFilter.Builder().setDeviceAddress(stHelper.mac3).build();
                scanFilters.add(scanFilter3);

                //위에서 정의한 ScanFilter을 인자로 받고 starScan 호출
                btHelper.startScan(scanFilters);

                Txtv_collectedSize_collection.setText("수집 개수: "+"/"+btHelper.getCountSize());
            }
        });

        //STOP 버튼을 눌렀을 때 이벤트 핸들러 정의
        stopBtn_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //스캔 중지
                btHelper.stopScan();

                //스캔한 값의 결과를 확인하기 위한 로그
                Log.d("ARRAY SIZE",rssiResult1.size()+", "+rssiResult2.size()+", "+rssiResult3.size());
                Log.d("ARRAY VALUES",rssiResult1+","+rssiResult2+","+rssiResult3);
            }
        });

        //BACK 버튼을 눌렀을 때 이벤트 핸들러 정의
        backBtn_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //현재 액티비티를 종료함
                btHelper.stopScan();
                finish();
            }
        });

        //SET 버튼을 눌렀을 때 이벤트 핸들러 정의
        setBtn_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //사전에 정의한 값을 호출하는 메소드
                stHelper.firstSetting();
            }
        });

        //SAVE 버튼을 눌렀을 때 이벤트 핸들러 정의
        saveBtn_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //AREA에 입력한 값을 String으로 변환
                AreaStr=Area_collection.getText().toString();
                //CSV로 변환하여 저장하는 메소드를 호출
                csvHelper.saveDataToCSV(rssiResult1,rssiResult2,rssiResult3,countSize_collection, AreaStr);
            }
        });

        //CLEAR 버튼을 눌렀을 때 이벤트 핸들러 정의
        clearBtn_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                // 에디트텍스트들의 텍스트를 지움
               stHelper.emptySetting();
            }
        });
        sendBtn_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CommunicationTest.class);
                startActivity(intent);
            }
        });
    }
}
