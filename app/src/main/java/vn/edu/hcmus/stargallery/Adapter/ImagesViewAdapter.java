package vn.edu.hcmus.stargallery.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import vn.edu.hcmus.stargallery.R;

public class ImagesViewAdapter extends RecyclerView.Adapter<ImagesViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> images_list;
    private OnClickListener onClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_image,null,true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
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
                Toast.makeText(context, "LONGGGGGGGGGG", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    @Override
    public int getItemCount() {
        return images_list.size();
    }

    public ImagesViewAdapter(Context context, ArrayList<String> images_list) {
        this.context = context;
        this.images_list = images_list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image=itemView.findViewById(R.id.item_image);
        }
    }
}
