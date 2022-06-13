package com.mtmt_timing.team_project;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class DeviceBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.intent.action.BOOT_COMPLETED")) {

            // on device boot complete, reset the alarm
            Intent alarmIntent = new Intent(context, AlarmReceiver.class); // AlarmReceiver클래스 화면으로 넘어감
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0); //alarmIntent가 작업할 때 pendingIntent notification 실행

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); //시스템 내의 알람 매니저 설정
//

            SharedPreferences sharedPreferences = context.getSharedPreferences("daily alarm", MODE_PRIVATE); //생성한 앱에서만 내용 저장
            long millis = sharedPreferences.getLong("nextNotifyTime", Calendar.getInstance().getTimeInMillis()); //다음 시간에 언제 알려줄 것인지 저장


            Calendar current_calendar = Calendar.getInstance();
            Calendar nextNotifyTime = new GregorianCalendar();
            nextNotifyTime.setTimeInMillis(sharedPreferences.getLong("nextNotifyTime", millis)); //다음 알람 시간 milisecond단위로 저장

            if (current_calendar.after(nextNotifyTime)) {
                nextNotifyTime.add(Calendar.DATE, 1); //저장한 시간이 이후면 내일 날짜에 저장
            }

            Date currentDateTime = nextNotifyTime.getTime();
            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context.getApplicationContext(),"[재부팅후] 다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show(); //언제 설정되었는지 사용자에게 보여줌


            if (manager != null) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, nextNotifyTime.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent); //정확한 시간에 알람 작동
            }
        }
    }
}



