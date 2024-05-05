package vn.edu.hcmus.stargallery.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import 	android.text.SpannableStringBuilder;
import androidx.activity.EdgeToEdge;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import vn.edu.hcmus.stargallery.Adapter.AlbumDetailAdapter;
import vn.edu.hcmus.stargallery.Adapter.ImagesViewAdapter;
import vn.edu.hcmus.stargallery.Helper.DatabaseHelper;
import vn.edu.hcmus.stargallery.Listener.OnSwipeTouchListener;
import vn.edu.hcmus.stargallery.R;

public class AlbumDetailActivity extends AppCompatActivity {
    String album_name;
    private static final int REQUEST_DELETE_ITEM = 100;
    DatabaseHelper dbHelper;
    ArrayList<String> images;
    AlbumDetailAdapter adapter;
    GridLayoutManager manager;
    RecyclerView imagesView;
    ImageButton backBtn;
    ImageButton addImgBtn;
    public void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        images=new ArrayList<>();
        dbHelper = new DatabaseHelper(this.getApplication());

        Intent intent = getIntent();
        if (intent != null) {
            album_name = intent.getStringExtra("album_name");
            images = intent.getStringArrayListExtra("images_list");
            images.removeAll(dbHelper.getTrashImages());

            adapter=new AlbumDetailAdapter(this, images);
            manager=new GridLayoutManager(this,4);

            imagesView = findViewById(R.id.album_images_view);
            imagesView.setAdapter(adapter);
            imagesView.setLayoutManager(manager);

            TextView total = findViewById(R.id.totalAlbumImage);
            SpannableStringBuilder s = new SpannableStringBuilder();
            s.append(album_name);
            s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, album_name.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
//            s.append("\n").append(images.size() + " images");
            total.setText(s);
            TextView txt = findViewById(R.id.totalImage);
            txt.setText(Integer.toString(images.size()) + " photos");
        }
        backBtn = findViewById(R.id.album_detail_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                finish();
           }
        });
        adapter.setOnClickListener(new AlbumDetailAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(AlbumDetailActivity.this, ImageDetailActivity.class);
                intent.putExtra("image_path", images.get(position));
                intent.putStringArrayListExtra("images_list", images);
                startActivity(intent);
            }
        });
        adapter.setOnLongClickListener(new AlbumDetailAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(int position) {
                Intent intent = new Intent(AlbumDetailActivity.this, MultiSelectImageActivity.class);
                intent.putExtra("album_name", album_name);
                intent.putExtra("image_path", images.get(position));
                intent.putStringArrayListExtra("images_list", images);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("AlbumDetailActivity", "onResume");
        // Check if there are changes to the images list while the activity was paused or stopped
        int previousSize = images.size();
        images.removeAll(dbHelper.getTrashImages());
        if (previousSize != images.size()) {
            // If there are changes, notify the adapter
            adapter.notifyDataSetChanged();
        }
    }
}
