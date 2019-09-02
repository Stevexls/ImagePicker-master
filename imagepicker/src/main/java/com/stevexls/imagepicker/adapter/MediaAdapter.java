package com.stevexls.imagepicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.bean.CustomCause;
import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.bean.SelectionSpec;
import com.stevexls.imagepicker.collection.SelectedItemCollection;
import com.stevexls.imagepicker.utils.DateUtils;
import com.stevexls.imagepicker.utils.ScreenUtils;
import com.stevexls.imagepicker.widget.CheckView;
import com.stevexls.imagepicker.widget.MarkImageView;

import java.util.List;

/**
 * <p>
 * 为了避免调用notifyDataSetChanged方法时造成item闪烁，调用setHasStableIds(true)方法以及重写getItemId来解决闪烁问题
 * 注意，使用了这种方法后，改变其他自定义view的值时，需要手动调用invalidate()方法，否则方法无效
 * </p>
 * Time：2019/4/26 17:04
 * Description: media item
 * Author:592172833@qq.com
 */
public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_CAPTURE = 0x01;
    private static final int VIEW_TYPE_MEDIA = 0x02;

    private SelectedItemCollection mSelectedCollection;
    private SelectionSpec mSelectionSpec;
    private OnMediaGridListener mOnMediaGridListener;
    private OnPhotoCaptureListener mOnPhotoCaptureListener;

    private int mImageResize = 0;
    private RecyclerView recyclerView;

    private List<Item> mDatas;
    private Context mContext;

    public MediaAdapter(Context context, List<Item> mDatas, SelectedItemCollection selectedCollection, RecyclerView recyclerView) {
        this.mContext = context;
        this.mDatas = mDatas;
        this.mSelectedCollection = selectedCollection;
        this.recyclerView = recyclerView;
        mSelectionSpec = SelectionSpec.getInstance();
        setHasStableIds(true);  // 配合重写getItemId解决图片闪烁问题
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_CAPTURE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_capture, viewGroup, false);
            return new CaptureViewHolder(view);
        } else if (viewType == VIEW_TYPE_MEDIA) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_media, viewGroup, false);
            return new MediaViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CaptureViewHolder) {
            CaptureViewHolder captureViewHolder = (CaptureViewHolder) holder;
            bindCapture(captureViewHolder, position, mDatas.get(position));
        } else if (holder instanceof MediaViewHolder) {
            MediaViewHolder mediaViewHolder = (MediaViewHolder) holder;
            bindMediaItem(mediaViewHolder, position, mDatas.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).isCapture() ? VIEW_TYPE_CAPTURE : VIEW_TYPE_MEDIA;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    // 配合设置setHasStableIds(true)解决图片闪烁问题
    @Override
    public long getItemId(int position) {
        if (mDatas == null || mDatas.isEmpty()) {
            return super.getItemId(position);
        }
        return mDatas.get(position).getId();
    }

    // 处理拍摄item数据
    private void bindCapture(CaptureViewHolder captureViewHolder, int position, Item item) {
        captureViewHolder.captureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPhotoCaptureListener != null) {
                    mOnPhotoCaptureListener.capture();
                }
            }
        });
    }

    // 处理图片item数据
    private void bindMediaItem(MediaViewHolder holder, final int position, final Item item) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnMediaGridListener != null) {
                    mOnMediaGridListener.onThumbnailClicked(item, position);
                }
            }
        });
        if (item.isGif()) {
            SelectionSpec.getInstance().imageEngine.loadGifThumbnail(mContext, (int) (getImageResize() * mSelectionSpec.thumbnailScale),
                    holder.ivThumb, item.uri);
        } else {
            SelectionSpec.getInstance().imageEngine.loadThumbnail(mContext, (int) (getImageResize() * mSelectionSpec.thumbnailScale),
                    holder.ivThumb, item.uri);
        }
        boolean isVideo = item.isVideo();
        holder.ivVideo.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        holder.tvDuration.setVisibility(isVideo ? View.VISIBLE : View.GONE);
        holder.tvDuration.setText(isVideo ? DateUtils.toStrDuration(item.getDuration()) : mContext.getString(R.string.init_duration));
        holder.ivGif.setVisibility(item.isGif() ? View.VISIBLE : View.GONE);
        holder.ivThumb.setItemSelected(mSelectedCollection.isSelected(item));
        bindCheckView(holder, position, item);
    }

    private void bindCheckView(final MediaViewHolder holder, final int position, final Item item) {
        if (mSelectionSpec.multiMode) {
            holder.checkView.reset();
            holder.checkView.setVisibility(View.VISIBLE);
            if (mSelectionSpec.showSelected
                    && mSelectedCollection.isSelected(item)) {
                // 如果要求标记出已选中的图片
                if (mSelectionSpec.countable) {
                    holder.checkView.setCountable(true);
                    holder.checkView.setCountNum(mSelectedCollection.checkedNumOf(item));
                } else {
                    holder.checkView.setChecked(true);
                }
            }
        } else {
            holder.checkView.setVisibility(View.GONE);
        }

        holder.checkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkViewClicked(holder, position, item);
                if (mOnMediaGridListener != null) {
                    mOnMediaGridListener.onCheckViewClicked(holder.checkView, item, position);
                }
            }
        });
    }

    private void checkViewClicked(MediaViewHolder holder, int position, Item item) {
        if (mSelectedCollection.isSelected(item)) {
            mSelectedCollection.remove(item);
        } else {    // 如果之前是未选中状态
            if (checkAcceptable(item)) {
                mSelectedCollection.add(item);
                if (mSelectionSpec.countable) {
                    holder.checkView.setCountable(true);
                    holder.checkView.setCountNum(mSelectedCollection.checkedNumOf(item));
                } else {
                    holder.checkView.setChecked(true);
                }
            } else {
                return;
            }
        }
        notifyDataSetChanged();
    }

    // 检测是否可以添加
    private boolean checkAcceptable(Item item) {
        CustomCause customCause = mSelectedCollection.isAcceptable(item);
        if (customCause != null) {
            customCause.showCause(mContext);
            return false;
        }
        return true;
    }

    private int getImageResize() {
        if (mImageResize == 0) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
            int screenWidth = ScreenUtils.getScreenWidth(mContext); // 屏幕宽度
            int availableWidth = screenWidth
                    - recyclerView.getPaddingRight()
                    - recyclerView.getPaddingLeft()
                    - (mContext.getResources().getDimensionPixelSize(R.dimen.media_grid_spacing) * (spanCount - 1)); // 获取除间隔外的可用宽度
            mImageResize = availableWidth / spanCount;  // 获取每个item的宽度
        }
        return mImageResize;
    }

    // 点击图片回调
    public interface OnMediaGridListener {
        void onThumbnailClicked(Item item, int itemPosition);

        void onCheckViewClicked(CheckView checkView, Item item, int position);
    }

    public interface OnPhotoCaptureListener {
        void capture();
    }

    public void setOnMediaGridListener(OnMediaGridListener listener) {
        this.mOnMediaGridListener = listener;
    }

    public void setOnPhotoCapture(OnPhotoCaptureListener listener) {
        this.mOnPhotoCaptureListener = listener;
    }

    private static class CaptureViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout captureView;

        public CaptureViewHolder(@NonNull View itemView) {
            super(itemView);
            captureView = itemView.findViewById(R.id.rl_capture);
        }
    }

    private static class MediaViewHolder extends RecyclerView.ViewHolder {

        CheckView checkView;
        MarkImageView ivThumb;  // 缩略图
        ImageView ivGif;  // gif
        ImageView ivVideo; // video
        TextView tvDuration;    // video的时长

        public MediaViewHolder(@NonNull View itemView) {
            super(itemView);
            checkView = itemView.findViewById(R.id.check_view);
            ivThumb = itemView.findViewById(R.id.iv_thumb);
            ivGif = itemView.findViewById(R.id.iv_gif);
            ivVideo = itemView.findViewById(R.id.iv_video);
            tvDuration = itemView.findViewById(R.id.tv_duration);
        }
    }
}
