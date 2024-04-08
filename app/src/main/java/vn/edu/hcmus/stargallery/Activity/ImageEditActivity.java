package vn.edu.hcmus.stargallery.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import vn.edu.hcmus.stargallery.Listener.OnSwipeTouchListener;
import vn.edu.hcmus.stargallery.R;

public class ImageEditActivity extends AppCompatActivity {

    ImageView imageView;
    private int currentIndex;
    private ArrayList<String> images_list;
    String image_path = "";
    PopupWindow popupWindow;
    BottomNavigationView nav_bot;
    BottomNavigationView editor_nav_bot;
    BottomNavigationView nav_top;
    float x1, x2;
    private float scaleFactor = 1.0f;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_editor);

//        editor_nav_bot = findViewById(R.id.editor_nav_bot);
//        editor_nav_bot.setVisibility(View.INVISIBLE);

        imageView = findViewById(R.id.imageView);

        nav_bot = findViewById(R.id.editor_nav_bot);
        nav_bot.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.cropBtn) {
                    onCropBtnClick();
                } else if (item.getItemId() == R.id.filterBtn) {
                    onFilterBtnClick();
                } else if (item.getItemId() == R.id.doodleBtn) {
                    onDoodleBtnClick();
                } else if (item.getItemId() == R.id.adjustBtn) {
                    onAdjustBtnClick();
                }
                return false;
            }
        });

        nav_top = findViewById(R.id.editor_nav_top);
        Menu menu = nav_top.getMenu();
        for (int i = 1; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setEnabled(false);
        }
        nav_top.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.backBtn) {
                    onBackBtnClick();
                }
                return false;
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            image_path = intent.getStringExtra("image_path");
//            images_list = intent.getStringArrayListExtra("images_list");

//            if (images_list == null || images_list.isEmpty()) {
//                // Handle case where no image paths are provided
//                // return;
//            }
//
//            currentIndex = images_list.indexOf(image_path);
            if (image_path != null) {
                loadImage(image_path);
            }
        }
        // Swipe gestures
//        imageView.setOnTouchListener(new OnSwipeTouchListener(this) {
//            @Override
//            public void onSwipeLeft() {
//                showNextImage();
//            }
//            @Override
//            public void onSwipeTop() throws IOException {
//                showInfo();
//            }
//            @Override
//            public void onSwipeRight() {
//                showPreviousImage();
//            }
//            public void onSwipeBottom() {
//                finish();
//            }
//        });
    }
//    @Override
//    public void onBackPressed() {
//        // Call your custom method to handle the back button press
//        super.onBackPressed();
//        if(editor_nav_bot.getVisibility() == View.VISIBLE){
//            editor_nav_bot.setVisibility(View.INVISIBLE);
//        } else finish();
//
//    }


    private void loadImage(String image_path) {
        Glide.with(this).load(image_path).into(imageView);
        this.image_path = image_path;
    }
//    private void showNextImage() {
//        if (currentIndex < images_list.size() - 1) {
//            currentIndex++;
//            loadImage(images_list.get(currentIndex));
//        } else {
//            Toast.makeText(ImageEditActivity.this, "No more images", Toast.LENGTH_SHORT).show();
//        }
//    }
//    private void showPreviousImage() {
//        if (currentIndex > 0) {
//            currentIndex--;
//            loadImage(images_list.get(currentIndex));
//        } else {
//            Toast.makeText(ImageDetailActivity.this, "No previous images", Toast.LENGTH_SHORT).show();
//        }
//    }
//    public void showInfo() throws IOException {
////        Uri uri = ImageView.setImageURI(new File(image_path)));
////        gfgIn = getContentResolver().openInputStream(uri);
////        ExifInterface exifInterface = new ExifInterface(new File(image_path));
//        ExifInterface exifInterface = new ExifInterface(new File(image_path).getAbsolutePath());
//        String imageDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
//        String imageMake = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
//        String imageModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
//        String imageFlash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
//        String imageFocalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
//        String imageLength = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
//        String imageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
//        String imageISO = exifInterface.getAttribute(ExifInterface.TAG_ISO);
//        String imageResolution = exifInterface.getAttribute(ExifInterface.TAG_RESOLUTION_UNIT);
//
////        Log.d("exif", imageDateTime);
////        Log.d("exif", imageMake);
////        Log.d("exif", imageModel);
////        Log.d("exif", imageFlash);
////        Log.d("exif", imageFocalLength);
////        Log.d("exif", imageLength);
////        Log.d("exif", imageWidth);
////        Log.d("exif", imageISO);
////        Log.d("exif", imageResolution);
//
//        BottomSheetDialog dialog = new BottomSheetDialog(this);
//        View bottomSheet = getLayoutInflater().inflate(R.layout.info_bottom_sheet, null);
//        dialog.setContentView(bottomSheet);
//        if (!dialog.isShowing()) {
//            TextView txt;
//            LinearLayout l;
//            if(imageDateTime != null){
//                Log.d("exif", imageDateTime);
//                txt = bottomSheet.findViewById(R.id.image_date_taken);
//                txt.setText(imageDateTime);
//            } else {
//                l = bottomSheet.findViewById(R.id.img_date);
//                l.setVisibility(View.GONE);
//            }
//            if(imageModel != null && imageFocalLength != null){
//                Log.d("exif", imageModel);
//                Log.d("exif", imageFocalLength);
//            } else {
//                l = bottomSheet.findViewById(R.id.img_camera);
//                l.setVisibility(View.GONE);
//            }
//            if(imageModel != null && imageFocalLength != null){
//                Log.d("exif", imageModel);
//                Log.d("exif", imageFocalLength);
//                txt = bottomSheet.findViewById(R.id.phone_describe);
//                txt.setText(imageModel + " · " + imageFocalLength);
//            }
//            if(imageLength != null && imageWidth != null && imageISO != null){
//                Log.d("exif", imageLength);
//                Log.d("exif", imageWidth);
//                Log.d("exif", imageISO);
//                txt = bottomSheet.findViewById(R.id.img_resolution);
//                txt.setText(imageLength + " x " + imageWidth + " pixels · " + imageISO);
//            }
//            if(image_path != null){
//                Log.d("exif", image_path);
//                txt = bottomSheet.findViewById(R.id.img_storage);
//                txt.setText("image_path");
//            }
//
//
//
//            dialog.show();
//        } else {
//            dialog.dismiss();
//        }
//    }
    public void onBackBtnClick(){
//        if(editor_nav_bot.getVisibility() == View.VISIBLE){
//            editor_nav_bot.setVisibility(View.INVISIBLE);
//        } else
        finish();
    }
//    public void onInfoBtnClick() throws IOException {
//        showInfo();
//    }
    public void onCropBtnClick() {
        Log.d("crop", "crop thui");
    }
    public void onFilterBtnClick() {
        Log.d("filter", "pha ke");
//        Intent intent = new Intent(this, ImageEditActivity.class);
////        intent.putExtra("image_path", images.get(position));
////        intent.putStringArrayListExtra("images_list", images);
//        startActivity(intent);
////        editor_nav_bot.setVisibility(View.VISIBLE);
////        finish();
    }
    public void onDoodleBtnClick() {
        Log.d("doodle", "ve tum lum");
//        finish();
    }
    public void onAdjustBtnClick() {
        Log.d("exif", "adjust");
    }
//    public void onRotateBtnClick() {
//        finish();
//    }
}
