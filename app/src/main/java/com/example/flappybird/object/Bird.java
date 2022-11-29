package com.example.flappybird.object;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.util.ArrayList;

public class Bird extends BaseObject {
    private final int vFlap;
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();
    private int count;
    private int idCurrentBitmap;
    private float drop;

    public Bird() {
        this.drop = 0;
        this.count = 0;
        this.vFlap = 5;
        this.idCurrentBitmap = 0;

    }

    public void setDrop(float drop) {
        this.drop = drop;
    }

    public void draw(Canvas canvas) {
        drop();
        canvas.drawBitmap(this.getBitmap(), this.x, this.y, null);
    }

    private void drop() {
        this.drop += 0.6;
        this.y += this.drop;
    }

    public void setBitmaps(ArrayList<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
        // Scale bitmaps to the height and width of the bird
        for (int i = 0; i < bitmaps.size(); i++) {
            this.bitmaps.set(i, Bitmap.createScaledBitmap(this.bitmaps.get(i), this.width, this.height, true));
        }
    }

    @Override
    public Bitmap getBitmap() {
        // Create animations for the bird to fly
        count++;
        if (this.count == this.vFlap) {
            if (this.idCurrentBitmap == bitmaps.size() - 1) {
                this.idCurrentBitmap = 0;
            } else {
                this.idCurrentBitmap = this.idCurrentBitmap + 1;
            }
            count = 0;
        }

        // Create animations for the bird when it's flying or dropping
        Bitmap currentBitmap = bitmaps.get(idCurrentBitmap);
        if (this.drop < 0) { // Fly
            Matrix matrix = new Matrix();
            matrix.postRotate(-25);
            return Bitmap.createBitmap(currentBitmap, 0, 0, currentBitmap.getWidth(), currentBitmap.getHeight(), matrix, true);
        } else if (drop >= 0) { // Drop
            Matrix matrix = new Matrix();
            if (drop < 70) {
                matrix.postRotate(-25 + (drop * 2));
            } else {
                matrix.postRotate(45);
            }
            return Bitmap.createBitmap(currentBitmap, 0, 0, currentBitmap.getWidth(), currentBitmap.getHeight(), matrix, true);
        }

        return bitmaps.get(this.idCurrentBitmap);
    }
}
