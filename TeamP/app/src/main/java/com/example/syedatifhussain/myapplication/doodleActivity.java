package com.example.syedatifhussain.myapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mukesh.DrawingView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

import static android.R.attr.format;

public class doodleActivity extends AppCompatActivity {
//    DrawingView dv ;
    private Paint mPaint;
    private float SIZE_PEN = 1;
    private float SIZE_ERASER = 0;
    private SeekBar seekBarPenSize;
    private Button btnSendDoodleDraw;
    private Button btnClearDoodle;
    private Button btnUndoDoodle;
    private ImageView testDoodle;
    private FirebaseStorage storage;
    private DatabaseReference mDatabase;
    private StorageReference mImageStorage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doodle);


        final DrawableView drawableView = (DrawableView) findViewById(R.id.scratch_pad);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        seekBarPenSize = (SeekBar) findViewById(R.id.seekBarPenSize);
        btnSendDoodleDraw = (Button) findViewById(R.id.btnSendDoodleDraw);
        testDoodle = (ImageView) findViewById(R.id.imageViewTestDoodle);
        storage = FirebaseStorage.getInstance();
        btnClearDoodle = (Button) findViewById(R.id.btnClearDrawingDoodle);
        btnUndoDoodle = (Button) findViewById(R.id.btnUndoDoodle);
        mImageStorage = FirebaseStorage.getInstance().getReference();


        final DrawableViewConfig config = new DrawableViewConfig();
        config.setStrokeColor(getResources().getColor(android.R.color.black));
        config.setShowCanvasBounds(true); // If the view is bigger than canvas, with this the user will see the bounds (Recommended)
        config.setStrokeWidth(20.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(1080);
        config.setCanvasWidth(1920);
        drawableView.setConfig(config);

        btnClearDoodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawableView.clear();
            }
        });
        btnUndoDoodle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawableView.undo();
            }
        });


        seekBarPenSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float test = (float) progress;
                config.setStrokeWidth(test);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        btnSendDoodleDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                File file = createImageFile();
                if (file != null) {
                    FileOutputStream fout;
                    try {
                        fout = new FileOutputStream(file);
                        drawableView.obtainBitmap().compress(Bitmap.CompressFormat.PNG, 100, fout);
                        fout.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    final Uri uri=Uri.fromFile(file);


                    mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Meeting").child("type").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String val = dataSnapshot.getValue(String.class);

                            final String current_user_ref = "Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects/"+getIntent().getExtras().getString("projectid")+"/Messages/"+val;
                            final String chat_user_ref = "Organizations/"+getIntent().getExtras().getString("orgid")+"/Projects/"+getIntent().getExtras().getString("projectid")+"/Messages/"+val;

                            DatabaseReference user_message_push = mDatabase.child("Organizations").child(getIntent().getExtras().getString("orgid")).child("Projects").child(getIntent().getExtras().getString("projectid")).child("Messages").child(val).push();

                            final String push_id = user_message_push.getKey();

                            StorageReference filepath = mImageStorage.child("message_images").child(push_id+".jpg");
                            filepath.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if(task.isSuccessful()){
                                        String download_url = task.getResult().getDownloadUrl().toString();

                                        Map messageMap = new HashMap();
                                        messageMap.put("message",download_url);
                                        messageMap.put("type","image");
                                        messageMap.put("time", ServerValue.TIMESTAMP);
                                        messageMap.put("pid",getIntent().getExtras().getString("projectid"));
                                        messageMap.put("from",getIntent().getExtras().getString("authuser"));

                                        Map messageUserMap = new HashMap();
                                        messageUserMap.put(current_user_ref+"/"+push_id,messageMap);
                                        messageUserMap.put(chat_user_ref+"/"+push_id,messageMap);

                                        finish();

                                        mDatabase.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                if(databaseError!=null){
                                                    Log.d("Chat Log : ",databaseError.getMessage().toString());
                                                }
                                                finish();
                                            }
                                        });

                                    }
                                    else {
                                        Log.d(task.getException().toString(),"Check this please ");
                                    }

                                }
                            });


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                }



            }
        });



    }

    public File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File mFileTemp = null;
        String root=doodleActivity.this.getDir("my_sub_dir",Context.MODE_PRIVATE).getAbsolutePath();
        File myDir = new File(root + "/Img");
        if(!myDir.exists()){
            myDir.mkdirs();
        }
        try {
            mFileTemp=File.createTempFile(imageFileName,".jpg",myDir.getAbsoluteFile());
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return mFileTemp;
    }

}
