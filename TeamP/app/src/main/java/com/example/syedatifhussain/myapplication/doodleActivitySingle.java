package com.example.syedatifhussain.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

public class doodleActivitySingle extends AppCompatActivity {
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
        setContentView(R.layout.activity_doodle_single);


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
        config.setStrokeColor(getResources().getColor(android.R.color.white));
        config.setShowCanvasBounds(true); // If the view is bigger than canvas, with this the user will see the bounds (Recommended)
        config.setStrokeWidth(20.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(2080);
        config.setCanvasWidth(2920);
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


                    final String current_user_ref = "Organizations/BUKC/Messages/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/"+getIntent().getExtras().getString("recieverID");
                    final String chat_user_ref = "Organizations/BUKC/Messages/"+ getIntent().getExtras().getString("recieverID") +"/"+FirebaseAuth.getInstance().getCurrentUser().getUid();

                    DatabaseReference user_message_push = mDatabase.child("Organizations").child("BUKC").child("Messages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(getIntent().getExtras().getString("recieverID")).push();

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
                                messageMap.put("from",FirebaseAuth.getInstance().getCurrentUser().getUid());

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
        String root=doodleActivitySingle.this.getDir("my_sub_dir", Context.MODE_PRIVATE).getAbsolutePath();
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
