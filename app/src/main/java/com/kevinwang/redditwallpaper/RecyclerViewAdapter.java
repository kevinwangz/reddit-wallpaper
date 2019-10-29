package com.kevinwang.redditwallpaper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    ImageModel imageModel;

    private ClickListener clickListener;

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        Button hrefView, downloadView, wallpaperView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.image_view);
            this.hrefView = (Button) itemView.findViewById(R.id.href_view);
            this.downloadView = (Button) itemView.findViewById(R.id.download_view);
            this.wallpaperView = (Button) itemView.findViewById(R.id.use_wallpaper_view);
            imageView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (clickListener != null) {
                        clickListener.onImageClick(v, getAdapterPosition());
                    }
                }
            });
            hrefView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (clickListener != null) {
                        clickListener.onHrefClick(v, getAdapterPosition());
                    }
                }
            });
            downloadView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (clickListener != null) {
                        clickListener.onDownloadClick(v, getAdapterPosition());
                    }
                }
            });
            wallpaperView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (clickListener != null) {
                        clickListener.onSetWallpaper(v, getAdapterPosition());
                    }
                }
            });
        }

    }

    public RecyclerViewAdapter() {
        this.imageModel = ImageModel.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView imageView = holder.imageView;

        imageView.setImageBitmap(imageModel.getImageWithDetails(position).getImage());
    }

    @Override
    public int getItemCount() {
        return imageModel.getNumImages();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface ClickListener {
        void onImageClick(View view, int position);
        void onHrefClick(View view, int position);
        void onDownloadClick(View view, int position);
        void onSetWallpaper(View view, int position);
    }
}
