package com.mirrorchannelth.internship.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mirrorchannelth.internship.R;
import com.mirrorchannelth.internship.model.Choice;
import com.mirrorchannelth.internship.model.Image;
import com.mirrorchannelth.internship.model.SurveyDetail;
import com.mirrorchannelth.internship.widget.DividerItemDecoration;
import com.mirrorchannelth.internship.widget.listener.RecyclerItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;

public class SurveyDetailAdapter2 extends RecyclerView.Adapter<SurveyDetailAdapter2.ViewHolder> {

    private Context context = null;
    private int viewId = 0;
    private ArrayList<SurveyDetail> surveyDetails = null;
    private ImageLoader imageLoader = null;
    private DisplayImageOptions options = null;
    private ArrayList<Integer> selectedList = null;
    private ChoiceAdapter choiceAdapter = null;

    public SurveyDetailAdapter2(Context context) {
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleLabel = null;
        public TextView nameLabel = null;
        public TextView vacancyLabel = null;
        public RecyclerView profileView = null;
        public RecyclerView choiceView = null;
        public LinearLayoutManager layoutManager = null;

        public ViewHolder(View itemView) {
            super(itemView);
            titleLabel = (TextView) itemView.findViewById(R.id.titleLabel);
            nameLabel = (TextView) itemView.findViewById(R.id.nameLabel);
            vacancyLabel = (TextView) itemView.findViewById(R.id.vacancyLabel);
            profileView = (RecyclerView) itemView.findViewById(R.id.profileView);
            choiceView = (RecyclerView) itemView.findViewById(R.id.choiceView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        SurveyDetail surveyDetail = surveyDetails.get(position);
        String layoutType = surveyDetail.getType();
        int type = 0;
        if(layoutType.equals("1")){
            type = 1;
        }
        return type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View convertView = null;
        if(viewType == 1) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_survey_choice, viewGroup, false);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_survey_comment, viewGroup, false);
        }
        ViewHolder viewHolder = new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SurveyDetail surveyDetail = surveyDetails.get(position);
        holder.titleLabel.setText(surveyDetail.getTitle());
        holder.vacancyLabel.setText(surveyDetail.getNo());
        //String title = surveyDetail.getTitle();
        //holder.bigTitleLabel.setText(title.substring(0, 1).toUpperCase());
        //holder.titleLabel.setText(title);
        //holder.descLabel.setText(surveyDetail.getDesc());
        ArrayList<Image> imageList = surveyDetail.getImageList();
        /*if (imageList == null) {
            //holder.viewPager.setVisibility(View.GONE);
        } else {*/
            ImagePagerAdapter imagePagerAdapter = (ImagePagerAdapter) holder.profileView.getAdapter();
            //holder.viewPager.setVisibility(View.VISIBLE);
            if (imagePagerAdapter == null) {
                imagePagerAdapter = new ImagePagerAdapter(context);
                holder.profileView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL_LIST));
                holder.profileView.setHasFixedSize(true);
                holder.layoutManager = new LinearLayoutManager(context);
                holder.layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                holder.profileView.setLayoutManager(holder.layoutManager);
                holder.profileView.setAdapter(imagePagerAdapter);
            } else {
                imagePagerAdapter.clear();
                imagePagerAdapter.notifyDataSetChanged();
            }
            if( imageList != null) {
                int size = imageList.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        Image image = imageList.get(i);
                        imagePagerAdapter.addItem(image);
                    }
                    imagePagerAdapter.notifyDataSetChanged();
                }
            }

        // Set view choice
        if (surveyDetail.getType().equals("1")) {
            ArrayList<Choice> choiceList = surveyDetail.getChoiceList();
            choiceAdapter = (ChoiceAdapter) holder.choiceView.getAdapter();
            if (choiceAdapter == null) {
                choiceAdapter = new ChoiceAdapter(context);
                holder.choiceView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL_LIST));
                holder.choiceView.setHasFixedSize(true);
                holder.layoutManager = new LinearLayoutManager(context);
                holder.layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                holder.choiceView.setLayoutManager(holder.layoutManager);
                holder.choiceView.setAdapter(choiceAdapter);
            } else {
                choiceAdapter.clear();
                choiceAdapter.notifyDataSetChanged();
            }
            if (choiceList != null) {
                int size = choiceList.size();
                if (size > 0) {
                    for (int i = 0; i < size; i++) {
                        Choice choice = choiceList.get(i);
                        choiceAdapter.addItem(choice);
                    }
                    choiceAdapter.notifyDataSetChanged();
                }
            }
            holder.choiceView.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View childView, int position) {
                    Log.d("childView" , childView.getRootView().toString());
                    choiceAdapter.addSelectedChoiceItem(position);
                    choiceAdapter.notifyDataSetChanged();
                }

                @Override
                public void onItemLongPress(View childView, int position) {

                }
            }));
        }
    }

    @Override
    public int getItemCount() {
        return surveyDetails == null ? 0 : surveyDetails.size();
    }

    public Object getItem(int position) {
        return surveyDetails.get(position);
    }

    public Boolean getLastItem(int position) {
        Boolean isTrue = false;
        if(position == surveyDetails.size()-1) {
            isTrue = true;
        }
        return  isTrue;
    }

    /**
     * Dynamic layout
     * @param viewId
     */
    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public void addSelectedItem(int position) {
        if (selectedList == null) {
            selectedList = new ArrayList<Integer>();
        }
        int index = selectedList.indexOf(position);
        if (index == -1) {
            selectedList.add(position);
        } else {
            selectedList.remove(index);
        }
    }

    public void removeSelectedItemForList() {
        if (selectedList != null) {
            //  Must sort before
            Collections.sort(selectedList);
            int lastIndex = selectedList.size() - 1;

            //  Remove data loop
            for (int i = lastIndex; i >= 0; i--) {
                int position = selectedList.get(i);
                surveyDetails.remove(position);
            }

            //  Animation loop
            for (int i = lastIndex; i >= 0 ; i--) {
                int position = selectedList.get(i);
                notifyItemRemoved(position);
            }
        }
    }

    public void clearSelection() {
        if (selectedList != null) {
            selectedList.clear();
        }
    }

    public int getSelectedCount() {
        return selectedList == null ? 0 : selectedList.size();
    }

    public ArrayList<Integer> getSelectedList() {
        return selectedList;
    }

    public void addItem(SurveyDetail surveyDetail) {
        if (surveyDetails == null) {
            surveyDetails = new ArrayList<SurveyDetail>();
            imageLoader = ImageLoader.getInstance();
            options = new DisplayImageOptions.Builder()
//                    .showImageOnLoading(R.color.white)
//                    .showImageForEmptyUri(R.color.white)
//                    .showImageOnFail(R.color.white)
                    .cacheInMemory(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
        }
        surveyDetails.add(surveyDetail);
    }

    public void clear() {
        if (surveyDetails != null) {
            surveyDetails.clear();
        }
    }

    private class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ViewHolder1> {

        private Context context = null;
        //private LayoutInflater layoutInflater1 = null;
        private ArrayList<Image> imageList = null;
        private DisplayImageOptions options1 = null;
        private ImageLoader imageLoader1 = null;

        public ImagePagerAdapter(Context context) {
            this.context = context;
        }

        /*
        @Override
        public int getCount() {
            return imageList == null ? 0 : imageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (layoutInflater == null) {
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            View itemView = layoutInflater.inflate(R.layout.item_image, container, false);

            Image image = imageList.get(position);
            String protocol = image.getProtocol();
            String fullUrl = (protocol == null ? "" : protocol) + image.getUrl();
            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageLoader.displayImage(fullUrl, imageView, options, null);
            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        } */

        public void addItem(Image image) {
            if (imageList == null) {
                imageList = new ArrayList<Image>();
                imageLoader1 = ImageLoader.getInstance();
                options1 = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .considerExifParams(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
            }
            imageList.add(image);
        }

        public void clear() {
            if (imageList != null) {
                imageList.clear();
            }
        }
        public class ViewHolder1 extends RecyclerView.ViewHolder {
            public LinearLayout contentView = null;
            public TextView bigTitleLabel = null;
            public TextView titleLabel = null;
            public TextView textView = null;
            //public ViewPager viewPager = null;
            public ImageView imageView = null;
            public ImageView imageView2 = null;

            public ViewHolder1(View itemView) {
                super(itemView);
                //contentView = (LinearLayout) itemView.findViewById(R.id.contentView);
                //bigTitleLabel = (TextView) itemView.findViewById(R.id.bigTitleLabel);
                //titleLabel = (TextView) itemView.findViewById(R.id.titleLabel);
                //textView = (TextView) itemView.findViewById(R.id.textView);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                //imageView2 = (ImageView) itemView.findViewById(R.id.imageView2);
            }

        }

        @Override
        public ImagePagerAdapter.ViewHolder1 onCreateViewHolder(ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_image, null);
            ViewHolder1 viewHolder = new ViewHolder1(convertView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder1 holder1, int position) {
            Image image = imageList.get(position);
            ViewHolder1 viewHolder1 = (ViewHolder1)holder1;

            String protocol = image.getProtocol();
            String fullUrl = (protocol == null ? "" : protocol) + image.getUrl();
            //Log.d("fullUrl", "imageUrl : " + fullUrl);
            //Log.d("position", "position : " + position);
            //imageLoader1.displayImage(fullUrl, holder1.imageView, options1, null);
            //if(position==0) {
                //holder1.textView.setText(fullUrl);
            imageLoader1.displayImage(fullUrl, viewHolder1.imageView, options1, null);
            //}
        }

        @Override
        public int getItemCount() {
            return imageList == null ? 0 : imageList.size();
        }
    }

    private class ChoiceAdapter extends RecyclerView.Adapter<ChoiceAdapter.ViewHolderChoice> {

        private Context context = null;
        //private LayoutInflater layoutInflater1 = null;
        private ArrayList<Choice> choiceList = null;
        private ArrayList<Integer> selectedChoiceList = null;

        public ChoiceAdapter(Context context) {
            this.context = context;
        }

        public void addItem(Choice choice) {
            if (choiceList == null) {
                choiceList = new ArrayList<Choice>();
            }
            choiceList.add(choice);
        }

        public void clear() {
            if (choiceList != null) {
                choiceList.clear();
            }
        }
        public class ViewHolderChoice extends RecyclerView.ViewHolder {
            //public LinearLayout contentView = null;
            //public TextView bigTitleLabel = null;
            //public TextView titleLabel = null;
            public TextView textView = null;
            //public ViewPager viewPager = null;
            //public ImageView imageView = null;
            //public ImageView imageView2 = null;

            public ViewHolderChoice(View itemView) {
                super(itemView);
                //contentView = (LinearLayout) itemView.findViewById(R.id.contentView);
                //bigTitleLabel = (TextView) itemView.findViewById(R.id.bigTitleLabel);
                //titleLabel = (TextView) itemView.findViewById(R.id.titleLabel);
                textView = (TextView) itemView.findViewById(R.id.textView);
                //imageView = (ImageView) itemView.findViewById(R.id.imageView);
                //imageView2 = (ImageView) itemView.findViewById(R.id.imageView2);
            }

        }

        @Override
        public ChoiceAdapter.ViewHolderChoice onCreateViewHolder(ViewGroup parent, int viewType) {
            View convertView = LayoutInflater.from(context).inflate(R.layout.item_choice, null);
            ViewHolderChoice viewHolder = new ViewHolderChoice(convertView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolderChoice holder1, int position) {
            Choice choice = choiceList.get(position);
            //ViewHolderChoice viewHolder1 = (ViewHolderChoice)holder1;
            holder1.textView.setText(choice.getChoiceText().toString());
            //imageLoader1.displayImage(fullUrl, holder1.imageView, options1, null);
            //if(position==0) {
            //holder1.textView.setText(fullUrl);
            //imageLoader1.displayImage(fullUrl, viewHolder1.imageView, options1, null);
            //}
            if (selectedChoiceList != null) {
                int backgroundColor = 0;
                if (selectedChoiceList.indexOf(position) == -1) {
                    backgroundColor = android.R.color.darker_gray;
                } else {
                    backgroundColor = R.color.colorPrimary;
                }
                holder1.textView.setBackgroundResource(backgroundColor);
            }
        }

        @Override
        public int getItemCount() {
            return choiceList == null ? 0 : choiceList.size();
        }

        public void addSelectedChoiceItem(int position) {
            if (selectedChoiceList == null) {
                selectedChoiceList = new ArrayList<Integer>();
            }
            int index = selectedChoiceList.indexOf(position);
            if (index == -1) {
                selectedChoiceList.add(position);
                if (selectedChoiceList.size() > 1) {
                    selectedChoiceList.remove(0);
                }
            } else {
                selectedChoiceList.remove(index);
            }
        }
    }

}
