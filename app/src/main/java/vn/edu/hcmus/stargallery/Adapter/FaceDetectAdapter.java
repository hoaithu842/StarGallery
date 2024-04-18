package vn.edu.hcmus.stargallery.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import vn.edu.hcmus.stargallery.R;

public class FaceDetectAdapter extends RecyclerView.Adapter<FaceDetectAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Bitmap> images_list;
    private FaceDetectAdapter.OnClickListener onClickListener;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_image,null,true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .asBitmap()
                .load(images_list.get(position))
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return images_list.size();
    }

    public void setOnClickListener(FaceDetectAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    public FaceDetectAdapter(Context context, ArrayList<Bitmap> images_list) {
        this.context = context;
        this.images_list = images_list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.item_image);
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//            int totalHorizontalSpacing = (int) (10* (ColNum - 1)); // Calculate total horizontal spacing
            int screenWidth = displayMetrics.widthPixels ; //- totalHorizontalSpacing; // Subtract total spacing from screen width
            int size =  (int)(screenWidth / 4); // Divide by number of columns
            image.requestLayout();
            image.getLayoutParams().height = size;
            image.getLayoutParams().width = size;
        }
    }
}
