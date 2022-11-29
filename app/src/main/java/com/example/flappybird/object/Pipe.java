package com.example.flappybird.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.example.flappybird.Constants;

import java.util.Random;

public class Pipe extends BaseObject {
    public static int speed;

    public Pipe(float x, float y, int width, int height) {
        super(x, y, width, height);
        speed = 10 * Constants.SCREEN_WIDTH / 1080;
    }

    public void draw(Canvas canvas) {
        // Create animations for the pipes to move from the left to the right
        this.x -= speed;
        canvas.drawBitmap(this.bitmap, this.x, this.y, null);
    }

    public void randomY() {
        Random random = new Random();
        this.y = random.nextInt((this.height / 4) + 1) - this.height / 4;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
    }
}
