package com.example.app0505;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

//TODO:FileDTO를 만들어 깔끔하게 다듬어보기
//수집한 RSSI를 CSV 형태로 변환해준다.
public class CSVHelper {
    private Context context;
    //context
    private int area_CSV;
    //영역번호를 받기 위한 변수 area_CSV
    private String csvFileName;

    //CSV의 생성자
    public CSVHelper(Context context) {
        this.context = context;
    }

    //CSV파일로 변환 후 저장을 함
    //rssi의 결과를 담은 ArrayList 1,2,3, countsize_CSV, area를 인자로 받는다
    public void saveDataToCSV(ArrayList<Integer> Result_CSV1, ArrayList<Integer> Result_CSV2, ArrayList<Integer> Result_CSV3, int countsize_CSV, String areaStr_CSV) {
        //파일 클래스의 폴더 객체 생성
        File directory = new File(context.getExternalFilesDir(null) + context.getPackageName());
        // 폴더가 존재하지 않으면 생성
        if (!directory.exists()) {
            directory.mkdirs();
        }

        //area가 없으면 반환함
        if (areaStr_CSV == null || areaStr_CSV.isEmpty()) {
            Toast.makeText(context, "영역 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        //액티비티에서 입력받은 Area를 정수로 변환하여 정수형 area에 저장
        area_CSV = Integer.parseInt(areaStr_CSV);

        //파일 이름을 area_CSV, countsize로 저장
        csvFileName = area_CSV + "_" + countsize_CSV + ".csv";

        //위에서 생성한 폴더 객체, 생성한 파일명을 인자로 받아 파일 객체 생성
        File file = new File(directory, csvFileName);
        //저장한 ArrayList의 인덱스만큼 결과를 추출해 a,b,c 형태로 변환하여 저장
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < countsize_CSV; i++) {
                int b1, b2, b3;
                b1 = Result_CSV1.get(i);
                b2 = Result_CSV2.get(i);
                b3 = Result_CSV3.get(i);
                String str = (b1 + "," + b2 + "," + b3 + "\n");
                writer.write(str);
            }
            Toast.makeText(context, "데이터가 성공적으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
            Log.d("CSV_SAVE", "CSV 파일이 성공적으로 저장되었습니다. 경로: " + file.getAbsolutePath());
        } catch (IOException e) {
            Toast.makeText(context, "데이터 저장 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            Log.e("CSV_SAVE", "CSV 파일 저장 중 오류가 발생했습니다.");
        }
    }
}
