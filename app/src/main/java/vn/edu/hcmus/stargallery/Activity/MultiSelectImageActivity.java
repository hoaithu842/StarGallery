package vn.edu.hcmus.stargallery.Activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import 	android.text.SpannableStringBuilder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationBarView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import vn.edu.hcmus.stargallery.Adapter.AlbumDetailAdapter;
import vn.edu.hcmus.stargallery.Adapter.MultiSelectImagesViewAdapter;
import vn.edu.hcmus.stargallery.Adapter.MultiSelectImagesViewAdapter;
import vn.edu.hcmus.stargallery.Helper.DatabaseHelper;
import vn.edu.hcmus.stargallery.Listener.OnSwipeTouchListener;
import vn.edu.hcmus.stargallery.R;

public class MultiSelectImageActivity extends AppCompatActivity {
    String album_name;
    String image_path;
    private static final int REQUEST_DELETE_ITEM = 100;

    ArrayList<String> images;
    ArrayList<String> uniqueSet ; //Selected images

    MultiSelectImagesViewAdapter adapter;
    GridLayoutManager manager;
    RecyclerView imagesView;
    DatabaseHelper db;
    ImageButton backBtn;
    ImageButton addBtn;
    ImageButton toTrashBtn;
    int REQUEST_CODE_TO_TRASH = 20;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiselect_image);

        db = new DatabaseHelper(this.getApplication());
        images = new ArrayList<>();
        uniqueSet = new ArrayList<>();
        Intent intent = getIntent();

        if (intent != null) {
            album_name = intent.getStringExtra("album_name");
            image_path = intent.getStringExtra("image_path");
            images = intent.getStringArrayListExtra("images_list");
            adapter = new MultiSelectImagesViewAdapter(this, images);
            manager = new GridLayoutManager(this,4);

            imagesView = findViewById(R.id.multi_images_view);
            imagesView.setAdapter(adapter);
            imagesView.setLayoutManager(manager);

        }
        backBtn = findViewById(R.id.multi_backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ArrayList<String> albums = db.getAlbums();
        albums.add("Favorite");
        albums.add("Create new album");
        addBtn = findViewById(R.id.multi_add_album_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MultiSelectImageActivity.this);
                builder.setTitle("Add images to");
                builder.setItems(albums.toArray(new String[albums.size()]), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String selectedAlbum = albums.get(which);
                        Toast.makeText(MultiSelectImageActivity.this, "Selected album: " + selectedAlbum, Toast.LENGTH_SHORT).show();
                        if(Objects.equals(selectedAlbum, "Favorite")){
                            db.insertMultiFavorite(uniqueSet);
                        }
                        else if(Objects.equals(selectedAlbum, "Create new album")){
                            final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(MultiSelectImageActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View dialogView = inflater.inflate(R.layout.create_alum_text, null);
                            final EditText input = dialogView.findViewById(R.id.album_name_input);
                            confirmDialog.setView(dialogView);
                            confirmDialog.setTitle("Add album");
                            confirmDialog.setMessage("Enter album's name");
                            confirmDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String abName = input.getText().toString().trim();
                                    if (!abName.isEmpty()) {
                                      if(db.createNewAlbum(abName)){
                                          Log.d("BAO CAO","TAO DUOC ALBUM ROI");
                                          db.insertMultiToAlbum(abName, uniqueSet);
                                          finish();
                                      }
                                      else{
                                          Toast.makeText(MultiSelectImageActivity.this, "Album already exists", Toast.LENGTH_SHORT).show();
                                      }
                                    } else {
                                        Toast.makeText(MultiSelectImageActivity.this, "Empty album name", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            confirmDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            confirmDialog.show();
                        }
                        else{
                            db.insertMultiToAlbum(selectedAlbum, uniqueSet);
                        }

                    }
                });
                builder.show();

            }
        });
        toTrashBtn = findViewById(R.id.multi_deletebtn);
        toTrashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(MultiSelectImageActivity.this);
                confirmDialog.setTitle("Delete photo");
                confirmDialog.setMessage("Do you want to delete it?");
                confirmDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.AddToTrashMultiImage(uniqueSet);
                        finish();
                    }
                });
                confirmDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                confirmDialog.show();
            }
        });
        adapter.setOnClickListener(new MultiSelectImagesViewAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                String img = images.get(position);
                Log.d("ANH NE", img);
                if(uniqueSet.contains(img)){
                    uniqueSet.remove(img);
                    adapter.unClickedImage(img);
                }
                else{
                    uniqueSet.add(img);
                    adapter.clickedImage(img);
                }
            }
        });
        adapter.setOnLongClickListener(new MultiSelectImagesViewAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(int position) {
                Toast.makeText(getApplicationContext(), "LONGGGG", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton slideshowBtn = findViewById(R.id.slideshow_btn);
        slideshowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSlideshow();
            }
        });
    }
    private void startSlideshow() {
        // Check if there are selected images
        if (uniqueSet.isEmpty()) {
            Toast.makeText(MultiSelectImageActivity.this, "No images selected for slideshow", Toast.LENGTH_SHORT).show();
            return;
        }

        // Start the slideshow with selected images
        Intent intent = new Intent(MultiSelectImageActivity.this, SlideshowActivity.class);
        intent.putStringArrayListExtra("selected_images", uniqueSet);
        startActivity(intent);
    }
}
