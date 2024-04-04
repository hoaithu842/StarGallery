package vn.edu.hcmus.stargallery;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.os.Environment.MEDIA_MOUNTED;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

import vn.edu.hcmus.stargallery.Adapter.ImagesViewAdapter;
import vn.edu.hcmus.stargallery.Fragment.AlbumsFragment;
import vn.edu.hcmus.stargallery.Fragment.ImagesFragment;

public class MainActivity extends AppCompatActivity {
    AlbumsFragment albumsFragment;
    ImagesFragment imagesFragment;
    static int PERMISSION_REQUEST_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        albumsFragment = new AlbumsFragment();
        imagesFragment = new ImagesFragment();

        checkPermissions();
        BottomNavigationView nav = findViewById(R.id.main_nav);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.show_photos) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, imagesFragment).commit();
                } else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, albumsFragment).commit();
                }
                return false;
            }
        });
    }
    private void checkPermissions() {
        int result= ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, imagesFragment).commit();
        }else{
            ActivityCompat.requestPermissions(this,new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
}