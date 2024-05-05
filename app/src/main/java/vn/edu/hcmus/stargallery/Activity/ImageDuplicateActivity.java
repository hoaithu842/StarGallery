package vn.edu.hcmus.stargallery.Activity;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.NotificationManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vn.edu.hcmus.stargallery.Adapter.AlbumDetailAdapter;
import vn.edu.hcmus.stargallery.Adapter.AlbumsViewAdapter;
import vn.edu.hcmus.stargallery.Helper.ImageDuplicateFinder;
import vn.edu.hcmus.stargallery.R;

public class ImageDuplicateActivity extends AppCompatActivity {
    private static final String CACHE_KEY_DUPLICATED_IMAGES = "duplicated_images";
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

        TextView totalImage = findViewById(R.id.totalImage);
        totalImage.setVisibility(View.GONE);

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

        adapter.setOnClickListener(new AlbumDetailAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(ImageDuplicateActivity.this, ImageDetailActivity.class);
                intent.putExtra("image_path", duplicated.get(position));
                intent.putStringArrayListExtra("images_list", duplicated);
                startActivity(intent);
            }
        });

        backBtn = findViewById(R.id.album_detail_back_btn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadPrefernces();
        findDuplicate();

        ImageButton syncButton = findViewById(R.id.sync_button);
        syncButton.setVisibility(View.VISIBLE);
        syncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Syncing", "Syncing");
                loadPrefernces();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void findDuplicate() {
        ImageDuplicateFinder finder = new ImageDuplicateFinder(images, new ImageDuplicateFinder.OnDuplicateImageFoundListener() {
            @Override
            public void onDuplicateImageFound(List<List<String>> duplicateGroups) {
                duplicated.clear();
                for (List<String> group : duplicateGroups) {
                    if (group.size() > 1) {
                        duplicated.addAll(group);
                    }
                }
                saveDuplicatedImagesToCache();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "You can hit the sync button to reload newest fetch", Toast.LENGTH_SHORT).show();
//                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
        finder.findDuplicateImagesAsync();
    }

    private void saveDuplicatedImagesToCache() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Set<String> duplicatedSet = new HashSet<>(duplicated);
        editor.putStringSet(CACHE_KEY_DUPLICATED_IMAGES, duplicatedSet);
        editor.apply();
        Log.d("Saved newest fetch", "Saved newest fetch");
    }

    private void loadPrefernces() {
        SharedPreferences preferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        if (preferences.contains(CACHE_KEY_DUPLICATED_IMAGES)) {
            duplicated.clear();
            Set<String> duplicatedSet = preferences.getStringSet(CACHE_KEY_DUPLICATED_IMAGES, new HashSet<>());
            duplicated.addAll(duplicatedSet);
            adapter.notifyDataSetChanged();
            if (duplicated.size()==0) {
                Toast.makeText(this, "Fetched! The result is empty", Toast.LENGTH_SHORT).show();            }
        } else {
            Log.d("Empty", "Empty");
            showNotification();
        }
    }

    private void showNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            CharSequence name = "YourChannelName";
            String description = "YourChannelDescription";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("my_channel_id", name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.icon_next)
                .setContentTitle("First time access will take time analyzing")
                .setContentText("Please hit the sync button again later to fetch the newest result.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }
}
