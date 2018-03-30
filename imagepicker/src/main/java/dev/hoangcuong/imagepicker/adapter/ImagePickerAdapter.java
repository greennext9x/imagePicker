package dev.hoangcuong.imagepicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import dev.hoangcuong.imagepicker.R;
import dev.hoangcuong.imagepicker.SendImage;
import dev.hoangcuong.imagepicker.adapter.ImagePickerAdapter.ImageViewHolder;
import dev.hoangcuong.imagepicker.helper.ImageHelper;
import dev.hoangcuong.imagepicker.listener.OnImageClickListener;
import dev.hoangcuong.imagepicker.listener.OnImageSelectionListener;
import dev.hoangcuong.imagepicker.model.Image;
import dev.hoangcuong.imagepicker.ui.common.BaseRecyclerViewAdapter;
import dev.hoangcuong.imagepicker.ui.imagepicker.ImageLoader;
import java.util.ArrayList;
import java.util.List;


public class ImagePickerAdapter extends BaseRecyclerViewAdapter<ImageViewHolder> {

    private List<Image> images = new ArrayList<>();
    private List<Image> selectedImages = new ArrayList<>();
    private OnImageClickListener itemClickListener;
    private OnImageSelectionListener imageSelectionListener;
    private SendImage sendImage;

    public ImagePickerAdapter(Context context, ImageLoader imageLoader, List<Image> selectedImages, OnImageClickListener itemClickListener) {
        super(context, imageLoader);
        this.itemClickListener = itemClickListener;

        if (selectedImages != null && !selectedImages.isEmpty()) {
            this.selectedImages.addAll(selectedImages);
        }
        sendImage = (SendImage)context;
    }
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = getInflater().inflate(R.layout.imagepicker_item_image, parent, false);
        return new ImageViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder viewHolder, final int position) {
        final Image image = images.get(position);
        final boolean isSelected = isSelected(image);

        getImageLoader().loadImage(image.getPath(), viewHolder.image);

        viewHolder.gifIndicator.setVisibility(ImageHelper.isGifFormat(image) ? View.VISIBLE : View.GONE);
        viewHolder.alphaView.setAlpha(isSelected ? 0.5f : 0.0f);
        viewHolder.container.setForeground(isSelected
            ? ContextCompat.getDrawable(getContext(), R.drawable.imagepicker_ic_selected)
            : null);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean shouldSelect = itemClickListener.onImageClick(view, viewHolder.getAdapterPosition(), !isSelected);
                if (isSelected) {
                    removeSelected(image, position);
                } else if (shouldSelect) {
                    addSelected(image, position);
                }
            }
        });
        viewHolder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sendImage != null){
                    sendImage.onSendImage(image.getPath());
                    Log.d("PathImage",image.getPath());
                }
            }
        });
    }

    private boolean isSelected(Image image) {
        for (Image selectedImage : selectedImages) {
            if (selectedImage.getPath().equals(image.getPath())) {
                return true;
            }
        }
        return false;
    }

    public void setOnImageSelectionListener(OnImageSelectionListener imageSelectedListener) {
        this.imageSelectionListener = imageSelectedListener;
    }

    @Override
    public int getItemCount() {
        return images.size();
    }


    public void setData(List<Image> images) {
        if (images != null) {
            this.images.clear();
            this.images.addAll(images);
        }
        notifyDataSetChanged();
    }

    public void addSelected(List<Image> images) {
        selectedImages.addAll(images);
        notifySelectionChanged();
    }

    private void addSelected(Image image, int position) {
        selectedImages.add(image);
        notifyItemChanged(position);
        notifySelectionChanged();
    }

    private void removeSelected(Image image, int position) {
        selectedImages.remove(image);
        notifyItemChanged(position);
        notifySelectionChanged();
    }

    public void removeAllSelected() {
        selectedImages.clear();
        notifyDataSetChanged();
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        if (imageSelectionListener != null) {
            imageSelectionListener.onSelectionUpdate(selectedImages);
        }
    }

    public List<Image> getSelectedImages() {
        return selectedImages;
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        private FrameLayout container;
        private ImageView image;
        private View alphaView;
        private View gifIndicator;
        private Button btnView;

        private ImageViewHolder(View itemView) {
            super(itemView);
            container = (FrameLayout) itemView;
            image = itemView.findViewById(R.id.image_thumbnail);
            alphaView = itemView.findViewById(R.id.view_alpha);
            gifIndicator = itemView.findViewById(R.id.gif_indicator);
            btnView = itemView.findViewById(R.id.btnZoom);
        }

    }

}
