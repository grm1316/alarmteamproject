package com.mtmt_timing.team_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    Button btn_timer;       //변수 재선언
    MediaPlayer mediaPlayer;        //변수 재선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {    //화면 복원 시 필요한 정보를 지닌 메소드
        super.onCreate(savedInstanceState);     //상속
        setContentView(R.layout.activity_main);     //화면 구성 메인 액티비티 호출
        btn_timer = (Button) findViewById(R.id.btn_timer);      //타이머 버튼
        changeActivity();       //화면 전환(클래스 선언)
        final TimePicker picker = (TimePicker) findViewById(R.id.tp_timepicker);        //지역 변수 상수화


        picker.setIs24HourView(true);   //24시간 형태의 시간으로 지정


        //앞서 설정한 값으로 보여주기
        //없으면 디폴트 값은 현재시간
        SharedPreferences sharedPreferences = getSharedPreferences("daily alarm", MODE_PRIVATE);
        long millis = sharedPreferences.getLong("nextNotify Time", Calendar.getInstance().getTimeInMillis());


        Calendar nextNotifyTime = new GregorianCalendar();
        nextNotifyTime.setTimeInMillis(millis);     //milli second 단위로 알람 시간 저장


        Date nextDate = nextNotifyTime.getTime();
        String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분", Locale.getDefault()).format(nextDate);
        Toast.makeText(getApplicationContext(), "[처음 실행시] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다", Toast.LENGTH_SHORT).show();      //사용자에게 언제 설정 되었는지 정보 제공


        //이전 설정값으로 TimePicker 초기화
        Date currentTime = nextNotifyTime.getTime();
        SimpleDateFormat HourFormat = new SimpleDateFormat("kk", Locale.getDefault());
        SimpleDateFormat MinuteFormat = new SimpleDateFormat("mm", Locale.getDefault());


        int pre_hour = Integer.parseInt(HourFormat.format(currentTime));        //현재 시간 불러오기
        int pre_minute = Integer.parseInt(MinuteFormat.format(currentTime));    //현재 분 불러오기

        //sdk 버전 23 이상일 경우
        if (Build.VERSION.SDK_INT >= 23) {
            picker.setHour(pre_hour);       //현재 시간 설정
            picker.setMinute(pre_minute);       //현재 분 설정
        } else {        //sdk 버전 23 미만일 경우
            picker.setCurrentHour(pre_hour);        //현재 시간 설정
            picker.setCurrentMinute(pre_minute);       //현재 분 설정
        }

        Button quitbutton = (Button) findViewById(R.id.quitBtn);
        //종료 버튼 설정
        quitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("정말로 종료하시겠습니까?");    //종료 버튼 내용
                builder.setTitle("종료 알림창")      //종료 버튼 제목
                       .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                moveTaskToBack(true);
                                finishAndRemoveTask();
                                System.exit(0);     //YES 클릭시 데이터 삭제
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {        //취소 버튼 설정
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("종료 알림창");
                alert.show();

            }
        });



        //알람 버튼 설정
        Button button = (Button) findViewById(R.id.setBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {


                int hour, hour_24, minute;
                String am_pm;
                if (Build.VERSION.SDK_INT >= 23) {      //sdk 버전 23 이상
                    hour_24 = picker.getHour();         //현재 시간 설정
                    minute = picker.getMinute();        //현재 분 설정
                } else {                                //sdk 버전 23 미만
                    hour_24 = picker.getCurrentHour();      //현재 시간 설정
                    minute = picker.getCurrentMinute();     //현재 분 설정
                }
                //12시 이전엔 am / 이후에는 pm으로 설정
                if (hour_24 > 12) {
                    am_pm = "PM";
                    hour = hour_24 - 12;
                } else {
                    hour = hour_24;
                    am_pm = "AM";
                }

                //현재 지정된 시간으로 알람 시간 설정
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hour_24);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);


                //이미 지난 시간을 지정했다면 다음날 같은 시간으로 설정
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                }


                Date currentDateTime = calendar.getTime();
                String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
                Toast.makeText(getApplicationContext(), date_text + "으로 알람이 설정되었습니다", Toast.LENGTH_SHORT).show();


                //  Preference에 설정한 값 저장
                SharedPreferences.Editor editor = getSharedPreferences("daily alarm", MODE_PRIVATE).edit();
                editor.putLong("nextNotifyTime", (long) calendar.getTimeInMillis());
                editor.apply();


                diaryNotification(calendar);
            }


        });



    }






    private void diaryNotification(Calendar calendar) {     //매일 알람 설정 클래스
        Boolean dailyNotify = true;     //매일 알람 사용
        PackageManager pm = this.getPackageManager();       //참조한 변수 선언
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);    //DeviceBootReceiver의 컴포넌트 호출
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);    //알람 인텐트 선언
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);      //알람 인텐트 작동시 실행 getBroadcast 호출
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);     //알람 서비스 호출 지역 변수 선언


        // 사용자가 매일 알람을 허용했다면
        if (dailyNotify) {


            if (alarmManager != null) {

                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),      //정확한 시간에 알람 울리게 함
                        AlarmManager.INTERVAL_DAY, pendingIntent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {       //sdk 버전 별 알람 구현 방법(sdk23이상)
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); //비활성화 상태에서도 알람 울리게 함
                }
            }

            // 부팅 후 실행되는 리시버 사용가능하게 설정
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,     //비활성화된 액티비티 재활성화
                    PackageManager.DONT_KILL_APP);      //앱은 종료되지 않은 상태로 설정값만 바꾸게 설정

        }
//
    }

    void changeActivity() {     //화면 전환 메소드
        btn_timer.setOnClickListener(new View.OnClickListener() {       //타이머 전환 버튼
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TimerActivity.class);     ////타이머를 켜기위한 인텐트 호출 과정
                startActivity(intent);      //화면 호출
                finish();       //종료
            }
        });
    }
}
