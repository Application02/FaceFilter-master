package com.android.facefilter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.facefilter.camera.CameraSourcePreview;
import com.android.facefilter.camera.GraphicOverlay;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static java.sql.Types.NULL;

public class FaceFilterActivity extends AppCompatActivity implements View.OnClickListener {
    File folder;
    String timeStamp;
    File file;
    String root;
    Bitmap bitmap;
    ImageView click,click1,click2,click3,click4,click5,click6,click7,click8,click9,click10,click11,click12,click13,click14,click15,click16;
    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    private CameraSourcePreview mPreview;
    private boolean mIsFrontFacing = true;
    RelativeLayout linearLayout;
    private GraphicOverlay mGraphicOverlay;
    private FaceGraphic mFaceGraphic;
    private static final int RC_HANDLE_GMS = 9001;
    ImageView imageView;
    private GraphicOverlay mOverlay;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    int image=R.drawable.fa;
    //==============================================================================================
    // Activity Methods
    //==============================================================================================

    /**
     * Initializes the UI and initiates the creation of a face detector.
     */

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_face_filter);
        init();
      linearLayout = (RelativeLayout)findViewById(R.id.linear_ss);
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        mPreview = (CameraSourcePreview) findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);
        final ImageView button = (ImageView) findViewById(R.id.flipButton);
        button.setOnClickListener(mFlipButtonListener);

        if (icicle != null) {
            mIsFrontFacing = icicle.getBoolean("IsFrontFacing");
        }

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }

        //----------------


        int rc1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (rc1 == PackageManager.PERMISSION_GRANTED) {

        }
        else {
            Toast.makeText(this, "Required Storage Premission!", Toast.LENGTH_SHORT).show();
            finish();
        }


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

    private void init() {
        ImageView img_download = (ImageView)findViewById(R.id.img_download);
        img_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bitmap = getBitmap(linearLayout);
                saveChart(bitmap, linearLayout.getMeasuredHeight(), linearLayout.getMeasuredWidth(), image);
                Toast.makeText(getApplicationContext(), " Download Successfull.", Toast.LENGTH_SHORT).show();
            }
        });
        click = (ImageView)findViewById(R.id.click);
        click1 = (ImageView)findViewById(R.id.click1);
        click2 = (ImageView)findViewById(R.id.click2);
        click3 = (ImageView)findViewById(R.id.click3);
        click4 = (ImageView)findViewById(R.id.click4);
        click5 = (ImageView)findViewById(R.id.click5);
        click6 = (ImageView)findViewById(R.id.click6);
        click7 = (ImageView)findViewById(R.id.click7);
        click8 = (ImageView)findViewById(R.id.click8);
        click9 = (ImageView)findViewById(R.id.click9);
        click10 = (ImageView)findViewById(R.id.click10);
        click11 = (ImageView)findViewById(R.id.click11);
         click12 = (ImageView)findViewById(R.id.click12);
         click13 = (ImageView)findViewById(R.id.click13);
         click14 = (ImageView)findViewById(R.id.click14);
         click15 = (ImageView)findViewById(R.id.click15);
         click16 = (ImageView)findViewById(R.id.click16);
        click.setOnClickListener(this);
        click1.setOnClickListener(this);
        click2.setOnClickListener(this);
        click3.setOnClickListener(this);
        click4.setOnClickListener(this);
        click5.setOnClickListener(this);
        click6.setOnClickListener(this);
        click7.setOnClickListener(this);
        click8.setOnClickListener(this);
        click9.setOnClickListener(this);
        click10.setOnClickListener(this);
        click11.setOnClickListener(this);
        click12.setOnClickListener(this);
        click13.setOnClickListener(this);
        click14.setOnClickListener(this);
        click15.setOnClickListener(this);
        click16.setOnClickListener(this);
    }
    public Bitmap getBitmap(RelativeLayout layout) {
        layout.setDrawingCacheEnabled(true);
        layout.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(layout.getDrawingCache());
        layout.setDrawingCacheEnabled(false);
        return bmp;
    }

    private void saveChart(Bitmap getbitmap, float height, float width, int image) {


        root = Environment.getExternalStorageDirectory().toString();
        folder = new File(root + "/Facefilter");
        boolean success = false;

        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss",
                Locale.getDefault()).format(new Date());
        file = new File(folder.getPath() + File.separator + image + ".png");

        if (!file.exists()) {
            try {
                success = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileOutputStream ostream = null;

        try {
            ostream = new FileOutputStream(file);
            System.out.println(ostream);
            Bitmap well = getbitmap;
            Bitmap save = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            Canvas now = new Canvas(save);
            now.drawRect(new Rect(0, 0, (int) width, (int) height), paint);
            now.drawBitmap(well,
                    new Rect(0, 0, well.getWidth(), well.getHeight()),
                    new Rect(0, 0, (int) width, (int) height), null);

            if (save == null) {
                System.out.println(NULL);
            }
            save.compress(Bitmap.CompressFormat.PNG, 100, ostream);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */

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

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */

    private void createCameraSource() {

        final Context context = getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(true)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(mIsFrontFacing)
                .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                .build();


        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w(TAG, "Face detector dependencies are not yet available.");
        }

        int facing = CameraSource.CAMERA_FACING_FRONT;
        if (!mIsFrontFacing) {
            facing = CameraSource.CAMERA_FACING_BACK;
        }
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(facing)
                .setRequestedFps(10.0f)
                .build();
    }


    /**
     * Restarts the camera.
     */

    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */

    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    /**
     * Releases the resources associated with the camera source, the associated detector, and the
     * rest of the processing pipeline.
     */

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

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

    //==============================================================================================
    // Camera Source Preview
    //==============================================================================================

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */

    private void startCameraSource() {

        // check that the device has play services available.

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

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.click:
           {
               startCameraSource();
               image=R.drawable.fa;
               new GraphicFaceTracker(mGraphicOverlay);
               break;
           }
           case R.id.click1:
           {
               startCameraSource();

               image= R.drawable.fb;
               new GraphicFaceTracker(mGraphicOverlay);
               break;
           }
           case R.id.click2:
           {
               startCameraSource();
               image=R.drawable.fc;
               new GraphicFaceTracker(mGraphicOverlay);
               break;
           }
           case R.id.click3:
           {
               startCameraSource();
               image=R.drawable.fd;
               new GraphicFaceTracker(mGraphicOverlay);
               break;
           }
           case R.id.click4:
           {
               startCameraSource();
               image=R.drawable.fe;
               new GraphicFaceTracker(mGraphicOverlay);
               break;
           }
           case R.id.click5:
           {
               startCameraSource();
               image=R.drawable.ff;
               new GraphicFaceTracker(mGraphicOverlay);
               break;
           }
           case R.id.click6:
           {
               startCameraSource();
               image=R.drawable.fg;
               new GraphicFaceTracker(mGraphicOverlay);
               break;

           }
           case R.id.click7:
           {
               startCameraSource();
               image=R.drawable.fh;
               new GraphicFaceTracker(mGraphicOverlay);
               break;

           } case R.id.click8:
           {
               startCameraSource();
               image=R.drawable.fi;
               new GraphicFaceTracker(mGraphicOverlay);
               break;

           }
           case R.id.click9:
           {
               startCameraSource();
               image=R.drawable.fj;
               new GraphicFaceTracker(mGraphicOverlay);
               break;

           }
           case R.id.click10:
           {
               startCameraSource();
               image=R.drawable.fk;
               new GraphicFaceTracker(mGraphicOverlay);
               break;

           }
           case R.id.click11:
           {
               startCameraSource();
               image=R.drawable.fl;
               new GraphicFaceTracker(mGraphicOverlay);
               break;

           }
           case R.id.click12:
           {
               startCameraSource();
               image=R.drawable.fm;
               new GraphicFaceTracker(mGraphicOverlay);
               break;

           }
           case R.id.click13:
           {
               startCameraSource();
               image=R.drawable.fn;
               new GraphicFaceTracker(mGraphicOverlay);
               break;

           }
           case R.id.click14:
           {
               startCameraSource();
               image=R.drawable.fo;
               new GraphicFaceTracker(mGraphicOverlay);
               break;

           }
           case R.id.click15:
           {
               startCameraSource();
               image=R.drawable.fp;
               new GraphicFaceTracker(mGraphicOverlay);
               break;

           }
           case R.id.click16:
           {
               startCameraSource();
               image=R.drawable.fq;
               new GraphicFaceTracker(mGraphicOverlay);
               break;

           }
       }
    }

    //==============================================================================================
    // Graphic Face Tracker
    //==============================================================================================

    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new GraphicFaceTracker(mGraphicOverlay);
        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {



        GraphicFaceTracker(GraphicOverlay overlay) {
            mOverlay = overlay;

            mFaceGraphic = new FaceGraphic(overlay,image);
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */

        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face);
        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */

        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
        }
    }
}