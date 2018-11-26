package com.ndzl.scanandtilt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;

public class MainActivity extends Activity implements EMDKManager.EMDKListener{
    private BroadcastReceiver receiver;
    TextView tvBigNum ;
    TextView tvTime ;
    TextView tvResult ;
    Button bt1 ;
    Button bt2 ;
    Button bt3 ;
    Button bt4 ;
    Button bt5 ;

    Random rnd = new Random();
    static int numCasuale=0;
    static int errori =0;
    static int corretti=0;

    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

    boolean isFirstExec = true;
    boolean keybLocked = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setIntentReceiver();

        tvBigNum = (TextView)findViewById(R.id.tvBigNum);
        tvTime = (TextView)findViewById(R.id.tvTime);
        tvResult = (TextView)findViewById(R.id.tvResult);
         bt1 = (Button)findViewById(R.id.bt1);
         bt2 = (Button)findViewById(R.id.bt2);
         bt3 = (Button)findViewById(R.id.bt3);
         bt4 = (Button)findViewById(R.id.bt4);
         bt5 = (Button)findViewById(R.id.bt5);



        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                giveFeedback(1);
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                giveFeedback(2);
            }
        });
        bt3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                giveFeedback(3);
            }
        });
        bt4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                giveFeedback(4);
            }
        });
        bt5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                giveFeedback(5);
            }
        });

        EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), this);
    }

    void giveFeedback(int numtyped){
        if(keybLocked==true)
            return;
        if(numCasuale==numtyped) {
            tvBigNum.setBackgroundResource(android.R.color.holo_green_light);
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 50);
            corretti++;
        }
        else {
            tvBigNum.setBackgroundResource(android.R.color.holo_red_light);
            toneG.startTone(ToneGenerator.TONE_CDMA_ANSWER, 1000);
            errori++;
        }
        keybLocked=true;
    }

    void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.symbol.datawedge.api.RESULT_ACTION");
        filter.addAction("com.ndzl.scan");
        filter.addCategory("android.intent.category.DEFAULT");
        Intent regres = registerReceiver(receiver, filter);
    }

    void startCountdown(){
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTime.setText("TIME: "  + (millisUntilFinished / 1000));
                tvResult.setText("");
            }

            public void onFinish() {
                tvTime.setText("TIME: 0");
                showDialogResult("");
                errori=0;
                corretti=0;
            }
        }.start();
    }

    void setIntentReceiver(){

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                tvBigNum.setBackgroundResource(android.R.color.white);

                if(isFirstExec){
                    startCountdown();
                    isFirstExec=false;
                }

                String barcode_value = intent.getStringExtra("com.symbol.datawedge.data_string");
                //String barcode_type = intent.getStringExtra("com.symbol.datawedge.label_type");
                numCasuale = 1+rnd.nextInt(5);

                tvBigNum.setText(""+numCasuale);
                keybLocked=false;
            }
        };
        registerReceivers();

    }

    void showDialogResult(String message){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("ZEBRA TILT AND SCAN");
            alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        startCountdown();
                        dialog.dismiss();
                    }
                }
                );
        alertDialog.show();
    }

    // Declare a variable to store ProfileManager object
    private ProfileManager profileManager = null;

    // Declare a variable to store EMDKManager object
    private EMDKManager emdkManager = null;

    @Override
    public void onOpened(EMDKManager emdkManager) {
        this.emdkManager = emdkManager;

        profileManager = (ProfileManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.PROFILE);

        String[] modifyData = new String[1];
        EMDKResults results = profileManager.processProfile("SCANANDTILT", ProfileManager.PROFILE_FLAG.SET, modifyData);

    }

    @Override
    public void onClosed() {

    }
}


