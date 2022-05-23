package com.example.riddlemethis;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class Launcher extends AppCompatActivity {
    //public static final String KEY_USERNAME = "keyUsername";
    private TextView highscoreView;
    private int highscore = 0;
    private int newscore;
    private String username = "";
    private String newname;
    HomeWatcher mHomeWatcher;

    //Typeface tf = Typeface.createFromAsset(getAssets(), "baloo.TTF");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        highscoreView = findViewById(R.id.high_score);
        Button start = findViewById(R.id.start);
        loadHighscore();
        Intent intent = getIntent();
        newscore = intent.getIntExtra(MainActivity.EXTRA_SCORE, 0);
        newname = intent.getStringExtra(MainActivity.EXTRA_USERNAME);
        //start.setTypeface(tf);
        doBindService();
        Intent music = new Intent();
        music.setClass(this, MusicService.class);
        startService(music);

        mHomeWatcher = new HomeWatcher(this);
        mHomeWatcher.setOnHomePressedListener(new HomeWatcher.OnHomePressedListener() {
            @Override
            public void onHomePressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
            @Override
            public void onHomeLongPressed() {
                if (mServ != null) {
                    mServ.pauseMusic();
                }
            }
        });
        mHomeWatcher.startWatch();

        if (newscore > highscore) {
            updateHighscore(newscore, newname);
        }
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });
    }
    private void start() {
        Intent intent = new Intent(Launcher.this, Username.class);
        startActivity(intent);
    }

    private void loadHighscore() {
        highscoreView.setText("Highscore: " + username + " " + highscore);
    }

    private void updateHighscore(int highscoreNew, String name) {
        highscore = highscoreNew;
        username = name;
        highscoreView.setText("Highscore: " + username + " " + highscore);
    }

    private boolean mIsBound = false;
    private MusicService mServ;
    private ServiceConnection Scon =new ServiceConnection(){

        public void onServiceConnected(ComponentName name, IBinder
                binder) {
            mServ = ((MusicService.ServiceBinder)binder).getService();
        }

        public void onServiceDisconnected(ComponentName name) {
            mServ = null;
        }
    };

    void doBindService(){
        bindService(new Intent(this,MusicService.class),
                Scon, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mServ != null) {
            mServ.resumeMusic();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);

    }

    @Override
    protected void onPause() {
        super.onPause();

        PowerManager pm = (PowerManager)
                getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = false;
        if (pm != null) {
            isScreenOn = pm.isScreenOn();
        }

        if (!isScreenOn) {
            if (mServ != null) {
                mServ.pauseMusic();
            }
        }

    }
}
