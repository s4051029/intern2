package com.mirrorchannelth.internship.model;

import android.os.Parcel;
import android.os.Parcelable;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskBean implements Parcelable {
    private List<TaskItem> taskList = new ArrayList<TaskItem>();
    private List<TaskItem> taskApproveList ;
    private List<TaskItem> taskDenyList ;

    private int itemTotal;
    private String totalHours;
    private String taskUserId;
    public TaskBean(JSONObject jsonObject){
        itemTotal = jsonObject.optInt("item_total");
        taskUserId = jsonObject.optString("task_user_id");
        totalHours = jsonObject.optString("task_total_hours");
        JSONArray task_list = jsonObject.optJSONArray("task_list");
        addTaskList(task_list);
    }

    public void AddTask(JSONObject jsonObject){
        itemTotal = jsonObject.optInt("item_total");
        taskUserId = jsonObject.optString("task_user_id");
        totalHours = jsonObject.optString("task_total_hours");
        JSONArray task_list = jsonObject.optJSONArray("task_list");
        addTaskList(task_list);
    }

    private void addTaskList(JSONArray task_list) {

        if(task_list !=null) {
            for (int i = 0; i < task_list.length(); i++) {
                JSONObject activityTmp = task_list.optJSONObject(i);
                TaskItem taskItem = new TaskItem(activityTmp);
                if(!taskList.contains(taskItem)) {
                    taskList.add( taskItem);
                }
            }
        }
    }

    public void insertTask(JSONObject jsonObject){
        itemTotal = jsonObject.optInt("item_total");
        taskUserId = jsonObject.optString("task_user_id");
        totalHours = jsonObject.optString("task_total_hours");
        JSONArray activity = jsonObject.optJSONArray("task_list");
        if(activity !=null) {
            for (int i = 0; i < activity.length(); i++) {
                JSONObject activityTmp = activity.optJSONObject(i);
                TaskItem taskItem = new TaskItem(activityTmp);
                if(!taskList.contains(taskItem)) {
                    taskList.add(i, taskItem);
                }
            }
        }
    }

    public TaskItem getTaskItem(int position){
        return taskList.get(position);
    }

    public int getTaskSize(){
        return taskList.size();

    }

    public int getItemTotal(){
        return itemTotal;
    }
    public double getTotalHours(){
        double total = 0;
        for (int i = 0; i < taskList.size(); i++) {
            total += Double.parseDouble(taskList.get(i).getTaskHours());
        }
        return total;
    }

    public TaskItem getTask(String Id){
        for (int i = 0 ;i< taskList.size(); i++){
            TaskItem taskItem = taskList.get(i);
            if(Id.equalsIgnoreCase(taskItem.getTaskId())){
                return taskItem;
            }
        }
        return null;
    }

    public void removeTask(String taskId) {
        for (int i = 0 ;i< taskList.size(); i++){
            TaskItem taskItem = taskList.get(i);
            if(taskId.equalsIgnoreCase(taskItem.getTaskId())){
                taskList.remove(i);
            }
        }
    }

    protected TaskBean(Parcel in) {
        if (in.readByte() == 0x01) {
            taskList = new ArrayList<TaskItem>();
            in.readList(taskList, TaskItem.class.getClassLoader());
        } else {
            taskList = null;
        }
        if (in.readByte() == 0x01) {
            taskApproveList = new ArrayList<TaskItem>();
            in.readList(taskApproveList, TaskItem.class.getClassLoader());
        } else {
            taskApproveList = null;
        }
        if (in.readByte() == 0x01) {
            taskDenyList = new ArrayList<TaskItem>();
            in.readList(taskDenyList, TaskItem.class.getClassLoader());
        } else {
            taskDenyList = null;
        }
        itemTotal = in.readInt();
        totalHours = in.readString();
        taskUserId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (taskList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(taskList);
        }
        if (taskApproveList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(taskApproveList);
        }
        if (taskDenyList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(taskDenyList);
        }
        dest.writeInt(itemTotal);
        dest.writeString(totalHours);
        dest.writeString(taskUserId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaskBean> CREATOR = new Parcelable.Creator<TaskBean>() {
        @Override
        public TaskBean createFromParcel(Parcel in) {
            return new TaskBean(in);
        }

        @Override
        public TaskBean[] newArray(int size) {
            return new TaskBean[size];
        }
    };

    public List<TaskItem> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskItem> taskList) {
        this.taskList = taskList;
    }

    public List<TaskItem> getTaskApproveList() {
        return taskApproveList;
    }

    public void setTaskApproveList(List<TaskItem> taskApproveList) {
        this.taskApproveList = taskApproveList;
    }

    public List<TaskItem> getTaskDenyList() {
        return taskDenyList;
    }

    public void setTaskDenyList(List<TaskItem> taskDenyList) {
        this.taskDenyList = taskDenyList;
    }
}