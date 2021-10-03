package com.example.facefilter;

import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.DisplayMetrics;

import com.example.facefilter.CameraAndOverlay.GraphicOverlay;
import com.example.facefilter.Singelton.SetAndGetData;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.Set;

class ApplyFilters extends GraphicOverlay.Graphic {
    private static final float EYE_RADIUS_PROPORTION = 0.45f;
    private static final float IRIS_RADIUS_PROPORTION = EYE_RADIUS_PROPORTION / 2.0f;
    GraphicOverlay overlay1;


    private Face face;
    private Paint mEyeWhitesPaint;
    private Paint mEyeIrisPaint;
    private Paint mEyeOutlinePaint1, mEyeOutlinePaint;
    private Paint mEyeLidPaint;
    Paint _paintSimple;
    Paint _paintBlur;

    private IrisPosition mLeftPhysics = new IrisPosition();
    private IrisPosition mRightPhysics = new IrisPosition();

    private volatile PointF mLeftPosition, leftPositionGlasses, rightPositionGlasses;
    private volatile PointF NOSE;
    private volatile PointF MOUTH_LEFT;
    private volatile PointF MOUTH_RIGHT;
    private volatile boolean mLeftOpen;

    private volatile PointF mRightPosition;
    private volatile boolean mRightOpen;

    ApplyFilters(GraphicOverlay overlay) {
        super(overlay);
        overlay1 = overlay;

        mEyeWhitesPaint = new Paint();
        mEyeWhitesPaint.setColor(Color.WHITE);
        mEyeWhitesPaint.setStyle(Paint.Style.FILL);

        mEyeLidPaint = new Paint();
        mEyeLidPaint.setColor(Color.BLACK);
        mEyeLidPaint.setStyle(Paint.Style.FILL);

        mEyeIrisPaint = new Paint();
        mEyeIrisPaint.setColor(Color.BLACK);
        mEyeIrisPaint.setStyle(Paint.Style.FILL);

        mEyeOutlinePaint = new Paint();
        mEyeOutlinePaint.setColor(Color.BLACK);
        mEyeOutlinePaint.setStyle(Paint.Style.STROKE);
        mEyeOutlinePaint.setStrokeWidth(5);
        mEyeOutlinePaint1 = new Paint();
        mEyeOutlinePaint1.setColor(Color.WHITE);
        mEyeOutlinePaint1.setStyle(Paint.Style.STROKE);
        mEyeOutlinePaint1.setStrokeWidth(5);

        _paintSimple = new Paint();
        _paintSimple.setAntiAlias(true);
        _paintSimple.setDither(true);
        _paintSimple.setColor(Color.argb(248, 255, 255, 255));
        _paintSimple.setStrokeWidth(1f);
        _paintSimple.setStyle(Paint.Style.STROKE);
        _paintSimple.setStrokeJoin(Paint.Join.ROUND);
        _paintSimple.setStrokeCap(Paint.Cap.ROUND);

        _paintBlur = new Paint();
        _paintBlur.set(_paintSimple);
        _paintBlur.setColor(Color.argb(235, 74, 138, 255));
        _paintBlur.setStrokeWidth(8f);
        _paintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
    }

