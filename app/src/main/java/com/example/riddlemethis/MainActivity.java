package com.example.riddlemethis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.PowerManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView riddle;
    private TextView score;
    private TextView countdown;
    private RadioGroup optionGroup;
    private RadioButton option1;
    private RadioButton option2;
    private RadioButton option3;
    private RadioButton option4;
    private Button confirm;
    private ColorStateList defaultColor;
    private List<Question> riddleList;
    private int riddleCounter;
    private int riddleTotal;
    private Question riddleCurrent;
    private int scoreCheck;
    private boolean answered;
    private long backPressed;
    private static final long countdownMils = 25000;
    private ColorStateList timerdefaultColor;
    private CountDownTimer countdownTimer;
    private long timeLeftInMils;
    private String username;
    private ProgressBar bar;
    private ImageView hart1;
    private ImageView hart2;
    private ImageView hart3;
    MediaPlayer win;
    MediaPlayer oof;
    public static final String EXTRA_SCORE = "tablayout.example.RiddleMethis.RiddleMethis.extraScore";
    public static final String EXTRA_USERNAME = "tablayout.example.RiddleMeThis.RiddleMeThis.extraUSERNAME";
    private int lives;
    HomeWatcher mHomeWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        username = intent.getStringExtra(Username.EXTRA_TEXT);

        score = findViewById(R.id.score);
        riddle = findViewById(R.id.riddle);
        countdown = findViewById(R.id.countdown);
        optionGroup = findViewById(R.id.radio_group);
        option1 = findViewById(R.id.option1);
        option2 = findViewById(R.id.option2);
        option3 = findViewById(R.id.option3);
        option4 = findViewById(R.id.option4);
        confirm = findViewById(R.id.confirm);
        bar = findViewById(R.id.progressBar);
        hart1 = findViewById(R.id.harto1);
        hart2 = findViewById(R.id.harto2);
        hart3 = findViewById(R.id.harto3);
        lives = 3;

        win = MediaPlayer.create(getApplicationContext(), R.raw.win);
        oof = MediaPlayer.create(getApplicationContext(), R.raw.oof);

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

        defaultColor = option1.getTextColors();
        timerdefaultColor = countdown  .getTextColors();

        score.setText(username + "'s \nscore: 0");

        RiddleDB dbHelper = new RiddleDB(this);
        riddleList = dbHelper.getAllQuestions();
        riddleTotal = riddleList.size();
        Collections.shuffle(riddleList);
        bar.setMax(riddleTotal);

        nextQuestion();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (option1.isChecked() || option2.isChecked() || option3.isChecked() || option4.isChecked()) {
                        checkAnswer();
                    } else {
                        Toast.makeText(MainActivity.this, "You need to answer this!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    nextQuestion();
                }
            }
        });
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

    private void nextQuestion() {
        option1.setTextColor(defaultColor);
        option2.setTextColor(defaultColor);
        option3.setTextColor(defaultColor);
        option4.setTextColor(defaultColor);
        optionGroup.clearCheck();

        if ((riddleCounter < riddleTotal) && (!(lives == 0))) {
            riddleCurrent = riddleList.get(riddleCounter);
            bar.setProgress(riddleCounter);

            riddle.setText(riddleCurrent.getQuestion());
            option1.setText(riddleCurrent.getOption1());
            option2.setText(riddleCurrent.getOption2());
            option3.setText(riddleCurrent.getOption3());
            option4.setText(riddleCurrent.getOption4());

            riddleCounter++;
            answered = false;
            confirm.setText("Confirm");
            timeLeftInMils = countdownMils;
            startCountdown();
        }  else {
            finishRiddle();
        }
    }

    private void startCountdown() {
        countdownTimer = new CountDownTimer(timeLeftInMils, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMils = millisUntilFinished;
                updateCountdown();
            }

            @Override
            public void onFinish() {
                timeLeftInMils = 0;
                updateCountdown();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountdown() {
        int minutes = (int) (timeLeftInMils / 1000) / 60;
        int seconds = (int) (timeLeftInMils / 1000) % 60;

        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        countdown.setText(timeFormatted);

        if (timeLeftInMils < 10000) {
            countdown.setTextColor(Color.RED);
        } else {
            countdown.setTextColor(timerdefaultColor);
        }
    }

    private void checkAnswer() {
        answered = true;
        countdownTimer.cancel();
        RadioButton optionSelected = findViewById(optionGroup.getCheckedRadioButtonId());
        int answer = optionGroup.indexOfChild(optionSelected) + 1;

        if (answer == riddleCurrent.getAnswer()) {
            win.start();
            scoreCheck++;
            score.setText(username + "'s \nscore: " + scoreCheck);
        }
        else {
            lives--;
            oof.start();
            switch(lives){
                case 2:
                    ImageViewCompat.setImageTintList(hart3, ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                    break;
                case 1:
                    ImageViewCompat.setImageTintList(hart2, ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                    break;
                case 0:
                    ImageViewCompat.setImageTintList(hart1, ColorStateList.valueOf(getResources().getColor(R.color.colorBlack)));
                    break;
            }
        }
        showSolution();
    }

    private void showSolution() {
        option1.setTextColor((getResources().getColor(R.color.colorBlack)));
        option2.setTextColor((getResources().getColor(R.color.colorBlack)));
        option3.setTextColor((getResources().getColor(R.color.colorBlack)));
        option4.setTextColor((getResources().getColor(R.color.colorBlack)));

        switch (riddleCurrent.getAnswer()) {
            case 1:
                option1.setTextColor((getResources().getColor(R.color.colorAccent)));
                riddle.setText("DING DING! Option 1 is the correct answer!");
                break;
            case 2:
                option2.setTextColor((getResources().getColor(R.color.colorAccent)));
                riddle.setText("DING DING! Option 2 is the correct answer!");
                break;
            case 3:
                option3.setTextColor((getResources().getColor(R.color.colorAccent)));
                riddle.setText("Ding Ding! Option 3 is the correct answer!");
                break;
            case 4:
                option4.setTextColor((getResources().getColor(R.color.colorAccent)));
                riddle.setText("Ding Ding! Option 4 is correct answer!");
                break;
        }

        if ((riddleCounter < riddleTotal) && (!(lives == 0))) {
            confirm.setText("Next");
        } else {
            confirm.setText("Finish");
        }
    }

    private void finishRiddle() {
        Intent resultIntent = new Intent(MainActivity.this, Launcher.class);
        resultIntent.putExtra(EXTRA_SCORE, scoreCheck);
        resultIntent.putExtra(EXTRA_USERNAME, username);
        finish();
        startActivity(resultIntent);
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            finishRiddle();
        } else {
            Toast.makeText(this, "Giving up? Press back again.", Toast.LENGTH_SHORT).show();
        }
        backPressed = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countdownTimer != null) {
            countdownTimer.cancel();
        }

        doUnbindService();
        Intent music = new Intent();
        music.setClass(this,MusicService.class);
        stopService(music);
    }
}
