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
public class CSVHelper {
    private Context context;

    public CSVHelper(Context context) {
        this.context = context;
    }

    public void saveDataToCSV(ArrayList<Integer> beacon1, ArrayList<Integer> beacon2, ArrayList<Integer> beacon3, int countsize, String areaStr) {
        File directory = new File(context.getExternalFilesDir(null) + context.getPackageName());
        if (!directory.exists()) {
            directory.mkdirs(); // 폴더가 존재하지 않으면 생성
        }

        if (areaStr == null || areaStr.isEmpty()) {
            Toast.makeText(context, "영역 번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        int area = Integer.parseInt(areaStr);

        String csvFileName = area + "_" + countsize + ".csv"; // 파일 이름 설정
        File file = new File(directory, csvFileName);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < countsize; i++) {
                int b1, b2, b3;
                b1 = beacon1.get(i);
                b2 = beacon2.get(i);
                b3 = beacon3.get(i);
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