    void updateEyes(Face mface, PointF leftPosition, boolean leftOpen,
                    PointF rightPosition, boolean rightOpen, PointF n, PointF r, PointF l) {
        face = mface;
        NOSE = n;
        MOUTH_LEFT = l;
        MOUTH_RIGHT = r;


        mLeftPosition = leftPosition;
        mLeftOpen = leftOpen;

        mRightPosition = rightPosition;
        mRightOpen = rightOpen;

        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        PointF detectLeftPosition = mLeftPosition;
        PointF detectRightPosition = mRightPosition;
        if ((detectLeftPosition == null) || (detectRightPosition == null)) {
            return;
        }
        PointF leftPosition =
                new PointF(translateX(detectLeftPosition.x), translateY(detectLeftPosition.y));
        PointF rightPosition =
                new PointF(translateX(detectRightPosition.x), translateY(detectRightPosition.y));
        leftPositionGlasses = leftPosition;
        rightPositionGlasses = rightPosition;
        float distance = (float) Math.sqrt(
                Math.pow(rightPosition.x - leftPosition.x, 2) +
                        Math.pow(rightPosition.y - leftPosition.y, 2));
        float eyeRadius = EYE_RADIUS_PROPORTION * distance;
        float irisRadius = IRIS_RADIUS_PROPORTION * distance;
        PointF leftIrisPosition =
                mLeftPhysics.nextIrisPosition(leftPosition, eyeRadius, irisRadius);
//        drawEye(canvas, leftPosition, eyeRadius, leftIrisPosition, irisRadius, mLeftOpen);
//        drawShiningGlasses(canvas, leftPosition, eyeRadius, leftIrisPosition, irisRadius, mLeftOpen);
        PointF rightIrisPosition =
                mRightPhysics.nextIrisPosition(rightPosition, eyeRadius, irisRadius);
//        drawEye(canvas, rightPosition, eyeRadius, rightIrisPosition, irisRadius, mRightOpen);

        float eulerY = face.getEulerY();
        float eulerZ = face.getEulerZ();

// Draw the hat only if the subject's head is titled at a sufficiently jaunty angle.
        final float HEAD_TILT_HAT_THRESHOLD = 20.0f;
        final float HEAD_TILT_HAT_THRESHOLD1 = 70.0f;
//        if (Math.abs(eulerZ) > HEAD_TILT_HAT_THRESHOLD && Math.abs(eulerZ) < HEAD_TILT_HAT_THRESHOLD1) {
//            drawHat(canvas);
//        }

        drawDesiredFilter(canvas, leftPosition, rightPosition, distance);

//        drawShiningGlasses(canvas, rightPosition, eyeRadius, rightIrisPosition, irisRadius, mRightOpen);
//        drawLineOfGlasses(canvas, leftPosition, rightPosition, eyeRadius);
//        drawGlassesRedLined(canvas, leftPosition, rightPosition, distance);
//        drawGlasses(canvas, leftPosition, rightPosition, distance);
//        rabbitOnFace(canvas, leftPosition, rightPosition);
//        dogFilter(canvas, leftPosition, rightPosition);
//        flowerOnHead(canvas, leftPosition, rightPosition);
//        heartsOnHead(canvas, leftPosition, rightPosition);
//        blushOnFace(canvas, leftPosition, rightPosition);
//        bearFaceEmpty(canvas, leftPosition, rightPosition);
//        fireOnHead(canvas, leftPosition, rightPosition);
//        drawMustache(canvas, NOSE, MOUTH_LEFT, MOUTH_RIGHT);
//        drawCigarette(canvas, MOUTH_LEFT, MOUTH_RIGHT);
//        drawGlasses(canvas);
//        drawNose(canvas);
    }

    private void drawDesiredFilter(Canvas canvas, PointF leftPosition, PointF rightPosition, float distance) {
        switch (SetAndGetData.data.getFilterName()) {
            case "bear_face":
                bearFace(canvas, leftPosition, rightPosition);
                break;
            case "yellow_face_mask":
                yellow_mask(canvas, leftPosition, rightPosition);
                break;
            case "bear_face_empty":
                bearFaceEmpty(canvas, leftPosition, rightPosition);
                break;
            case "black_hearts":
                heartsOnHead(canvas, leftPosition, rightPosition);
                break;
            case "blue_shaded_glasses":
                drawGlassesRedLined(canvas, leftPosition, rightPosition, distance);
                break;
            case "blush":
                blushOnFace(canvas, leftPosition, rightPosition);
                break;
            case "cat":
                replaceFace(canvas, leftPosition, rightPosition);
                break;
            case "cat_with_crown":
                crownOnFace(canvas, leftPosition, rightPosition);
                break;
            case "cigarette":
                drawCigarette(canvas, MOUTH_LEFT, MOUTH_RIGHT);
                break;
            case "dog":
                dogFilter(canvas, leftPosition, rightPosition);
                break;
//            case :
//
//                break;
            case "dog_face":
                dogFace(canvas, leftPosition, rightPosition);
                break;
            case "fire_on_head":
                fireOnHead(canvas, leftPosition, rightPosition);
                break;
            case "flowers_on_head":
                flowerOnHead(canvas, leftPosition, rightPosition);
                break;
            case "memes_glasses":
                drawGlasses(canvas, leftPosition, rightPosition, distance);
                break;
            case "purple_glasses":
                drawGlasses(canvas, leftPosition, rightPosition, distance);
                break;
            case "rabbit":
                rabbitOnFace(canvas, leftPosition, rightPosition);
                break;
            case "red_hat":
//                if (Math.abs(eulerZ) > HEAD_TILT_HAT_THRESHOLD && Math.abs(eulerZ) < HEAD_TILT_HAT_THRESHOLD1) {
//                    drawHat(canvas);
//                }
                break;
            case "simple_glasses":
                drawGlasses(canvas, leftPosition, rightPosition, distance);
                break;
            default:
                break;
        }
    }


