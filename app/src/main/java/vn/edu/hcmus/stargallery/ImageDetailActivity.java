package vn.edu.hcmus.stargallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.util.ArrayList;

import vn.edu.hcmus.stargallery.Listener.OnSwipeTouchListener;

public class ImageDetailActivity extends AppCompatActivity {

    ImageView imageView;
    private int currentIndex;
    private ArrayList<String> images_list;
    String image_path = "";
    PopupWindow popupWindow;
    float x1, x2;
    private float scaleFactor = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);

        imageView = findViewById(R.id.imageView);

        BottomNavigationView nav_bot = findViewById(R.id.detail_nav_bot);
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

        BottomNavigationView nav_top = findViewById(R.id.detail_nav_top);
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
            public void onSwipeRight() {
                showPreviousImage();
            }
            public void onSwipeBottom() {
                finish();
            }
        });
    }
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
    public void onBackBtnClick(){
        finish();
    }
    public void onDetailBtnClick(View v) {
        finish();
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
        finish();
    }
    public void onFavoriteBtnClick() {
        finish();
    }
    public void onDeleteBtnClick() {
//                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(ImageDetailActivity.this);
//                confirmDialog.setTitle("Delete photo");
//                confirmDialog.setMessage("Do you want to delete it?");
//                confirmDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        new File(images_list.get(currentIndex)).delete();
//                        images_list.remove(currentIndex);
//                        if (images_list.size() > currentIndex) {
//                            loadImage(images_list.get(currentIndex));
//                        } else if (images_list.size() > currentIndex - 1) {
//                            currentIndex--;
//                            loadImage(images_list.get(currentIndex-1));
//                        }
//                    }
//                });
//                confirmDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.cancel();
//                    }
//                });
//                confirmDialog.show();
    }
    public void onRotateBtnClick() {
        finish();
    }
}
