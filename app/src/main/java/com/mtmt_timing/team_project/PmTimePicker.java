package com.mtmt_timing.team_project;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;

public class PmTimePicker extends TimePicker   //TimePicker 상속
{


    private final int m_iColor = 0xFFA561BD;  // 시간 선택 시 밑줄에 색깔을 두어 구분하기 위한 라인 색상 지정

    //PmTimePicker 만들기(override)
    public PmTimePicker( Context context )  //PmTimePicker 생성자
    {
        super( context );   //부모클래스 참조
        Create( context, null );    //메소드 생성
    }



    public PmTimePicker( Context context, AttributeSet attrs )  //XML에서 만들어진 PmTimePicker를 호출하는 생성자
    {
        super( context, attrs );    //부모클래스 참조
        Create( context, attrs );   //메소드 생성
    }



    public PmTimePicker(Context context, AttributeSet attrs, int defStyle )  //PmTimePicker를 상속받아 확장하기 위한 생성자
    {
        super( context, attrs, defStyle );  //부모클래스 참조
        Create( context, attrs );   //메소드 생성
    }



    private void Create( Context clsContext, AttributeSet attrs )
    {
        try
        {
            Class<?> clsParent = Class.forName( "com.android.internal.R$id" );
            NumberPicker clsAmPm = (NumberPicker)findViewById( clsParent.getField( "amPm" ).getInt( null ) );       //오전,오후 선택
            NumberPicker clsHour = (NumberPicker)findViewById( clsParent.getField( "hour" ).getInt( null ) );       //시(hour) 숫자 선택
            NumberPicker clsMin = (NumberPicker)findViewById( clsParent.getField( "minute" ).getInt( null ) );      //분(minute) 숫자 선택
            Class<?> clsNumberPicker = Class.forName( "android.widget.NumberPicker" );      //안드로이드 스튜디오의 numberpicker 참조
            Field clsSelectionDivider = clsNumberPicker.getDeclaredField( "mSelectionDivider" );        //색상구분 필드 선언

            clsSelectionDivider.setAccessible( true );      //
            ColorDrawable clsDrawable = new ColorDrawable( m_iColor );      // 구분하는 색상 보라색으로 지정



            // 오전/오후, 시, 분 구분 라인의 색상을 변경한다.
            clsSelectionDivider.set( clsAmPm, clsDrawable );    // 오전,오후 선택 시 밑줄에 색깔을 두어 구분하기 위한 라인 색상 지정
            clsSelectionDivider.set( clsHour, clsDrawable );    // 시(hour)선택 시 밑줄에 색깔을 두어 구분하기 위한 라인 색상 지정
            clsSelectionDivider.set( clsMin, clsDrawable );     // 분(minute) 선택 시 밑줄에 색깔을 두어 구분하기 위한 라인 색상 지정
        }
        catch( Exception e )   //예외 처리 해결방법 기재
        {
            e.printStackTrace( );  //에러의 발생 근원지를 찾아 단계별로 에러 출력
        }
    }
}

