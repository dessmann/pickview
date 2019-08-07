package com.pickerview.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pickerview.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ColorScreenTimePickerView extends TimePickerView {

    private final WheelTime wheelTime2;
    private TimePickerView.OnTimeSelectListener timeSelectListener2;
    private TextView tvHelpInfo;

    public ColorScreenTimePickerView(Context context, TimePickerView.Type type) {
        super(context, type);

        contentContainer.removeAllViews();
        LayoutInflater.from(context).inflate(R.layout.pickerview_time_extend, this.contentContainer);
        findViewById(R.id.btnSubmit).setOnClickListener(this);
        findViewById(R.id.btnCancel).setOnClickListener(this);

        findViewById(R.id.llHelpInfo).setOnClickListener(this);
        tvHelpInfo = (TextView) findViewById(R.id.tvHelpInfo);

        ImageView ivHelp = (ImageView)findViewById(R.id.ivSystemHelp);
        ivHelp.setOnClickListener(this);

        View timepickerview = findViewById(R.id.timepicker);
        this.wheelTime2 = new WheelTime(timepickerview, type);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime2.setPicker(year, month, day, hours, minute);
    }

    @Override
    public void setRange(int startYear, int endYear) {
        this.wheelTime2.setStartYear(startYear);
        this.wheelTime2.setEndYear(endYear);
    }

    @Override
    public void setTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date == null) {
            calendar.setTimeInMillis(System.currentTimeMillis());
        } else {
            calendar.setTime(date);
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        wheelTime2.setPicker(year, month, day, hours, minute);
    }

    @Override
    public void setCyclic(boolean cyclic) {
        wheelTime2.setCyclic(cyclic);
    }

    @Override
    public void setOnTimeSelectListener(TimePickerView.OnTimeSelectListener timeSelectListener) {
        this.timeSelectListener2 = timeSelectListener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivSystemHelp) {
            tvHelpInfo.setVisibility(tvHelpInfo.getVisibility() == View.VISIBLE? View.INVISIBLE : View.VISIBLE);
        } else if (v.getId() == R.id.btnCancel || v.getId() == R.id.llHelpInfo) {
            dismiss();
        } else if (v.getId() == R.id.btnSubmit) {
            if (timeSelectListener2 != null) {
                try {
                    Date date = (new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())).parse(wheelTime2.getTime());
                    timeSelectListener2.onTimeSelect(date);
                } catch (ParseException var4) {
                    var4.printStackTrace();
                }
                dismiss();
            }
        }
    }
}
