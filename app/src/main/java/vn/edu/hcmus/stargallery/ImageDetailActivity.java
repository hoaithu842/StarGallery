package vn.edu.hcmus.stargallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageDetailActivity extends AppCompatActivity {

    ImageView imageView;
    String image_path = "";
    PopupWindow popupWindow;
    ImageButton backBtn, detailBtn, shareBtn, editBtn, favorBtn, delBtn, rotateBtn;
    float x1, x2;
    static final int MIN_DISTANCE = 150; // Minimum distance for a swipe to be recognized
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private ArrayList<String> images_list;
    private int currentIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.imageView);
//        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());


        Intent intent = getIntent();

        if (intent != null) {

            images_list = intent.getStringArrayListExtra("images_list");
            if (images_list == null || images_list.isEmpty()) {
                // Handle case where no image paths are provided
                // return;
            }

            image_path = intent.getStringExtra("image_path");
            currentIndex = Arrays.asList(image_path).indexOf(image_path);
            if (image_path != null) {
                loadImage(image_path);
            }
        }

        backBtn = findViewById(R.id.backBtn);
        detailBtn = findViewById(R.id.detailBtn);
        shareBtn = findViewById(R.id.shareBtn);
        editBtn = findViewById(R.id.editBtn);
        favorBtn = findViewById(R.id.favoriteBtn);
        delBtn = findViewById(R.id.deleteBtn);
        rotateBtn = findViewById(R.id.rotateBtn);
    }
    private void loadImage(String image_path) {
        Glide.with(this).load(image_path).into(imageView);
        this.image_path = image_path;
    }
}
