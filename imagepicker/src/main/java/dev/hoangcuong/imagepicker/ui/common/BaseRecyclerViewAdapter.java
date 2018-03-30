package dev.hoangcuong.imagepicker.ui.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import dev.hoangcuong.imagepicker.ui.imagepicker.ImageLoader;



public abstract class BaseRecyclerViewAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {
    private final Context context;
    private final LayoutInflater inflater;
    private final ImageLoader imageLoader;

    public BaseRecyclerViewAdapter(Context context, ImageLoader imageLoader) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.imageLoader = imageLoader;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public Context getContext() {
        return context;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }
}
