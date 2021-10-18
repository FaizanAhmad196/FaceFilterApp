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
//import android.hardware.camera2.CameraAccessException;
//import android.hardware.camera2.CameraManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
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
import androidx.core.content.FileProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.utils.Utils;
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
import java.util.Random;

public class MainActivity extends AppCompatActivity {
//    private CameraManager mCameraManager;
    private String mCameraId;
    private static final String TAG = "TAG";
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final int RC_WRITE_STORAGE = 3;
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private boolean mIsFrontFacing = false;
    ImageView button_capture, facing_switch;
    ConstraintLayout imagePreviewConstrint;
    RelativeLayout cameraRetakeContainer, ssLayout, cameraCaptureContainer, filterCategories;
    ImageView resultImage, resultImage1;
    LinearLayout tv_ok, button_retake, filter_container, share_btn;
    ConstraintLayout camPrew;
    FaceDetector detector;
    Bitmap resultedBmp;
    LottieAnimationView loader;
    private ConstraintLayout save_or_not_header, scrollerLayout, no_filter_header, ok_fil;
    private HorizontalScrollView glasses_filters_scroller, filters_cat_scroller, mask_filters_scroller, cap_filters_scroller,
            crown_filters_scroller, hearts_filters_scroller, face_filters_scroller;
    private Bitmap filterOnFace;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SetAndGetData.data.setActivity(this);
        ssLayout = findViewById(R.id.camActivity);
        ssLayout.setDrawingCacheEnabled(true);
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);

        filters_cat_scroller = findViewById(R.id.filters_cat_scroller);
        mask_filters_scroller = findViewById(R.id.mask_filters_scroller);
        cap_filters_scroller = findViewById(R.id.cap_filters_scroller);
        crown_filters_scroller = findViewById(R.id.crown_filters_scroller);
        hearts_filters_scroller = findViewById(R.id.hearts_filters_scroller);
        face_filters_scroller = findViewById(R.id.face_filters_scroller);
        glasses_filters_scroller = findViewById(R.id.glasses_filters_scroller);

        no_filter_header = findViewById(R.id.no_filter_header);
        scrollerLayout = findViewById(R.id.scrollerLayout);
        ok_fil = findViewById(R.id.ok_fil);


        save_or_not_header = findViewById(R.id.save_or_not_header);
        cameraCaptureContainer = findViewById(R.id.cameraCaptureContainer);
        filterCategories = findViewById(R.id.filterCategories);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        facing_switch = findViewById(R.id.facing_switch);
        button_capture = findViewById(R.id.button_capture);
        imagePreviewConstrint = findViewById(R.id.imagePreviewConstrint);
        cameraRetakeContainer = findViewById(R.id.cameraRetakeContainer);
        resultImage = findViewById(R.id.resultImage);
        resultImage1 = findViewById(R.id.resultImage1);
        tv_ok = findViewById(R.id.tv_ok);
        button_retake = findViewById(R.id.button_retake);
        loader = findViewById(R.id.loader);
        filter_container = findViewById(R.id.filter_container);
        share_btn = findViewById(R.id.share_btn);

        filter_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraRetakeContainer.setVisibility(View.GONE);
                cameraCaptureContainer.setVisibility(View.GONE);
                filterCategories.setVisibility(View.VISIBLE);
            }
        });

        ok_fil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraRetakeContainer.setVisibility(View.GONE);
                cameraCaptureContainer.setVisibility(View.VISIBLE);
                filterCategories.setVisibility(View.GONE);
            }
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SetAndGetData.data.getToShareUri() == null) {
                    shareBitmap(getScreenShot());
                }else {
                    share(SetAndGetData.data.getToShareUri());
                }
            }
        });


//        boolean isFlashAvailable = getApplicationContext().getPackageManager()
//                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
//        if (!isFlashAvailable) {
//            showNoFlashError();
//        }
//        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        try {
//            mCameraId = mCameraManager.getCameraIdList()[0];
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }



        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rc = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    saveImg();
                } else {
                    requestStoragePermission();
                }
