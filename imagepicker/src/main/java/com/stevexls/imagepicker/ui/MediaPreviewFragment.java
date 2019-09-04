package com.stevexls.imagepicker.ui;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.bean.SelectionSpec;
import com.stevexls.photoview.PhotoView;

/**
 * Time：2019/6/27 16:49
 * Description:预览界面
 */
public class MediaPreviewFragment extends Fragment {
    private static final String ARGS_ITEM = "args_item";

    private OnViewClickListener onViewClickListener;
    private boolean isInited = false;
    private PhotoView photoView;

    public static MediaPreviewFragment newInstance(Item item) {
        MediaPreviewFragment fragment = new MediaPreviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Item item = getArguments().getParcelable(ARGS_ITEM);
        if (item == null) {
            return;
        }

        photoView = view.findViewById(R.id.photo_view);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onViewClickListener != null) {
                    onViewClickListener.onViewClick();
                }
            }
        });

        if (item.isGif()) {
            SelectionSpec.getInstance().imageEngine.loadGifImage(getContext(), item.width, item.height, photoView, item.uri);
        } else if (item.isVideo()) {
            // 部分手机拍摄的视频获取宽高为0
            if (item.width <= 0 || item.height <= 0) {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(item.path);
                String width = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String height = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                SelectionSpec.getInstance().imageEngine.loadImage(getContext(), Integer.valueOf(width), Integer.valueOf(height), photoView, item.uri);
                mmr.release();
                mmr = null;
            } else {
                SelectionSpec.getInstance().imageEngine.loadImage(getContext(), item.width, item.height, photoView, item.uri);
            }
        } else {
            photoView.setZoomEnabled(true);
            SelectionSpec.getInstance().imageEngine.loadImage(getContext(), item.width, item.height, photoView, item.uri);
        }

        ImageView ivPlay = view.findViewById(R.id.iv_play);
        ivPlay.setVisibility(item.isVideo() ? View.VISIBLE : View.GONE);
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo(item);
            }
        });
        isInited = true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnViewClickListener) {
            onViewClickListener = (OnViewClickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onViewClickListener = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser && isInited) {
            if (photoView != null) {
                photoView.resetMatrix();
            }
        }
    }

    private void playVideo(Item item) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(item.uri, "video/*");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.error_no_video_activity, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.error_no_video_activity, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInited = false;
        photoView.setImageBitmap(null);
        photoView = null;
    }

    public interface OnViewClickListener {
        void onViewClick();
    }
}
