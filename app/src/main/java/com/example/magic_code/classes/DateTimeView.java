package com.example.magic_code.classes;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.magic_code.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeView extends ConstraintLayout {
    private TextView textViewDate;
    private ImageView imageViewClock;

    public DateTimeView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public DateTimeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DateTimeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_date_time, this, true);

        textViewDate = findViewById(R.id.textViewDate);
        imageViewClock = findViewById(R.id.imageViewClock);
        setDate(0);
    }

    public void setDate(long utcTimestamp) {
        Date utcDate = new Date(utcTimestamp * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy h:mm a", Locale.getDefault());
        DateFormatSymbols symbols = new DateFormatSymbols(Locale.getDefault());
        symbols.setAmPmStrings(new String[] { "AM", "PM" });
        sdf.setDateFormatSymbols(symbols);
        sdf.setTimeZone(TimeZone.getDefault());

        String formattedDate = sdf.format(utcDate);
        textViewDate.setText(formattedDate);
    }
}