    private void drawEye(Canvas canvas, PointF eyePosition, float eyeRadius,
                         PointF irisPosition, float irisRadius, boolean isOpen) {
        if (isOpen) {
            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeWhitesPaint);
            canvas.drawCircle(irisPosition.x, irisPosition.y, irisRadius, mEyeIrisPaint);
        } else {
            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeLidPaint);
            float y = eyePosition.y;
            float start = eyePosition.x - eyeRadius;
            float end = eyePosition.x + eyeRadius;
            canvas.drawLine(start, y, end, y, mEyeOutlinePaint);
        }
        canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeOutlinePaint);
    }

    private void drawCigarette(Canvas canvas, PointF leftMouth, PointF rightMouth) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        if (leftMouth != null && rightMouth != null) {
            int mouthLength = (int) ((translateX(leftMouth.x) - translateX(rightMouth.x)) * widthScaleFactor);

            Rect cigaretteRect = new Rect(
                    Math.round(translateX(leftMouth.x)) - mouthLength,
                    Math.round(translateY(leftMouth.y)),
                    Math.round(translateX(leftMouth.x)),
                    Math.round(translateY(leftMouth.y)) + mouthLength
            );

            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, cigaretteRect, null);
        }
    }

    private void drawGlasses(Canvas canvas, PointF leftEye, PointF rightEye, float dis) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();
//        Landmark leftEye = face.getLandmarks().get(Landmark.LEFT_EYE);
//        Landmark rightEye = face.getLandmarks().get(Landmark.RIGHT_EYE);
//        if (leftEye != null && rightEye != null) {
        float eyeDistance = mLeftPosition.x - mRightPosition.x;
        int delta = Math.round(widthScaleFactor * dis / 2);
//            face.getPosition().
//            Rect glassesRect = new Rect(
//                    Math.round(translateX(mLeftPosition.x)) - delta,
//                    Math.round(translateY(mLeftPosition.y)) - delta,
//                    Math.round(translateX(mRightPosition.x)) + delta,
//                    Math.round(translateY(mRightPosition.y)) + delta);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - delta + delta / 6,
                Math.round(leftEye.y) - delta / 5,
                Math.round(rightEye.x) + delta - delta / 6,
                Math.round(rightEye.y) + delta / 5);

        int orientation = SetAndGetData.data.getActivity().getResources().getConfiguration().orientation;
        if (face.getEulerY() > -15 && face.getEulerY() < 15 && face.getEulerZ() > -5 && face.getEulerZ() < 5) {
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
        }
//        }
    }

    private void drawGlassesRedLined(Canvas canvas, PointF leftEye, PointF rightEye, float dis) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();
//        Landmark leftEye = face.getLandmarks().get(Landmark.LEFT_EYE);
//        Landmark rightEye = face.getLandmarks().get(Landmark.RIGHT_EYE);
//        if (leftEye != null && rightEye != null) {
//        float eyeDistance = mLeftPosition.x - mRightPosition.x;
        int delta = Math.round(widthScaleFactor * dis / 2);
//            face.getPosition().
//            Rect glassesRect = new Rect(
//                    Math.round(translateX(mLeftPosition.x)) - delta,
//                    Math.round(translateY(mLeftPosition.y)) - delta,
//                    Math.round(translateX(mRightPosition.x)) + delta,
//                    Math.round(translateY(mRightPosition.y)) + delta);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - delta + delta / 6,
                Math.round(leftEye.y) - delta / 2,
                Math.round(rightEye.x) + delta - delta / 6,
                Math.round(rightEye.y) + delta / 2);


        int orientation = SetAndGetData.data.getActivity().getResources().getConfiguration().orientation;
        if (face.getEulerY() > -15 && face.getEulerY() < 15 && face.getEulerZ() > -5 && face.getEulerZ() < 5) {
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
        }
