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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import vn.edu.hcmus.stargallery.Adapter.ImagesViewAdapter;
import vn.edu.hcmus.stargallery.Activity.ImageDetailActivity;
import vn.edu.hcmus.stargallery.R;

public class ImagesFragment extends Fragment {
    RecyclerView imagesView;
    LinearLayout layout;
    static int PERMISSION_REQUEST_CODE=100;
    ArrayList<String> images;
    ImagesViewAdapter adapter;
    GridLayoutManager manager;
    boolean shouldExecuteOnResume = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        shouldExecuteOnResume = false;
        super.onCreate(savedInstanceState);

        images=new ArrayList<>();
        adapter=new ImagesViewAdapter(getContext(),images);
        manager=new GridLayoutManager(getContext(),4);
        adapter.setOnClickListener(new ImagesViewAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getActivity(), ImageDetailActivity.class);
                intent.putExtra("image_path", images.get(position));
                intent.putStringArrayListExtra("images_list", images);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        boolean SDCard= Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
        if(SDCard){
            final String[] colums={MediaStore.Images.Media.DATA,MediaStore.Images.Media._ID};
            final String order=MediaStore.Images.Media.DATE_TAKEN+" DESC";
            Cursor cursor=getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,colums,null,null,order);
            int count =cursor.getCount();
            for(int i=0;i<count;i++){
                cursor.moveToPosition(i);
                int colunmindex=cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                if (!images.contains(cursor.getString(colunmindex))) {
                    images.add(0, cursor.getString(colunmindex));
                } else {
                    imagesView.getAdapter().notifyDataSetChanged();
                    return;
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_images, container, false);
        imagesView = layout.findViewById(R.id.images_view);
        imagesView.setAdapter(adapter);
        imagesView.setLayoutManager(manager);
        loadImages();
        return layout;
    }

    private void loadImages() {
        boolean SDCard= Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
        if(SDCard){
            final String[] colums={MediaStore.Images.Media.DATA,MediaStore.Images.Media._ID};
            final String order=MediaStore.Images.Media.DATE_TAKEN+" DESC";
            Cursor cursor=getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,colums,null,null,order);
            int count =cursor.getCount();
            for(int i=0;i<count;i++){
                cursor.moveToPosition(i);
                int colunmindex=cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                images.add(cursor.getString(colunmindex));
            }
            imagesView.getAdapter().notifyDataSetChanged();
        }
    }
}