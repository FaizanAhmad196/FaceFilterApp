package com.example.facefilter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;

import com.example.facefilter.Singelton.SetAndGetData;
import com.google.android.gms.vision.face.Landmark;

public class ApplyFilterOnCaptured {

    public Rect drawDesiredFilter(Canvas canvas, Bitmap bmp, PointF leftEye, PointF rightEye) {
        String filterName = SetAndGetData.data.getFilterName();
//            case "bear_face":
//                bearFace(canvas, leftPosition, rightPosition);
//                break;
//            case "yellow_face_mask":
//                yellow_mask(canvas, leftPosition, rightPosition);
//                break;
//            case "bear_face_empty":
//                bearFaceEmpty(canvas, leftPosition, rightPosition);
//                break;
//            case "black_hearts":
//                heartsOnHead(canvas, leftPosition, rightPosition);
//                break;
//            case "blue_shaded_glasses":
//                drawGlassesRedLined(canvas, leftPosition, rightPosition, distance);
//                break;
//            case "blush":
//                blushOnFace(canvas, leftPosition, rightPosition);
//                break;
//            case "cat":
//                replaceFace(canvas, leftPosition, rightPosition);
//                break;
//            case "cat_with_crown":
//                crownOnFace(canvas, leftPosition, rightPosition);
//                break;
//            case "cigarette":
//                drawCigarette(canvas, MOUTH_LEFT, MOUTH_RIGHT);
//                break;
//            case "dog":
//                dogFilter(canvas, leftPosition, rightPosition);
//                break;
////            case :
////
////                break;
//            case "dog_face":
//                dogFace(canvas, leftPosition, rightPosition);
//                break;
        if(filterName.contains("fire_on_head")){
            return fireOnHead(canvas, bmp, leftEye, rightEye);
        }
//            case "flowers_on_head":
//                flowerOnHead(canvas, leftPosition, rightPosition);
//                break;
//            case "memes_glasses":
//                drawGlasses(canvas, leftPosition, rightPosition, distance);
//                break;
//            case "purple_glasses":
//                drawGlasses(canvas, leftPosition, rightPosition, distance);
//                break;
//            case "rabbit":
//                rabbitOnFace(canvas, leftPosition, rightPosition);
//                break;
//            case "red_hat":
//                if (Math.abs(eulerZ) > HEAD_TILT_HAT_THRESHOLD && Math.abs(eulerZ) < HEAD_TILT_HAT_THRESHOLD1) {
//                    drawHat(canvas);
//                }
//                break;
//            case "simple_glasses":
//                drawGlasses(canvas, leftPosition, rightPosition, distance);
//                break;

        return null;
    }


    private Rect fireOnHead(Canvas tempCanvas, Bitmap finalBitmap, PointF LEFT_EYE, PointF RIGHT_EYE){
        float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();
        tempCanvas.drawBitmap(finalBitmap, 0, 0, null);
        float eyeDistance = LEFT_EYE.x - RIGHT_EYE.x;
        int delta = Math.round(widthScaleFactor * eyeDistance / 2);
        Rect fireRect = new Rect(
                Math.round(LEFT_EYE.x) - (delta - delta/2),
                Math.round(LEFT_EYE.y) - (delta*2),
                Math.round(RIGHT_EYE.x) + delta + delta/4,
                Math.round(RIGHT_EYE.y) - delta);

        return fireRect;
    }
}
