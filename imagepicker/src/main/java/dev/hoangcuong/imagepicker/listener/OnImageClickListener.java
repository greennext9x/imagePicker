package dev.hoangcuong.imagepicker.listener;

import android.view.View;


public interface OnImageClickListener {
    boolean onImageClick(View view, int position, boolean isSelected);
}
