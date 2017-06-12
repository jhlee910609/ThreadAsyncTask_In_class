# [170612] AsyncTask

### 1. AsyncTask

##### 1.1. AsyncTask 개념

- 재활용이 가능한 Thread 객체와 달리 재활용이 불가한 AsnycTask 객체
- 추상 클래스이기 때문에 메소드를 implements 하여 로직을 작성
- 한정적인 작업(예를 들면 A작업을 10 번 수행한다.) 에 주로 사용되며, 특히 네트워크를 통해 서버의 데이터를 가져올 때 자주 쓰는 객체임
- Thread, Handler, Message 등을 모두 구현해야 하는 Thread 객체와 달리 코드 구현 관점에서는 간단한 편임

![AsyncTask 설명](http://cfile26.uf.tistory.com/image/2711E13A57A92E010EFA22)

##### 1.2. AsyncTask 구현

```java
new AsyncTask<Params, Progress, Result>(){
  // ======== [Generic 위치에 따른 인자타입] ========
  // Params = doInBackGround(); 의 인자타입
  // Progress = onProgressUpdate(); 의 인자타입
  // Result = onPostExecute(); 의 인자타입
  
  // main thread(ui thread)에서 실행 -> main thread에 직접 acceess하여 main ui 조작
  // 계산을 위한 초기화나 프로그래스 대화상자를 준비하는 등의 작업 수행
   @Override
   protected void onPreExecute() {
   		super.onPreExecute();
   }
  
  // Thread의 run(); 메소드와 같은 역할을 하는 메소드
   @Override
   protected Params doInBackground(Params... params) {
    	super.doInBackground();
     	return null;
   }   
  
  // 백그라운드 작업을 마친 후, ui thread에서 실행
  // 인수로 작업의 결과가 전달되는데 취소되었거나 예외가 발생했으면 null이 전달됨 
   @Override
   protected Result onPostExecute(Result aVoid) {
                // 결과값을 메인 UI에 셋팅하는 로직을 여기에 작성하면 됨
   }
  
   // doInBackground에서 publishProgress 메소드를 호출할 때 작업 경과 표시를 위해 호출되며 ui thread에서 실행된다. 프로그  래스바에 진행 상태를 표시하는 역할을 한다. 얼마나 자주 호출될 것인가는 정의되어 있지 않으므로 매경과마다 호출된다고 보장할 수 없다. 
  @Override
   protected Progress onProgressUpdate(Progress... values) {

   }
  }.execute(); // <- doInBackGround를 실행 // 배열처럼 안에 인자들을 꺼내 사용할 수 있음
}
```

-  Generic 관련 보충 이미지 

![제네릭 관련 보충 이미지](http://cfile22.uf.tistory.com/image/143C23054B987FDDB74F04)

![각 메소드 작업 thread](https://camo.githubusercontent.com/236817eb3e63795158deb6e20f66a3e74b65f4ac/687474703a2f2f7777772e6c75636164656e74656c6c612e69742f626c6f672f77702d636f6e74656e742f75706c6f6164732f323031342f30352f6173796e637461736b2e6a7067)

##### 1.3. AsyncTask와 주로 쓰는 객체

###### 1.3.1. ProgressDialog 

```java
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		//=======[ ProgressDialog 객체는 화면에 진행상태를 표시해주는 객체 ]=======
        progress = new ProgressDialog(this);
        // ==== [ 기본적으로 셋팅해줘야 하는 것들 ] ====
        progress.setTitle("진행 중 입니다...");
        progress.setMessage("잠시만 기다려주세요.");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		....
}

 private void runAsync() {
        new AsyncTask<String, Integer, Float>() {
          ...
            @Override
            protected Float doInBackground(String... params) { // 데이터 처리 작업까지만 담당
                // 10초 후에
                try {
                    for (int i = 0; i < 10; i++) {
                      	// publishProgress(); = progress 실행 메소드
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
          
```

######  