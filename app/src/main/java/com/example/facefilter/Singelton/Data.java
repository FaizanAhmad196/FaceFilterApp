package com.example.facefilter.Singelton;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;

import com.example.facefilter.CameraAndOverlay.GraphicOverlay;
import com.google.android.gms.vision.face.Face;

public class Data {
    private Drawable mustache;
    private Bitmap icon;
    private int previewWidth, previewHeight;
    private float widthScaleFactor, heightScaleFactor;
    private Activity activity;
    private Face face;
    private GraphicOverlay mOverlay;
    private String filterName = "";
    int format;
    Uri toShareUri;
    Canvas canvas;
    Rect rect;
    boolean isFronCam = false, isFlash = false;

    public Uri getToShareUri() {
        return toShareUri;
    }

    public void setToShareUri(Uri toShareUri) {
        this.toShareUri = toShareUri;
    }

    public boolean isFlash() {
        return isFlash;
    }

    public void setFlash(boolean flash) {
        isFlash = flash;
    }

    public boolean isFronCam() {
        return isFronCam;
    }

    public void setFronCam(boolean fronCam) {
        isFronCam = fronCam;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public GraphicOverlay getmOverlay() {
        return mOverlay;
    }

    public void setmOverlay(GraphicOverlay mOverlay) {
        this.mOverlay = mOverlay;
    }

    public Face getFace() {
        return face;
    }

    public void setFace(Face face) {
        this.face = face;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public float getWidthScaleFactor() {
        return widthScaleFactor;
    }

    public void setWidthScaleFactor(float widthScaleFactor) {
        this.widthScaleFactor = widthScaleFactor;
    }

    public float getHeightScaleFactor() {
        return heightScaleFactor;
    }

    public void setHeightScaleFactor(float heightScaleFactor) {
        this.heightScaleFactor = heightScaleFactor;
    }

    public int getPreviewHeight() {
        return previewHeight;
    }

    public void setPreviewHeight(int previewHeight) {
        this.previewHeight = previewHeight;
    }

    public int getPreviewWidth() {
        return previewWidth;
    }

    public void setPreviewWidth(int previewWidth) {
        this.previewWidth = previewWidth;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public Drawable getMustache() {
        return mustache;
    }

    public void setMustache(Drawable mustache) {
        this.mustache = mustache;
    }
}
