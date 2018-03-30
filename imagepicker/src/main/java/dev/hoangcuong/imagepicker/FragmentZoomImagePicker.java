package dev.hoangcuong.imagepicker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



public class FragmentZoomImagePicker extends DialogFragment {
    private String TAG = FragmentZoomImagePicker.class.getSimpleName();
    private ZoomImageView imageView;
    private String path;
    private int selectedPosition = 0;
    private Button btnClose;

    public static FragmentZoomImagePicker newInstance() {
        FragmentZoomImagePicker f = new FragmentZoomImagePicker();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_fullscreen_preview, container, false);
        imageView = (ZoomImageView) v.findViewById(R.id.image_preview);
        btnClose = (Button)v.findViewById(R.id.zoomImageBtnClose);
        try {
            selectedPosition = getArguments().getInt("position");
            path = getArguments().getString("path");
            Log.d("PathImage", path);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Bitmap resized = Bitmap
            .createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 0.6), (int) (bitmap.getHeight() * 0.6), true);
        imageView.setImageBitmap(resized);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentZoomImagePicker.this.dismiss();
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }
}