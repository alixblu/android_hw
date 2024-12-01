package com.example.btimggallery.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.btimggallery.DetailActivity;
import com.example.btimggallery.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private Context context;
    private List<String> imagePaths;

    // Constructor để nhận vào context và danh sách đường dẫn ảnh
    public ImageAdapter(Context context, List<String> imagePaths) {
        this.context = context;
        this.imagePaths = imagePaths;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_image
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String imagePath = imagePaths.get(position);

        // Dùng Glide để tải ảnh vào ImageView
        Glide.with(context)
                .load(imagePath)
                .placeholder(R.drawable.placeholder_image)  // Thêm ảnh placeholder
                .error(R.drawable.error_image)             // Thêm ảnh lỗi
                .into(holder.imageView);  // Tải ảnh vào ImageView

        // Thêm sự kiện click vào ảnh
        holder.itemView.setOnClickListener(v -> {
            // Khi click vào ảnh, chuyển sang DetailActivity và truyền dữ liệu ảnh
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putStringArrayListExtra("imagePaths", (ArrayList<String>) imagePaths); // Gửi danh sách ảnh
            intent.putExtra("position", position); // Gửi vị trí ảnh để hiển thị ảnh chi tiết
            context.startActivity(intent); // Mở DetailActivity
        });
    }

    @Override
    public int getItemCount() {
        return imagePaths.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Tìm ImageView trong mỗi item của RecyclerView
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