//        }
    }

    private void replaceFace(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = leftEye.x - rightEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - delta,
                Math.round(leftEye.y) - delta,
                Math.round(rightEye.x) + delta,
                Math.round(rightEye.y) + delta);
        canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
    }

    private void drawHat(Canvas canvas) {
        PointF facePosition = face.getPosition();
        float faceWidth = scaleX(face.getWidth());
        float faceHeight = scaleY(face.getHeight());

        final float HAT_FACE_WIDTH_RATIO = (float) (1.0 / 4.0);
        final float HAT_FACE_HEIGHT_RATIO = (float) (1.0 / 6.0);
        final float HAT_CENTER_Y_OFFSET_FACTOR = (float) (1.0 / 8.0);

        float hatCenterY = translateY(facePosition.y) + (faceHeight * HAT_CENTER_Y_OFFSET_FACTOR);
        float hatWidth = faceWidth * HAT_FACE_WIDTH_RATIO;
        float hatHeight = faceHeight * HAT_FACE_HEIGHT_RATIO;

        int left = (int) (translateX(NOSE.x) - (hatWidth / 2));
        int right = (int) (translateX(NOSE.x) + (hatWidth / 2));
        int top = (int) (hatCenterY - (hatHeight / 2));
        int bottom = (int) (hatCenterY + (hatHeight / 2));

        Rect noseRect = new Rect(left, top, right, bottom);
        canvas.drawBitmap(SetAndGetData.data.getIcon(), null, noseRect, null);

//        mHatGraphic.setBounds(left, top, right, bottom);
//        mHatGraphic.draw(canvas);
    }

    private void drawShiningGlasses(Canvas canvas, PointF eyePosition, float eyeRadius,
                                    PointF irisPosition, float irisRadius, boolean isOpen) {
        Paint _paintSimple = new Paint();
        _paintSimple.setAntiAlias(true);
        _paintSimple.setDither(true);
        _paintSimple.setColor(Color.argb(248, 255, 255, 255));
        _paintSimple.setStrokeWidth(1f);
        _paintSimple.setStyle(Paint.Style.STROKE);
        _paintSimple.setStrokeJoin(Paint.Join.ROUND);
        _paintSimple.setStrokeCap(Paint.Cap.ROUND);

        Paint _paintBlur = new Paint();
        _paintBlur.set(_paintSimple);
        _paintBlur.setColor(Color.argb(235, 74, 138, 255));
        _paintBlur.setStrokeWidth(15f);
        _paintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));

//            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius - 15, _paintBlur);
        canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, _paintBlur);
        canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, _paintSimple);
//            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius - 15, _paintSimple);

    }

    private void crownOnFace(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

//        float eyeDistance = leftEye.x - rightEye.x;
        float eyeDistance = rightEye.x - leftEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
//        Rect glassesRect = new Rect(
//                Math.round(leftEye.x) - delta,
//                Math.round(leftEye.y) - (delta + delta / 6),
//                Math.round(rightEye.x) + delta,
//                Math.round(rightEye.y) + delta / 2);

        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - delta/2,
                Math.round(leftEye.y) - (delta + delta / 6),
                Math.round(rightEye.x) + delta/2,
                Math.round(rightEye.y) + delta / 2);
//                Math.round(leftEye.x) - 115,
//                Math.round(leftEye.y) - (30),
//                Math.round(rightEye.x) + 115,
//                Math.round(rightEye.y) + 200);

        canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
//        canvas.drawLine(leftEye.x, leftEye.y, leftEye.x + 115, leftEye.y, _paintBlur);
    }

    private void rabbitOnFace(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = leftEye.x - rightEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - (delta + delta / 4),
                Math.round(leftEye.y) - (delta + delta / 25),
                Math.round(rightEye.x) + (delta + delta / 4),
                Math.round(rightEye.y) + (delta / 2));
        if (face.getEulerY() > -15 && face.getEulerY() < 15 && face.getEulerZ() > -15 && face.getEulerZ() < 15) {
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
        }
    }

    private void dogFilter(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = leftEye.x - rightEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - (delta + delta / 4),
                Math.round(leftEye.y) - (delta + delta / 35),
                Math.round(rightEye.x) + (delta),
                Math.round(rightEye.y) + (delta + delta / 2));
        if (face.getEulerY() > -15 && face.getEulerY() < 15 && face.getEulerZ() > -12 && face.getEulerZ() < 12) {
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
        }
    }

    private void flowerOnHead(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = leftEye.x - rightEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - (delta),
                Math.round(leftEye.y) - (delta),
                Math.round(rightEye.x) + (delta),
                Math.round(rightEye.y) + delta / 3);
        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -18 && face.getEulerZ() < 18) {
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
        }
    }

    private void heartsOnHead(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = leftEye.x - rightEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - (delta),
                Math.round(leftEye.y) - (delta - delta / 10),
                Math.round(rightEye.x) + (delta),
                Math.round(rightEye.y) + delta / 20);

