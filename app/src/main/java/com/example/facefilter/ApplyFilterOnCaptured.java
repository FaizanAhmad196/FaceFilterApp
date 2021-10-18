package com.example.facefilter;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.example.facefilter.Singelton.SetAndGetData;
import com.google.android.gms.vision.face.Landmark;

public class ApplyFilterOnCaptured {

    public Rect drawDesiredFilter(Canvas tempCanvas, Bitmap bmp, PointF LEFT_EYE, PointF RIGHT_EYE) {
        String filterName = SetAndGetData.data.getFilterName();

        if (filterName.contains("bear_face")) {
            return bearFace(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("yellow_face_mask")) {
            return yellow_mask(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("bear_face_empty")) {
            return bearFaceEmpty(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("black_hearts")) {
            return heartsOnHead(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("blue_shaded_glasses")) {
            return drawGlassesRedLined(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("blush")) {
            return blushOnFace(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("cat")) {
//            return replaceFace(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("cat_with_crown")) {
            return crownOnFace(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("cigarette")) {
//            return drawCigarette(tempCanvas, MOUTH_LEFT, MOUTH_RIGHT);
        } else if (filterName.contains("dog")) {
            return dogFilter(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("dog_face")) {
            return dogFace(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("fire_on_head")) {
            return fireOnHead(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("flowers_on_head")) {
            return flowerOnHead(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("memes_glasses")) {
            return drawGlasses(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("purple_glasses")) {
            return drawGlasses(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        } else if (filterName.contains("rabbit")) {
            return rabbitOnFace(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        }
//            else if(filterName.contains("bear_face_empty")){
//            case "red_hat":
//                if (Math.abs(eulerZ) > HEAD_TILT_HAT_THRESHOLD && Math.abs(eulerZ) < HEAD_TILT_HAT_THRESHOLD1) {
//                    drawHat(tempCanvas);
//                }
//                }
        else if (filterName.contains("simple_glasses")) {
            return drawGlasses(tempCanvas, bmp, LEFT_EYE, RIGHT_EYE);
        }

        return null;
    }

//    private Rect drawEye(Canvas tempCanvas, PointF eyePosition, float eyeRadius,
//                         PointF irisPosition, float irisRadius, boolean isOpen) {
//        if (isOpen) {
//            tempCanvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeWhitesPaint);
//            tempCanvas.drawCircle(irisPosition.x, irisPosition.y, irisRadius, mEyeIrisPaint);
//        } else {
//            tempCanvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeLidPaint);
//            float y = eyePosition.y;
//            float start = eyePosition.x - eyeRadius;
//            float end = eyePosition.x + eyeRadius;
//            tempCanvas.drawLine(start, y, end, y, mEyeOutlinePaint);
//        }
//        tempCanvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeOutlinePaint);
//    }

//    private Rect drawCigarette(Canvas tempCanvas, Bitmap finalBitmap, PointF leftMouth, PointF rightMouth) {
//        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();
//
//        if (leftMouth != null && rightMouth != null) {
//            int mouthLength = (int) ((translateX(leftMouth.x) - translateX(rightMouth.x)) * widthScaleFactor);
//
//            Rect cigaretteRect = new Rect(
//                    Math.round(translateX(leftMouth.x)) - mouthLength,
//                    Math.round(translateY(leftMouth.y)),
//                    Math.round(translateX(leftMouth.x)),
//                    Math.round(translateY(leftMouth.y)) + mouthLength
//            );
//
//            tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, cigaretteRect, null);
//        }
//    }

//    private Rect replaceFace(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
//        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();
//
//        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
//        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
//        Rect rectPoints = new Rect(
//                Math.round(LEFT_EYE.x) - delta,
//                Math.round(LEFT_EYE.y) - delta,
//                Math.round(RIGHT_EYE.x) + delta,
//                Math.round(RIGHT_EYE.y) + delta);
//        tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//    }

//    private Rect drawHat(Canvas tempCanvas) {
//        PointF facePosition = face.getPosition();
//        float faceWidth = scaleX(face.getWidth());
//        float faceHeight = scaleY(face.getHeight());
//
//        final float HAT_FACE_WIDTH_RATIO = (float) (1.0 / 4.0);
//        final float HAT_FACE_HEIGHT_RATIO = (float) (1.0 / 6.0);
//        final float HAT_CENTER_Y_OFFSET_FACTOR = (float) (1.0 / 8.0);
//
//        float hatCenterY = translateY(facePosition.y) + (faceHeight * HAT_CENTER_Y_OFFSET_FACTOR);
//        float hatWidth = faceWidth * HAT_FACE_WIDTH_RATIO;
//        float hatHeight = faceHeight * HAT_FACE_HEIGHT_RATIO;
//
//        int left = (int) (translateX(NOSE.x) - (hatWidth / 2));
//        int right = (int) (translateX(NOSE.x) + (hatWidth / 2));
//        int top = (int) (hatCenterY - (hatHeight / 2));
//        int bottom = (int) (hatCenterY + (hatHeight / 2));
//
//        Rect noseRect = new Rect(left, top, right, bottom);
//        tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, noseRect, null);
//
////        mHatGraphic.setBounds(left, top, right, bottom);
////        mHatGraphic.draw(tempCanvas);
//    }


    private Rect fireOnHead(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();
        tempCanvas.drawBitmap(finalBitmap, 0, 0, null);
        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect fireRect = new Rect(
                Math.round(LEFT_EYE.x) - (delta - delta / 2),
                Math.round(LEFT_EYE.y) - (delta * 2),
                Math.round(RIGHT_EYE.x) + delta + delta / 4,
                Math.round(RIGHT_EYE.y) - delta);

        return fireRect;
    }

    private Rect drawGlasses(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();
        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
//            face.getPosition().
//            Rect rectPoints = new Rect(
//                    Math.round(translateX(mLEFT_EYE.x)) - delta,
//                    Math.round(translateY(mLEFT_EYE.y)) - delta,
//                    Math.round(translateX(mRIGHT_EYE.x)) + delta,
//                    Math.round(translateY(mRIGHT_EYE.y)) + delta);
        Rect rectPoints = new Rect(
                Math.round(LEFT_EYE.x) - delta + delta / 6,
                Math.round(LEFT_EYE.y) - delta / 5,
                Math.round(RIGHT_EYE.x) + delta - delta / 6,
                Math.round(RIGHT_EYE.y) + delta / 5);

        return rectPoints;

//        int orientation = SetAndGetData.data.getActivity().getResources().getConfiguration().orientation;
//        if (face.getEulerY() > -15 && face.getEulerY() < 15 && face.getEulerZ() > -5 && face.getEulerZ() < 5) {
//            tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//        }
//        }
    }

    private Rect drawGlassesRedLined(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();
//        Landmark LEFT_EYE = face.getLandmarks().get(Landmark.LEFT_EYE);
//        Landmark RIGHT_EYE = face.getLandmarks().get(Landmark.RIGHT_EYE);
//        if (LEFT_EYE != null && RIGHT_EYE != null) {
        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
//            face.getPosition().
//            Rect rectPoints = new Rect(
//                    Math.round(translateX(mLEFT_EYE.x)) - delta,
//                    Math.round(translateY(mLEFT_EYE.y)) - delta,
//                    Math.round(translateX(mRIGHT_EYE.x)) + delta,
//                    Math.round(translateY(mRIGHT_EYE.y)) + delta);
        Rect rectPoints = new Rect(
                Math.round(LEFT_EYE.x) - delta + delta / 6,
                Math.round(LEFT_EYE.y) - delta / 2,
                Math.round(RIGHT_EYE.x) + delta - delta / 6,
                Math.round(RIGHT_EYE.y) + delta / 2);


//        int orientation = SetAndGetData.data.getActivity().getResources().getConfiguration().orientation;
//        if (face.getEulerY() > -15 && face.getEulerY() < 15 && face.getEulerZ() > -5 && face.getEulerZ() < 5) {
//            tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//        }
//        }
        return rectPoints;
    }

//    private Rect drawShiningGlasses(Canvas tempCanvas, Bitmap finalBitmap, PointF eyePosition, float eyeRadius,
//                                    PointF irisPosition, float irisRadius, boolean isOpen) {
//        Paint _paintSimple = new Paint();
//        _paintSimple.setAntiAlias(true);
//        _paintSimple.setDither(true);
//        _paintSimple.setColor(Color.argb(248, 255, 255, 255));
//        _paintSimple.setStrokeWidth(1f);
//        _paintSimple.setStyle(Paint.Style.STROKE);
//        _paintSimple.setStrokeJoin(Paint.Join.ROUND);
//        _paintSimple.setStrokeCap(Paint.Cap.ROUND);
//
//        Paint _paintBlur = new Paint();
//        _paintBlur.set(_paintSimple);
//        _paintBlur.setColor(Color.argb(235, 74, 138, 255));
//        _paintBlur.setStrokeWidth(15f);
//        _paintBlur.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
//
////            tempCanvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius - 15, _paintBlur);
////        tempCanvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, _paintBlur);
////        tempCanvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, _paintSimple);
////            tempCanvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius - 15, _paintSimple);
//
////        return rectPoints;
//    }

    private Rect crownOnFace(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();

//        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        float eyeDistance = RIGHT_EYE.x - LEFT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
//        Rect rectPoints = new Rect(
//                Math.round(LEFT_EYE.x) - delta,
//                Math.round(LEFT_EYE.y) - (delta + delta / 6),
//                Math.round(RIGHT_EYE.x) + delta,
//                Math.round(RIGHT_EYE.y) + delta / 2);

        Rect rectPoints = new Rect(
                Math.round(LEFT_EYE.x) - delta / 2,
                Math.round(LEFT_EYE.y) - (delta + delta / 6),
                Math.round(RIGHT_EYE.x) + delta / 2,
                Math.round(RIGHT_EYE.y) + delta / 2);
//                Math.round(LEFT_EYE.x) - 115,
//                Math.round(LEFT_EYE.y) - (30),
//                Math.round(RIGHT_EYE.x) + 115,
//                Math.round(RIGHT_EYE.y) + 200);

        tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//        tempCanvas.drawLine(LEFT_EYE.x, LEFT_EYE.y, LEFT_EYE.x + 115, LEFT_EYE.y, _paintBlur);
        return rectPoints;
    }

    private Rect rabbitOnFace(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect rectPoints = new Rect(
                Math.round(LEFT_EYE.x) - (delta + delta / 4),
                Math.round(LEFT_EYE.y) - (delta + delta / 25),
                Math.round(RIGHT_EYE.x) + (delta + delta / 4),
                Math.round(RIGHT_EYE.y) + (delta / 2));
//        if (face.getEulerY() > -15 && face.getEulerY() < 15 && face.getEulerZ() > -15 && face.getEulerZ() < 15) {
//            tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//        }
        return rectPoints;
    }

    private Rect dogFilter(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect rectPoints = new Rect(
                Math.round(LEFT_EYE.x) - (delta + delta / 4),
                Math.round(LEFT_EYE.y) - (delta + delta / 35),
                Math.round(RIGHT_EYE.x) + (delta),
                Math.round(RIGHT_EYE.y) + (delta + delta / 2));
//        if (face.getEulerY() > -15 && face.getEulerY() < 15 && face.getEulerZ() > -12 && face.getEulerZ() < 12) {
//            tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//        }
        return rectPoints;
    }

    private Rect flowerOnHead(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect rectPoints = new Rect(
                Math.round(LEFT_EYE.x) - (delta),
                Math.round(LEFT_EYE.y) - (delta),
                Math.round(RIGHT_EYE.x) + (delta),
                Math.round(RIGHT_EYE.y) + delta / 3);
//        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -18 && face.getEulerZ() < 18) {
//            tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//        }
        return rectPoints;
    }

    private Rect heartsOnHead(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect rectPoints = new Rect(
                Math.round(LEFT_EYE.x) - (delta),
                Math.round(LEFT_EYE.y) - (delta - delta / 10),
                Math.round(RIGHT_EYE.x) + (delta),
                Math.round(RIGHT_EYE.y) + delta / 20);

//        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -18 && face.getEulerZ() < 18) {
//        tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//        }
        return rectPoints;
    }

    private Rect blushOnFace(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect rectPoints = new Rect(
                Math.round(LEFT_EYE.x) - delta,
                Math.round(LEFT_EYE.y) - delta / 10,
                Math.round(RIGHT_EYE.x) + delta,
                Math.round(RIGHT_EYE.y) + delta / 2);
//        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -18 && face.getEulerZ() < 18) {
//            tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//        }
        return rectPoints;
    }

    private Rect dogFace(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect rectPoints = new Rect(
                Math.round(LEFT_EYE.x) - (delta + delta / 6),
                Math.round(LEFT_EYE.y) - (delta + delta / 15),
                Math.round(RIGHT_EYE.x) + (delta + delta / 6),
                Math.round(RIGHT_EYE.y) + (delta + delta / 15));
//        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -18 && face.getEulerZ() < 18) {
//            tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//        }
        return rectPoints;
    }

    private Rect bearFace(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect rectPoints = new Rect(
                Math.round(LEFT_EYE.x) - (delta + delta / 10),
                Math.round(LEFT_EYE.y) - (delta - delta / 10),
                Math.round(RIGHT_EYE.x) + (delta + delta / 10),
                Math.round(RIGHT_EYE.y) + (delta));
//        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -14 && face.getEulerZ() < 14) {
//            tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//        }
        return rectPoints;
    }

    private Rect bearFaceEmpty(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();

        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect rectPoints = new Rect(
                Math.round(LEFT_EYE.x) - (delta + delta / 10),
                Math.round(LEFT_EYE.y) - (delta - delta / 10),
                Math.round(RIGHT_EYE.x) + (delta + delta / 10),
                Math.round(RIGHT_EYE.y) + (delta));
//        if (face.getEulerY() > -18 && face.getEulerY() < 18 && face.getEulerZ() > -14 && face.getEulerZ() < 14) {
//            tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, rectPoints, null);
//        }
        return rectPoints;
    }

    private Rect yellow_mask(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE) {
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();

//        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
//        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
//        Rect rectPoints = new Rect(
//                Math.round(LEFT_EYE.x) - (delta + delta / 8),
//                Math.round(LEFT_EYE.y) - delta / 8,
//                Math.round(RIGHT_EYE.x) + (delta + delta / 8),
//                Math.round(RIGHT_EYE.y) + (delta + delta / 15));
        Rect rectPoints;
        if (SetAndGetData.data.isFronCam()) {
            float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
            int delta = Math.round(widthScaleFactor * eyeDistance / 2);
            rectPoints = new Rect(
                    Math.round(LEFT_EYE.x) - (delta/2 + delta/8),
                    Math.round(LEFT_EYE.y) - (delta/2 + delta/4),
                    Math.round(RIGHT_EYE.x) + (delta + delta/7),
                    Math.round(RIGHT_EYE.y) + (delta));
        }else{
            float eyeDistance = RIGHT_EYE.x - LEFT_EYE.x;
            int delta = Math.round(widthScaleFactor * eyeDistance / 2);
            rectPoints = new Rect(
                    Math.round(LEFT_EYE.x) - (delta - delta/4),
                    Math.round(LEFT_EYE.y) - delta / 6,
                    Math.round(RIGHT_EYE.x) + (delta - delta/4),
                    Math.round(RIGHT_EYE.y) + (delta + delta / 2 + delta/4));
        }
        return rectPoints;
    }
}
