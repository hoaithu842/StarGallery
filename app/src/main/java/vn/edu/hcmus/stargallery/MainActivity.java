package vn.edu.hcmus.stargallery;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                } else if (item.getItemId() == R.id.show_albums){
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, albumsFragment).commit();
                } else {
                    qrHelper.scanQRCode();
//                    ArrayList<String> list = new ArrayList<>();
//                    list.add("/storage/emulated/0/DCIM/Camera/PXL_20240409_024304167.jpg");
//                    list.add("/storage/emulated/0/DCIM/Camera/PXL_20240403_120504867.jpg");
//                    list.add("/storage/emulated/0/DCIM/Camera/PXL_20240328_104917260.jpg");
////                    list.add("/storage/emulated/0/Pictures/Screenshots/Screenshot_20240319-010738.png");
//                    gifHelper.createGif(list);
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
        int result1 = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        if(result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, imagesFragment).commit();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0){
            boolean accepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
            if(accepted){
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, imagesFragment).commit();
            }else{
                Toast.makeText(this,"You have denied the permission",Toast.LENGTH_LONG).show();
            }
        }else{

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