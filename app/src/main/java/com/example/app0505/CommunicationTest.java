package com.example.app0505;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;

public class CommunicationTest extends AppCompatActivity {
    private Button SendBtn;
    private static final int FILE_PICK_REQUEST_CODE = 1;

    private static final String NETWORK_TWK = "NETWORK_TWK";
    private String uploadFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        SendBtn=findViewById(R.id.testBtn);

        //TODO: 파일 선택 화면 구현 -> 파일 경로 얻어오기
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*"); // 모든 파일 타입
        startActivityForResult(intent, FILE_PICK_REQUEST_CODE);

        SendBtn.setOnClickListener(view -> {

            //TODO: 1. AsynkTask 클래스 활용하기 2. 네트워크 기능 함수로 분리하기
            //네트워크 사용시 스레드를 이용해야 함
            new Thread(() -> {
                HttpURLConnection conn = null;
                try {
                    /* URL 접속, 요청, 헤더 타입과 같은 http 기능 */
                    URL url = new URL("http://192.168.0.20:38283/File/");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST"); //POST 방식 사용 시 App의 views.py에 @csrf_exemf 추가해야 함. GET방식은 별도 추가 x
                    conn.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/text");
//                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.101");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();

                    /* 연결 후 응답 결과 처리 */
                    int resultCode = conn.getResponseCode();
                    if(resultCode == HttpURLConnection.HTTP_OK) {
                        String str = null;
                        InputStreamReader reader = new InputStreamReader(conn.getInputStream(), "UTF-8");

                        /* 수신할 최대 크기 만큼 서버 접속 결과 (텍스트) 읽어오기 */
                        int maxLength = 10000;
                        int numChars = 0;
                        int readSize = 0;
                        char[] buffer = new char[maxLength];
                        while(numChars < maxLength && readSize != -1){
                            numChars += readSize;
                            readSize = reader.read(buffer, numChars, buffer.length - numChars); //버퍼에 정해진 크기만큼 결과를 채움
                        }
                        if(numChars != -1) {
                            numChars = Math.min(numChars, maxLength);
                            str = new String(buffer, 0, numChars);

                            //디버깅을 위해 결과값을 저장
                            String finalStr = str;
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "응답:" + finalStr, Toast.LENGTH_SHORT).show());

                            Log.d(NETWORK_TWK, "Response: \n" + str);
                        }
                    }
                    else{
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "접속오류:" + resultCode, Toast.LENGTH_SHORT).show());
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                    Log.e( "Error: ","Error message: \n" + e.getLocalizedMessage());
                }
                finally {
                    conn.disconnect();
                }
            }).start();
        });
    }

    // onActivityResult에서 선택한 파일 처리
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_PICK_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri selectedFileUri = data.getData();
            String filePath = getRealPathFromUri(selectedFileUri); // 선택한 파일의 실제 경로 얻기

            if (filePath != null) {
                // 파일 업로드 실행
//                uploadFileToServer(filePath);
            }
        }
    }

    // 파일의 실제 경로 얻는 메서드 예시
    private String getRealPathFromUri(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, projection, null, null, null);

        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }

        return null;
    }


    // 파일 업로드 예시
    private void uploadFileToServer() {

    }
}
