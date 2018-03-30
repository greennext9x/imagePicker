package dev.hoangcuong.imagepicker.ui.camera;


import dev.hoangcuong.imagepicker.model.Image;
import java.util.List;

public interface OnImageReadyListener {
    void onImageReady(List<Image> images);
}
