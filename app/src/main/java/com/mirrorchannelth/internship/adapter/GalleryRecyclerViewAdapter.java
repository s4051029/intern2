package com.mirrorchannelth.internship.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.mirrorchannelth.internship.R;
import com.mirrorchannelth.internship.model.Image;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by boss on 5/22/16.
 */
public class GalleryRecyclerViewAdapter extends RecyclerView.Adapter<GalleryRecyclerViewAdapter.ViewHolder>{
    private List<Image> imageList;
    private Context context;

    public GalleryRecyclerViewAdapter(Context context, List imageList){
        this.imageList = imageList;
        this.context = context;

    }

    public void setImages(List<Image> imageList){
        this.imageList = imageList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_gallery, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Image image = imageList.get(position);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        Display display = wm.getDefaultDisplay();
        display.getSize(size);
        int width = size.x;
        final int THUMBSIZE = width/3;
        Bitmap ThumbImage = null;
        if("video".equals(image.getMediaType())){
            Bitmap temp = ThumbnailUtils.createVideoThumbnail(image.getUrl(),MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            ThumbImage = ThumbnailUtils.extractThumbnail(temp, THUMBSIZE,THUMBSIZE);
            holder.videoIconImageview.setVisibility(View.VISIBLE);
        } else {
           ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(image.getUrl()),
                    THUMBSIZE, THUMBSIZE);
            holder.videoIconImageview.setVisibility(View.GONE);
        }
        holder.imageView.setImageBitmap(ThumbImage);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                if("video".equals(image.getMediaType())) {
                    intent.setDataAndType(Uri.fromFile(new File(image.getUrl())), "video/*");
                }else {
                        intent.setDataAndType(Uri.fromFile(new File(image.getUrl())), "image/*");

                    }
                context.startActivity(intent);

            }
        });
    }


    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public ImageView videoIconImageview;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image);
            videoIconImageview = (ImageView) itemView.findViewById(R.id.videoIconImageviews);
        }


    }
}
