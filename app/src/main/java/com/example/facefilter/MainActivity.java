package com.example.facefilter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.facefilter.CameraAndOverlay.CameraSourcePreview;
import com.example.facefilter.CameraAndOverlay.GraphicOverlay;
import com.example.facefilter.Singelton.SetAndGetData;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor;
import com.google.android.material.snackbar.Snackbar;
import com.mindorks.Screenshot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private boolean mIsFrontFacing = true;
    ImageView button_capture, facing_switch;
    ConstraintLayout imagePreviewConstrint;
    RelativeLayout cameraRetakeContainer;
    ImageView resultImage;
    LinearLayout tv_ok, button_retake;
    ConstraintLayout camPrew;
    FaceDetector detector;
    Bitmap resultedBmp;
    LottieAnimationView loader;
    private ConstraintLayout save_or_not_header;
    private HorizontalScrollView filters_scroller;

    private Bitmap filterOnFace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetAndGetData.data.setActivity(this);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        filters_scroller = findViewById(R.id.filters_scroller);
        save_or_not_header = findViewById(R.id.save_or_not_header);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        facing_switch = findViewById(R.id.facing_switch);
        button_capture = findViewById(R.id.button_capture);
        imagePreviewConstrint = findViewById(R.id.imagePreviewConstrint);
        cameraRetakeContainer = findViewById(R.id.cameraRetakeContainer);
        resultImage = findViewById(R.id.resultImage);
        tv_ok = findViewById(R.id.tv_ok);
        button_retake = findViewById(R.id.button_retake);
        loader = findViewById(R.id.loader);
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loader.setVisibility(View.VISIBLE);
//                mPreview.setVisibility(View.GONE);
//                filters_scroller.setVisibility(View.GONE);
                tv_ok.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
                button_retake.setVisibility(View.GONE);
//                storeImage(resultedBmp);
                new CallModel(resultedBmp).execute();
            }
        });

        button_retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loader.setVisibility(View.GONE);
                imagePreviewConstrint.setVisibility(View.GONE);
                mPreview.setVisibility(View.VISIBLE);
                filters_scroller.setVisibility(View.VISIBLE);
                save_or_not_header.setVisibility(View.GONE);
                cameraRetakeContainer.setVisibility(View.GONE);
                tv_ok.setVisibility(View.VISIBLE);
                button_retake.setVisibility(View.VISIBLE);
            }
        });

        camPrew = findViewById(R.id.camPrev);
        mGraphicOverlay.getLayoutParams().width = mPreview.getWidth();
        switchCamera();


        button_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loader.setVisibility(View.VISIBLE);
//                mPreview.setVisibility(View.GONE);
//                filters_scroller.setVisibility(View.GONE);
                takePhoto();

            }
        });

        if (savedInstanceState != null) {
            mIsFrontFacing = savedInstanceState.getBoolean("IsFrontFacing");
        }

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    public void setFilterIcon(View view) {
        switch (view.getId()) {
            case R.id.no_filter:
                SetAndGetData.data.setIcon(null);
                SetAndGetData.data.setFilterName("no_filter");
                break;
            case R.id.yellow_face_mask:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_face_mask);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("yellow_face_mask");
                break;
            case R.id.bear_face:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.bear_face);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("bear_face");
                break;
            case R.id.bear_face_empty:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.bear_face_empty);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("bear_face_empty");
                break;
            case R.id.black_hearts:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.black_hearts);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("black_hearts");
                break;
            case R.id.blue_shaded_glasses:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.blue_shaded_glasses);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("blue_shaded_glasses");
                break;
            case R.id.blush:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.blush);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("blush");
                break;
            case R.id.cat:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.cat);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("cat");
                break;
            case R.id.cat_with_crown:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.cat_with_crown);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("cat_with_crown");
                break;
            case R.id.cigarette:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.cigarette);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("cigarette");
                break;
            case R.id.dog:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.dog);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("dog");
                break;
