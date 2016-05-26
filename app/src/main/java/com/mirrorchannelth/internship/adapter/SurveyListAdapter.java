package com.mirrorchannelth.internship.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mirrorchannelth.internship.R;
import com.mirrorchannelth.internship.model.Survey;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by rooney on 4/18/2016.
 */
public class SurveyListAdapter extends RecyclerView.Adapter<SurveyListAdapter.ViewHolder> {

    private Context context = null;
    private ArrayList<Survey> surveyList = null;
    private ArrayList<Integer> selectedList = null;
    private int viewId = 0;

    public SurveyListAdapter(Context context) {
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleLabel = null;
        public TextView nameLabel = null;
        public TextView status = null;
        public ImageView profileImage = null;

        public ViewHolder(View itemView) {
            super(itemView);
            titleLabel = (TextView) itemView.findViewById(R.id.titleLabel);
            nameLabel = (TextView) itemView.findViewById(R.id.nameLabel);
            status = (TextView) itemView.findViewById(R.id.status);
            profileImage = (ImageView) itemView.findViewById(R.id.profileImage);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(context).inflate(viewId, parent, false);
        ViewHolder viewHolder = new ViewHolder(convertView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Survey survey = surveyList.get(position);
        holder.titleLabel.setText(survey.getTitle());
        holder.nameLabel.setText(survey.getName());
        if(holder.status.getText().toString() != "0" ) {
            holder.status.setBackground(ContextCompat.getDrawable(context, R.drawable.green_circle));
        }

        /*if (selectedList == null) {
            holder.statusLabel.setText(R.string.employer_job_dialog_new);
        } else {
            int stringId = R.string.employer_job_dialog_apply;
            if (selectedList.indexOf(position) == -1) {
                stringId = R.string.employer_job_dialog_new;
            }
            holder.statusLabel.setText(stringId);
        } */
    }

    @Override
    public int getItemCount() {
        return surveyList == null ? 0 : surveyList.size();
    }

    public Object getItem(int position) {
        return surveyList.get(position);
    }

    /**
     * Dynamic layout
     * @param viewId
     */
    public void setViewId(int viewId) {
        this.viewId = viewId;
    }

    public void addItem(Survey survey) {
        if(surveyList == null) {
            surveyList = new ArrayList<Survey>();
        }
        surveyList.add(survey);
    }

    public void clear() {
        if (surveyList != null) {
            surveyList.clear();
        }
    }

    public void addSelectedItem(int position) {
        if (selectedList == null) {
            selectedList = new ArrayList<>();
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
                surveyList.remove(position);
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
}
