package com.mtmt_timing.team_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TimerActivity extends AppCompatActivity {      // AppCompatActivity 상속

    LinearLayout timeCountSettingLV;    //화면을 지정된 타이머 화면으로 배치할 변수 선언
    EditText hourET, minuteET, secondET;        //시간, 분, 초의 변수 선언
    Button startBtn, btn_alarm, stopBtn;        //시작,알람,정지 버튼의 변수 선언
    int hour = 0, minute = 0, second = 0;   //시간의 초기값, 분의 초기값, 초의 초기값
    MediaPlayer mediaPlayer;        //소리가 나게할 변수 선언
    Boolean useTimer = false;       //초기 알람 사용 여부 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {        //화면 복원 시 필요한 정보를 지닌 메소드
        super.onCreate(savedInstanceState);             //부모클래스 참조
        setContentView(R.layout.activity_timer);        //xml에 선언 된 알람 이미지로 구성
        btn_alarm = findViewById(R.id.btn_alarm);       //xml에 선언 된 알람 이미지 호출
        timeCountSettingLV = (LinearLayout) findViewById(R.id.timeCountSettingLV);      //xml에 선언 된 타이머 화면 이미지 호출
        hourET = (EditText) findViewById(R.id.hourET);      //xml에 선언 된 시간 이미지 호출
        minuteET = (EditText) findViewById(R.id.minuteET);      //xml에 선언 된 분 이미지 호출
        secondET = (EditText) findViewById(R.id.secondET);      //xml에 선언 된 초 이미지 호출
        startBtn = (Button) findViewById(R.id.startBtn);        //xml에 선언 된 시작 버튼 이미지 호출
        stopBtn = (Button) findViewById(R.id.stopBtn);          //xml에 선언 된 멈춤 버튼 호출

        changeActivity();       //화면 전환
        timerStart();       //타미어 시작
        timerStop();        //타이머 정지

    }


    void timerStart() {     //타이머 시작 메소드
        startBtn.setOnClickListener(new View.OnClickListener() {        //시작 버튼 클릭시 이벤트 발생
            @Override
            public void onClick(View view) {

                try {
                    hour = Integer.parseInt(hourET.getText().toString());       //화면의 시(hour) 값 가져오기
                }catch (Exception e){}
                try {
                    minute = Integer.parseInt(minuteET.getText().toString());       //화면의 분(minute) 값 가져오기
                }catch (Exception e){}
                try {
                    second = Integer.parseInt(secondET.getText().toString());       //화면의 초(second) 값 가져오기
                }catch (Exception e){}  //예외 처리 해결방법 기재

                handler.sendEmptyMessage(0);    //예외 시 what 값은 0
            }

        });
    }

    void timerStop() {      //타이머 정지 메소드
        stopBtn.setOnClickListener(new View.OnClickListener() {     //정지 버튼  클릭 시 이벤트 발생
            @Override
            public void onClick(View view) {
                handler.removeMessages(0); //핸들러의 작동 정지
                if(hour == 0) {
                    hourET.setText("");
                } else if (minute == 0) {
                    minuteET.setText("");
                } else if (second == 0) {
                    secondET.setText("");
                }
                mediaPlayer.stop();     //노래 작동 정지
                mediaPlayer.release();  //노래 작동 정지까지 시간
                useTimer = false;       //타이머 사용 여부 거짓으로 설정
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {       //타이머 동작 메소드
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);   //상속
            if(second != 0) {
                //1초씩 감소
                second--;

                // 0분 이상이면
            } else if(minute != 0) {
                // 1분 = 60초
                second = 60;
                second--;
                minute--;

                // 0시간 이상이면
            } else if(hour != 0) {
                // 1시간 = 60분
                second = 60;
                minute = 60;
                second--;
                minute--;
                hour--;
            }

            //시, 분, 초가 10이하(한자리수) 라면
            // 숫자 앞에 0을 붙인다 ( 8 -> 08 )
            if(second <= 9){
                secondET.setText("0" + second);
            } else {
                secondET.setText(Integer.toString(second));
            }

            if(minute <= 9){
                minuteET.setText("0" + minute);
            } else {
                minuteET.setText(Integer.toString(minute));
            }

            if(hour <= 9){
                hourET.setText("0" + hour);
            } else {
                hourET.setText(Integer.toString(hour));
            }

//             시분초가 다 0이라면 toast를 띄우고 타이머를 종료한다.
            if(hour == 0 && minute == 0 && second == 0 && useTimer == false) {
                hourET.setText("");     //시 값 공백
                minuteET.setText("");   //분 값 공백
                secondET.setText("");   //초 값 공백
                mediaPlayer = MediaPlayer.create(TimerActivity.this, R.raw.timer_sound);        //타이머의 알림음 발생
                mediaPlayer.start();        //노래 시작
                Vibrator vibrator = (Vibrator) getSystemService(TimerActivity.this.VIBRATOR_SERVICE);       //핸드폰의 진동 발생 서비스 작동
                vibrator.vibrate(500);      //진동시간 0.5초
                useTimer = true;        //타이머 사용 여부 참
            }
            handler.sendEmptyMessageDelayed(0, 1000);    //타이머에 따라 위의 과정을 반복함(1초의 지연 시간 후)
        }
    };

    @Override
    protected void onDestroy() {        //onCreat에서 사용했던 리소스 제거
        super.onDestroy();
        try {
            handler.removeMessages(0);
        }catch (Exception e){}  //예외 처리 해결방법 기재
    }

    void changeActivity() {         //화면전환 메소드
        btn_alarm.setOnClickListener(new View.OnClickListener() {       //알람 전환 버튼
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimerActivity.this, MainActivity.class);     //타이머를 켜기위한 인텐트 호출 과정
                startActivity(intent);  //화면 호출
                finish();       //종료
            }
        });
    }
}
