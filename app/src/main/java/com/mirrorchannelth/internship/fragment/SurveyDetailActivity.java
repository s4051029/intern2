package com.mirrorchannelth.internship.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mirrorchannelth.internship.R;
import com.mirrorchannelth.internship.adapter.SurveyDetailAdapter2;
import com.mirrorchannelth.internship.anim.Animator;
import com.mirrorchannelth.internship.config.WebAPI;
import com.mirrorchannelth.internship.model.Choice;
import com.mirrorchannelth.internship.model.Image;
import com.mirrorchannelth.internship.model.ShareData;
import com.mirrorchannelth.internship.model.SurveyDetail;
import com.mirrorchannelth.internship.model.UserProfile;
import com.mirrorchannelth.internship.net.Connection;
import com.mirrorchannelth.internship.widget.DividerItemDecoration;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rooney on 4/19/2016.
 */
public class SurveyDetailActivity extends AppCompatActivity {

    private int pageNumber = 0;
    private boolean isLoadMore = false, isEndLoad = false;

    private LinearLayoutManager layoutManager = null;
    private SurveyDetailAdapter2 surveyDetailAdapter = null;

    private RecyclerView surveyListView = null;
    private CoordinatorLayout coordinatorLayout = null;
    private LinearLayout noDataContainer = null;
    private ProgressBar progressBar = null;
    private FloatingActionButton saveButton = null;

