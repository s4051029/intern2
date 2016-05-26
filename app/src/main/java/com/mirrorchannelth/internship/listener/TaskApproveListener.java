package com.mirrorchannelth.internship.listener;

/**
 * Created by boss on 5/25/16.
 */
public interface TaskApproveListener {
    public void approve(String taskId);
    public void denied(String taskId);

}
