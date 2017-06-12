package com.junhee.android.threadasynctask;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView textView;
    public static final int SET_DONE = 1;
    // AsyncTask를 여러 번 사용할 경우 전역으로 빼서 쓴다
    // TODO AsyncTask를 한 번만 쓸 경우, AsynTask 로직 안에다가 넣어준다
    ProgressDialog progress;

    // thread에서 호출하기 위한 핸들러
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SET_DONE:
                    setDone();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAsync();
            }
        });
        //ProgressDialog 객체는 화면에 진행상태를 표시해주는 객체
        progress = new ProgressDialog(this);
        // ==== [ 기본적으로 셋팅해줘야 하는 것들 ] ====
        progress.setTitle("진행 중 입니다...");
        progress.setMessage("잠시만 기다려주세요.");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    }


    private void setDone() {
        textView.setText("DONE!!!");
        // progress 창을 해제
        progress.dismiss();
    }

    // ========= [ AsyncTask ] ==========
    // * 한정적인 작업 (예를 들면 10번 한다..)에만 사용할 수 있음
    // * 주로 네트워크를 통해 서버의 데이터를 가져올 때, 주로 사용하는 객체.

    private void runAsync() {

        new AsyncTask<String, Integer, Float>() {
            // Generic type 1 = doInBackGround(); 의 인자
            // Generic type 2 = onProgressUpdate(); 의 인자
            // Generic type 3 = onPostExecute(); 의 인자

            // Main Thread에서 실행 -> 메인에 직접 access해서 main ui 조작
            // doInBackGround가 호출되기 전에 먼저 호출됨
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progress.show();
            }

            // 인자 타입 변화
            //                             // '...' -> 길이를 모르는 배열을 의미
            // Thread의 run()과 같은 메소드
            @Override
            protected Float doInBackground(String... params) { // 데이터 처리 작업까지만 담당
                // 10초 후에
                try {
                    for (int i = 0; i < 10; i++) {
                        publishProgress(i * 10); // <- onProgressUpdate를 주기적으로 업데이트 해줌
                        Thread.sleep(10000);

                    }
                    // Main UI에 현재 thread가 접근할 수 없으므로
                    // handler를 통해 호출해준다
                    // 굳이 Message 객체를 만들어서 보내줄 필요가 없음
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null; // <- 의 리턴값 onPostExecute(인자값) 과 동일
            }

            // Main Thread에서 실행 -> 메인에 직접 access해서 main ui 조작
            // 위에서 return null;을 할 경우 onPostExecute(); 메소드가 호출됨
            // doInBackGround가 호출된 후, 호출됨
            @Override
            protected void onPostExecute(Float aVoid) {
                Log.e("AsyncTask", "======================== [ 결과값 ]");
                // 결과값을 메인 UI에 셋팅하는 로직을 여기에 작성하면 됨
                setDone();
                progress.dismiss();
            }

            // 어싱크타스크 두번 째 인자 값 설정 시 (void)가 아닐 시
            // 주기적으로 doInBackGround에서 호출이 가능한 함수
            // 프로그레스바에 % 채울 때
            @Override
            protected void onProgressUpdate(Integer... values) {
                progress.setMessage("진행율 -" + values[0] + "%");

            }
        }.execute("안녕", "하세요"); // <- doInBackGround를 실행 // 배열처럼 안에 인자들을 꺼내 사용할 수 있음
    }

    private void runThread() {
        // progress 창을 띄워줌
        progress.show();
        CustomThread thread = new CustomThread(handler);
        thread.start();
        // 10초 후에
        // 10초
        // 바로 실행시켜줌 0.1초 내에 바로 됨
        // 이 코드 run() 함수 안에 들어가야함
        // textView.setText("DONE!!!");
    }
}

class CustomThread extends Thread {
    Handler handler;

    public CustomThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(10000);
            // Main UI에 현재 thread가 접근할 수 없으므로
            // handler를 통해 호출해준다
            // 굳이 Message 객체를 만들어서 보내줄 필요가 없음
            handler.sendEmptyMessage(MainActivity.SET_DONE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

