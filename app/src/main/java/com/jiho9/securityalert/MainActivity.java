package com.jiho9.securityalert;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private OkHttpClient mClient;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // 텍스트뷰 할당
        mTextView = findViewById(R.id.textViewTest);
        // 네트워크 통신 객체 생성
        mClient = new OkHttpClient();

        connect();
    }

    /**
     * 웹 소켓 통신 기능
     */
    private void connect() {
        // json 파서 생성
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        // 웹 소켓 연결 설정
        Request request = new Request.Builder().url("ws://172.31.22.94:8765").build();
        // 웹 소켓 연결
        WebSocket ws = mClient.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                Log.d(TAG, "onClosed: ");
            }

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                Log.d(TAG, "onOpen: ");
            }

            @Override
            public void onMessage(WebSocket webSocket, final String text) {
                super.onMessage(webSocket, text);
                /*
                메세지 수신 시
                 */
                Log.d(TAG, "onMessage: " + text);
                // UI 스레드에서
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String result = gson.fromJson(text, Map.class).toString();
                        Log.d(TAG, "run: " + result);
                        // 수신한 텍스트 표시
                        mTextView.setText(result);
                    }
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);
                Log.d(TAG, "onFailure: " + t.toString());

            }
        });
        mClient.dispatcher().executorService().shutdown();
    }

}
