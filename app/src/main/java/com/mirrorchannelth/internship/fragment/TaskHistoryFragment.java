package com.mirrorchannelth.internship.fragment;


import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.mirrorchannelth.internship.R;
import com.mirrorchannelth.internship.adapter.NewsRecyclerViewAdapter;
import com.mirrorchannelth.internship.adapter.SpaceItemDecoration;
import com.mirrorchannelth.internship.adapter.TaskHistoryRecyclerViewAdapter;
import com.mirrorchannelth.internship.config.WebAPI;
import com.mirrorchannelth.internship.listener.RecyclerViewItemClickListener;
import com.mirrorchannelth.internship.listener.TaskApproveListener;
import com.mirrorchannelth.internship.model.ShareData;
import com.mirrorchannelth.internship.model.TaskBean;
import com.mirrorchannelth.internship.net.Connection;
import com.mirrorchannelth.internship.service.ServiceDao;
import com.mirrorchannelth.internship.util.AnimationUtil;
import com.mirrorchannelth.internship.util.DateUtil;
import com.mirrorchannelth.internship.util.WindowsUtil;
import com.mirrorchannelth.internship.view.DatePickerFragment;
import com.mirrorchannelth.internship.view.DefaultDisplayView;
import com.mirrorchannelth.internship.view.RefreshView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskHistoryFragment extends Fragment implements View.OnClickListener, OnLoadMoreListener, OnRefreshListener, Connection.OnConnectionCallBackListener, TaskApproveListener {
    private IRecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TaskHistoryRecyclerViewAdapter mAdapter;
    private TextView toolbartitle;
    private ImageView backButton;
    private boolean isLoadmore = false;
    private boolean isRefresh = false;
    private RefreshView header;
    private RefreshView footer;
    private ProgressBar progressBar;
    private CoordinatorLayout coordinatorLayout;
    private DefaultDisplayView defaultDisplayview;
    private String pageId = "1";
    private String taskUserId;
    private ServiceDao serviceDao;
    private TaskBean taskBean;
    private TextView startDateTextview;
    private TextView endDateTextview;
    private ImageView searchImageview;
    private static final String approve = "1";
    private static final String denied = "2";



    public TaskHistoryFragment() {
        // Required empty public constructor
    }

    public static TaskHistoryFragment newInstance(String userId) {
        TaskHistoryFragment taskHistory = new TaskHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("userId", userId);
        taskHistory.setArguments(bundle);
        return taskHistory;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_history, container, false);
        bindWidget(view);
        initWidget();
        initListener();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            taskUserId = bundle.getString("userId");
        }
        if(savedInstanceState != null) {
            taskBean = savedInstanceState.getParcelable("task");
            mAdapter.setTaskBean(taskBean);
            mRecyclerView.setIAdapter(mAdapter);
            if(taskBean.getTaskSize() == 0){
                showDefaultView(getResources().getString(R.string.content_empty),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_content_copy_black_48dp, null), refreshClickListener);
            } else {
                mRecyclerView.setRefreshEnabled(true);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }
        if (bundle != null) {
            taskUserId = bundle.getString("userId");
        }
        if (taskBean != null) {
            mAdapter.setTaskBean(taskBean);
            mRecyclerView.setIAdapter(mAdapter);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            getTaskList(pageId, taskUserId);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("task", taskBean);
        super.onSaveInstanceState(outState);
    }

    private void bindWidget(View rootview) {
        mRecyclerView = (IRecyclerView) rootview.findViewById(R.id.task_recycler_view);
        backButton = (ImageView) rootview.findViewById(R.id.leftMenu);
        progressBar = (ProgressBar) rootview.findViewById(R.id.taskProgressBar);
        coordinatorLayout = (CoordinatorLayout) rootview.findViewById(R.id.coordinatorLayout);
        startDateTextview = (TextView) rootview.findViewById(R.id.startDateTextview);
        endDateTextview = (TextView) rootview.findViewById(R.id.endDateTextview);
        searchImageview = (ImageView) rootview.findViewById(R.id.searchImageview);
        toolbartitle = (TextView) rootview.findViewById(R.id.toolbar_title);
    }

    private void initWidget() {
        serviceDao = new ServiceDao(WebAPI.URL);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(getActivity()));
        header = new RefreshView(getActivity());
        footer = new RefreshView(getActivity());
        mRecyclerView.setRefreshHeaderView(header);
        mRecyclerView.setLoadMoreFooterView(footer);
        mRecyclerView.setRefreshEnabled(true);
        mRecyclerView.setLoadMoreEnabled(true);
        mRecyclerView.setRefreshing(false);
        mAdapter = new TaskHistoryRecyclerViewAdapter(getActivity(), this);
        mRecyclerView.getLoadMoreFooterView().setVisibility(View.GONE);

        startDateTextview.setText(DateUtil.getCurrentDate("dd/MM/yyyy"));
        endDateTextview.setText(DateUtil.getCurrentDate("dd/MM/yyyy"));

        defaultDisplayview = new DefaultDisplayView(getActivity());

        backButton.setImageResource(R.drawable.ic_arrow_back_white_24dp);

        if (ShareData.getUserProfile().getUser_type().equals("E")) {
            backButton.setVisibility(View.VISIBLE);
            toolbartitle.setText(R.string.task_history_employee_toolbar_title);
        } else {
            backButton.setVisibility(View.GONE);
            taskUserId = ShareData.getUserProfile().getUser_id();
            toolbartitle.setText(R.string.task_history_student_toolbar_title);
        }
    }

    private void initListener() {
        startDateTextview.setOnClickListener(this);
        endDateTextview.setOnClickListener(this);
        searchImageview.setOnClickListener(this);
        mRecyclerView.setOnRefreshListener(this);
        mRecyclerView.setOnLoadMoreListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.leftMenu:
                getActivity().getSupportFragmentManager()
                        .popBackStack("taskHistory", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;
            case R.id.startDateTextview:
                openDateDialog(startDateTextview);
                break;
            case R.id.endDateTextview:
                openDateDialog(endDateTextview);
                break;
            case R.id.searchImageview:
                getTaskList(pageId, taskUserId, startDateTextview.getText().toString(), endDateTextview.getText().toString());
                break;
        }
    }

    private void getTaskList(String pageId, String task_user_id) {

        serviceDao.getTaskList(ShareData.getUserProfile(), pageId, task_user_id, "", "", this);
    }

    private void getTaskList(String pageId, String task_user_id, String startDate, String endDate) {
        taskBean = null;
        mRecyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        String oldDateformat = "dd/mm/yyyy";
        String newFormat = "yyyy-mm-dd";
        startDate = DateUtil.changeFormatDate(oldDateformat, newFormat, startDate);
        endDate = DateUtil.changeFormatDate(oldDateformat, newFormat, endDate);
        serviceDao.getTaskList(ShareData.getUserProfile(), pageId, task_user_id, startDate, endDate, this);
    }

    private void openDateDialog(TextView textview) {
        DatePickerFragment dateDialog = DatePickerFragment.getInstance(getActivity(), textview);
        dateDialog.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onLoadMore(View view) {
        isLoadmore = true;
        mRecyclerView.getLoadMoreFooterView().setVisibility(View.VISIBLE);
        int itemTotal = taskBean.getItemTotal();
        int pageId = itemTotal / 10;
        getTaskList(String.valueOf(pageId), taskUserId);
    }

    @Override
    public void onRefresh() {
        getTaskList(pageId, taskUserId);
    }

    @Override
    public void onSuccess(String result) {
        try {
            progressBar.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            JSONObject response = new JSONObject(result);
            if (response.getString("error").equals("0")) {
                if (!isLoadmore) {
                    JSONObject resultObject = response.getJSONObject("result");
                    if (taskBean == null) {
                        taskBean = new TaskBean(resultObject);
                        mAdapter.setTaskBean(taskBean);
                        mRecyclerView.setIAdapter(mAdapter);
                        mRecyclerView.startAnimation(AnimationUtil.animationSlideUp(getActivity()));
                    } else {
                        taskBean.insertTask(resultObject);
                    }
                } else {
                    taskBean.AddTask(response.getJSONObject("result"));
                    isLoadmore = false;
                }
                if (taskBean.getTaskSize() == 0) {
                    showDefaultView(getResources().getString(R.string.content_empty), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_content_copy_black_48dp, null), refreshClickListener);
                } else {
                    mRecyclerView.setRefreshEnabled(true);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                mRecyclerView.setRefreshing(false);
                mRecyclerView.getLoadMoreFooterView().setVisibility(View.GONE);
                mAdapter.notifyDataSetChanged();
                String toolsbarTitle = String.format(toolbartitle.getText().toString(), taskBean.getTotalHours());
                toolbartitle.setText(toolsbarTitle);
            } else{
                showDefaultView(getResources().getString(R.string.default_message_dialog), ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_refresh_black_48dp, null) , refreshClickListener);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e){
            if(isAdded()) {
                showDefaultView(getResources().getString(R.string.content_empty),
                        ResourcesCompat.getDrawable(getResources(), R.drawable.ic_content_copy_black_48dp, null), refreshClickListener);
            }
        }
    }

    View.OnClickListener refreshClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup view = (RelativeLayout) getView();
            view.removeView(defaultDisplayview);
            progressBar.setVisibility(View.VISIBLE);
            getTaskList(pageId, ShareData.getUserProfile().getUser_id());
        }
    };

    @Override
    public void onLostConnection() {
        if (taskBean != null && taskBean.getTaskSize() !=0) {
            showSnackbar(getResources().getString(R.string.no_internet_connection));
        } else  {
            showDefaultView(getResources().getString(R.string.no_internet_connection), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_refresh_black_48dp, null) , refreshClickListener);
        }
    }

    @Override
    public void onUnreachHost() {
        if (taskBean != null && taskBean.getTaskSize() !=0) {
            showSnackbar(getResources().getString(R.string.no_internet_connection));
        } else {
            showDefaultView(getResources().getString(R.string.no_internet_connection), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_refresh_black_48dp, null) , refreshClickListener);
        }
    }

    private void showSnackbar(String text) {
        mRecyclerView.setRefreshing(false);
        View f = mRecyclerView.getLoadMoreFooterView();
        f.setVisibility(View.GONE);
        coordinatorLayout.setVisibility(View.VISIBLE);
        Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG)
                .show();
    }

    private void showDefaultView(String text, Drawable drawable, View.OnClickListener onClickListener) {
        progressBar.setVisibility(View.GONE);
        RelativeLayout rootview = (RelativeLayout) this.getView();
        defaultDisplayview.setImage(drawable);
        defaultDisplayview.setImageOnclicklistener(onClickListener);
        defaultDisplayview.setText(text);
        mRecyclerView.setVisibility(View.GONE);
        rootview.addView(defaultDisplayview);
    }
    String taskId;
    @Override
    public void approve(String taskId) {
        WindowsUtil.alertWithTwoButton("Task Approve", "ทำการอนุมัติงาน " +taskBean.getTask(taskId).getTaskTitle(), "Approve", "Cancel", getActivity(), approveDialogListener, cencelListener);
        this.taskId = taskId;

    }
    private DialogInterface.OnClickListener deniedDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            progressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            serviceDao.addapproveTask(ShareData.getUserProfile(), taskId, denied, approveListener);
        }
    };
    DialogInterface.OnClickListener approveDialogListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            progressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            serviceDao.addapproveTask(ShareData.getUserProfile(), taskId, approve, approveListener);

        }
    };
    DialogInterface.OnClickListener cencelListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
        }
    };
    
    @Override
    public void denied(String taskId) {
        WindowsUtil.alertWithTwoButton("Task Approve", "ทำการปฏิเสธ " +taskBean.getTask(taskId).getTaskTitle(), "Deny", "Cancel", getActivity(), deniedDialogListener, cencelListener);
        this.taskId = taskId;

    }
    private Connection.OnConnectionCallBackListener approveListener = new Connection.OnConnectionCallBackListener() {
        @Override
        public void onSuccess(String result) {
            try {
                progressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                JSONObject response = new JSONObject(result);
                if (response.getString("error").equals("0")) {
                    taskBean.removeTask(taskId);
                    if(taskBean.getTaskSize() == 0) {
                        showDefaultView(getResources().getString(R.string.content_empty), ResourcesCompat.getDrawable(getResources(), R.drawable.ic_content_copy_black_48dp, null), refreshClickListener);
                    }else {
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.startAnimation(AnimationUtil.animationSlideUp(getActivity()));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onLostConnection() {
            progressBar.setVisibility(View.GONE);
            WindowsUtil.defaultAlertDialog(getString(R.string.default_dialog_header), getString(R.string.default_message_dialog), getString(R.string.default_label_dialog_button),getActivity());
        }
        @Override
        public void onUnreachHost() {
            progressBar.setVisibility(View.GONE);
            WindowsUtil.defaultAlertDialog(getString(R.string.default_dialog_header), getString(R.string.default_message_dialog), getString(R.string.default_label_dialog_button), getActivity());
        }

    };

}
