package dev.hoangcuong.imagepicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import dev.hoangcuong.imagepicker.R;
import dev.hoangcuong.imagepicker.adapter.FolderPickerAdapter.FolderViewHolder;
import dev.hoangcuong.imagepicker.listener.OnFolderClickListener;
import dev.hoangcuong.imagepicker.model.Folder;
import dev.hoangcuong.imagepicker.ui.common.BaseRecyclerViewAdapter;
import dev.hoangcuong.imagepicker.ui.imagepicker.ImageLoader;
import java.util.ArrayList;
import java.util.List;


public class FolderPickerAdapter extends BaseRecyclerViewAdapter<FolderViewHolder> {

    private List<Folder> folders = new ArrayList<>();
    private OnFolderClickListener itemClickListener;

    public FolderPickerAdapter(Context context, ImageLoader imageLoader, OnFolderClickListener itemClickListener) {
        super(context, imageLoader);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public FolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = getInflater().inflate(R.layout.imagepicker_item_folder, parent, false);
        return new FolderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FolderViewHolder holder, int position) {

        final Folder folder = folders.get(position);

        getImageLoader().loadImage(folder.getImages().get(0).getPath(), holder.image);

        holder.name.setText(folder.getFolderName());

        final int count = folder.getImages().size();
        holder.count.setText(String.valueOf(count));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onFolderClick(folder);
            }
        });

    }

    public void setData(List<Folder> folders) {
        if (folders != null) {
            this.folders.clear();
            this.folders.addAll(folders);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return folders.size();
    }

    static class FolderViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView name;
        private TextView count;

        private FolderViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_folder_thumbnail);
            name = itemView.findViewById(R.id.text_folder_name);
            count = itemView.findViewById(R.id.text_photo_count);
        }
    }

}
