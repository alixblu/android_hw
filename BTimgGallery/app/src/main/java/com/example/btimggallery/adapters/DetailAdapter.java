package com.example.btimggallery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.example.btimggallery.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {
    private Context context;
    private List<String> imagePaths;

    public DetailAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);

        // Load ảnh với Glide
        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.placeholder_image) // Ảnh chờ
                .error(R.drawable.error_image)             // Ảnh lỗi
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.photoView);
        }
    }
}
