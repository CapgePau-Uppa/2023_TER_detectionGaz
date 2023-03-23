package com.arangarcia.gazdetector.ui.plan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class Plan {

    private Bitmap image;
    private double latTopLeft;
    private double longTopLeft;
    private double latBottomRight;
    private double longBottomRight;

    public Plan(Context context, int imageResId, double latTopLeft, double longTopLeft, double latBottomRight, double longBottomRight) {
        this.image = BitmapFactory.decodeResource(context.getResources(), imageResId);
        this.latTopLeft = latTopLeft;
        this.longTopLeft = longTopLeft;
        this.latBottomRight = latBottomRight;
        this.longBottomRight = longBottomRight;
    }

    public boolean isOnPlan(double latitude, double longitude) {
        return latitude >= latTopLeft && latitude <= latBottomRight && longitude >= longTopLeft && longitude <= longBottomRight;
    }

    public Point getCoordinatesOnPlan(double latitude, double longitude) {

        int x = (int) (((longitude - longTopLeft) / (longBottomRight - longTopLeft)) * image.getWidth());
        int y = (int) (((latBottomRight - latitude) / (latBottomRight - latTopLeft)) * image.getHeight());
        return new Point(x, y);
    }

    public Bitmap getImage() {
        return image;
    }
}
