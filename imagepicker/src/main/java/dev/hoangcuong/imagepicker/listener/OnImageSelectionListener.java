package dev.hoangcuong.imagepicker.listener;


import dev.hoangcuong.imagepicker.model.Image;
import java.util.List;



public interface OnImageSelectionListener {
    void onSelectionUpdate(List<Image> images);
}