    private boolean isFragmentCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_survey_detail);
        initToolbar();
        initImageCache();
        initWidget();
    }

    public void onResume() {
        super.onResume();
        if (!isFragmentCreated) {
            Log.d("resume", "resumeresumeresume");
            requestSurveyDetail();
            isFragmentCreated = true;
        } else {
            requestSurveyDetail();
        }
    }

    private void initImageCache() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (! imageLoader.isInited()) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .memoryCacheSizePercentage(13)
                    .defaultDisplayImageOptions(options)
                    .build();
            imageLoader.init(config);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); //((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        TextView textTitle = (TextView) findViewById(R.id.toolbar_title);
        textTitle.setText(getString(R.string.survey_detail_title));
        setTitle(getString(R.string.survey_title));//((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.survey_title));
        getSupportActionBar().setDisplayShowTitleEnabled(false);//((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);//((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        //((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //SurveyListFragment mFragment = new SurveyListFragment();
                //getActivity().getSupportFragmentManager().beginTransaction().add(R.id.coordinatorLayout, mFragment).commit();
                //getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                //Snackbar.make(coordinatorLayout,"BAS Panupong",Snackbar.LENGTH_LONG).show();
            }
        });
        //getSupportActionBar().setIcon(R.mipmap.ic_toolbar_logo);
    }

    private void initWidget() {
        //View rootView = getView();
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        //final NestedScrollView scrollView = (NestedScrollView) findViewById (R.id.nestScrollview);
        //scrollView.setFillViewport (true);

        saveButton = (FloatingActionButton) findViewById(R.id.saveBtn);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // requestSaveSurvey();
                finish();
            }
        });

        surveyListView = (RecyclerView) findViewById(R.id.surveyDetailView);
        //surveyListView.setNestedScrollingEnabled(false);
        surveyListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        surveyListView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        surveyListView.setLayoutManager(layoutManager);

        //  Scroll listener
        surveyListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Animator animator = new Animator();
                if(newState == 0) {
                    //animator.fadeIn(saveButton);
                    saveButton.setVisibility(View.VISIBLE);
                } else {
                    //animator.fadeOut(saveButton);
                    saveButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

/*
        if (surveyDetailAdapter == null) {
            surveyDetailAdapter = new SurveyDetailAdapter(getActivity());
            surveyDetailAdapter.setViewId(R.layout.item_survey_list);
            surveyListView.setAdapter(surveyDetailAdapter);
            //for (int i=1;i<50;i++) {
            //    SurveyDetail survey = new SurveyDetail();
            //    survey.setType("สมศรี");
            //    survey.setTitle("เขียนโปรแกรม สั่งอาหาร");
                //survey.setVacancy(i);

            //    surveyDetailAdapter.setViewId(R.layout.item_survey_comment);
            //    surveyDetailAdapter.addItem(survey);
            //    }
            //isLoadMore = false;
        } */
            //surveyListView.setAdapter(surveyListAdapter);
        //surveyDetailAdapter.notifyDataSetChanged();
        //surveyListView.scrollToPosition(surveyDetailAdapter.getItemCount());
    }

    private void requestSurveyDetail(){
        Log.d("requestSurveyDetail", "requestSurveyDetail");
        String url = WebAPI.URL;
        UserProfile userProfile = ShareData.getUserProfile();

        Connection connection = new Connection(url);
        connection.addPostData("function", "surveyDetail");
        connection.addPostData("user_id", userProfile.getUser_id());
        connection.addPostData("token_key", userProfile.getToken_key());
        connection.addPostData("user_group", userProfile.getUser_group());
        connection.addPostData("user_type", userProfile.getUser_type());
        connection.addPostData("survey_id", "1");
        connection.addPostData("survey_user_id", "1");
        connection.setOnConnectionCallBackListener(new Connection.OnConnectionCallBackListener() {
            @Override
            public void onSuccess(String result) {
                Log.d("onSuccess", "onSuccess" + result);

                try {
                    if (surveyDetailAdapter == null) {
                        surveyDetailAdapter = new SurveyDetailAdapter2(SurveyDetailActivity.this);
                        //surveyDetailAdapter.setViewId(R.layout.item_survey_comment);
                        surveyListView.setAdapter(surveyDetailAdapter);
                    } else {
                        surveyDetailAdapter.clear();
                    }
                    JSONObject jsonObject = new JSONObject(result);
                    int error = jsonObject.getInt("error");
                    //String message = jsonObject.getString("message");
                    JSONObject surveyJObj = jsonObject.getJSONObject("result");
                    if(error == 0) {
                        JSONArray resultJArr = surveyJObj.getJSONArray("survey_result");
                        int size_result = resultJArr.length();
                        for (int i = 0; i < size_result; i++) {
                            JSONObject resultJObj = resultJArr.getJSONObject(i);
                            String survey_group_name = resultJObj.getString("survey_group_name");
                            Log.d("Group Name", "GN" + survey_group_name);
                            JSONArray imagesJArr = resultJObj.getJSONArray("survey_list");
                            int size = imagesJArr.length();
                            for (int j = 0; j < size; j++) {
                                JSONObject imageJObj = imagesJArr.getJSONObject(j);
                                String survey_detail_id = imageJObj.getString("survey_detail_id");
                                String survey_type = imageJObj.getString("survey_type");
                                String survey_no = imageJObj.getString("survey_no");
                                String survey_title = imageJObj.getString("survey_title");
                                //String survey_picture = imageJObj.getString("survey_picture");
                                String survey_choice = imageJObj.getString("survey_choice");

                                SurveyDetail surveyDetail = new SurveyDetail();
                                surveyDetail.setId(survey_detail_id);
                                surveyDetail.setType(survey_type);
                                surveyDetail.setNo(survey_no);
                                surveyDetail.setTitle(survey_title);
                                //surveyDetail.setPicture(picList);
                                if(survey_type.equals("1")) {
                                    JSONArray choiceJArr = imageJObj.getJSONArray("survey_choice");
                                    for (int m = 0; m < choiceJArr.length(); m++) {
                                        JSONObject choiceJObj = choiceJArr.getJSONObject(m);
                                        String choiceId = choiceJObj.getString("id");
                                        String choiceText = choiceJObj.getString("text");

                                        //  Add to Choice
                                        Choice choice = new Choice();
                                        choice.setChoiceId(choiceId);
                                        choice.setChoiceText(choiceText);
                                        surveyDetail.addChoice(choice);
                                    }
                                }
                                    // Get choice
                                //JSONArray choiceJArr = imageJObj.getJSONArray("survey_picture");
                                //ArrayList<Image> picList = new ArrayList<Image>();
                                //if (picJArr.length() != 0) {
                                //for (int k = 0; k < choiceJArr.length(); k++) {
                                //    String survey_choices = choiceJArr.getString(j);
                                //    Log.d("choiceList", survey_choices);
                                //    String choice = survey_choices; //new Image(survey_picture);
                                //    surveyDetail.addChoice(choice);
                                //}

                                // Get image
                                JSONArray picJArr = imageJObj.getJSONArray("survey_picture");
                                ArrayList<Image> picList = new ArrayList<Image>();
                                //if (picJArr.length() != 0) {
                                for (int l = 0; l < picJArr.length(); l++) {
                                    String survey_picture = picJArr.getString(l);
                                    Image image = new Image(survey_picture);
                                    surveyDetail.addImage(image);
                                }
                                //Log.d("survey_picture", surveyDetail.getPicture().toString());
                                //}

                                surveyDetailAdapter.addItem(surveyDetail);
                            }
                        }
                       surveyDetailAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLostConnection() {
                Log.d("onLostConnection", "onLostConnection");
                requestSurveyDetail();
            }

            @Override
            public void onUnreachHost() {
                Log.d("onUnreachHost", "onUnreachHost");
                requestSurveyDetail();
            }
        });
        connection.execute();
    }
}
