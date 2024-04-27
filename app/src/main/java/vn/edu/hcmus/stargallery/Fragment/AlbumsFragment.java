package vn.edu.hcmus.stargallery.Fragment;

import static android.os.Environment.MEDIA_MOUNTED;

import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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
    static int PERMISSION_REQUEST_CODE = 100;
    HashMap<String, ArrayList<String>> albums;
    AlbumsViewAdapter adapter;
    GridLayoutManager manager;
//    AppCompatImageButton addAlbumBtn;
    ImageButton addAlbumBtn;
    boolean shouldExecuteOnResume = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        shouldExecuteOnResume = false;
        super.onCreate(savedInstanceState);

        albums = new HashMap<>();
        adapter = new AlbumsViewAdapter(getContext(), albums);
        manager = new GridLayoutManager(getContext(), 2);
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_albums, container, false);
        albumsView = layout.findViewById(R.id.albums_view);
        albumsView.setAdapter(adapter);
        albumsView.setLayoutManager(manager);
        addAlbumBtn = layout.findViewById(R.id.add_album_btn);

        addAlbumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder confirmDialog = new AlertDialog.Builder(requireContext());
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.create_alum_text, null);
                final EditText input = dialogView.findViewById(R.id.album_name_input);
                confirmDialog.setView(dialogView);
                confirmDialog.setTitle("Add album");
                confirmDialog.setMessage("Enter album's name");
                confirmDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String abName = input.getText().toString().trim();
                        if (!abName.isEmpty()) {
                            createAlbum(abName);
//                            Log.d("TEN ALBUM", input.getText().toString());
                        } else {
                            Log.d("KO PHAI TEN ALBUM", "___");
                        }
                    }
                });
                confirmDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
                        dialogInterface.dismiss();
                    }
                });
                confirmDialog.show();
            }
        });
        loadAlbums();
        return layout;
    }

    private void loadAlbums() {
        DatabaseHelper dbHelper = new DatabaseHelper((Application) requireActivity().getApplicationContext());

        ArrayList<String> imagesList = dbHelper.getFavoriteImages();
        albums.put("Favorites", imagesList);

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
                    imagesList = new ArrayList<>();
                    imagesList.add(imagePath);
                    albums.put(albumName, imagesList);
                } else {
                    imagesList = albums.get(albumName);
                    imagesList.add(imagePath);
                }
            }
        }

        ArrayList<String> albumNames = dbHelper.getAlbumNames();
        for (int i=0; i<albumNames.size(); i++) {
            imagesList = dbHelper.getAlbumImages(albumNames.get(i));
            albums.put(albumNames.get(i), imagesList);
        }

        albumsView.getAdapter().notifyDataSetChanged();
    }

    private void createAlbum(String albName) {
        DatabaseHelper dbHelper = new DatabaseHelper((Application) requireActivity().getApplicationContext());
        if (dbHelper.albumExists(albName)) {
            Log.d("DB", "Duplicated!");
        } else {
            Log.d("DB", "Lam gi do di");
//            dbHelper.createAlbum(albName);
            Log.d("DB", "Created! -> Notify");
        }
    }
}
