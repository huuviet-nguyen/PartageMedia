package com.example.viet.multimedia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CarteActivity extends AppCompatActivity {
    private GoogleMap googleMap;
    private static final String TAG_MEDIAS = "medias";
    private static final String TAG_SUCCESS = "success";
    LatLngBounds.Builder builder;
    CameraUpdate cu;
    JSONParser jParser = new JSONParser();
    JSONArray medias = null;
    private static ArrayList<HashMap<String, String>> mediasList;
    String url_media_file = "http://192.168.1.30/pfephp/LoadData/uploads/";
    String url_get_all = "http://192.168.1.30/pfephp/LoadInfo/get_all.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carte);
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
        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        new LoadAllProducts().execute();
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for (int i = 0 ; i< mediasList.size() ; i++) {
                    if (marker.getTitle().equals(mediasList.get(i).get("description"))) {
                        if (mediasList.get(i).get("type").equals("video")) {
                        String media = url_media_file + mediasList.get(i).get("name");
                        Uri uri = Uri.parse(media);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "video/*");
                        startActivity(intent);
                        }
                        else {
                            String media = url_media_file + mediasList.get(i).get("name");
                            Uri uri = Uri.parse(media);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "image/*");
                            startActivity(intent);
                        }
                    }
                }

            }
        });
    }

    class LoadAllProducts extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            mediasList = new ArrayList<HashMap<String, String>>();
            JSONObject json = jParser.makeHttpRequest(url_get_all, "GET", params);
            try {
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    medias = json.getJSONArray(TAG_MEDIAS);
                    for (int i = 0; i < medias.length(); i++) {
                        JSONObject c = medias.getJSONObject(i);
                        String type = c.getString("type");
                        String name = c.getString("name");
                        String categorie = c.getString("categorie");
                        String description = c.getString("description");
                        String latitude = c.getString("latitude");
                        String longtitude = c.getString("longtitude");
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("name", name);
                        map.put("description",description);
                        map.put("latitude", latitude);
                        map.put("longtitude", longtitude );
                        map.put("type",type);
                        mediasList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String resultat) {
            if (mediasList.size() > 0){
                builder = new LatLngBounds.Builder();
                for (Marker m : addMarker()) {
                    builder.include(m.getPosition());
                }
                int padding = 100;
                LatLngBounds bounds = builder.build();
                cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        googleMap.animateCamera(cu);
                    }
                });
            }
        }
    }

    private List<Marker> addMarker() {
        List<Marker> markerlist = new ArrayList<>();
        for (int i = 0 ; i < mediasList.size() ; i++) {
            Marker marker = googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(mediasList.get(i).get("latitude")), Double.parseDouble(mediasList.get(i).get("longtitude")))).title(mediasList.get(i).get("description")));
            markerlist.add(marker);
        }
        return markerlist;
    }

}
