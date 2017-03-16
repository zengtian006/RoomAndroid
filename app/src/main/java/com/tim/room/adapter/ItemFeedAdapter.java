package com.tim.room.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tim.room.R;
import com.tim.room.helper.ItemFeedAdapterHelper;
import com.tim.room.model.ItemLikes;
import com.tim.room.model.Items;
import com.tim.room.rest.RESTFulService;
import com.tim.room.rest.RESTFulServiceImp;
import com.tim.room.view.TagContainerLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tim.room.MainActivity.session;
import static com.tim.room.app.AppConfig.IMG_BASE_URL;

/**
 * Created by jameson on 8/30/16.
 */
public class ItemFeedAdapter extends RecyclerView.Adapter<ItemFeedAdapter.ViewHolder> {
    public static final String ACTION_LIKE_BUTTON_CLICKED = "action_like_button_button";
    public static final String ACTION_LIKE_IMAGE_CLICKED = "action_like_image_button";

    private List<Items> mItems = new ArrayList<>();
    private ItemFeedAdapterHelper mCardAdapterHelper = new ItemFeedAdapterHelper();
    private OnFeedItemClickListener onFeedItemClickListener;
    Context mContext;

    public ItemFeedAdapter(Context mContext, List<Items> mItems) {
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
                RESTFulService updateItemLikesService = RESTFulServiceImp.createService(RESTFulService.class);

                final int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                final Items item = mItems.get(adapterPosition);
                ItemLikes newItemLike = new ItemLikes(item.getId(), session.getUser().getId());
                updateItemLikesService.updateItemLike(newItemLike)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) throws Exception {
                                if (aBoolean) {
                                    if (item.isLiked()) {
                                        item.setLikesCount(mItems.get(adapterPosition).getLikesCount() - 1);
                                        item.setLiked(false);
                                    } else {
                                        item.setLikesCount(mItems.get(adapterPosition).getLikesCount() + 1);
                                        item.setLiked(true);

                                    }
                                    notifyItemChanged(adapterPosition, ACTION_LIKE_BUTTON_CLICKED);
                                }
                            }
                        });

            }
        });
        cellFeedViewHolder.ivFeedCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int adapterPosition = cellFeedViewHolder.getAdapterPosition();
                Items item = mItems.get(adapterPosition);
                if (!item.isLiked()) {
                    item.setLikesCount(item.getLikesCount() + 1);
                    item.setLiked(true);
                    notifyItemChanged(adapterPosition, ACTION_LIKE_IMAGE_CLICKED);
                }
            }
        });
        cellFeedViewHolder.layout_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFeedItemClickListener.onProfileClick(itemView, cellFeedViewHolder.getAdapterPosition());
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
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        mCardAdapterHelper.onBindViewHolder(holder.itemView, position, getItemCount());
        Items item = mItems.get(position);

        ((ViewHolder) holder).bindView(item);

        Glide.with(mContext).load(IMG_BASE_URL + item.getUser().getId().toString() + "/" + item.getImageName())
                .thumbnail(0.5f)
                .fitCenter()
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ivFeedCenter);
        if (item.getUser().getId().equals(session.getUser().getId())) {//only set public to your own item
            holder.stPublic.setVisibility(View.VISIBLE);
            if (item.getGlobal().equals("1")) {//public item
                holder.stPublic.setChecked(true);
            } else {
                holder.stPublic.setChecked(false);
            }
        } else {
            holder.stPublic.setVisibility(View.GONE);
        }
        holder.profile_name.setText(item.getUser().getName());

        holder.itemTitle.setText(item.getTitle());
        holder.userName.setText(item.getUser().getName());
//        List<String> list = new ArrayList<>();
//        list.add("fashion");
//        list.add("man");
//        list.add("great");
//        Log.v("CCCC", "COUNT: " + item.getLikesCount());
        if (!item.getTags().isEmpty()) {
            holder.tagcontainerLayout.setTags(item.getTags());
        }
        holder.btnLike.setImageResource(item.isLiked() ? R.drawable.ic_heart_red : R.drawable.ic_heart_outline_grey);
        holder.tsLikesCounter.setCurrentText(holder.vImageRoot.getResources().getQuantityString(
                R.plurals.likes_count, item.getLikesCount(), item.getLikesCount()
        ));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFeedCenter;
        ImageButton btnComments;
        ImageButton btnLike;
        ImageButton btnMore;
        LinearLayout layout_profile;
        Switch stPublic;
        TextView itemTitle, userName, profile_name;
        TagContainerLayout tagcontainerLayout;
        TextSwitcher tsLikesCounter;
        FrameLayout vImageRoot;
        View vBgLike;
        ImageView ivLike;

        Items item;

        public ViewHolder(final View itemView) {
            super(itemView);
            ivFeedCenter = (ImageView) itemView.findViewById(R.id.ivFeedCenter);
            btnComments = (ImageButton) itemView.findViewById(R.id.btnComments);
            btnLike = (ImageButton) itemView.findViewById(R.id.btnLike);
            btnMore = (ImageButton) itemView.findViewById(R.id.btnMore);
            layout_profile = (LinearLayout) itemView.findViewById(R.id.layout_profile);
            stPublic = (Switch) itemView.findViewById(R.id.switcher_public);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            tagcontainerLayout = (TagContainerLayout) itemView.findViewById(R.id.tagcontainerLayout);
            tsLikesCounter = (TextSwitcher) itemView.findViewById(R.id.tsLikesCounter);
            vImageRoot = (FrameLayout) itemView.findViewById(R.id.vImageRoot);
            vBgLike = itemView.findViewById(R.id.vBgLike);
            ivLike = (ImageView) itemView.findViewById(R.id.ivLike);
            profile_name = (TextView) itemView.findViewById(R.id.profile_name);
        }

        public void bindView(Items item) {
            this.item = item;
        }

        public Items getFeedItem() {
            return item;
        }
    }

    public interface OnFeedItemClickListener {
        void onCommentsClick(View v, int position);

        void onMoreClick(View v, int position);

        void onPublicClick(View v, int position, boolean b);

        void onProfileClick(View v, int position);
    }

}