//            case R.id.:
//
//                SetAndGetData.data.setFilterName("");
//            break;
            case R.id.dog_face:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.dog_face);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("dog_face");
                break;
            case R.id.fire_on_head:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.fire_on_head);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("fire_on_head");
                break;
            case R.id.flowers_on_head:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.flowers_on_head);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("flowers_on_head");
                break;
            case R.id.memes_glasses:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.memes_glasses);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("memes_glasses");
                break;
            case R.id.purple_glasses:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.purple_glasses);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("purple_glasses");
                break;
            case R.id.rabbit:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.rabbit);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("rabbit");
                break;
            case R.id.red_hat:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.red_hat);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("red_hat");
                break;
            case R.id.simple_glasses:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.simple_glasses);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("simple_glasses");
                break;
            default:
                SetAndGetData.data.setFilterName("");
                break;
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            try {
                mCameraSource.release();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Face Tracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("IsFrontFacing", mIsFrontFacing);
    }

    private View.OnClickListener mFlipButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            mIsFrontFacing = !mIsFrontFacing;

            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }

            createCameraSource();
            startCameraSource();
        }
    };

    private void switchCamera() {
        facing_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIsFrontFacing = !mIsFrontFacing;

                if (mCameraSource != null) {
                    try {
                        mCameraSource.release();
                    } catch (Exception e) {

                    }
                    mCameraSource = null;
                }

                createCameraSource();
                startCameraSource();
            }
        });
    }

    @NonNull
    private FaceDetector createFaceDetector(Context context) {

        detector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(mIsFrontFacing)
                .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                .build();


        Detector.Processor<Face> processor;
        if (mIsFrontFacing) {
            Tracker<Face> tracker = new GetAndSetFace(mGraphicOverlay);
            processor = new LargestFaceFocusingProcessor.Builder(detector, tracker).build();
        } else {
            MultiProcessor.Factory<Face> factory = new MultiProcessor.Factory<Face>() {
                @Override
                public Tracker<Face> create(Face face) {
                    return new GetAndSetFace(mGraphicOverlay);
                }
            };
            processor = new MultiProcessor.Builder<>(factory).build();
        }

        detector.setProcessor(processor);

        if (!detector.isOperational()) {
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }
        return detector;
    }

    public static Bitmap flip(Bitmap src) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    private void createCameraSource() {
        Context context = getApplicationContext();
        FaceDetector detector = createFaceDetector(context);

        int facing = CameraSource.CAMERA_FACING_FRONT;
        if (!mIsFrontFacing) {
            facing = CameraSource.CAMERA_FACING_BACK;
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setFacing(facing)
                .setRequestedPreviewSize(320, 240)
                .setRequestedFps(60.0f)
                .setAutoFocusEnabled(true)
                .build();
    }

    private void startCameraSource() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    public void takePhoto() {

        CameraSource.ShutterCallback shutterCallback = new CameraSource.ShutterCallback() {
            public void onShutter() {
                Log.d(TAG, "onShutter: ");
            }
        };

        CameraSource.PictureCallback jpegCallback = new CameraSource.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Frame outputFrame = new Frame.Builder().setBitmap(bitmap).build();
                try {
                    Face detectedFace = detector.detect(outputFrame).valueAt(0);
                    if (detectedFace != null) {

                        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                        Canvas tempCanvas = new Canvas(bitmap);
                        Bitmap finalBitmap = bitmap;
                        GraphicOverlay.Graphic graphic = new GraphicOverlay.Graphic(mGraphicOverlay) {
                            @Override
                            public void draw(Canvas tempCanvas) {
                                PointF LEFT_EYE = detectedFace.getLandmarks().get(Landmark.LEFT_EYE).getPosition();
                                PointF RIGHT_EYE = detectedFace.getLandmarks().get(Landmark.RIGHT_EYE).getPosition();

                                ApplyFilterOnCaptured applyFilterOnCaptured = new ApplyFilterOnCaptured();
                                Rect filterRect = applyFilterOnCaptured.drawDesiredFilter(tempCanvas, finalBitmap, LEFT_EYE, RIGHT_EYE);
                                tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, filterRect, null);

                                save_or_not_header.setVisibility(View.VISIBLE);
                                filters_scroller.setVisibility(View.GONE);
//                                loader.setVisibility(View.GONE);
                                cameraRetakeContainer.setVisibility(View.VISIBLE);
                                imagePreviewConstrint.setVisibility(View.VISIBLE);
                                mPreview.setVisibility(View.GONE);
                                resultedBmp = flip(finalBitmap);
                                resultImage.setImageBitmap(resultedBmp);
                            }
                        };
                        graphic.draw(tempCanvas);
                        Log.i("faizan", "detected");
                    }
                } catch (Exception e) {
                    Log.i("faizan", "e.getMessage()");
                }


//                Bitmap copy = new Bitmap(bitmap);
//                Frame outputFrame = new Frame.Builder().setBitmap(bitmap).build();
//                try {
//                    Face f = detector.detect(outputFrame).valueAt(0);
//                    if (f != null){
//                        Paint mEyeLidPaint = new Paint();
//                        mEyeLidPaint.setColor(Color.WHITE);
//                        mEyeLidPaint.setStyle(Paint.Style.FILL);
//                        mEyeLidPaint.setStrokeWidth(25);
//
////                        Bitmap finalBitmap = bitmap;
//                        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//                        Canvas tempCanvas = new Canvas(bitmap);
//                        Bitmap finalBitmap = bitmap;
//                        GraphicOverlay.Graphic graphic = new GraphicOverlay.Graphic(mGraphicOverlay) {
//                            @Override
//                            public void draw(Canvas tempCanvas) {
////                                Canvas tempCanvas = new Canvas(bitmap);
////                        Bitmap workingBitmap = Bitmap.createBitmap(outputFrame);
////                        Bitmap mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true);
////                        Canvas canvas = new Canvas(mutableBitmap);
////                        Canvas canvas = new Canvas(workingBitmap);
//
////                        Canvas tempCanvas = new Canvas(bitmap);
//                                float widthScaleFactor = tempCanvas.getWidth() / SetAndGetData.data.getPreviewWidth();
//                                tempCanvas.drawBitmap(finalBitmap, 0, 0, null);
//                                float eyeDistance = f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x - f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x;
//                                int delta = Math.round(widthScaleFactor * eyeDistance / 2);
////                                Rect glassesRect = new Rect(
////                                        Math.round(leftEye.x) - (delta/2),
////                                        Math.round(leftEye.y) - (delta),
////                                        Math.round(rightEye.x) + (delta/2),
////                                        Math.round(rightEye.y) - delta / 6);
//                                Rect glassesRect = new Rect(
//                                        Math.round(f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x) - (delta - delta/2),
//                                        Math.round(f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().y) - (delta*2),
//                                        Math.round(f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x) + delta + delta/4,
//                                        Math.round(f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().y) - delta);
//                                tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, glassesRect, null);
//
////                                tempCanvas.drawLine(f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x-50, f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x + 10, f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x-100, f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x + 10, mEyeLidPaint);
////                        tempCanvas.drawLine(f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x, f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x, f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x, f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x, mEyeLidPaint);
////                                tempCanvas.drawLine(translateX(f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x), translateY(f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x), translateX(f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x), translateY(f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x), mEyeLidPaint);
//                                cameraRetakeContainer.setVisibility(View.VISIBLE);
//                                imagePreviewConstrint.setVisibility(View.VISIBLE);
//                                mPreview.setVisibility(View.GONE);
//                                resultImage.setImageBitmap(flip(finalBitmap));
////                resultImage.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
//                                resultImage1.setVisibility(View.GONE);
//                            }
//                        };
//
//                        graphic.draw(tempCanvas);
//
////                        Canvas tempCanvas = new Canvas(bitmap);
////                        tempCanvas.drawBitmap(bitmap, 0, 0, null);
////                        tempCanvas.drawLine(f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x, f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().y, f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x, f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().y, mEyeLidPaint);
////                        tempCanvas.drawLine(f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().x, f.getLandmarks().get(Landmark.LEFT_EYE).getPosition().y, f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().x, f.getLandmarks().get(Landmark.RIGHT_EYE).getPosition().y, mEyeLidPaint);
//
//                        //                        tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, SetAndGetData.data.getRect(), null);
//                        Log.i("faizan", "detected");
//                    }
//                }catch(Exception e){
//                    Log.i("faizan", e.getMessage());
//                }

//                SetAndGetData.data.getCanvas().drawBitmap(SetAndGetData.data.getIcon(), null, SetAndGetData.data.getRect(), null);
////                Bitmap bb = Bitmap.createBitmap(bitmap, 0,0,w1, h1);
//                Bitmap tempBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
//                Canvas tempCanvas = new Canvas(tempBitmap);
//                tempCanvas.drawBitmap(bitmap, 0, 0, null);
//                tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, SetAndGetData.data.getRect(), null);
//                cameraRetakeContainer.setVisibility(View.VISIBLE);
//                imagePreviewConstrint.setVisibility(View.VISIBLE);
//                mPreview.setVisibility(View.GONE);
//                resultImage.setImageBitmap(finalBitmap);
////                resultImage.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
//                resultImage1.setVisibility(View.GONE);
//                int a = 0;

//                getBitmap(bytes);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                Bitmap mutableBitmap = null;
//                try {
//                    //for a PORTRAIT overlay and taking the image holding the phone in PORTRAIT mode
//                    mutableBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options).copy(Bitmap.Config.RGB_565, true);
//                    Matrix matrix = new Matrix();
//                    int width = mutableBitmap.getWidth();
//                    int height = mutableBitmap.getHeight();
//                    int newWidth = overlayImage.getDrawable().getBounds().width();
//                    int newHeight = overlayImage.getDrawable().getBounds().height();
//                    float scaleWidth = ((float) newWidth) / width;
//                    float scaleHeight = ((float) newHeight) / height;
//                    matrix.postScale(scaleWidth, scaleHeight);
//                    matrix.postRotate(90);
//
//                    Bitmap resizedBitmap = Bitmap.createBitmap(mutableBitmap, 0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight(), matrix, true);
//                    finalBitmap = resizedBitmap.copy(Bitmap.Config.RGB_565, true);
//                    Canvas canvas = new Canvas(finalBitmap);
//
//                    Bitmap overlayBitmap = BitmapFactory.decodeResource(getResources(), overlay);
//                    matrix = new Matrix();
//                    matrix.postRotate(90);
//                    Bitmap resizedOverlay = Bitmap.createBitmap(overlayBitmap, 0, 0, overlayBitmap.getWidth(), overlayBitmap.getHeight(), matrix, true);
//                    canvas.drawBitmap(resizedOverlay, 0, 0, new Paint());
//                    canvas.scale(50, 0);
//                    canvas.save();
//                    //finalBitmap is the image with the overlay on it
//                }
//                catch(OutOfMemoryError e) {
//                    //fail
//                }


//                try{
//                // convert byte array into bitmap
//                Bitmap loadedImage = null;
//                Bitmap rotatedBitmap = null;
//                loadedImage = BitmapFactory.decodeByteArray(bytes, 0,
//                        bytes.length);
////                        rotateMatrix.postRotate(getWindowManager().getDefaultDisplay().getRotation());
//
//                // rotate Image
//                Matrix rotateMatrix = new Matrix();
//
//                int degreesTo = 360;
//                if (mCameraSource.getCameraFacing() == CameraSource.CAMERA_FACING_BACK) {
//                    degreesTo = 90;
//                }
//
//                rotateMatrix.postRotate(degreesTo);//90 degrees for Back, 270 degrees for Front
//                rotatedBitmap = Bitmap.createBitmap(loadedImage, 0, 0,
//                        loadedImage.getWidth(), loadedImage.getHeight(),
//                        rotateMatrix, false);
//                Bitmap loadedBitmap = loadBitmapFromView(mGraphicOverlay);
////                showImageDialog(rotatedBitmap, loadedBitmap);
//
//
////                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
////                Bitmap loadedBitmap = loadBitmapFromView(mGraphicOverlay);
//                cameraRetakeContainer.setVisibility(View.VISIBLE);
//                imagePreviewConstrint.setVisibility(View.VISIBLE);
//                mPreview.setVisibility(View.GONE);
//                resultImage.setImageBitmap(rotatedBitmap);
//                resultImage1.setImageBitmap(loadedBitmap);
//                int a = 0;
//
//            } catch (Exception e) {
//                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//                e.printStackTrace();
//            }


//                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                Bitmap loadedBitmap = loadBitmapFromView(mGraphicOverlay);
//                cameraRetakeContainer.setVisibility(View.VISIBLE);
//                imagePreviewConstrint.setVisibility(View.VISIBLE);
//                mPreview.setVisibility(View.GONE);
//                resultImage.setImageBitmap(bitmap);
//                resultImage1.setImageBitmap(loadedBitmap);
//                int a = 0;
            }
        };
        mCameraSource.takePicture(shutterCallback, jpegCallback);
    }

    private void storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            loader.setVisibility(View.GONE);
            Toast.makeText(this, "Could not save.", Toast.LENGTH_SHORT).show();
            imagePreviewConstrint.setVisibility(View.GONE);
            mPreview.setVisibility(View.VISIBLE);
            filters_scroller.setVisibility(View.VISIBLE);
            cameraRetakeContainer.setVisibility(View.GONE);
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            loader.setVisibility(View.GONE);
            imagePreviewConstrint.setVisibility(View.GONE);
            mPreview.setVisibility(View.VISIBLE);
            filters_scroller.setVisibility(View.VISIBLE);

            cameraRetakeContainer.setVisibility(View.GONE);
        } catch (FileNotFoundException e) {
            loader.setVisibility(View.GONE);
            imagePreviewConstrint.setVisibility(View.GONE);
            mPreview.setVisibility(View.VISIBLE);
            filters_scroller.setVisibility(View.VISIBLE);
            cameraRetakeContainer.setVisibility(View.GONE);
            Toast.makeText(this, "Could not save.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            loader.setVisibility(View.GONE);
            imagePreviewConstrint.setVisibility(View.GONE);
            mPreview.setVisibility(View.VISIBLE);
            filters_scroller.setVisibility(View.VISIBLE);
            cameraRetakeContainer.setVisibility(View.GONE);
            Toast.makeText(this, "Could not save.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private  File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/DCIM/FaceFilters");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.i("Faizan", "noo noo noo");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="MI_"+ timeStamp +".jpg";
        mediaFile = new File(mediaStorageDir + File.separator + mImageName);
        return mediaFile;
    }

    class CallModel extends AsyncTask<String, String, Boolean> {
        Bitmap image;
        public CallModel(Bitmap resultedBmp) {
            image= resultedBmp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... f_url) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
//                loader.setVisibility(View.GONE);
////                Toast.makeText(this, "Could not save.", Toast.LENGTH_SHORT).show();
//                imagePreviewConstrint.setVisibility(View.GONE);
//                mPreview.setVisibility(View.VISIBLE);
//                filters_scroller.setVisibility(View.VISIBLE);
//                cameraRetakeContainer.setVisibility(View.GONE);
                Log.d(TAG,
                        "Error creating media file, check storage permissions: ");// e.getMessage());
                return false;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                image.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
                return true;
//                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
//                loader.setVisibility(View.GONE);
//                imagePreviewConstrint.setVisibility(View.GONE);
//                mPreview.setVisibility(View.VISIBLE);
//                filters_scroller.setVisibility(View.VISIBLE);

//                cameraRetakeContainer.setVisibility(View.GONE);
            } catch (FileNotFoundException e) {
//                loader.setVisibility(View.GONE);
//                imagePreviewConstrint.setVisibility(View.GONE);
//                mPreview.setVisibility(View.VISIBLE);
//                filters_scroller.setVisibility(View.VISIBLE);
//                cameraRetakeContainer.setVisibility(View.GONE);
//                Toast.makeText(this, "Could not save.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "File not found: " + e.getMessage());
                return false;
            } catch (IOException e) {
//                loader.setVisibility(View.GONE);
//                imagePreviewConstrint.setVisibility(View.GONE);
//                mPreview.setVisibility(View.VISIBLE);
//                filters_scroller.setVisibility(View.VISIBLE);
//                cameraRetakeContainer.setVisibility(View.GONE);
//                Toast.makeText(this, "Could not save.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Error accessing file: " + e.getMessage());
                return false;
            }
//            return true;
        }

        protected void onProgressUpdate(String... progress) {

        }

        @Override
        protected void onPostExecute(Boolean res) {
            if (!res){
                Toast.makeText(MainActivity.this, "Unable to save it.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(MainActivity.this, "Saved successfully", Toast.LENGTH_SHORT).show();
            }
            loader.setVisibility(View.GONE);
            imagePreviewConstrint.setVisibility(View.GONE);
            mPreview.setVisibility(View.VISIBLE);
            filters_scroller.setVisibility(View.VISIBLE);
            save_or_not_header.setVisibility(View.GONE);
            cameraRetakeContainer.setVisibility(View.GONE);
            tv_ok.setVisibility(View.VISIBLE);
            button_retake.setVisibility(View.VISIBLE);
        }

    }

}