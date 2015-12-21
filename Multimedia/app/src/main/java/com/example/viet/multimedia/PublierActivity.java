package com.example.viet.multimedia;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PublierActivity extends AppCompatActivity {
    Button Picture;
    Button Video;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    private Uri fileUriPicture;
    private Uri fileUriVideo;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final String TAG = PublierActivity.class.getSimpleName();
    private static Context context;
    public static Context getAppContext() {
        return PublierActivity.context;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publier);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        PublierActivity.context = getApplicationContext();
        Picture = (Button) findViewById(R.id.picture);
        Video = (Button) findViewById(R.id.video);
        Picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTakePicture();
            }
        });
        Video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTakeVideo();
            }
        });
    }

    private void startTakePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUriPicture = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUriPicture);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void startTakeVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUriVideo = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_VIDEO));  // create a file to save the video
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUriVideo);  // set the image file name
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                launchUploadActivity(true);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

        if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Video captured and saved to fileUri specified in the Intent
                Toast.makeText(this, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                launchUploadActivity(false);
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the video capture
            } else {
                // Video capture failed, advise user
            }
        }
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(PublierActivity.context.getExternalFilesDir(null).getAbsolutePath(), "hello");
        Log.d("lien", mediaStorageDir.getPath());
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdirs();
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(PublierActivity.this, PreparerPublierActivity.class);
        i.putExtra("isImage", isImage);
        if (isImage){
            i.putExtra("filePath", fileUriPicture.getPath());
        }
        else {
            i.putExtra("filePath", fileUriVideo.getPath());
        }
        startActivity(i);
    }

}
