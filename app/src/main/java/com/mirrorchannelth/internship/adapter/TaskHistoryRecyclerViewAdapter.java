package com.mirrorchannelth.internship.adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mirrorchannelth.internship.R;
import com.mirrorchannelth.internship.fragment.TaskHistoryFragment;
import com.mirrorchannelth.internship.listener.RecyclerViewItemClickListener;
import com.mirrorchannelth.internship.listener.TaskApproveListener;
import com.mirrorchannelth.internship.model.ShareData;
import com.mirrorchannelth.internship.model.TaskBean;
import com.mirrorchannelth.internship.model.TaskItem;
import com.mirrorchannelth.internship.view.DateView;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.List;

/**
 * Created by boss on 4/19/16.
 */
public class TaskHistoryRecyclerViewAdapter extends RecyclerView.Adapter<TaskHistoryRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private TaskBean taskBean;
    private List<TaskItem> taskItemList;
    private TaskApproveListener taskApproveListener;

    public TaskHistoryRecyclerViewAdapter() {
    }

    public TaskHistoryRecyclerViewAdapter(Context context, TaskApproveListener taskApproveListener) {
        this.context = context;
        this.taskApproveListener = taskApproveListener;
    }

    public void setTaskBean(TaskBean taskBean) {
        this.taskBean = taskBean;
        taskItemList = taskBean.getTaskList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_task_history, parent, false);
        ViewHolder vh = new ViewHolder(view, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final TaskItem item = taskBean.getTaskItem(position);
            if (ShareData.getUserProfile().getUser_type().equals("E")) {
                holder.approveButtonGroup.setVisibility(View.VISIBLE);
            } else {
                holder.approveButtonGroup.setVisibility(View.GONE);
            }

            holder.taskTitleTextview.setText(item.getTaskTitle());
            holder.taskHourTextview.setText(item.getTaskHours());
            holder.taskDescriptionTextview.setText(item.getTaskDescription());
            holder.taskDate.setDate(item.getTaskDate());
            holder.approveTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskApproveListener.approve(item.getTaskId());
                }
            });

            holder.deniedTextview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskApproveListener.denied(item.getTaskId());
                }
            });

    }

    @Override
    public int getItemCount() {
        return taskBean.getTaskSize();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout approveButtonGroup;
        public TextView taskTitleTextview;
        public TextView taskDescriptionTextview;
        public TextView taskHourTextview;
        private RecyclerViewItemClickListener itemClickListener;
        public DateView taskDate;
        public TextView approveTextview;
        public TextView deniedTextview;
        private Context context;
        public ViewHolder(View itemView, Context context) {
            super(itemView);
            approveButtonGroup = (LinearLayout) (itemView).findViewById(R.id.approveButtonGroup);
            taskDescriptionTextview = (TextView) itemView.findViewById(R.id.taskDescriptionTextview);
            taskTitleTextview = (TextView) itemView.findViewById(R.id.taskTitleTextview);
            taskHourTextview = (TextView) itemView.findViewById(R.id.taskHoursTextview);
            taskDate = (DateView) itemView.findViewById(R.id.taskDate);
            approveTextview = (TextView) itemView.findViewById(R.id.approveTextview);
            deniedTextview = (TextView) itemView.findViewById(R.id.deniedTextview);
            this.context = context;

        }
    }
}
