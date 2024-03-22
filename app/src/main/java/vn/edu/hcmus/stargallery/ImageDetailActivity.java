package vn.edu.hcmus.stargallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

public class ImageDetailActivity extends AppCompatActivity {

    ImageView imageView;
    String imagePath = "";
    PopupWindow popupWindow;
    ImageButton backBtn, detailBtn, shareBtn, editBtn, favorBtn, delBtn, rotateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.imageView);


        Intent intent = getIntent();

        if (intent != null) {
            imagePath = intent.getStringExtra("imagePath");
            if (imagePath != null) {
                Glide.with(this).load(imagePath).into(imageView);
            }
            Toast.makeText(ImageDetailActivity.this, imagePath, Toast.LENGTH_SHORT).show();
        }



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
