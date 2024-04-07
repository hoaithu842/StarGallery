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

public class ImageDetailActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_image_detail);

        editor_nav_bot = findViewById(R.id.editor_nav_bot);
        editor_nav_bot.setVisibility(View.INVISIBLE);

        imageView = findViewById(R.id.imageView);

        nav_bot = findViewById(R.id.detail_nav_bot);
        nav_bot.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.shareBtn) {
                    onShareBtnClick();
                } else if (item.getItemId() == R.id.editBtn) {
                    onEditBtnClick();
                } else if (item.getItemId() == R.id.favoriteBtn) {
                    onFavoriteBtnClick();
                } else if (item.getItemId() == R.id.deleteBtn) {
                    onDeleteBtnClick();
                } else if (item.getItemId() == R.id.rotateBtn) {
                    onRotateBtnClick();
                }
                return false;
            }
        });

        nav_top = findViewById(R.id.detail_nav_top);
        Menu menu = nav_top.getMenu();
        for (int i = 1; i < menu.size()-1; i++) {
            MenuItem menuItem = menu.getItem(i);
            menuItem.setEnabled(false);
        }
        nav_top.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.backBtn) {
                    onBackBtnClick();
                }
                else if(item.getItemId() == R.id.infoBtn) {
                    try {
                        onInfoBtnClick();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                return false;
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            image_path = intent.getStringExtra("image_path");
            images_list = intent.getStringArrayListExtra("images_list");

            if (images_list == null || images_list.isEmpty()) {
                // Handle case where no image paths are provided
                // return;
            }

            currentIndex = images_list.indexOf(image_path);
            if (image_path != null) {
                loadImage(image_path);
            }
        }
        // Swipe gestures

        imageView.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                showNextImage();
            }
            @Override
            public void onSwipeTop() throws IOException {
                showInfo();
            }
            @Override
            public void onSwipeRight() {
                showPreviousImage();
            }
            public void onSwipeBottom() {
                finish();
            }
        });
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
    private void showNextImage() {
        if (currentIndex < images_list.size() - 1) {
            currentIndex++;
            loadImage(images_list.get(currentIndex));
        } else {
            Toast.makeText(ImageDetailActivity.this, "No more images", Toast.LENGTH_SHORT).show();
        }
    }
    private void showPreviousImage() {
        if (currentIndex > 0) {
            currentIndex--;
            loadImage(images_list.get(currentIndex));
        } else {
            Toast.makeText(ImageDetailActivity.this, "No previous images", Toast.LENGTH_SHORT).show();
        }
    }
    public void showInfo() throws IOException {
//        Uri uri = ImageView.setImageURI(new File(image_path)));
//        gfgIn = getContentResolver().openInputStream(uri);
//        ExifInterface exifInterface = new ExifInterface(new File(image_path));
        ExifInterface exifInterface = new ExifInterface(new File(image_path).getAbsolutePath());
        String imageDateTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
        String imageMake = exifInterface.getAttribute(ExifInterface.TAG_MAKE);
        String imageModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL);
        String imageFlash = exifInterface.getAttribute(ExifInterface.TAG_FLASH);
        String imageFocalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);
        String imageLength = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        String imageWidth = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        String imageISO = exifInterface.getAttribute(ExifInterface.TAG_ISO);
        String imageResolution = exifInterface.getAttribute(ExifInterface.TAG_RESOLUTION_UNIT);

        Log.d("exif", imageDateTime);
        Log.d("exif", imageMake);
        Log.d("exif", imageModel);
        Log.d("exif", imageFlash);
        Log.d("exif", imageFocalLength);
        Log.d("exif", imageLength);
        Log.d("exif", imageWidth);
        Log.d("exif", imageISO);
        Log.d("exif", imageResolution);

        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View bottomSheet = getLayoutInflater().inflate(R.layout.info_bottom_sheet, null);
        dialog.setContentView(bottomSheet);
        if (!dialog.isShowing()) {
            TextView txt;

            txt = bottomSheet.findViewById(R.id.image_date_taken);
            txt.setText(imageDateTime);

            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
    public void onBackBtnClick(){
        if(editor_nav_bot.getVisibility() == View.VISIBLE){
            editor_nav_bot.setVisibility(View.INVISIBLE);
        } else finish();
    }
    public void onInfoBtnClick() throws IOException {
        showInfo();
    }
    public void onShareBtnClick() {
        if (images_list != null && currentIndex >= 0 && currentIndex < images_list.size()) {
            String filePath = images_list.get(currentIndex);
            File file = new File(filePath);
            if (file.exists()) {
                Uri fileUri = FileProvider.getUriForFile(ImageDetailActivity.this, getPackageName() + ".provider", file);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, fileUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                startActivity(Intent.createChooser(intent, "Share to:"));
            } else {
                Toast.makeText(ImageDetailActivity.this, "File not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ImageDetailActivity.this, "No image to share", Toast.LENGTH_SHORT).show();
        }
    }
    public void onEditBtnClick() {
        editor_nav_bot.setVisibility(View.VISIBLE);
//        finish();
    }
    public void onFavoriteBtnClick() {
        finish();
    }
    public void onDeleteBtnClick() {
        final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(ImageDetailActivity.this);
        confirmDialog.setTitle("Delete photo");
        confirmDialog.setMessage("Do you want to delete it?");
        confirmDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getApplicationContext(), "H notify ne", Toast.LENGTH_SHORT).show();
//                notifyImageDeleted(currentIndex); // Notify the fragment about image deletion
//                new File(images_list.get(currentIndex)).delete();
//                images_list.remove(currentIndex);
//                if (images_list.size() > currentIndex) {
//                    loadImage(images_list.get(currentIndex));
//                } else if (images_list.size() > currentIndex - 1) {
//                    currentIndex--;
//                    loadImage(images_list.get(currentIndex-1));
//                }
            }
        });
        confirmDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
                dialogInterface.dismiss();
            }
        });
        confirmDialog.show();
    }
    public void onRotateBtnClick() {
        finish();
    }
}
