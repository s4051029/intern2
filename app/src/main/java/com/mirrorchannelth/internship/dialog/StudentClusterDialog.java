package com.mirrorchannelth.internship.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.mirrorchannelth.internship.R;
import com.mirrorchannelth.internship.adapter.StudentClusterAdapter;
import com.mirrorchannelth.internship.model.Student;
import com.mirrorchannelth.internship.widget.DividerItemDecoration;
import com.mirrorchannelth.internship.widget.MyLinearLayoutManager;
import com.mirrorchannelth.internship.widget.listener.RecyclerItemClickListener;

import java.util.ArrayList;

public class StudentClusterDialog extends DialogFragment {

    public static final String TAG = "SeekerClusterDialog";

    private OnClusterActionListener onClusterActionListener = null;

    private RecyclerView studentView = null;

    private ArrayList<Student> studentList = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
//        window.setDimAmount(0f);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_image, container, false);

        //studentView = (RecyclerView) rootView.findViewById(R.id.seekerView);
        studentView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        studentView.setHasFixedSize(true);
        MyLinearLayoutManager layoutManager = new MyLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        studentView.setLayoutManager(layoutManager);
        studentView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childView, int position) {
                onClusterActionListener.onClusterAction(studentList.get(position));
                dismiss();
            }

            @Override
            public void onItemLongPress(View childView, int position) {
            }

        }));

        StudentClusterAdapter studentClusterDialog = new StudentClusterAdapter(getActivity());
        studentClusterDialog.setItems(studentList);
        //studentClusterDialog.setViewId(R.layout.item_cluster_seeker);
        studentView.setAdapter(studentClusterDialog);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void setOnClusterActionListener(OnClusterActionListener onClusterActionListener) {
        this.onClusterActionListener = onClusterActionListener;
    }

    public void setStudentList(ArrayList<Student> studentList) {
        this.studentList = studentList;
    }

    public interface OnClusterActionListener {
        public void onClusterAction(Student student);
    }

}

