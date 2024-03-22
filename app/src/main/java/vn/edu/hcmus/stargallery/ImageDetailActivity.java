package vn.edu.hcmus.stargallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import java.util.Arrays;

public class ImageDetailActivity extends AppCompatActivity {

    ImageView imageView;
    String imagePath = "";
    PopupWindow popupWindow;
    ImageButton backBtn, detailBtn, shareBtn, editBtn, favorBtn, delBtn, rotateBtn;
    float x1, x2;
    static final int MIN_DISTANCE = 150; // Minimum distance for a swipe to be recognized
    private ScaleGestureDetector scaleGestureDetector;
    private float scaleFactor = 1.0f;
    private String[] imagePaths;
    private int currentIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.imageView);
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());


        Intent intent = getIntent();

        if (intent != null) {
            imagePaths = intent.getStringArrayExtra("imagePaths");
            if (imagePaths == null || imagePaths.length == 0) {
                // Handle case where no image paths are provided
                // return;
            }

            imagePath = intent.getStringExtra("imagePath");
            currentIndex = Arrays.asList(imagePaths).indexOf(imagePath);
            if (imagePath != null) {
                loadImage(imagePath);
            }
            Toast.makeText(ImageDetailActivity.this, imagePath, Toast.LENGTH_SHORT).show();
        }

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Get the initial X coordinate of the touch
                        x1 = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        // Get the final X coordinate of the touch
                        x2 = event.getX();

                        // Calculate the horizontal distance covered by the swipe
                        float deltaX = x2 - x1;

                        // Check if the swipe distance is greater than the minimum distance
                        if (Math.abs(deltaX) > MIN_DISTANCE) {
                            // Swipe detected
                            if (x2 > x1) {
                                // Right swipe
                                showNextImage();
                            } else {
                                // Left swipe
                                showPreviousImage();
                            }
                        }
                        break;
                }
                return true;
            }
        });

        backBtn = findViewById(R.id.backBtn);
        detailBtn = findViewById(R.id.detailBtn);
        shareBtn = findViewById(R.id.shareBtn);
        editBtn = findViewById(R.id.editBtn);
        favorBtn = findViewById(R.id.favoriteBtn);
        delBtn = findViewById(R.id.deleteBtn);
        rotateBtn = findViewById(R.id.rotateBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Toast.makeText(ImageDetailActivity.this, "backBtn clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
//                Toast.makeText(ImageDetailActivity.this, "detailBtn clicked!", Toast.LENGTH_SHORT).show();
                Bitmap bitmap;
                bitmap = BitmapFactory.decodeFile(imagePath);
                // Check if the image was loaded successfully
                if (bitmap != null && imagePath != null) {

                    String name = imagePath.substring(imagePath.lastIndexOf("/") + 1);
                    // Get image dimensions
                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();

                    // Get image size in bytes
                    int sizeInBytes = bitmap.getByteCount();

                    // Get image type (JPEG, PNG, etc.)
                    String imageType = getImageType(imagePath);

                    // Show image details
//                    Toast.makeText(ImageDetailActivity.this, "Image Width: " + width +
//                            "\nImage Height: " + height +
//                            "\nImage Size: " + sizeInBytes + " bytes" +
//                            "\nImage Type: " + imageType, Toast.LENGTH_LONG).show();


                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View popupView = inflater.inflate(R.layout.popup, null);
                    TextView imgDet = popupView.findViewById(R.id.imgDet);
                    imgDet.setText(String.format("Name: %s \n" +
                                                "Resolutions: %d x %d pixels\n" +
                                                "Size: %d bytes\n" +
                                                "Type: %s", name, width, height, sizeInBytes, imageType));
                    // create the popup window
                    int size = LinearLayout.LayoutParams.WRAP_CONTENT;
                    boolean focusable = true; // lets taps outside the popup also dismiss it
                    popupWindow = new PopupWindow(popupView, size, size, focusable);

                    // show the popup window
                    // which view you pass in doesn't matter, it is only used for the window tolken
                    popupWindow.showAtLocation(findViewById(R.id.imgLayout), Gravity.CENTER, 0, 0);

                    // dismiss the popup window when touched
                    popupView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            popupWindow.dismiss();
                            return true;
                        }
                    });
                    bitmap.recycle();
                } else {
                    // Failed to load the image
                    Toast.makeText(ImageDetailActivity.this, "Failed to load the image", Toast.LENGTH_SHORT).show();
                }
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Toast.makeText(ImageDetailActivity.this, "shareBtn clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Toast.makeText(ImageDetailActivity.this, "editBtn clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        favorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Toast.makeText(ImageDetailActivity.this, "favorBtn clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Toast.makeText(ImageDetailActivity.this, "delBtn clicked!", Toast.LENGTH_SHORT).show();
            }
        });
        rotateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event here
                Toast.makeText(ImageDetailActivity.this, "rotateBtn clicked!", Toast.LENGTH_SHORT).show();
            }
        });


    }
    private void showNextImage() {
        if (currentIndex < imagePaths.length - 1) {
            currentIndex++;
            loadImage(imagePaths[currentIndex]);
        } else {
            Toast.makeText(ImageDetailActivity.this, "No more images", Toast.LENGTH_SHORT).show();
        }
    }
    private void showPreviousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            loadImage(imagePaths[currentIndex]);
        } else {
            Toast.makeText(ImageDetailActivity.this, "No previous images", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadImage(String imagePath) {
        Glide.with(this).load(imagePath).into(imageView);
        this.imagePath = imagePath;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // Scale factor to adjust zoom level
            scaleFactor *= detector.getScaleFactor();

            // Limit the scale factor for zooming in and out
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));

            // Apply scale transformation to imageView
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);

            return true;
        }
    }

    private String getImageType(String imagePath) {
        if (imagePath.toLowerCase().endsWith(".jpg") || imagePath.toLowerCase().endsWith(".jpeg")) {
            return "JPEG";
        } else if (imagePath.toLowerCase().endsWith(".png")) {
            return "PNG";
        } else if (imagePath.toLowerCase().endsWith(".gif")) {
            return "GIF";
        } else {
            return "Unknown";
        }
    }
}
