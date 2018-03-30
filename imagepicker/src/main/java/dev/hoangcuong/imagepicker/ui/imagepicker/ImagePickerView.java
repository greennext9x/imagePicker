package dev.hoangcuong.imagepicker.ui.imagepicker;


import dev.hoangcuong.imagepicker.model.Folder;
import dev.hoangcuong.imagepicker.model.Image;
import dev.hoangcuong.imagepicker.ui.common.MvpView;
import java.util.List;


public interface ImagePickerView extends MvpView {

    void showLoading(boolean isLoading);

    void showFetchCompleted(List<Image> images, List<Folder> folders);

    void showError(Throwable throwable);

    void showEmpty();

    void showCapturedImage(List<Image> images);

    void finishPickImages(List<Image> images);

}