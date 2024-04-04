package vn.edu.hcmus.stargallery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import vn.edu.hcmus.stargallery.R;

public class AlbumsViewAdapter extends RecyclerView.Adapter<AlbumsViewAdapter.ViewHolder>{
    private Context context;
    HashMap<String, ArrayList<String>> albums;
    ArrayList<String> albums_name;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_album,null,true);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        this.albums_name = new ArrayList<>(albums.keySet());
//        holder.album_name.setText(albums_name.get(position));

        File album_cover = new File(albums.get(albums_name.get(position)).get(0));
        if(album_cover.exists()){
            Glide.with(context).load(album_cover).into(holder.album_cover);
        }

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (onClickListener != null) {
//                    onClickListener.onClick(position);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
//        return 0;
        return albums_name.size();
    }

    public AlbumsViewAdapter(Context context, HashMap<String, ArrayList<String>> albums) {
        this.context = context;
        this.albums = albums;
//        this.albums_name = (ArrayList<String>) albums.keySet();
        this.albums_name = new ArrayList<>(albums.keySet());
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView album_cover;
//        private TextView album_name;
//        private TextView album_quant;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            album_cover = itemView.findViewById(R.id.album_cover);
//            album_name = itemView.findViewById(R.id.album_name);
//            album_quant = itemView.findViewById(R.id.album_quant);
        }
    }
}
