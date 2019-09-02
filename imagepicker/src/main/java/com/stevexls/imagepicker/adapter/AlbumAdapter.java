package com.stevexls.imagepicker.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.bean.Album;
import com.stevexls.imagepicker.bean.SelectionSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * Time：2019/2/13 15:12
 * Description:相册适配器
 */
public class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;
    private List<Album> mDatas;
    private OnItemClickListener mOnItemClickListener;   // 相册选择事件监听
    private int lastSelected = 0;     // 最后一次选中的item position

    public AlbumAdapter(Context context, List<Album> data) {
        mContext = context;
        mDatas = data;
        if (mDatas == null) {
            mDatas = new ArrayList<>();
        }
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_list, viewGroup, false);
        return new AlbumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int position) {
        final AlbumViewHolder albumViewHolder = (AlbumViewHolder) viewHolder;
        albumViewHolder.ivAlbumSelected.setVisibility(lastSelected == position ? View.VISIBLE : View.INVISIBLE);    // item是否选中
        Album album = mDatas.get(position);
        albumViewHolder.tvAlbumName.setText(album.getDisplayName());
        albumViewHolder.tvImageCount.setText(String.valueOf(album.getCount()));
        SelectionSpec.getInstance().imageEngine.loadThumbnail(mContext, mContext.getResources().getDimensionPixelSize(R
                .dimen.album_item_height), albumViewHolder.ivCover, album.getCoverPath());

        albumViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, albumViewHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    private static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;    // 封面
        TextView tvAlbumName; // 相册名称
        TextView tvImageCount;// 图片总数
        ImageView ivAlbumSelected; // 选中图标

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvAlbumName = itemView.findViewById(R.id.tv_album_name);
            tvImageCount = itemView.findViewById(R.id.tv_image_count);
            ivAlbumSelected = itemView.findViewById(R.id.iv_album_selected);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    // 相册item点击监听
    public interface OnItemClickListener{
        void onItemClick(View itemView, int position);
    }

    // 设置选中项
    public void setSelectIndex(int i) {
        if (lastSelected == i) {
            return;
        }
        lastSelected = i;
    }

    public int getSelectIndex() {
        return lastSelected;
    }
}
