package dev.hoangcuong.imagepicker.ui.camera;


import dev.hoangcuong.imagepicker.model.Image;
import dev.hoangcuong.imagepicker.ui.common.MvpView;
import java.util.List;


public interface CameraView extends MvpView {

    void finishPickImages(List<Image> images);
}
