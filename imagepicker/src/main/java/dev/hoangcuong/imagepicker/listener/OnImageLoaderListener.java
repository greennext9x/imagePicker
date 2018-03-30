package dev.hoangcuong.imagepicker.listener;

import dev.hoangcuong.imagepicker.model.Folder;
import dev.hoangcuong.imagepicker.model.Image;
import java.util.List;



public interface OnImageLoaderListener {
    void onImageLoaded(List<Image> images, List<Folder> folders);

    void onFailed(Throwable throwable);
}
