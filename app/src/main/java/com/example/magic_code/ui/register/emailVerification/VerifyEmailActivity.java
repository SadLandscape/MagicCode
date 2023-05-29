package com.example.magic_code.ui.register.emailVerification;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.magic_code.R;
import com.example.magic_code.api.API;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class VerifyEmailActivity extends AppCompatActivity {
    String email;
    TextView status_text;
    TextView countdown_expiry;
    private boolean isAlertDialogShowing = false;
    Button btnResend;
    Boolean timer1Finished = false;
    Boolean timer2Finished = false;
    TextView countdown_reset;
    List<EditText> editTexts;
    private String password;
    private String username;
    private ClipboardManager clipBoard;

    public String convertMillisecondsToTime(long milliseconds) {
        long minutes = (milliseconds / 1000) / 60;
        long seconds = (milliseconds / 1000) % 60;

        String formattedTime = String.format("%02d:%02d", minutes, seconds);
        return formattedTime;
    }
    public String getAuthCode() {
        StringBuilder emailCode = new StringBuilder();
        for (EditText ed: editTexts) {
            emailCode.append(ed.getText());
        }
        return emailCode.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        Bundle args = getIntent().getExtras();
        email = args.getString("email");
        password = args.getString("password");
        username = args.getString("username");
        status_text = findViewById(R.id.tv_email);
        status_text.setText(status_text.getText().toString().replace("\"email\"",email));
        countdown_expiry = findViewById(R.id.countdown_expire);
        countdown_reset = findViewById(R.id.countdown_reset);
        btnResend = findViewById(R.id.btn_resend);
        clipBoard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        editTexts = new ArrayList<EditText>(){{
            add(findViewById(R.id.et_code1));
            add(findViewById(R.id.et_code2));
            add(findViewById(R.id.et_code3));
            add(findViewById(R.id.et_code4));
            add(findViewById(R.id.et_code5));
            add(findViewById(R.id.et_code6));
        }};
        for (int i = 0; i < editTexts.size()-1; i++) {
            EditText editText = editTexts.get(i);
            int finalI = i;
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!editText.getText().toString().isEmpty()) {
                        editTexts.get(finalI + 1).requestFocus();
                        return;
                    }
                    if (finalI!=0) {
                        editTexts.get(finalI - 1).requestFocus();
                    }
                }
            });
            editText.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    editText.setSelection(editText.getText().length());
                }
            });

        }
        clipBoard.addPrimaryClipChangedListener(()->{
            ClipData clipData = clipBoard.getPrimaryClip();
            if (clipData == null){return;}
            ClipData.Item item = clipData.getItemAt(0);
            if (item == null){return;}
            String clipboard = item.getText().toString();
            if (!(clipboard.length() == 6 && NumberUtils.isDigits(clipboard))){return;}
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Attention");
            builder.setMessage("An OTP (6 digit) code was found in your clipboard, would you like to paste it?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                for (int i = 0; i < clipboard.length(); i++) {
                    editTexts.get(i).setText(String.valueOf(clipboard.charAt(i)));
                }
            });
            builder.setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        EditText lastEdit = editTexts.get(5);
        lastEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (lastEdit.getText().toString().isEmpty()) {
                    editTexts.get(4).requestFocus();
                    return;
                }
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(lastEdit.getWindowToken(), 0);
                lastEdit.clearFocus();
                if (getAuthCode().length()!=6){
                    return;
                }
                new Thread(()->{
                    String authToken = API.Authentication.checkEmailCode(getAuthCode(),email,VerifyEmailActivity.this);
                    runOnUiThread(()->{
                        if (authToken==null){
                            return;
                        }
                        Intent intent = new Intent();
                        intent.putExtra("authToken",authToken);
                        setResult(Activity.RESULT_OK,intent);
                        finish();
                    });
                }).start();
            }
        });
        lastEdit.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                lastEdit.setSelection(lastEdit.getText().length());
            }
        });

        CountDownTimer timer1 = new CountDownTimer(300000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer1Finished = false;
                countdown_expiry.setText("Expires in: " + convertMillisecondsToTime(millisUntilFinished));
            }

            public void onFinish() {
                timer1Finished = true;
                countdown_expiry.setError("");
                countdown_expiry.setText("Code expired, please request another code!");
            }

        }.start();
        CountDownTimer timer2 = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer2Finished = false;
                countdown_reset.setText("Resend in: " + convertMillisecondsToTime(millisUntilFinished));
            }

            public void onFinish() {
                timer2Finished = true;
                btnResend.setEnabled(true);
            }

        }.start();
        btnResend.setOnClickListener(view->{
            new Thread(()->{
                boolean status = API.Authentication.requestNewCode(email,password,username,this);
                runOnUiThread(()->{
                    if (status){
                        countdown_expiry.setError(null);
                        view.setEnabled(false);
                        if (!timer1Finished) {
                            timer1.cancel();
                        }
                        timer1.start();
                        if (!timer2Finished) {
                            timer2.cancel();
                        }
                        timer2.start();
                    }
                });
            }).start();
        });
        findViewById(R.id.wrong_email_link).setOnClickListener(v->{
            finish();
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && !isAlertDialogShowing) {
            ClipData clipData = clipBoard.getPrimaryClip();
            if (clipData == null){return;}
            ClipData.Item item = clipData.getItemAt(0);
            if (item == null){return;}
            String clipboard = item.getText().toString();
            if (!(clipboard.length() == 6 && NumberUtils.isDigits(clipboard))){return;}
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Attention");
            builder.setMessage("An OTP (6-digit) code was found in your clipboard. Would you like to paste it?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                for (int i = 0; i < clipboard.length(); i++) {
                    editTexts.get(i).setText(String.valueOf(clipboard.charAt(i)));
                }
            });
            builder.setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.setOnDismissListener(dialogInterface -> {
                isAlertDialogShowing = true;
            });
            dialog.show();
            return;
        }
        if (isAlertDialogShowing){
            isAlertDialogShowing = false;
        }
    }

    @Override
    public void onBackPressed() {}
}