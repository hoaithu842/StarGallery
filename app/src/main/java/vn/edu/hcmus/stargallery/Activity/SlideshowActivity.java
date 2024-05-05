package vn.edu.hcmus.stargallery.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;


import java.io.File;
import java.util.ArrayList;

import vn.edu.hcmus.stargallery.R;

public class SlideshowActivity extends AppCompatActivity {
    private ArrayList<String> selectedImages;
    private ImageView imageView;
    private int currentIndex;
    private Handler handler;
    private Runnable runnable;
    private ArrayList<Bitmap> images;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        imageView = findViewById(R.id.imageView);
        selectedImages = getIntent().getStringArrayListExtra("selected_images");
        images = new ArrayList<>();
        for (String imagePath : selectedImages) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(imagePath);
            images.add(imageBitmap);
        }
        handler = new Handler();

        Button saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String currentImagePath = selectedImages.get(currentIndex);
//                String videoFilePath = "/path/to/generated/video.mp4";
//                saveVideoFile(selectedImages, videoFilePath);
            }
        });

        Button shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // Start the slideshow
        startSlideshow();
    }

    private void startSlideshow() {
        currentIndex = 0;
        if (selectedImages.size() > 0) {
            // Load the first image
            loadImage(currentIndex);
            // Start a runnable to switch images periodically
            handler.postDelayed(runnable = new Runnable() {
                @Override
                public void run() {
                    currentIndex++;
                    if (currentIndex >= selectedImages.size()) {
                        currentIndex = 0;
                    }
                    loadImage(currentIndex);
                    handler.postDelayed(this, 3000); // Change image every 3 seconds
                }
            }, 3000); // Start after 3 seconds
        }
    }

    private void loadImage(int index) {
        String imagePath = selectedImages.get(index);
//        RequestOptions options = new RequestOptions()
//                .centerCrop()
//                .diskCacheStrategy(DiskCacheStrategy.NONE)
//                .skipMemoryCache(true); // This is optional, depends on your caching strategy
        Glide.with(this)
                .load(imagePath)
//                .apply(options)
                .into(imageView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop the slideshow when the activity is destroyed
        handler.removeCallbacks(runnable);
    }
}
