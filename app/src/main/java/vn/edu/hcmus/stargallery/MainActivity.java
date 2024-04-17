package vn.edu.hcmus.stargallery;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;
import android.webkit.MimeTypeMap;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import java.util.ArrayList;

import vn.edu.hcmus.stargallery.Activity.ImageDetailActivity;
import vn.edu.hcmus.stargallery.Fragment.AlbumsFragment;
import vn.edu.hcmus.stargallery.Fragment.ImagesFragment;
import vn.edu.hcmus.stargallery.Helper.QRHelper;

public class MainActivity extends AppCompatActivity implements QRHelper.QRScanListener {
    AlbumsFragment albumsFragment;
    ImagesFragment imagesFragment;
    static int PERMISSION_REQUEST_CODE=100;
    QRHelper qrHelper;
    private void setStatusBarColorFromLayout() {
        // For demonstration, let's assume you have a layout with ID "mainLayout"
//        int backgroundColor = getResources().getColor(R.color.gradient); // Get color from your layout

        // Set the status bar color
        int statusBarColor = ContextCompat.getColor(this, R.color.black);
        setStatusBarColor(statusBarColor);
    }

    private void setStatusBarColor(int color) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBarColorFromLayout();

        qrHelper = new QRHelper(this);
        qrHelper.setQRScanListener(this);

        albumsFragment = new AlbumsFragment();
        imagesFragment = new ImagesFragment();

        checkPermissions();
        BottomNavigationView nav = findViewById(R.id.main_nav);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.show_photos) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, imagesFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.show_albums){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, albumsFragment).commit();
                    return true;
                } else if (item.getItemId() == R.id.scan_qr){
                    qrHelper.scanQRCode();
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        qrHelper.handleScanResult(requestCode, resultCode, data);
    }
    private void checkPermissions() {
        var context = getApplicationContext();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            var intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            startActivityForResult(intent, 2296);
        }

        int result1 = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        if (result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, imagesFragment).commit();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            boolean accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (accepted) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, imagesFragment).commit();
            } else {
                Toast.makeText(this, "You have denied the permission", Toast.LENGTH_LONG).show();
            }
        } else {

        }
    }

    @Override
    public void onQRScanSuccess(String scannedData) {
        if (isValidUrl(scannedData)) {
            openUrlInBrowser(scannedData);
        } else {
            if (isImage(scannedData)) {
                displayImage(scannedData);
            } else {
                Log.d("QR", "Scanned data is neither a URL nor an image");
            }
        }
    }

    @Override
    public void onQRScanFailure() {
        Log.d("QR", "Scan failed or cancelled");
    }

    private boolean isValidUrl(String url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }

    private void openUrlInBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private boolean isImage(String filePath) {
        String mimeType = getMimeType(filePath);
        return mimeType != null && mimeType.startsWith("image");
    }

    private String getMimeType(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    private void displayImage(String imagePath) {
        Intent intent = new Intent(this, ImageDetailActivity.class);
        intent.putExtra("image_path", imagePath);

        ArrayList<String> images = new ArrayList<>();
        images.add(imagePath);
        intent.putStringArrayListExtra("images_list", images);
        int REQUEST_DELETE_ITEM = 100;

        startActivityForResult(intent, REQUEST_DELETE_ITEM);
    }
}