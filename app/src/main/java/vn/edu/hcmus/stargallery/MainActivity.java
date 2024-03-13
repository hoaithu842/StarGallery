package vn.edu.hcmus.stargallery;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    final List<File> files = new ArrayList<>();
    List<File> imageDirectories = new ArrayList<>();
    List<String> directoriesName = new ArrayList<>();

    private Button buttonAllImages;
    private Button buttonAlbums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAllImages = findViewById(R.id.buttonAllImages);
        buttonAlbums = findViewById(R.id.buttonAlbums);
    }
    private boolean isStarGalleryInitialized;
    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static int PERMISSIONS_COUNT = 2;
    private boolean arePermissionsDenied() {
        for (int i=0; i<PERMISSIONS_COUNT; i++) {
            if (checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_PERMISSIONS && grantResults.length>0) {
            if (arePermissionsDenied()) {
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            } else {
                onResume();
            }
        }
    }
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }
        // init app
        if (!isStarGalleryInitialized) {
            final GridView gridView = findViewById(R.id.gridView);
            final StarGalleryAdapter galleryAdapter = new StarGalleryAdapter();

            File imagesDirPictures = new File(String.valueOf(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                    )));

            File imagesDirDownloads = new File(String.valueOf(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOWNLOADS
                    )));

            File imagesDirDCIM = new File(String.valueOf(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DCIM
                    )));

            imageDirectories.add(imagesDirPictures);
            imageDirectories.add(imagesDirDownloads);
            imageDirectories.add(imagesDirDCIM);

            directoriesName.add("Pictures");
            directoriesName.add("Downloads");
            directoriesName.add("DCIM");

            for (File directory : imageDirectories) {
                File[] dir_files = directory.listFiles();
                if (dir_files != null) {
                    files.addAll(Arrays.asList(dir_files));
                }
            }

            final int filesCount = files.size();
            final List<String> filesList = new ArrayList<>();
            for (int i=0; i<filesCount; i++) {
                final String path = files.get(i).getAbsolutePath();
                if (path.endsWith(".jpg") || path.endsWith(".png") || path.endsWith(".jpeg")) {
                    filesList.add(path);
                }
            }
            galleryAdapter.setData(filesList);
            gridView.setAdapter(galleryAdapter);
            isStarGalleryInitialized = true;
        }
    }

    final class StarGalleryAdapter extends BaseAdapter {
        private List<String> data = new ArrayList<>();
        void setData(List<String> data) {
            if (this.data.size()>0) {
                data.clear();
            }
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final ImageView imageView;
            if (convertView==null) {
                imageView = (ImageView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
            } else {
                imageView = (ImageView) convertView;
            }
            Glide.with(MainActivity.this).load(data.get(position)).centerCrop().into(imageView);
            return imageView;
        }
    }
}