package vn.edu.hcmus.stargallery.Adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import vn.edu.hcmus.stargallery.R;

public class MultiSelectImagesViewAdapter extends RecyclerView.Adapter<MultiSelectImagesViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<String> images_list;
    private OnClickListener onClickListener;
    private Set<Integer> selectedPositions = new HashSet<>();
    private OnLongClickListener onLongClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.multiselect_item_image,null,true);
        Log.d("ViewType", Integer.toString(viewType));

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
                    holder.setClicked();
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

    public void setOnClickListener(OnClickListener onClickListener) {
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

    public MultiSelectImagesViewAdapter(Context context, ArrayList<String> images_list) {
        this.context = context;
        this.images_list = images_list;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private ImageView image_checked;
        private ImageView image_filtered;
        private boolean clicked;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clicked = false;
            image = itemView.findViewById(R.id.multi_images_view);
            image_filtered = itemView.findViewById(R.id.selected_img_1);
            image_checked = itemView.findViewById(R.id.selected_img_2);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image_filtered.setVisibility(View.VISIBLE);
                    image_checked.setVisibility(View.VISIBLE);
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onClickListener != null) {
                        onClickListener.onClick(position);
                    }
                }
            });
            image_filtered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image_filtered.setVisibility(View.GONE);
                    image_checked.setVisibility(View.GONE);
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onClickListener != null) {
                        onClickListener.onClick(position);
                    }
                }
            });
            image_checked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    image_filtered.setVisibility(View.GONE);
                    image_checked.setVisibility(View.GONE);
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onClickListener != null) {
                        onClickListener.onClick(position);
                    }
                }
            });
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//            int totalHorizontalSpacing = (int) (10* (ColNum - 1)); // Calculate total horizontal spacing
            int screenWidth = displayMetrics.widthPixels ; //- totalHorizontalSpacing; // Subtract total spacing from screen width
            int size =  (int)(screenWidth / 4); // Divide by number of columns
            image.requestLayout();
            image.getLayoutParams().height = size;
            image.getLayoutParams().width = size;

            image_filtered.requestLayout();
            image_filtered.getLayoutParams().height = size;
            image_filtered.getLayoutParams().width = size;
        }

        public void setClicked() {
            clicked = !clicked;
        }
        public boolean isClicked() {
            return clicked;
        }
    }
}
