package vn.edu.hcmus.stargallery;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean isStarGalleryInitialized;
    private List<String> filesList;
    private static final int REQUEST_PERMISSIONS = 1234;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static int PERMISSIONS_COUNT = 2;

    private boolean arePermissionsDenied() {
        for (int i = 0; i < PERMISSIONS_COUNT; i++) {
            if (checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        return false;
    }

    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS && grantResults.length > 0) {
            if (arePermissionsDenied()) {
                ((ActivityManager) Objects.requireNonNull(this.getSystemService(ACTIVITY_SERVICE))).clearApplicationUserData();
                recreate();
            } else {
                onResume();
            }
        }
    }

    void addImagesFrom(String dirPath, String dirName) {
        File imagesDir = new File(String.valueOf(
                Environment.getExternalStoragePublicDirectory(dirPath)
            ));
        imageDirectories.add(imagesDir);
        directoriesName.add(dirName);
    }

    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && arePermissionsDenied()) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
            return;
        }
        // init app
        if (!isStarGalleryInitialized) {
            filesList = new ArrayList<>();

            final GridView gridView = findViewById(R.id.gridView);
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            int ColNum = 4;
            int screenWidth = displayMetrics.widthPixels ; //- totalHorizontalSpacing; // Subtract total spacing from screen width
            int columnWidth = screenWidth / ColNum; // Divide by number of columns
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, screenWidth + " " + gridView.getHorizontalSpacing() + " " + columnWidth, duration);
            toast.show();
            gridView.setColumnWidth(columnWidth);

            final StarGalleryAdapter galleryAdapter = new StarGalleryAdapter();
            galleryAdapter.setItemSize(columnWidth);


            addImagesFrom(Environment.DIRECTORY_PICTURES, "Pictures");
            addImagesFrom(Environment.DIRECTORY_DOWNLOADS, "Downloads");
            addImagesFrom(Environment.DIRECTORY_DCIM, "DCIM");
            addImagesFrom(Environment.DIRECTORY_SCREENSHOTS,"Screenshots");
//            addImagesFrom(Environment.D);
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

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    String name = filesList.get(position).substring(filesList.get(position).lastIndexOf('/')+1);
                    String imagePath = filesList.get(position);
                    Intent intent = new Intent(MainActivity.this, ImageDetailActivity.class);
                    intent.putExtra("imagePaths", filesList.toArray(new String[0])); // Pass the array of image paths
                    intent.putExtra("imagePath", imagePath);
                    startActivity(intent);
                }
            });
            // onLongClick...
            isStarGalleryInitialized = true;
        }
    }

    final class StarGalleryAdapter extends BaseAdapter {
        private List<String> data = new ArrayList<>();
        private int itemSize; // Size of each item

        void setData(List<String> data) {
            if (this.data.size() > 0) {
                data.clear();
            }
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        void setItemSize(int size) {
            this.itemSize = size;
            notifyDataSetChanged(); // Notify adapter about the size change
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
            if (convertView == null) {
                imageView = new ImageView(viewGroup.getContext());
                imageView.setLayoutParams(new GridView.LayoutParams(itemSize, itemSize)); // Set square dimensions
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView = (ImageView) convertView;
            }
            Glide.with(MainActivity.this).load(data.get(position)).centerCrop().into(imageView);
            return imageView;
        }
    }
}