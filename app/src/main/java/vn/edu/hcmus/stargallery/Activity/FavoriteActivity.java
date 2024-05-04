package vn.edu.hcmus.stargallery.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import vn.edu.hcmus.stargallery.Adapter.AlbumDetailAdapter;
import vn.edu.hcmus.stargallery.Helper.DatabaseHelper;
import vn.edu.hcmus.stargallery.R;

public class FavoriteActivity extends AppCompatActivity {
    private static final int REQUEST_DELETE_ITEM = 100;
    ArrayList<String> images;
    AlbumDetailAdapter adapter;
    GridLayoutManager manager;
    RecyclerView imagesView;
    ImageButton backBtn;
    ImageButton recoverBtn;
    ImageButton delBtn;

    DatabaseHelper db;
    public void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        db = new DatabaseHelper(this.getApplication());
        images = new ArrayList<>();
        Intent intent = getIntent();

        if (intent != null) {

            images = db.getFavoriteImages();

            adapter = new AlbumDetailAdapter(this, images);
            manager = new GridLayoutManager(this,4);

            imagesView = findViewById(R.id.favorite_images_view);
            imagesView.setAdapter(adapter);
            imagesView.setLayoutManager(manager);

            TextView txt = findViewById(R.id.totalImage);
            txt.setText(Integer.toString(images.size()) + " photos");
        }
        adapter.setOnClickListener(new AlbumDetailAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(FavoriteActivity.this, ImageDetailActivity.class);
                intent.putExtra("image_path", images.get(position));
                intent.putStringArrayListExtra("images_list", images);
                startActivity(intent);
            }
        });
        adapter.setOnLongClickListener(new AlbumDetailAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(int position) {
//                Intent intent = new Intent(FavoriteCanActivity.this, MultiSelectImageActivity.class);
////                intent.putExtra("album_name", album_name);
//                intent.putExtra("image_path", images.get(position));
//                intent.putStringArrayListExtra("images_list", images);
//                Toast.makeText(getApplicationContext(), "LONGGGG", Toast.LENGTH_SHORT).show();
//                startActivity(intent);
            }
        });

        backBtn = findViewById(R.id.favorite_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_DELETE_ITEM && resultCode == Activity.RESULT_OK) {
//
//            Integer itemDeleted = data.getIntExtra("itemDeleted", 0);
//            if (itemDeleted >= 0 && itemDeleted < images.size()) {
//                String imagePath = images.get(itemDeleted);
//                images.remove(images.get(itemDeleted));
//                imagesView.getAdapter().notifyItemRemoved(itemDeleted);
//                // Handle item deletion
//                // Check if the image path exists before deletion (avoid potential errors)
//                if (new File(imagePath).exists()) {
//                    int deleted = getApplicationContext().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                            MediaStore.Images.Media.DATA + " = ?", new String[]{imagePath});
//                    if (deleted == 1) {
//                        // Image deleted successfully
//                        Log.i("ImageDelete", "Image deleted: " + imagePath);
//                    } else {
//                        Log.w("ImageDelete", "Failed to delete image: " + imagePath);
//                    }
//                } else {
//                    Log.w("ImageDelete", "Image path not found: " + imagePath);
//                }
//
//
//            }
//        }
//    }

}
