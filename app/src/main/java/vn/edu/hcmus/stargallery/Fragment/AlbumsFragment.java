package vn.edu.hcmus.stargallery.Fragment;

import static android.os.Environment.MEDIA_MOUNTED;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import vn.edu.hcmus.stargallery.Adapter.AlbumsViewAdapter;
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
//        adapter.setOnClickListener(new ImagesViewAdapter.OnClickListener() {
//            @Override
//            public void onClick(int position) {
//                Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
//                intent.putExtra("image_path", images.get(position));
//                intent.putStringArrayListExtra("images_list", images);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public void onResume() {
        super.onResume();
        albums.clear();
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
//                cursor.close();
//            }
            albumsView.getAdapter().notifyDataSetChanged();
        }
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
//                cursor.close();
//            }
            albumsView.getAdapter().notifyDataSetChanged();
        }
    }
}
