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

import java.util.ArrayList;

import vn.edu.hcmus.stargallery.R;

public class PersonAvatarAdapter extends RecyclerView.Adapter<PersonAvatarAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Bitmap> imagesList;
    private OnClickListener onClickListener;
    private OnLongClickListener onLongClickListener;

    public PersonAvatarAdapter(Context context, ArrayList<Bitmap> imagesList) {
        this.context = context;
        this.imagesList = imagesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_avatar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bitmap bitmap = imagesList.get(position);
        Glide.with(context).load(bitmap).into(holder.image);

        holder.itemView.setOnClickListener(view -> {
            if (onClickListener != null) {
                onClickListener.onClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (onLongClickListener != null) {
                onLongClickListener.onLongClick(position);
            }
            return true;
        });
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

    public interface OnClickListener {
        void onClick(int position);
    }

    public interface OnLongClickListener {
        void onLongClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);

            DisplayMetrics displayMetrics = itemView.getResources().getDisplayMetrics();
            int screenWidth = displayMetrics.widthPixels;
            int size = screenWidth / 4;
            image.getLayoutParams().height = size;
            image.getLayoutParams().width = size;
        }
    }
}
