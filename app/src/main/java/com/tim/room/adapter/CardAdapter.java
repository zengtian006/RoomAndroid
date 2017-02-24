package com.tim.room.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tim.room.R;
import com.tim.room.helper.CardAdapterHelper;
import com.tim.room.model.Items;

import java.util.ArrayList;
import java.util.List;

import jameson.io.library.util.ToastUtils;

import static com.tim.room.MainActivity.session;
import static com.tim.room.app.AppConfig.IMG_BASE_URL;

/**
 * Created by jameson on 8/30/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    private List<Items> mItems = new ArrayList<>();
    private CardAdapterHelper mCardAdapterHelper = new CardAdapterHelper();
    private OnFeedItemClickListener onFeedItemClickListener;
    Context mContext;

    public CardAdapter(Context mContext, List<Items> mItems) {
        this.mContext = mContext;
        this.mItems = mItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_feed, parent, false);
        mCardAdapterHelper.onCreateViewHolder(parent, itemView);
        ViewHolder cellFeedViewHolder = new ViewHolder(itemView);
        setupClickableViews(itemView, cellFeedViewHolder);
        return cellFeedViewHolder;
    }

    public void setOnFeedItemClickListener(OnFeedItemClickListener onFeedItemClickListener) {
        this.onFeedItemClickListener = onFeedItemClickListener;
    }

    private void setupClickableViews(final View itemView, final ViewHolder cellFeedViewHolder) {
        cellFeedViewHolder.btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFeedItemClickListener.onCommentsClick(itemView, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onMoreClick(itemView, cellFeedViewHolder.getAdapterPosition());
            }
        });
        cellFeedViewHolder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        cellFeedViewHolder.ivUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onProfileClick(itemView);
            }
        });

        cellFeedViewHolder.stPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Boolean isCheck = cellFeedViewHolder.stPublic.isChecked();
                String message = "";
                if (isCheck) {
                    message = "Do you want to set this item as public?";
                } else {
                    message = "Do you want to set this item as private?";
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("Room");
                builder.setMessage(message);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cellFeedViewHolder.stPublic.setChecked(!isCheck);
                    }
                });
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onFeedItemClickListener.onPublicClick(itemView, cellFeedViewHolder.getAdapterPosition(), cellFeedViewHolder.stPublic.isChecked());
                    }
                });
                builder.show();
            }
        });

//        cellFeedViewHolder.stPublic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                onFeedItemClickListener.onPublicClick(itemView, cellFeedViewHolder.getAdapterPosition(), b);
//
//            }
//        });
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        Items item = mItems.get(position);
        Glide.with(mContext).load(IMG_BASE_URL + session.getUser().getId().toString() + "/" + item.getImageName())
                .thumbnail(0.5f)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.imageView);
        if (item.getGlobal().equals("1")) {//public item
            holder.stPublic.setChecked(true);
        } else {
            holder.stPublic.setChecked(false);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.show(holder.imageView.getContext(), "" + position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ImageButton btnComments;
        ImageButton btnLike;
        ImageButton btnMore;
        ImageView ivUserProfile;
        Switch stPublic;

        public ViewHolder(final View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            btnComments = (ImageButton) itemView.findViewById(R.id.btnComments);
            btnLike = (ImageButton) itemView.findViewById(R.id.btnLike);
            btnMore = (ImageButton) itemView.findViewById(R.id.btnMore);
            ivUserProfile = (ImageView) itemView.findViewById(R.id.ivUserProfile);
            stPublic = (Switch) itemView.findViewById(R.id.switcher_public);
        }

    }

    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);

        void onPublicClick(View v, int position, boolean b);

        void onProfileClick(View v);
    }

}
