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
import android.widget.Button;
import android.widget.LinearLayout;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import java.util.ArrayList;

import vn.edu.hcmus.stargallery.Activity.ImageDuplicateActivity;
import vn.edu.hcmus.stargallery.R;

public class UtilitiesFragment extends Fragment {
    private static final int REQUEST_DELETE_ITEM = 100;
    LinearLayout layout;
    static int PERMISSION_REQUEST_CODE = 100;
    ArrayList<String> images;

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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_utilities, container, false);

        Button button = layout.findViewById(R.id.show_duplicated);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ImageDuplicateActivity.class);
                intent.putExtra("album_name", "Recently Deleted");
                intent.putStringArrayListExtra("images_list", images);
                startActivity(intent);
            }
        });

        loadImages();
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
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_DELETE_ITEM && resultCode == Activity.RESULT_OK) {
//
//            Integer itemDeleted = data.getIntExtra("itemDeleted", 0);
//            if (itemDeleted >= 0 && itemDeleted < images.size()) {
//                String imagePath = images.get(itemDeleted);
//                images.remove(images.get(itemDeleted));
//                imagesView.getAdapter().notifyItemRemoved(itemDeleted);
//                // Handle item deletion
//                // Check if the image path exists before deletion (avoid potential errors)
//                if (new File(imagePath).exists()) {
//                    int deleted = getContext().getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                            MediaStore.Images.Media.DATA + " = ?", new String[]{imagePath});
//                    if (deleted == 1) {
//                        // Image deleted successfully
//                        Log.i("ImageDelete", "Image deleted: " + imagePath);
//                    } else {
//                        Log.w("ImageDelete", "Failed to delete image: " + imagePath);
//                    }
//                } else {
//                    Log.w("ImageDelete", "Image path not found: " + imagePath);
//                }
//            }
//        }
//    }
}