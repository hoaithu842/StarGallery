package vn.edu.hcmus.stargallery.Fragment;

import static android.os.Environment.MEDIA_MOUNTED;

import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import vn.edu.hcmus.stargallery.Activity.AlbumDetailActivity;
import vn.edu.hcmus.stargallery.Activity.ImageDetailActivity;
import vn.edu.hcmus.stargallery.Adapter.AlbumsViewAdapter;
import vn.edu.hcmus.stargallery.Adapter.ImagesViewAdapter;
import vn.edu.hcmus.stargallery.Helper.DatabaseHelper;
import vn.edu.hcmus.stargallery.MainActivity;
import vn.edu.hcmus.stargallery.R;

public class AlbumsFragment extends Fragment {
    RecyclerView albumsView;
    LinearLayout layout;
    static int PERMISSION_REQUEST_CODE=100;
    HashMap<String, ArrayList<String>> albums;
    AlbumsViewAdapter adapter;
    GridLayoutManager manager;
    boolean shouldExecuteOnResume = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        shouldExecuteOnResume = false;
        super.onCreate(savedInstanceState);

        albums = new HashMap<>();
        adapter = new AlbumsViewAdapter(getContext(), albums);
        manager = new GridLayoutManager(getContext(),2);

        adapter.setOnClickListener(new AlbumsViewAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                String album_name = new ArrayList<>(albums.keySet()).get(position);
                Log.d("ab_name", album_name);
                Log.d("ab_", Integer.toString(albums.get(album_name).size()));
                Intent intent = new Intent(getActivity(), AlbumDetailActivity.class);
                intent.putExtra("album_name", album_name);
                intent.putStringArrayListExtra("images_list", albums.get(album_name));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        albums.clear();
        loadAlbums();
//        boolean SDCard = Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
//        if (SDCard) {
//            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
//            final String order = MediaStore.Images.Media.DATE_TAKEN + " DESC";
//            Cursor cursor = getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, order);
////            if (cursor != null) {
//            while (cursor.moveToNext()) {
//                int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//                int bucketIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//
//                String imagePath = cursor.getString(columnIndex);
//                String albumName = cursor.getString(bucketIndex);
//
//                if (!albums.containsKey(albumName)) {
//                    ArrayList<String> imagesList = new ArrayList<>();
//                    imagesList.add(imagePath);
//                    albums.put(albumName, imagesList);
//                } else {
//                    ArrayList<String> imagesList = albums.get(albumName);
//                    imagesList.add(imagePath);
//                }
//            }
////                cursor.close();
////            }
//            albumsView.getAdapter().notifyDataSetChanged();
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_albums, container, false);
        albumsView = layout.findViewById(R.id.albums_view);
        albumsView.setAdapter(adapter);
        albumsView.setLayoutManager(manager);
        loadAlbums();
        return layout;
    }
    private void loadAlbums() {
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

                    if (!albums.containsKey(albumName)) {
                        ArrayList<String> imagesList = new ArrayList<>();
                        imagesList.add(imagePath);
                        albums.put(albumName, imagesList);
                    } else {
                        ArrayList<String> imagesList = albums.get(albumName);
                        imagesList.add(imagePath);
                    }
                }
        }
        Log.d("Empty", "Tu local " + Integer.toString(albums.size()));
        DatabaseHelper dbHelper = new DatabaseHelper((Application) requireActivity().getApplicationContext());
        ArrayList<String> imagesList = dbHelper.getFavoriteImages();
        if (imagesList.isEmpty()) {
            albumsView.getAdapter().notifyDataSetChanged();
        } else {
            albums.put("Favorites", imagesList);
            Log.d("Empty", "Sau khi doc DB " + Integer.toString(albums.size()));
            albumsView.getAdapter().notifyDataSetChanged();

        }
    }
}
