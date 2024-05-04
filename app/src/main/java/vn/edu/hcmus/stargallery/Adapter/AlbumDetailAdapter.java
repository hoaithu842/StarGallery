package vn.edu.hcmus.stargallery.Adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import vn.edu.hcmus.stargallery.Activity.AlbumDetailActivity;
import vn.edu.hcmus.stargallery.R;

public class AlbumDetailAdapter extends RecyclerView.Adapter<AlbumDetailAdapter.ViewHolder>{
    private Context context;
    private ArrayList<String> images_list;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;


    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_image,null,true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumDetailAdapter.ViewHolder holder, int position) {
        File image_file=new File(images_list.get(position));
        if(image_file.exists()){
            Glide.with(context).load(image_file).into(holder.image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onClick(position);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
//                Toast.makeText(context, "LONGGGGGGGGGG", Toast.LENGTH_SHORT).show();
                if (onLongClickListener != null) {
                    onLongClickListener.onLongClick(position);
                }
                return true;
            }
        });
    }

    public void setOnClickListener(AlbumDetailAdapter.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
    public interface OnClickListener {
        void onClick(int position);
    }
    public interface OnLongClickListener {
        void onLongClick(int position);
    }
    @Override
    public int getItemCount() {
        return images_list.size();
    }

    public AlbumDetailAdapter(Context context, ArrayList<String> images_list) {
        this.context = context;

        this.images_list = images_list;

    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
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
