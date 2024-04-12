package vn.edu.hcmus.stargallery.Fragment;

import static android.os.Environment.MEDIA_MOUNTED;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import vn.edu.hcmus.stargallery.Adapter.ImagesViewAdapter;
import vn.edu.hcmus.stargallery.Activity.ImageDetailActivity;
import vn.edu.hcmus.stargallery.R;

public class ImagesFragment extends Fragment {
    private static final int REQUEST_DELETE_ITEM = 100;
    RecyclerView imagesView;
    LinearLayout layout;
    static int PERMISSION_REQUEST_CODE = 100;
    ArrayList<String> images;
    ImagesViewAdapter adapter;
    GridLayoutManager manager;
    boolean shouldExecuteOnResume = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        shouldExecuteOnResume = false;
        super.onCreate(savedInstanceState);

        images = new ArrayList<>();
        adapter = new ImagesViewAdapter(getContext(),images);
        manager = new GridLayoutManager(getContext(),4);

        adapter.setOnClickListener(new ImagesViewAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Log.d("GIF", images.get(position));
                Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                intent.putExtra("image_path", images.get(position));
                intent.putStringArrayListExtra("images_list", images);
                startActivityForResult(intent, REQUEST_DELETE_ITEM);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_DELETE_ITEM && resultCode == Activity.RESULT_OK) {

            Integer itemDeleted = data.getIntExtra("itemDeleted", 0);
            if (itemDeleted >= 0 && itemDeleted < images.size()) {
                // Handle item deletion
                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                String selection = MediaStore.Images.Media.DATA + "=?";
                String[] selectionArgs = new String[]{ images.get(itemDeleted) };
                Cursor cursor = getContext().getContentResolver().query(imageUri, null, selection, selectionArgs, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int uriIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    long imageId = cursor.getLong(uriIndex);
                    imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId);
                    cursor.close();
                } else {
                    // Handle case when the image is not found in MediaStore
                    Toast.makeText(getContext(), "Failed to delete file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        boolean SDCard = Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
        if(SDCard){
            final String[] colums = {MediaStore.Images.Media.DATA,MediaStore.Images.Media._ID};
            final String order = MediaStore.Images.Media.DATE_TAKEN+" DESC";
            Cursor cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,colums,null,null,order);
            int count = cursor.getCount();
            for(int i=0; i<count; i++){
                cursor.moveToPosition(i);
                int colunmindex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (!images.contains(cursor.getString(colunmindex))) {
                    images.add(0, cursor.getString(colunmindex));
                } else {
                    imagesView.getAdapter().notifyDataSetChanged();
                    return;
                }
            }
        }
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
        txt.setText("Total " + Integer.toString(images.size()) + " images");
        return layout;
    }

    private void loadImages() {
        boolean SDCard= Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
        if(SDCard){
            final String[] colums = {MediaStore.Images.Media.DATA,MediaStore.Images.Media._ID};
            final String order = MediaStore.Images.Media.DATE_TAKEN+" DESC";
            Cursor cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,colums,null,null,order);
            int count = cursor.getCount();
            for(int i=0; i<count; i++){
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