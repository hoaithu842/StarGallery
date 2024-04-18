package vn.edu.hcmus.stargallery.Fragment;

import static android.os.Environment.MEDIA_MOUNTED;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;
import java.util.HashMap;

import vn.edu.hcmus.stargallery.Activity.FaceDetectActivity;
import vn.edu.hcmus.stargallery.Activity.ImageDuplicateActivity;
import vn.edu.hcmus.stargallery.R;

public class UtilitiesFragment extends Fragment {
    private static final int REQUEST_DELETE_ITEM = 100;
    LinearLayout layout;
    static int PERMISSION_REQUEST_CODE = 100;
    ArrayList<String> images;
    ArrayList<String> selfies;
    HashMap<String, ArrayList<String>> albums;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        images = new ArrayList<>();
    }

    @Override
    public void onResume() {
        super.onResume();
        images.clear();
        loadImages();
//        loadSelfies();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_utilities, container, false);

        LinearLayout duplicate_button = layout.findViewById(R.id.show_duplicated);
        duplicate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageDuplicateActivity.class);
                intent.putExtra("album_name", "Duplicated Images");
                intent.putStringArrayListExtra("images_list", images);

                startActivity(intent);
            }
        });

        ArrayList<String> album = loadAlbums();
        LinearLayout familiar_pp_button = layout.findViewById(R.id.show_faces_detected);
        familiar_pp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FaceDetectActivity.class);
                intent.putExtra("album_name", "Faces Detected");
                intent.putStringArrayListExtra("images_list", album);

                startActivity(intent);
            }
        });

        loadImages();
//        loadSelfies();
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
        }
    }

    private ArrayList<String> loadAlbums() {
        albums = new HashMap<>();
        boolean SDCard = Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
        if (SDCard) {
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
            final String order = MediaStore.Images.Media.DATE_TAKEN + " DESC";
            Cursor cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, order);
//            if (cursor != null) {
            while (cursor.moveToNext()) {
                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                int bucketIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                String imagePath = cursor.getString(columnIndex);
                String albumName = cursor.getString(bucketIndex);

                ArrayList<String> imagesList;
                if (!albums.containsKey(albumName)) {
                    imagesList = new ArrayList<>();
                    imagesList.add(imagePath);
                    albums.put(albumName, imagesList);
                } else {
                    imagesList = albums.get(albumName);
                    imagesList.add(imagePath);
                }
            }
        }
        return albums.get("Camera");
    }

//    private void loadSelfies() {
//        selfies = new ArrayList<>();
//        // Add logic to filter out selfies based on specific criteria
//        // For example, you can check the image path or image orientation to identify selfies
//        // Here, let's assume selfies contain "selfie" in their path
//        for (String imagePath : images) {
//            if (imagePath.toLowerCase().contains("selfie")) {
//                selfies.add(imagePath);
//            }
//        }
//    }
}