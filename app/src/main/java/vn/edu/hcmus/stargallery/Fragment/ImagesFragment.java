package vn.edu.hcmus.stargallery.Fragment;

import static android.os.Environment.MEDIA_MOUNTED;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

import vn.edu.hcmus.stargallery.Activity.MultiSelectImageActivity;
import vn.edu.hcmus.stargallery.Adapter.ImagesViewAdapter;
import vn.edu.hcmus.stargallery.Activity.ImageDetailActivity;
import vn.edu.hcmus.stargallery.R;

public class ImagesFragment extends Fragment {
    private static final int REQUEST_DELETE_ITEM = 100;
    RecyclerView imagesView;
    LinearLayout layout;
    static int PERMISSION_REQUEST_CODE = 100;
    ArrayList<String> images;
    ArrayList<String> filteredImages = null;
    ImagesViewAdapter adapter;
    GridLayoutManager manager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        images = new ArrayList<>();
        adapter = new ImagesViewAdapter(getContext(),images);
        manager = new GridLayoutManager(getContext(),4);

        adapter.setOnClickListener(new ImagesViewAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Log.d("GIF", images.get(position));
                Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                if (filteredImages == null) {
                    intent.putExtra("image_path", images.get(position));
                    intent.putStringArrayListExtra("images_list", images);
                } else {
                    intent.putExtra("image_path", filteredImages.get(position));
                    intent.putStringArrayListExtra("images_list", filteredImages);
                }

                startActivityForResult(intent, REQUEST_DELETE_ITEM);
            }
        });
        adapter.setOnLongClickListener(new ImagesViewAdapter.OnLongClickListener() {
            @Override
            public void onLongClick(int position) {
                Intent intent = new Intent(getActivity(), MultiSelectImageActivity.class);
                intent.putExtra("image_path", images.get(position));
                intent.putStringArrayListExtra("images_list", images);
                startActivity(intent);
                Toast.makeText(getContext(), "LONGGGG", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterImages(String query) {
        filteredImages = new ArrayList<>();
        for (String imagePath : images) {
            // Perform a case-insensitive search on the image file path
            if (imagePath.toLowerCase().contains(query.toLowerCase())) {
                filteredImages.add(imagePath);
            }
        }
        Log.d("Searched: ", Integer.toString(filteredImages.size()));
        // Update the adapter with the filtered list of images
        TextView txt = layout.findViewById(R.id.totalImage);
        if (filteredImages.size()==0) {
            txt.setText("Empty");
        } else if (filteredImages.size()==1) {
            txt.setText(Integer.toString(filteredImages.size()) + " photo");
        } else {
            txt.setText(Integer.toString(filteredImages.size()) + " photos");
        }
        adapter.setImages(filteredImages);
        imagesView.getAdapter().notifyDataSetChanged();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DELETE_ITEM && resultCode == Activity.RESULT_OK) {

            Integer itemDeleted = data.getIntExtra("itemDeleted", 0);
            if (itemDeleted >= 0 && itemDeleted < images.size()) {
                String imagePath = images.get(itemDeleted);
                images.remove(images.get(itemDeleted));
                imagesView.getAdapter().notifyItemRemoved(itemDeleted);
                // Handle item deletion
                // Check if the image path exists before deletion (avoid potential errors)
                if (new File(imagePath).exists()) {
                    int deleted = getContext().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Images.Media.DATA + " = ?", new String[]{imagePath});
                    if (deleted == 1) {
                        // Image deleted successfully
                        Log.i("ImageDelete", "Image deleted: " + imagePath);
                    } else {
                        Log.w("ImageDelete", "Failed to delete image: " + imagePath);
                    }
                } else {
                    Log.w("ImageDelete", "Image path not found: " + imagePath);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        images.clear();
        loadImages();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_images, container, false);
        imagesView = layout.findViewById(R.id.images_view);
        imagesView.setAdapter(adapter);
        imagesView.setLayoutManager(manager);
        loadImages();
        TextView txt = layout.findViewById(R.id.totalImage);
        txt.setText(Integer.toString(images.size()) + " photos");

        EditText searchEditText = layout.findViewById(R.id.search_et);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used, but required for TextWatcher interface
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not used, but required for TextWatcher interface
                Log.d("You typed: ", s.toString());
                if (s.toString().length()==0) {
                    adapter.setImages(images);
                    imagesView.getAdapter().notifyDataSetChanged();
                    filteredImages = null;
                    TextView txt = layout.findViewById(R.id.totalImage);
                    if (images.size()==0) {
                        txt.setText("Empty");
                    } else if (images.size()==1) {
                        txt.setText(Integer.toString(images.size()) + " photo");
                    } else {
                        txt.setText(Integer.toString(images.size()) + " photos");
                    }
                } else {
                    filterImages(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Filter the images based on the search query

            }
        });

        return layout;
    }

    private void loadImages() {
        boolean SDCard = Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
        if (SDCard) {
            final String[] colums = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String order = MediaStore.Images.Media.DATE_TAKEN + " DESC";
            Cursor cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, colums, null, null, order);
            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                cursor.moveToPosition(i);
                int colunmindex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                images.add(cursor.getString(colunmindex));
            }
            imagesView.getAdapter().notifyDataSetChanged();
        }
    }

    public Boolean deleteFile(Context context, Uri uri) {
        File file = new File(uri.getPath());
        String [] selectionArgs = {(file.getAbsolutePath())};
        ContentResolver contentResolver = context.getContentResolver();
        String where = null;
        Uri filesUri = null;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            filesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            where = MediaStore.Images.Media._ID + "=?";
            selectionArgs = new String[] {(file.getName())};
        } else {
            where = MediaStore.MediaColumns.DATA + "=?";
            filesUri = MediaStore.Files.getContentUri("external");
        }
        int result =  contentResolver.delete(filesUri, where, selectionArgs);
        return file.exists();
    }
}