//                saveImg();
            }
        });

        button_retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultImage1.setVisibility(View.GONE);
                loader.setVisibility(View.GONE);
                imagePreviewConstrint.setVisibility(View.GONE);
                mPreview.setVisibility(View.VISIBLE);
                scrollerLayout.setVisibility(View.GONE);
                no_filter_header.setVisibility(View.VISIBLE);
                save_or_not_header.setVisibility(View.GONE);
                cameraRetakeContainer.setVisibility(View.GONE);
                tv_ok.setVisibility(View.VISIBLE);
                button_retake.setVisibility(View.VISIBLE);

                SetAndGetData.data.setToShareUri(null);
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
        SetAndGetData.data.setFronCam(mIsFrontFacing);
    }

    private void saveImg(){
//                loader.setVisibility(View.VISIBLE);
//                mPreview.setVisibility(View.GONE);
//                filters_scroller.setVisibility(View.GONE);
        tv_ok.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        button_retake.setVisibility(View.GONE);
//                Bitmap ss = Screenshot.INSTANCE.with(MainActivity.this).getScreenshot();
//                storeImage(resultedBmp);
        new CallModel(getScreenShot()).execute();
    }

    public void showNoFlashError() {
        AlertDialog alert = new AlertDialog.Builder(this)
                .create();
        alert.setTitle("Oops!");
        alert.setMessage("Flash not available in this device...");
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alert.show();
    }

    public void switchFlashLight(boolean status) {
//        try {
//            mCameraManager.setTorchMode(mCameraId, status);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
    }

    public void setFilterIcon(View view) {
        switch (view.getId()) {
            case R.id.no_filter_cap:
            case R.id.no_filter_glasses:
            case R.id.no_filter_crown:
            case R.id.no_filter_face:
            case R.id.no_filter_hearts:
            case R.id.no_filter_mask:
                SetAndGetData.data.setIcon(null);
                SetAndGetData.data.setFilterName("no_filter");
                break;
            case R.id.yellow_face_mask:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.yellow_face_mask);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("yellow_face_mask");
                break;


            case R.id.no_filter_cat:
                no_filter_header.setVisibility(View.VISIBLE);
                save_or_not_header.setVisibility(View.GONE);
                scrollerLayout.setVisibility(View.GONE);
                break;
            case R.id.yellow_face_mask_cat:
                no_filter_header.setVisibility(View.GONE);
                save_or_not_header.setVisibility(View.GONE);
                scrollerLayout.setVisibility(View.VISIBLE);

                crown_filters_scroller.setVisibility(View.GONE);
                cap_filters_scroller.setVisibility(View.GONE);
                glasses_filters_scroller.setVisibility(View.GONE);
                face_filters_scroller.setVisibility(View.GONE);
                hearts_filters_scroller.setVisibility(View.GONE);
                mask_filters_scroller.setVisibility(View.VISIBLE);
                break;
            case R.id.black_hearts_cat:
                no_filter_header.setVisibility(View.GONE);
                save_or_not_header.setVisibility(View.GONE);
                scrollerLayout.setVisibility(View.VISIBLE);

                crown_filters_scroller.setVisibility(View.GONE);
                cap_filters_scroller.setVisibility(View.GONE);
                glasses_filters_scroller.setVisibility(View.GONE);
                face_filters_scroller.setVisibility(View.GONE);
                hearts_filters_scroller.setVisibility(View.VISIBLE);
                mask_filters_scroller.setVisibility(View.GONE);
                break;
            case R.id.dog_face_cat:
                no_filter_header.setVisibility(View.GONE);
                save_or_not_header.setVisibility(View.GONE);
                scrollerLayout.setVisibility(View.VISIBLE);

                crown_filters_scroller.setVisibility(View.GONE);
                cap_filters_scroller.setVisibility(View.GONE);
                glasses_filters_scroller.setVisibility(View.GONE);
                face_filters_scroller.setVisibility(View.VISIBLE);
                hearts_filters_scroller.setVisibility(View.GONE);
                mask_filters_scroller.setVisibility(View.GONE);
                break;
            case R.id.purple_glasses_cat:
                no_filter_header.setVisibility(View.GONE);
                save_or_not_header.setVisibility(View.GONE);
                scrollerLayout.setVisibility(View.VISIBLE);

                crown_filters_scroller.setVisibility(View.GONE);
                cap_filters_scroller.setVisibility(View.GONE);
                glasses_filters_scroller.setVisibility(View.VISIBLE);
                face_filters_scroller.setVisibility(View.GONE);
                hearts_filters_scroller.setVisibility(View.GONE);
                mask_filters_scroller.setVisibility(View.GONE);
                break;
            case R.id.red_hat_cat:
                no_filter_header.setVisibility(View.GONE);
                save_or_not_header.setVisibility(View.GONE);
                scrollerLayout.setVisibility(View.VISIBLE);

                crown_filters_scroller.setVisibility(View.GONE);
                cap_filters_scroller.setVisibility(View.VISIBLE);
                glasses_filters_scroller.setVisibility(View.GONE);
                face_filters_scroller.setVisibility(View.GONE);
                hearts_filters_scroller.setVisibility(View.GONE);
                mask_filters_scroller.setVisibility(View.GONE);
                break;
            case R.id.cat_with_crown_cat:
                no_filter_header.setVisibility(View.GONE);
                save_or_not_header.setVisibility(View.GONE);
                scrollerLayout.setVisibility(View.VISIBLE);

                crown_filters_scroller.setVisibility(View.VISIBLE);
                cap_filters_scroller.setVisibility(View.GONE);
                glasses_filters_scroller.setVisibility(View.GONE);
                face_filters_scroller.setVisibility(View.GONE);
                hearts_filters_scroller.setVisibility(View.GONE);
                mask_filters_scroller.setVisibility(View.GONE);
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
            case R.id.flash_glasses:
                filterOnFace = BitmapFactory.decodeResource(getResources(), R.drawable.flash_glasses);
                SetAndGetData.data.setIcon(filterOnFace);
                SetAndGetData.data.setFilterName("flash_glasses");
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

    private void requestStoragePermission() {
        Log.w(TAG, "Storage permission is not granted. Requesting permission");
        final String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, permissions, RC_WRITE_STORAGE);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_WRITE_STORAGE);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_storage_rationale,
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
        switchFlashLight(false);
        SetAndGetData.data.setFlash(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            try {
                mCameraSource.release();
                switchFlashLight(false);
                SetAndGetData.data.setFlash(false);
            } catch (Exception e) {

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM && requestCode != RC_WRITE_STORAGE) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (requestCode == RC_HANDLE_CAMERA_PERM && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        if (requestCode == RC_WRITE_STORAGE && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Storage permission granted - saving image");
            // we have permission, so create the camerasource
            saveImg();
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
            SetAndGetData.data.setFronCam(mIsFrontFacing);
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
                SetAndGetData.data.setFronCam(mIsFrontFacing);
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
    private com.google.android.gms.vision.face.FaceDetector createFaceDetector(Context context) {

        detector = new com.google.android.gms.vision.face.FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(true)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(mIsFrontFacing)
                .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                .build();


        Detector.Processor<Face> processor;
//        if (mIsFrontFacing) {
            Tracker<Face> tracker = new GetAndSetFace(mGraphicOverlay);
            processor = new LargestFaceFocusingProcessor.Builder(detector, tracker).build();
//        } else {
//            MultiProcessor.Factory<Face> factory = new MultiProcessor.Factory<Face>() {
//                @Override
//                public Tracker<Face> create(Face face) {
//                    return new GetAndSetFace(mGraphicOverlay);
//                }
//            };
//            processor = new MultiProcessor.Builder<>(factory).build();
//        }

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
                switchFlashLight(false);
                SetAndGetData.data.setFlash(false);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                if(SetAndGetData.data.isFronCam()){
                    resultImage.setImageBitmap(flip(RotateBitmap(bitmap, 270)));
                }else{
                    resultImage.setImageBitmap(RotateBitmap(bitmap, 90));
                }
                resultImage1.setVisibility(View.VISIBLE);
                resultImage1.setImageBitmap(loadBitmapFromView(mGraphicOverlay));
                save_or_not_header.setVisibility(View.VISIBLE);
                scrollerLayout.setVisibility(View.GONE);
                no_filter_header.setVisibility(View.GONE);
//                                loader.setVisibility(View.GONE);
                cameraRetakeContainer.setVisibility(View.VISIBLE);
                imagePreviewConstrint.setVisibility(View.VISIBLE);
                mPreview.setVisibility(View.GONE);



//                Frame outputFrame = new Frame.Builder().setBitmap(bitmap).build();
//                try {
//                    Face detectedFace = detector.detect(outputFrame).valueAt(0);
//                    if (detectedFace != null) {
//
//                        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
//                        Canvas tempCanvas = new Canvas(bitmap);
//                        Bitmap finalBitmap = bitmap;
//                        GraphicOverlay.Graphic graphic = new GraphicOverlay.Graphic(mGraphicOverlay) {
//                            @Override
//                            public void draw(Canvas tempCanvas) {
//                                if(!SetAndGetData.data.getFilterName().equalsIgnoreCase("no_filter")) {
//                                    PointF LEFT_EYE = detectedFace.getLandmarks().get(Landmark.LEFT_EYE).getPosition();
//                                    PointF RIGHT_EYE = detectedFace.getLandmarks().get(Landmark.RIGHT_EYE).getPosition();
//
//                                    ApplyFilterOnCaptured applyFilterOnCaptured = new ApplyFilterOnCaptured();
//                                    Rect filterRect = applyFilterOnCaptured.drawDesiredFilter(tempCanvas, finalBitmap, LEFT_EYE, RIGHT_EYE);
//                                    tempCanvas.drawBitmap(SetAndGetData.data.getIcon(), null, filterRect, null);
//                                }
//                                save_or_not_header.setVisibility(View.VISIBLE);
//                                filters_scroller.setVisibility(View.GONE);
////                                loader.setVisibility(View.GONE);
//                                cameraRetakeContainer.setVisibility(View.VISIBLE);
//                                imagePreviewConstrint.setVisibility(View.VISIBLE);
//                                mPreview.setVisibility(View.GONE);
//                                if(SetAndGetData.data.isFronCam()) {
//                                    resultedBmp = flip(finalBitmap);
//                                    resultImage.setImageBitmap(resultedBmp);
//                                }else{
//                                    resultImage.setImageBitmap(finalBitmap);
//                                }
//                            }
//                        };
//                        graphic.draw(tempCanvas);
//                        Log.i("faizan", "detected");
//                    }
//                } catch (Exception e) {
//                    Log.i("faizan", "e.getMessage()");
//                }
int a = 0;
//---------------------------------------------------------------------------------------------------//
int b = 0;



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
        try {
            mCameraSource.takePicture(shutterCallback, jpegCallback);
        }catch (Exception e){
            Toast.makeText(this, "Please click again!", Toast.LENGTH_SHORT).show();
        }
    }

//    private Bitmap imageRotate(Bitmap bmp){
//        int rotate = 0;
//        try {
//            getContentResolver().notifyChange(bmp, null);
//            File imageFile = new File(bmp.);
//            ExifInterface exif = new ExifInterface(
//                    imageFile.getAbsolutePath());
//            int orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotate = 270;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotate = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotate = 90;
//                    break;
//            }
//            Log.v("TAG", "Exif orientation: " + orientation);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        Matrix matrix = new Matrix();
//        matrix.postRotate(rotate);
//        return Bitmap.createBitmap(bmp, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//
//
//        /****** Image rotation ****/
//        Matrix matrix = new Matrix();
//        matrix.postRotate(orientation);
//        Bitmap cropped = Bitmap.createBitmap(scaled, x, y, width, height, matrix, true);
//    }


    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

//    private void storeImage(Bitmap image) {
//        File pictureFile = getOutputMediaFile();
//        if (pictureFile == null) {
//            loader.setVisibility(View.GONE);
//            Toast.makeText(this, "Could not save.", Toast.LENGTH_SHORT).show();
//            imagePreviewConstrint.setVisibility(View.GONE);
//            mPreview.setVisibility(View.VISIBLE);
//            filters_scroller.setVisibility(View.VISIBLE);
//            cameraRetakeContainer.setVisibility(View.GONE);
//            Log.d(TAG,
//                    "Error creating media file, check storage permissions: ");// e.getMessage());
//            return;
//        }
//        try {
//            FileOutputStream fos = new FileOutputStream(pictureFile);
//            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
//            fos.close();
//            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
//            loader.setVisibility(View.GONE);
//            imagePreviewConstrint.setVisibility(View.GONE);
//            mPreview.setVisibility(View.VISIBLE);
//            filters_scroller.setVisibility(View.VISIBLE);
//
//            cameraRetakeContainer.setVisibility(View.GONE);
//        } catch (FileNotFoundException e) {
//            loader.setVisibility(View.GONE);
//            imagePreviewConstrint.setVisibility(View.GONE);
//            mPreview.setVisibility(View.VISIBLE);
//            filters_scroller.setVisibility(View.VISIBLE);
//            cameraRetakeContainer.setVisibility(View.GONE);
//            Toast.makeText(this, "Could not save.", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "File not found: " + e.getMessage());
//        } catch (IOException e) {
//            loader.setVisibility(View.GONE);
//            imagePreviewConstrint.setVisibility(View.GONE);
//            mPreview.setVisibility(View.VISIBLE);
//            filters_scroller.setVisibility(View.VISIBLE);
//            cameraRetakeContainer.setVisibility(View.GONE);
//            Toast.makeText(this, "Could not save.", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "Error accessing file: " + e.getMessage());
//        }
//    }

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
            scrollerLayout.setVisibility(View.GONE);
            no_filter_header.setVisibility(View.VISIBLE);
            save_or_not_header.setVisibility(View.GONE);
            cameraRetakeContainer.setVisibility(View.GONE);
            tv_ok.setVisibility(View.VISIBLE);
            button_retake.setVisibility(View.VISIBLE);
            resultImage1.setVisibility(View.GONE);

            SetAndGetData.data.setToShareUri(null);
        }

    }

    private Bitmap loadBitmapFromView(View v) {
        DisplayMetrics dm = MainActivity.this.getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);
        return returnedBitmap;
    }

    private Bitmap getScreenShot() {//View view
        Bitmap bitmap = Bitmap.createBitmap(ssLayout.getDrawingCache());
//        ssLayout.setDrawingCacheEnabled(false);
        return cropBitmap(bitmap);
    }

    private Bitmap cropBitmap(Bitmap bmpToCrop) {

        Matrix matrix = new Matrix();
//        if (!isFrontCam) {
//            matrix.postRotate(90);
//        } else {
//            matrix.postRotate(270);
//        }
        bmpToCrop = Bitmap.createBitmap(bmpToCrop, 0, 0, bmpToCrop.getWidth(), bmpToCrop.getHeight(), matrix, true);

        Display display = getWindowManager().getDefaultDisplay();
        int heightOriginal = display.getHeight();
        int widthOriginal = display.getWidth();
        int heightFrame, widthFrame, leftFrame, topFrame;

            heightFrame = resultImage.getHeight();
            widthFrame = resultImage.getWidth();
            leftFrame = resultImage.getLeft();
            topFrame = imagePreviewConstrint.getTop();



        int heightReal = bmpToCrop.getHeight();
        int widthReal = bmpToCrop.getWidth();
        int widthFinal = widthFrame * widthReal / widthOriginal;
        int heightFinal = heightFrame * heightReal / heightOriginal;
        int leftFinal = leftFrame * widthReal / widthOriginal;
        int topFinal = topFrame * heightReal / heightOriginal;
        Bitmap croppedBmp = Bitmap.createBitmap(bmpToCrop, leftFinal, topFinal, widthFinal, heightFinal);

        return croppedBmp;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        switchFlashLight(false);
        SetAndGetData.data.setFlash(false);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void shareBitmap(@NonNull Bitmap bitmap) {
        //---Save bitmap to external cache directory---//
        //get cache directory
        File cachePath = new File(getExternalCacheDir(), "my_images/");
        cachePath.mkdirs();

        //create png file
        File file = new File(cachePath,
                "Image_" + String.valueOf(getRandomNmbr(999))
                        + ".png");
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();


            Uri myImageFileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            SetAndGetData.data.setToShareUri(myImageFileUri);
            share(myImageFileUri);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //---Share File---//
        //get file uri



        //create a intent
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri);
//        intent.setType("image/png");
//        startActivity(Intent.createChooser(intent, "Share with"));
    }

    private void share(Uri myImageFileUri){
        try {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri);
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share with"));
        }catch(Exception e){
            Log.i("E", e.getMessage());
        }
    }

    private String getRandomNmbr(int length) {
        return String.valueOf(new Random().nextInt(length) + 1);
    }
}