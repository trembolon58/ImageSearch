package ru.findprofy.imagesearch;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class ShowImageFragment extends Fragment implements ImageLoader.ImageCache {

    public static final String IMG_SRC_TAG = "img_src_tag";
    private Bitmap bitmap;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_show_image, container, false);
        Bundle args = getArguments();
        String url = args.getString(IMG_SRC_TAG);
        NetworkImageView imageView = (NetworkImageView) root.findViewById(R.id.network_image_view);
        imageView.setImageUrl(url, new ImageLoader(ServerApi.getInstance(getActivity()).getQueue(), this));
        progressBar = (ProgressBar) root.findViewById(R.id.progress);
        return root;
    }

    @Override
    public Bitmap getBitmap(String url) {
        return bitmap;
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        this.bitmap = bitmap;
        progressBar.setVisibility(View.GONE);
    }
}
