package com.example.viet.multimedia;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.VideoView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PreparerPublierActivity extends AppCompatActivity implements LocationListener {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private ProgressDialog pDialog;
    LocationManager locationManager;
    String provider;
    ImageView imageView;
    VideoView videoView;
    RadioGroup categories;
    RadioButton evenement;
    RadioButton incident;
    EditText description;
    private static String status;
    Button Publier;
    String filePath;
    long totalSize = 0;
    boolean isImage;
    JSONParser jsonParser = new JSONParser();
    private static String categoriChoisir;
    private static String latitude;
    private static String longtitude;
    private static String url_create_product = "http://192.168.1.30/pfephp/LoadInfo/create_product.php";
    private static String url_create_file = "http://192.168.1.30/pfephp/LoadData/fileUpload.php";
    private static final String TAG_SUCCESS = "success";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preparer_publier);
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
        imageView = (ImageView) findViewById(R.id.imageView);
        videoView = (VideoView) findViewById(R.id.videoView);
        Intent i = getIntent();
        filePath = i.getStringExtra("filePath");
        isImage = i.getBooleanExtra("isImage", true);
        if (filePath != null) {
            previewMedia(isImage);
        }
        description = (EditText) findViewById(R.id.descripion);
        categories = (RadioGroup) findViewById(R.id.radioGroup);
        evenement = (RadioButton) findViewById(R.id.radioevent);
        incident = (RadioButton) findViewById(R.id.radioincident);
        categories.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioevent) {
                    categoriChoisir = (String) evenement.getText();
                    Toast.makeText(getApplicationContext(), "choice: " + categoriChoisir, Toast.LENGTH_SHORT).show();
                }
                if (checkedId == R.id.radioincident) {
                    categoriChoisir = (String) incident.getText();
                    Toast.makeText(getApplicationContext(), "choice: " + categoriChoisir, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Publier = (Button) findViewById(R.id.publier);
        Publier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (categoriChoisir != null) {
                    status = description.getText().toString();
                    getLocationOfDevice();
                    new CreateNewEvenement().execute();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please choisi la categorie !!!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void previewMedia(boolean isImage) {
        if (isImage) {
            imageView.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
            imageView.setImageBitmap(myBitmap);
        }
        else {
            imageView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoPath(filePath);
            videoView.start();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void getLocationOfDevice() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if (locationManager != null) {
            int hasCoarseLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasFineLocationPermission = checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            List<String> permissions = new ArrayList<String>();
            if (hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (!permissions.isEmpty()) {
                String[] params = permissions.toArray(new String[permissions.size()]);
                requestPermissions(params, REQUEST_CODE_ASK_PERMISSIONS);
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = String.valueOf(location.getLatitude());
                longtitude = String.valueOf(location.getLongitude());
                Toast.makeText(getApplicationContext(),"latitude: "+ latitude +" longitude: "+longtitude, Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Location is null", Toast.LENGTH_LONG).show();
            }
        }
    }

    class CreateNewEvenement extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PreparerPublierActivity.this);
            pDialog.setMessage("Creating Evenement..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            String resultat;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String filename = filePath.substring(filePath.length() - 23);
            Log.d("Ten cua file", filename);
            if (isImage) {
                params.add(new BasicNameValuePair("type", "image"));
            }
            else {
                params.add(new BasicNameValuePair("type", "video"));
            }
            params.add(new BasicNameValuePair("name",filename));
            params.add(new BasicNameValuePair("categorie", categoriChoisir));
            params.add(new BasicNameValuePair("description", status));
            params.add(new BasicNameValuePair("latitude", latitude));
            params.add(new BasicNameValuePair("longtitude", longtitude));
            resultat = upLoadMedia(filePath);
            JSONObject json = jsonParser.makeHttpRequest(url_create_product, "POST", params);
            Log.d("Create Response", json.toString());

            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    Intent i = new Intent(getApplicationContext(), PublierActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Log.d("fail","can't create");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return resultat;
        }
        protected void onPostExecute(String result) {
            Log.d("ketqua", "Response from server: " + result);
            super.onPostExecute(result);
            }
        }
        private String upLoadMedia (String FileName) {
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url_create_file);
            FileBody filebodyVideo = new FileBody(new File(FileName));
            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("media", filebodyVideo);
            httppost.setEntity(reqEntity);
            HttpResponse response = null;
            try {
                response = httpclient.execute( httppost );
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity resEntity = response.getEntity( );
            if (resEntity != null) {
                try {
                    responseString = EntityUtils.toString( resEntity );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return responseString;
        }



}
