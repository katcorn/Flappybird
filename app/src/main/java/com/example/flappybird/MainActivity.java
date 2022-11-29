package com.example.flappybird;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static RelativeLayout rlGameOver;
    public static TextView txtScore, txtBestScore, txtScoreOver;
    public static MediaPlayer mediaPlayer;
    public ImageButton btnStart, btnSound;
    public Button btnMode, btnSkin;
    public GameView gameView;
    public ImageView gameTitle;
    public boolean sound;
    private int mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.sound = true;
        this.mode = 0;
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Get width and height of the device
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Constants.SCREEN_WIDTH = displayMetrics.widthPixels;
        Constants.SCREEN_HEIGHT = displayMetrics.heightPixels;

        setContentView(R.layout.activity_main);
        btnMode = findViewById(R.id.btn_mode);
        btnStart = findViewById(R.id.btn_start);
        btnSound = findViewById(R.id.btn_sound);
        btnSkin = findViewById(R.id.btn_skin);
        gameView = findViewById(R.id.game_view);
        txtScore = findViewById(R.id.txt_score);
        gameTitle = findViewById(R.id.game_title);
        rlGameOver = findViewById(R.id.rl_game_over);
        txtBestScore = findViewById(R.id.txt_best_score);
        txtScoreOver = findViewById(R.id.txt_score_over);

        // Action when click mode button
        btnMode.setOnClickListener(view -> {
            switch (this.mode) {
                case 0:
                    btnMode.setText("Normal");
                    btnMode.setBackground(this.getResources().getDrawable(R.drawable.yellow_button));
                    this.mode = 1;
                    break;
                case 1:
                    btnMode.setText("Hard");
                    btnMode.setBackground(this.getResources().getDrawable(R.drawable.red_button));
                    this.mode = 2;
                    break;
                case 2:
                    btnMode.setText("Asian");
                    btnMode.setBackground(this.getResources().getDrawable(R.drawable.purple_button));
                    this.mode = 3;
                    break;
                default:
                    btnMode.setText("Easy");
                    btnMode.setBackground(this.getResources().getDrawable(R.drawable.green_button));
                    this.mode = 0;
                    break;
            }
            controlDistance();
        });

        // Action when click sound button
        btnSound.setOnClickListener(view -> {
            if (this.sound) {
                btnSound.setImageResource(R.drawable.ic_music_off);
                this.sound = false;
            } else {
                btnSound.setImageResource(R.drawable.ic_music_on);
                this.sound = true;
            }
            controlMusic();
        });

        // Action when click start button
        btnStart.setOnClickListener(view -> {
            gameView.setStart(true);
            txtScore.setVisibility(View.VISIBLE);
            gameTitle.setVisibility(View.INVISIBLE);
            btnStart.setVisibility(View.INVISIBLE);
            btnSound.setVisibility(View.INVISIBLE);
            btnMode.setVisibility(View.INVISIBLE);
            btnSkin.setVisibility(View.INVISIBLE);
        });

        // Action when click reStart on Game Over Screen
        rlGameOver.setOnClickListener(view -> {
            gameTitle.setVisibility(View.VISIBLE);
            btnStart.setVisibility(View.VISIBLE);
            btnSound.setVisibility(View.VISIBLE);
            btnMode.setVisibility(View.VISIBLE);
            btnSkin.setVisibility(View.VISIBLE);
            rlGameOver.setVisibility(View.INVISIBLE);
            gameView.setStart(false);
            gameView.reStart();
            controlDistance();
            controlMusic();
        });

        // Create background music
        mediaPlayer = MediaPlayer.create(this, R.raw.sillychipsong);
        mediaPlayer.setLooping(true);
        controlMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }


    // Function control background music ON or OFF
    protected void controlMusic() {
        if (this.sound) {
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
        }
    }

    // Function control distance between top pipe and bottom pipe
    protected void controlDistance() {
        switch (this.mode) {
            case 0:
                gameView.setDistance(500);
                break;
            case 1:
                gameView.setDistance(400);
                break;
            case 2:
                gameView.setDistance(300);
                break;
            default:
                gameView.setDistance(200);
                break;
        }
    }
}