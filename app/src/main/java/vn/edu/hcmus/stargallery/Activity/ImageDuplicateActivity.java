package vn.edu.hcmus.stargallery.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.edu.hcmus.stargallery.Adapter.AlbumDetailAdapter;
import vn.edu.hcmus.stargallery.Adapter.AlbumsViewAdapter;
import vn.edu.hcmus.stargallery.Helper.ImageDuplicateFinder;
import vn.edu.hcmus.stargallery.R;

public class ImageDuplicateActivity extends AppCompatActivity {
    String album_name;
    private static final int REQUEST_DELETE_ITEM = 100;
    ArrayList<String> images;
    ArrayList<String> duplicated;
    ImageButton backBtn;
    AlbumDetailAdapter adapter;
    GridLayoutManager manager;
    RecyclerView imagesView;
    public void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        images = new ArrayList<>();
        duplicated = new ArrayList<>();

        Intent intent = getIntent();
        if (intent != null) {
            album_name = intent.getStringExtra("album_name");
            images = intent.getStringArrayListExtra("images_list");

            adapter = new AlbumDetailAdapter(this, duplicated);
            manager = new GridLayoutManager(this,4);

            imagesView = findViewById(R.id.album_images_view);
            imagesView.setAdapter(adapter);
            imagesView.setLayoutManager(manager);

            TextView total = findViewById(R.id.totalAlbumImage);
            SpannableStringBuilder s = new SpannableStringBuilder();
            s.append(album_name);
            s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, album_name.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            total.setText(s);
//            TextView txt = findViewById(R.id.totalImage);
//            txt.setText(Integer.toString(images.size()) + " photos");
        }

        backBtn = findViewById(R.id.album_detail_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


//        adapter.setOnClickListener(new AlbumDetailAdapter.OnClickListener() {
//            @Override
//            public void onClick(int position) {
//                Intent intent = new Intent(ImagesDuplicatedActivity.this, ImageDetailActivity.class);
//                intent.putExtra("image_path", images.get(position));
//                intent.putStringArrayListExtra("images_list", images);
//                startActivity(intent);
//            }
//        });
        findDuplicate();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void findDuplicate() {
        ImageDuplicateFinder finder = new ImageDuplicateFinder(images, new ImageDuplicateFinder.OnDuplicateImageFoundListener() {
            @Override
            public void onDuplicateImageFound(List<List<String>> duplicateGroups) {
                for (List<String> group : duplicateGroups) {
                    if (group.size() > 1) {
                        duplicated.addAll(group);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        finder.findDuplicateImagesAsync();
    }
}
