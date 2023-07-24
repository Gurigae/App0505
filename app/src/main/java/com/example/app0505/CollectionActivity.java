package com.example.app0505;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CollectionActivity extends AppCompatActivity {
    Button startBtn_collection,stopBtn_collection,sendBtn_collection,saveBtn_collection, backBtn_collection,setBtn_collection,clearBtn_collection;
    EditText MAC1_collection, MAC2_collection,MAC3_collection, Area_collection, Size_collection;
    LeDeviceListAdapter adapter_collection;

    BluetoothManager bluetoothM;
    BluetoothAdapter bluetoothA;
    String mac1,mac2,mac3;
    int countsize;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        Log.d("collection_activity","화면전환");

        backBtn_collection=findViewById(R.id.back_collectionpage);
        setBtn_collection=findViewById(R.id.set_collectionpage);
        startBtn_collection=findViewById(R.id.start_collectionpage);
        stopBtn_collection=findViewById(R.id.stop_collectionpage);
        sendBtn_collection=findViewById(R.id.send_collectionpage);
        clearBtn_collection=findViewById(R.id.clear_collectionpage);

        MAC1_collection=findViewById(R.id.mac1_collectionpage);
        MAC2_collection=findViewById(R.id.mac2_collectionpage);
        MAC3_collection=findViewById(R.id.mac3_collectionpage);
        Size_collection=findViewById(R.id.size_collectionpage);
        Area_collection=findViewById(R.id.area_collectionpage);

        int targetSize=Integer.parseInt(Size_collection.getText().toString());
        BluetoothHelper btHelper=new BluetoothHelper(this,this,targetSize);
        CSVHelper CSVhelper=new CSVHelper(this);
        SettingHelper sethelper=new SettingHelper(MAC1_collection,MAC2_collection,MAC3_collection,Size_collection);

        btHelper.bleCall();
        btHelper.bleCheck(bluetoothA);


        ListView listView = findViewById(R.id.listview);
        adapter_collection = new LeDeviceListAdapter(this);
        listView.setAdapter(adapter_collection);

        startBtn_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sethelper.preparingScan();
                List<ScanFilter> scanFilters = new ArrayList<>();
                // Add your custom scan filters here
                ScanFilter scanFilter1 = new ScanFilter.Builder().setDeviceAddress(sethelper.mac1).build();
                scanFilters.add(scanFilter1);

                ScanFilter scanFilter2 = new ScanFilter.Builder().setDeviceAddress(sethelper.mac2).build();
                scanFilters.add(scanFilter2);

                ScanFilter scanFilter3 = new ScanFilter.Builder().setDeviceAddress(sethelper.mac3).build();
                scanFilters.add(scanFilter3);

                btHelper.startScan(scanFilters);
                adapter_collection.notifyDataSetChanged();


            }
        });
        stopBtn_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btHelper.stopScan();
            }
        });
        backBtn_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        setBtn_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sethelper.firstSetting();
                sethelper.preparingScan();

            }
        });
        clearBtn_collection.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                MAC1_collection.setText("");
                MAC2_collection.setText("");
                MAC3_collection.setText("");
                Size_collection.setText("");

            }
        });

    }
}