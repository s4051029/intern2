package com.mirrorchannelth.internship.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirrorchannelth.internship.R;
import com.mirrorchannelth.internship.model.Student;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class StudentClusterAdapter extends RecyclerView.Adapter<StudentClusterAdapter.ViewHolder> {

    private Context context = null;
    private int viewId = 0;
    private ImageLoader imageLoader = null;
    private DisplayImageOptions profileOptions = null;
    //private DataHelper dataHelper = null;
    private ArrayList<Student> studentList = null;

    public StudentClusterAdapter(Context context) {
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profileImage = null;
        public TextView positionLabel = null;
        public TextView educationLabel = null;

        public ViewHolder(View itemView) {
            super(itemView);
            //profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
            //positionLabel = (TextView) itemView.findViewById(R.id.positionLabel);
            //educationLabel = (TextView) itemView.findViewById(R.id.educationLabel);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(viewId, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public int getItemCount() {
        return studentList != null ? studentList.size() : 0;
    }

    public Object getItem(int position) {
        return studentList != null ? studentList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        /*
        Seeker seeker = seekerList.get(position);

        imageLoader.displayImage(seeker.getProfileImageUrl(), viewHolder.profileImage, profileOptions, null);

        SpannableStringBuilder builder = new SpannableStringBuilder();
        String positionText = seeker.getPosition();
        SpannableString positionSpan = new SpannableString(positionText);
        positionSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, positionText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(positionSpan);

        String expText = context.getString(R.string.seeker_item_exp, seeker.getExperience());
        SpannableString expSpan = new SpannableString(expText);
        expSpan.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.sky)), 0, expText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(expSpan);
        viewHolder.positionLabel.setText(builder);

        String educationName = dataHelper.getEducationName(seeker.getEducation());
        viewHolder.educationLabel.setText(context.getString(R.string.seeker_item_salary, educationName, seeker.getSalary()));
        */
    }

    /**
     * Dynamic layout
     * @param viewId
     */
    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public void setItems(ArrayList<Student> studentList) {
        if (imageLoader == null) {
            imageLoader = ImageLoader.getInstance();
            profileOptions = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.color.colorPrimary)
                    .showImageForEmptyUri(R.color.colorPrimary)
                    .showImageOnFail(R.color.colorPrimary)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            //dataHelper = new DataHelper(context);
        }
        this.studentList = studentList;
    }

    public void clear() {
        if (studentList != null) {
            studentList.clear();
        }
    }

}
