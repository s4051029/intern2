package com.mirrorchannelth.internship.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mirrorchannelth.internship.R;
import com.mirrorchannelth.internship.adapter.SurveyListAdapter;
import com.mirrorchannelth.internship.config.WebAPI;
import com.mirrorchannelth.internship.model.ShareData;
import com.mirrorchannelth.internship.model.Survey;
import com.mirrorchannelth.internship.model.UserProfile;
import com.mirrorchannelth.internship.net.Connection;
import com.mirrorchannelth.internship.widget.DividerItemDecoration;
import com.mirrorchannelth.internship.widget.listener.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rooney on 4/13/2016.
 */
public class SurveyListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final int ACTION_DETAIL = 1;

    private boolean isLoadMore = false, isEndLoad = false;
    private boolean isFirstFinding = true, isRefresh = false;
    private int pageNumber = 1;

    private LinearLayoutManager layoutManager = null;
    private SurveyListAdapter surveyListAdapter = null;

    private RecyclerView surveyListView = null;
    private CoordinatorLayout coordinatorLayout = null;
    private LinearLayout noDataContainer = null;
    private ProgressBar progressBar = null;
    private SwipeRefreshLayout mSwipeRefreshLayout = null;

    private boolean isFragmentCreated = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_survey_list, container, false);
    }

    public void onResume() {
        super.onResume();
        if (!isFragmentCreated) {
            requestSurvey();
            isFragmentCreated = true;
        }
    }

    private void initToolbar() {
        View rootView = getView();
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView textTitle = (TextView) rootView.findViewById(R.id.toolbar_title);
        textTitle.setText(getString(R.string.survey_title));
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_toolbar_logo);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initToolbar();
        initWidget();
        if (isFragmentCreated) {
            isFragmentCreated = true;
            //progressBar.setVisibility(View.VISIBLE);
            requestSurvey();
            surveyListView.setAdapter(surveyListAdapter);
        } else {
            //  Check adapter
            if (surveyListAdapter == null || surveyListAdapter.getItemCount() == 0) {
                //noDataContainer.setVisibility(View.VISIBLE);
            } else {
                surveyListView.setAdapter(surveyListAdapter);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void initWidget() {
        View rootView = getView();

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setProgressViewOffset(false, 0, 120);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        NestedScrollView scrollView = (NestedScrollView) rootView.findViewById (R.id.nestScrollview);
        scrollView.setFillViewport (true);

        surveyListView = (RecyclerView) rootView.findViewById(R.id.surveyView);
        surveyListView.setNestedScrollingEnabled(false);
        surveyListView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        surveyListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        surveyListView.setLayoutManager(layoutManager);
        surveyListView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childView, int position) {
                SurveyDetailActivity mFragment = new SurveyDetailActivity();
                Intent intent = new Intent(getActivity(), SurveyDetailActivity.class);
                startActivity(intent);
                //getActivity().getSupportFragmentManager().beginTransaction().add(R.id.coordinatorLayout, mFragment).commit();
            }

            @Override
            public void onItemLongPress(View childView, int position) {
                Snackbar snackbar = Snackbar.make(coordinatorLayout,"Long Click", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();
            }
        }));
        /*
        if (surveyListAdapter == null) {
            surveyListAdapter = new SurveyListAdapter(getActivity());
            surveyListAdapter.setViewId(R.layout.item_survey_list);
            surveyListView.setAdapter(surveyListAdapter);
            for (int i=0;i<50;i++) {
                Survey survey = new Survey();
                survey.setName("สมศรี มีดี");
                survey.setTitle("ประเมินการฝึกงาน");

                //  Add to adapter
                surveyListAdapter.addItem(survey);
            }
            surveyListAdapter.notifyDataSetChanged();
        } */
        noDataContainer = (LinearLayout) rootView.findViewById(R.id.noDataContainer);
        TextView noDataLabel = (TextView) rootView.findViewById(R.id.noDataLabel);
        noDataLabel.setText(getString(R.string.survey_no_data));
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
    }

    private void changeToDetailEmployerActivity(int position) {
        //EmployerAdapter.ViewHolder viewHolder = (EmployerAdapter.ViewHolder) employerView.findViewHolderForAdapterPosition(position);
        //selectedPosition = position;
        //selectedEmployer = (Employer) employerAdapter.getItem(selectedPosition);

        //Intent intent = new Intent(getActivity(), DetailEmployerActivity.class);
        //Bundle extras = new Bundle();
        //extras.putSerializable(DetailEmployerActivity.EMPLOYER, selectedEmployer);
        //intent.putExtras(extras);
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        //    ActivityOptionsCompat options = ActivityOptionsCompat.
        //            makeSceneTransitionAnimation(getActivity(), viewHolder.profileImage, "profileImage");
        //    getActivity().startActivityForResult(intent, ACTION_DETAIL, options.toBundle());
        //} else {
        //    startActivityForResult(intent, ACTION_DETAIL);
        //}
    }

    private void requestSurvey(){
        Log.d("requestSurveyDetail", "requestSurveyDetail");
        String url = WebAPI.URL;
        UserProfile userProfile = ShareData.getUserProfile();

        Connection connection = new Connection(url);
        connection.addPostData("function", "surveyUserList");
        connection.addPostData("user_id", userProfile.getUser_id());
        connection.addPostData("token_key", userProfile.getToken_key());
        connection.addPostData("user_group", userProfile.getUser_group());
        connection.addPostData("user_type", userProfile.getUser_type());
        connection.setOnConnectionCallBackListener(new Connection.OnConnectionCallBackListener() {
            @Override
            public void onSuccess(String result) {
                Log.d("onSuccess", "onSuccess");
                mSwipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                try {
                    if (surveyListAdapter == null) {
                        surveyListAdapter = new SurveyListAdapter(getActivity());
                        surveyListAdapter.setViewId(R.layout.item_survey_list);
                        surveyListView.setAdapter(surveyListAdapter);
                    } else {
                        Log.d("clear", "clear");
                        surveyListAdapter.clear();
                    }
                        JSONObject jsonObject = new JSONObject(result);
                        int error = jsonObject.getInt("error");
                        String message = jsonObject.getString("message");
                        //JSONObject surveyJObj = jsonObject.getJSONObject("result");
                        if (error == 0) {
                            //  Get images
                            JSONArray imagesJArr = jsonObject.getJSONArray("result");
                            int size = imagesJArr.length();
                            for (int i = 0; i < size; i++) {
                                JSONObject imageJObj = imagesJArr.getJSONObject(i);
                                String user_id = imageJObj.getString("user_id");
                                String name = imageJObj.getString("name");
                                String picture = imageJObj.getString("picture");
                                String title = imageJObj.getString("title");
                                String survey_id = imageJObj.getString("survey_id");
                                String is_survey = imageJObj.getString("is_survey");

                                Log.d("user_id", user_id);
                                Log.d("name", name);
                                Log.d("picture", picture);
                                Log.d("title", title);
                                Log.d("survey_id", survey_id);
                                Log.d("is_survey", is_survey);

                                Survey survey = new Survey();
                                survey.setName(name);
                                survey.setProfileUrl(picture);
                                survey.setTitle(title);
                                survey.setUserId(user_id);
                                survey.setSurveyId(survey_id);
                                survey.setIsSurvey(is_survey);

                                surveyListAdapter.addItem(survey);
                            }
                            surveyListAdapter.notifyDataSetChanged();
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLostConnection() {
                Log.d("onLostConnection", "onLostConnection");
                requestSurvey();
            }

            @Override
            public void onUnreachHost() {
                Log.d("onUnreachHost", "onUnreachHost");
                requestSurvey();
            }
        });
        connection.execute();
    }

    @Override
    public void onRefresh() {
        requestSurvey();
    }

    /*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_DETAIL) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                //employerAdapter.removeItem(selectedPosition, true);
                //notifyDatasetWithDelay(700);
            }
        }
    }
    */
}
