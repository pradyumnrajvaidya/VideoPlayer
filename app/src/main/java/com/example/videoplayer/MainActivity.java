package com.example.videoplayer;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SelectListener{
     RecyclerView recyclerView;
     List<File> files;
     CustomAdapter customAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        // Dexter module is used to take run time permissions from the user.
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                          files = fetchVideos(Environment.getExternalStorageDirectory());
                          customAdapter = new CustomAdapter(getApplicationContext(),files,MainActivity.this);
                          recyclerView.setAdapter(customAdapter);

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast.makeText(getApplicationContext(),"Permission is Required",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                           permissionToken.continuePermissionRequest();
                    }
                })
                .check();
    }
    // This method is used to find all the mp4 files from user's secondary storage.
    public ArrayList<File> fetchVideos(File file){
        ArrayList<File> videos = new ArrayList<>();
        File[] files = file.listFiles();
        if(files!=null){
            for(File Videofiles:files){
                if(Videofiles.isDirectory() && !Videofiles.isHidden()){
                    videos.addAll(fetchVideos(Videofiles));
                } else {
                    if(Videofiles.getName().toLowerCase().endsWith(".mp4") && !Videofiles.getName().startsWith(".")){
                        videos.add(Videofiles);
                    }
                }
            }
        }
        return videos;
    }
    // When we click on the video this method will take us to next activity.
    public void onFileClick(File file){
        Intent intent = new Intent(MainActivity.this,PlayVideo.class);
        intent.putExtra("PlayVideo",file.getAbsolutePath());
        startActivity(intent);
    }
}