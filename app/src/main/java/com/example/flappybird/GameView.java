package com.example.flappybird;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.flappybird.object.Bird;
import com.example.flappybird.object.Pipe;

import java.util.ArrayList;

public class GameView extends View {
    private final Handler handler;
    private final Runnable runnable;
    private final Context context;
    private final int soundJump;
    private final int soundHit;
    private final SoundPool soundPool;
    private Bird bird;
    private ArrayList<Pipe> pipes;
    private int sumPipe, distance;
    private int score;
    private int bestScore;
    private boolean start;
    private boolean loadedSound, hit;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.score = 0;
        this.start = false;
        this.hit = false;
        this.context = context;
        this.distance = 500;

        // Get the best score
        SharedPreferences sharedPreferences = context.getSharedPreferences("gameSetting", Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            this.bestScore = sharedPreferences.getInt("bestScore", 0);
        }
        // Create bird
        initBird();

        // Create pipe
        initPipe();

        // Create a loop to update the interface
        this.handler = new Handler();

        // Update method draw
        this.runnable = this::invalidate;

        // Create sound
        if (Build.VERSION.SDK_INT > 21) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setAudioAttributes(audioAttributes).setMaxStreams(5);
            this.soundPool = builder.build();
        } else {
            this.soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        }
        this.soundPool.setOnLoadCompleteListener((soundPool1, i, i1) -> loadedSound = true);
        this.soundJump = this.soundPool.load(context, R.raw.jump, 1);
        this.soundHit = this.soundPool.load(context, R.raw.hit, 1);
    }

    private void initBird() {
        bird = new Bird();
        bird.setWidth(100 * Constants.SCREEN_WIDTH / 1080);
        bird.setHeight(100 * Constants.SCREEN_HEIGHT / 1920);
        bird.setX(100 * Constants.SCREEN_WIDTH / 1080);
        bird.setY(Constants.SCREEN_HEIGHT / 2 - bird.getHeight() / 2);
        ArrayList<Bitmap> bitmaps = new ArrayList<>();
        bitmaps.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.bird1));
        bitmaps.add(BitmapFactory.decodeResource(this.getResources(), R.drawable.bird2));
        bird.setBitmaps(bitmaps);
    }

    private void initPipe() {
        sumPipe = 6;
        distance = this.distance * Constants.SCREEN_HEIGHT / 1920;
        pipes = new ArrayList<>();
        float x, y;
        int width, height;
        for (int i = 0; i < sumPipe; i++) {
            if (i < sumPipe / 2) { // Pipe on top
                x = Constants.SCREEN_WIDTH + i * ((Constants.SCREEN_WIDTH + 200 * Constants.SCREEN_WIDTH / 1080) / (sumPipe / 2));
                y = 0;
                width = 200 * Constants.SCREEN_WIDTH / 1080;
                height = Constants.SCREEN_HEIGHT / 2;
                this.pipes.add(new Pipe(x, y, width, height));
                this.pipes.get(this.pipes.size() - 1).setBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.pipe2));
                this.pipes.get(this.pipes.size() - 1).randomY();
            } else { // Pipe at bottom
                x = this.pipes.get(i - sumPipe / 2).getX();
                y = this.pipes.get(i - sumPipe / 2).getY() + this.pipes.get(i - sumPipe / 2).getHeight() + this.distance;
                width = 200 * Constants.SCREEN_WIDTH / 1080;
                height = Constants.SCREEN_HEIGHT / 2;
                this.pipes.add(new Pipe(x, y, width, height));
                this.pipes.get(this.pipes.size() - 1).setBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.pipe1));
            }
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (start) {
            // Render the bird on the GameView
            bird.draw(canvas);

            for (int i = 0; i < sumPipe; i++) {
                // If the bird flies over or drop out of the screen or touch the pipe, GAME OVER
                if (bird.getRect().intersect(pipes.get(i).getRect())
                        || bird.getY() - bird.getHeight() < 0
                        || bird.getY() > Constants.SCREEN_HEIGHT) {
                    // Sound if the bird hit
                    if (loadedSound && !hit) {
                        MainActivity.mediaPlayer.pause();
                        this.soundPool.play(this.soundHit, (float) 0.5, (float) 0.5, 1, 0, 1f);
                        this.hit = true;
                    }
                    // Check the best score
                    int finalScore = Integer.parseInt(MainActivity.txtScore.getText().toString());
                    if (finalScore > bestScore) {
                        bestScore = finalScore;
                        SharedPreferences sharedPreferences = context.getSharedPreferences("gameSetting", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("bestScore", bestScore);
                        editor.apply();
                    }
                    // Set text and visibility for components
                    Pipe.speed = 0;
                    MainActivity.txtScoreOver.setText("" + finalScore);
                    MainActivity.txtBestScore.setText("Best score: " + bestScore);
                    MainActivity.txtScore.setVisibility(INVISIBLE);
                    MainActivity.rlGameOver.setVisibility(VISIBLE);
                }


                // Update the score when the bird passes through a pipe
                if (this.bird.getX() + this.bird.getWidth() > pipes.get(i).getX() + pipes.get(i).getWidth() / 2
                        && this.bird.getX() + this.bird.getWidth() <= pipes.get(i).getX() + pipes.get(i).getWidth() / 2 + Pipe.speed
                        && i < sumPipe / 2) {
                    score++;
                    MainActivity.txtScore.setText(""+score);
                }

                // Render the pipes on the GameView
                if (this.pipes.get(i).getX() < -pipes.get(i).getWidth()) {
                    this.pipes.get(i).setX(Constants.SCREEN_WIDTH);
                    if (i < sumPipe / 2) {
                        pipes.get(i).randomY();
                    } else {
                        pipes.get(i).setY(this.pipes.get(i - sumPipe / 2).getY() + this.pipes.get(i - sumPipe / 2).getHeight() + this.distance);
                    }
                }
                this.pipes.get(i).draw(canvas);
            }

        } else { // When GameView is not started, create animations for the bird to fly continuously
            if (bird.getY() > Constants.SCREEN_HEIGHT / 2) {
                bird.setDrop(-10 * Constants.SCREEN_HEIGHT / 1920);
            }
            bird.draw(canvas);
        }
        // Update every 0.01 seconds
        handler.postDelayed(runnable, 10);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            bird.setDrop(-10);
            if (loadedSound) {
                this.soundPool.play(this.soundJump, (float) 0.5, (float) 0.5, 1, 0, 1f);
            }
        }
        return true;
    }

    public void setDistance(int distance) {
        this.distance = distance;
        initPipe();
    }

    public void setStart(boolean start) {
        this.start = start;
    }

    public void reStart() {
        MainActivity.txtScore.setText("0");
        this.hit = false;
        this.score = 0;
        initBird();
        initPipe();
    }
}
