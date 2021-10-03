package com.example.facefilter;

import android.graphics.PointF;

import com.example.facefilter.CameraAndOverlay.GraphicOverlay;
import com.example.facefilter.Singelton.SetAndGetData;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.HashMap;
import java.util.Map;

class GetAndSetFace extends Tracker<Face> {
    private static final float EYE_CLOSED_THRESHOLD = 0.4f;

    private GraphicOverlay mOverlay;
    private ApplyFilters mEyesGraphic;

    private Map<Integer, PointF> mPreviousProportions = new HashMap<>();

    private boolean mPreviousIsLeftOpen = true;
    private boolean mPreviousIsRightOpen = true;

    GetAndSetFace(GraphicOverlay overlay) {
        mOverlay = overlay;
    }

    @Override
    public void onNewItem(int id, Face face) {
        mEyesGraphic = new ApplyFilters(mOverlay);
    }

    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {

//        faces.get(0).getLandmark(FaceLandmark.NOSE_BASE).getPosition(), faces.get(0).getLandmark(FaceLandmark.MOUTH_LEFT).getPosition()
//                , faces.get(0).getLandmark(FaceLandmark.MOUTH_RIGHT).getPosition();
        PointF NOSE_BASE = face.getLandmarks().get(Landmark.NOSE_BASE).getPosition();
        PointF MOUTH_LEFT = face.getLandmarks().get(Landmark.LEFT_MOUTH).getPosition();
        PointF MOUTH_RIGHT = face.getLandmarks().get(Landmark.RIGHT_MOUTH).getPosition();


        mOverlay.add(mEyesGraphic);
        SetAndGetData.data.setmOverlay(mOverlay);

        updatePreviousProportions(face);

        PointF leftPosition = getLandmarkPosition(face, Landmark.LEFT_EYE);
        PointF rightPosition = getLandmarkPosition(face, Landmark.RIGHT_EYE);

        float leftOpenScore = face.getIsLeftEyeOpenProbability();
        boolean isLeftOpen;
        if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isLeftOpen = mPreviousIsLeftOpen;
        } else {
            isLeftOpen = (leftOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsLeftOpen = isLeftOpen;
        }

        float rightOpenScore = face.getIsRightEyeOpenProbability();
        boolean isRightOpen;
        if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
            isRightOpen = mPreviousIsRightOpen;
        } else {
            isRightOpen = (rightOpenScore > EYE_CLOSED_THRESHOLD);
            mPreviousIsRightOpen = isRightOpen;
        }

        mEyesGraphic.updateEyes(face, leftPosition, isLeftOpen, rightPosition, isRightOpen, NOSE_BASE, MOUTH_RIGHT, MOUTH_LEFT);
    }

    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mEyesGraphic);
    }

    @Override
    public void onDone() {
        mOverlay.remove(mEyesGraphic);
    }

    private void updatePreviousProportions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousProportions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }

    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF prop = mPreviousProportions.get(landmarkId);
        if (prop == null) {
            return null;
        }

        float x = face.getPosition().x + (prop.x * face.getWidth());
        float y = face.getPosition().y + (prop.y * face.getHeight());
        return new PointF(x, y);
    }
}