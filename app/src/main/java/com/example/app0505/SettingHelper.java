package com.example.app0505;

import android.bluetooth.le.ScanFilter;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class SettingHelper {
    String mac1,mac2,mac3;
    private int countsize;
    EditText editMac1,editMac2,editMac3,editCount;

    public SettingHelper(EditText editMac1, EditText editMac2, EditText editMac3, EditText count) {
        this.editMac1 = editMac1;
        this.editMac2 = editMac2;
        this.editMac3 = editMac3;
        this.editCount = count;

    }
    public SettingHelper(int countsize)
    {
        this.countsize=countsize;
        //생성자에서 다시 0으로 초기화되버림)
        this.setCountsize(countsize);
    }

    public void setCountsize(int countsize)
    {
        //(1)setcountsize:10 저장
        this.countsize=countsize;
    }

    public int getCountSize()
    {
        //countsize가 0을 리턴함
        return countsize;
    }

    public void convertType()
    {
        mac1 = editMac1.getText().toString();
        mac2 = editMac2.getText().toString();
        mac3 = editMac3.getText().toString();
        countsize = Integer.parseInt(editCount.getText().toString());
        setCountsize(countsize);
    }

    //초기 세팅 함수. 실험을 위해 준비된 비콘의 MAC주소, 카운트 갯수를 미리 지정한다.
    public void firstSetting()
    {
        //입력받은 MAC주소 가져오기
        editMac1.setText("D0:39:72:A4:B3:95");
        editMac2.setText("D0:39:72:A4:B4:A9");
        editMac3.setText("D0:39:72:A4:9E:AB");
        editCount.setText("10");
    }

    public void emptySetting(){
        editMac1.setText("");
        editMac2.setText("");
        editMac3.setText("");
        editCount.setText("");
    }

    /*public ArrayList<ScanFilter> SetScanFilters(String mac1, String mac2, String mac3){
        List<ScanFilter> scanFilters = new ArrayList<>();
        ScanFilter scanFilter1=new ScanFilter.Builder().setDeviceAddress(mac1).build();
        ScanFilter scanFilter2=new ScanFilter.Builder().setDeviceAddress(mac2).build();
        ScanFilter scanFilter3=new ScanFilter.Builder().setDeviceAddress(mac3).build();
        return (ArrayList<ScanFilter>) scanFilters;
    }*/
}
