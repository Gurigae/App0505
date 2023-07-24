package com.example.app0505;

import android.widget.EditText;

public class SettingHelper {
    public SettingHelper(EditText editMac1, EditText editMac2, EditText editMac3, EditText count) {
        this.editMac1 = editMac1;
        this.editMac2 = editMac2;
        this.editMac3 = editMac3;
        this.Count = count;
    }
    String mac1,mac2,mac3;
    int countsize;
    EditText editMac1,editMac2,editMac3,Count;
    public void preparingScan()
    {
        mac1 = editMac1.getText().toString();
        mac2 = editMac2.getText().toString();
        mac3 = editMac3.getText().toString();
        countsize = Integer.parseInt(Count.getText().toString());

//        beacon1.clear();
//        beacon2.clear();
//        beacon3.clear();
    }

    //초기 세팅 함수. 실험을 위해 준비된 비콘의 MAC주소, 카운트 갯수를 미리 지정한다.
    public void firstSetting()
    {
        //입력받은 MAC주소 가져오기
        editMac1.setText("D0:39:72:A4:B3:95");
        editMac2.setText("D0:39:72:A4:B4:A9");
        editMac3.setText("D0:39:72:A4:9E:AB");
        Count.setText("10");
    }
}