//        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -18 && face.getEulerZ() < 18) {
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
//        }
    }

    private void blushOnFace(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = leftEye.x - rightEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - delta,
                Math.round(leftEye.y) - delta / 10,
                Math.round(rightEye.x) + delta,
                Math.round(rightEye.y) + delta / 2);
        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -18 && face.getEulerZ() < 18) {
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
        }
    }

    private void dogFace(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = leftEye.x - rightEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - (delta + delta / 6),
                Math.round(leftEye.y) - (delta + delta / 15),
                Math.round(rightEye.x) + (delta + delta / 6),
                Math.round(rightEye.y) + (delta + delta / 15));
        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -18 && face.getEulerZ() < 18) {
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
        }
    }

    private void bearFace(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = leftEye.x - rightEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - (delta + delta / 10),
                Math.round(leftEye.y) - (delta - delta / 10),
                Math.round(rightEye.x) + (delta + delta / 10),
                Math.round(rightEye.y) + (delta));
        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -14 && face.getEulerZ() < 14) {
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
        }
    }

    private void bearFaceEmpty(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = leftEye.x - rightEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - (delta + delta / 10),
                Math.round(leftEye.y) - (delta - delta / 10),
                Math.round(rightEye.x) + (delta + delta / 10),
                Math.round(rightEye.y) + (delta));
        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -14 && face.getEulerZ() < 14) {
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
        }
    }

    private void fireOnHead(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = leftEye.x - rightEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - (delta),
                Math.round(leftEye.y) - (delta),
                Math.round(rightEye.x) + (delta),
                Math.round(rightEye.y) - delta / 6);
//        float eyeDistance = rightEye.x - leftEye.x;
//        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
//        Rect glassesRect = new Rect(
//                Math.round(leftEye.x) - (delta/2),
//                Math.round(leftEye.y) - (delta),
//                Math.round(rightEye.x) + (delta/2),
//                Math.round(rightEye.y) - delta / 6);
        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -14 && face.getEulerZ() < 14) {
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
        }
    }

    private void yellow_mask(Canvas canvas, PointF leftEye, PointF rightEye) {
        float widthScaleFactor = canvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = leftEye.x - rightEye.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect glassesRect = new Rect(
                Math.round(leftEye.x) - (delta + delta / 8),
                Math.round(leftEye.y) - delta/8,
                Math.round(rightEye.x) + (delta + delta / 8),
                Math.round(rightEye.y) + (delta + delta / 15));
        if (face.getEulerY() > -20 && face.getEulerY() < 20 && face.getEulerZ() > -20 && face.getEulerZ() < 20) {
            SetAndGetData.data.setCanvas(canvas);
            SetAndGetData.data.setRect(glassesRect);
//            canvas.drawLine(leftEye.x, leftEye.y, rightEye.x, rightEye.y, mEyeLidPaint);
            canvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
        }
    }

    /*In Development Phase*/
    private void drawNose(Canvas canvas) {
        final float NOSE_FACE_WIDTH_RATIO = (float) (1 / 5.0);
        float noseWidth = face.getWidth() * NOSE_FACE_WIDTH_RATIO;
        int left = (int) (translateX(NOSE.x) - (noseWidth / 2));
        int right = (int) (translateX(NOSE.x) + (noseWidth / 2));
        int top = (int) (translateY(mLeftPosition.y) + translateY(mRightPosition.y)) / 2;
        int bottom = (int) translateY(NOSE.y);

        Rect noseRect = new Rect(left, top, right, bottom);
        canvas.drawBitmap(SetAndGetData.data.getIcon(), null, noseRect, null);
//        mPigNoseGraphic.setBounds(left, top, right, bottom);
//        mPigNoseGraphic.draw(canvas);
    }

//    private void drawMustache(Canvas canvas,
//                              PointF noseBasePosition,
//                              PointF mouthLeftPosition, PointF mouthRightPosition) {
//        int left = (int)mouthLeftPosition.x;
//        int top = (int)noseBasePosition.y;
//        int right = (int)mouthRightPosition.x;
//        int bottom = (int)Math.min(mouthLeftPosition.y, mouthRightPosition.y);
//
//        if (mIsFrontFacing) {
//            mMustacheGraphic.setBounds(left, top, right, bottom);
//        } else {
//            mMustacheGraphic.setBounds(right, top, left, bottom);
//        }
//        mMustacheGraphic.draw(canvas);
//    }

    private void drawLineOfGlasses(Canvas canvas, PointF leftPosition, PointF rightPosition, float eyeRadius) {
//        float y1 = leftPosition.y;
//        float y2 = rightPosition.y;
//        float start = leftPosition.x - eyeRadius;
//        float end = rightPosition.x + eyeRadius;
//        canvas.drawLine(start, y1, end, y2, mEyeOutlinePaint);

//        float y1 = leftPosition.y;
////        float y2 = rightPosition.y;
//        float start = leftPosition.x - eyeRadius;
//        float end = leftPosition.x + eyeRadius;
//        canvas.drawLine(start, y1, face.getPosition().y, y1, mEyeOutlinePaint);
    }

}
