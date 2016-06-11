package com.mirrorchannelth.internship.fragment;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mirrorchannelth.internship.R;
import com.mirrorchannelth.internship.adapter.GalleryRecyclerViewAdapter;
import com.mirrorchannelth.internship.adapter.GalleryItemDecoration;
import com.mirrorchannelth.internship.config.WebAPI;
import com.mirrorchannelth.internship.model.Image;
import com.mirrorchannelth.internship.model.ShareData;
import com.mirrorchannelth.internship.net.Connection;
import com.mirrorchannelth.internship.service.ServiceDao;
import com.mirrorchannelth.internship.util.DateUtil;
import com.mirrorchannelth.internship.util.FileUtil;
import com.mirrorchannelth.internship.util.FormValidation;
import com.mirrorchannelth.internship.util.WindowsUtil;
import com.mirrorchannelth.internship.view.DatePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddActivityFragment extends Fragment implements View.OnClickListener, Connection.OnConnectionCallBackListener {

    private RecyclerView galleryRecyclerView;
    protected RecyclerView.LayoutManager layoutManager;
    private GalleryRecyclerViewAdapter adapter;
    private TextView toolbarTitleTextview;
    private ImageView rightMenuImageview;
    private FrameLayout cameraLayout;
    private FrameLayout imageLayout;
    private FrameLayout videoLayout;
    private EditText titleactivityEditText;
    private TextView dateActivityTextview;
    private EditText detailActivityEditText;
    private Button saveButton;
    private ProgressBar progressbar;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_SELECT_IMAGE = 2;
    static final int REQUEST_VIDEO_CAPTURE = 3;
    private Uri imageUri;
    private ServiceDao serviceDao;
    private List<Image> imageList;
    private String imageUrlTemp = null;
    private LinearLayout mainContentLayout;
    private static final int REQUEST_STORAGE_PERMISSION_CAMERA= 1;
    private static final int REQUEST_STORAGE_PERMISSION_VIDEO= 2;
    private static final int span = 3;


    public AddActivityFragment() {
        // Required empty public constructor
    }
    public static AddActivityFragment newInstance(){
        AddActivityFragment addActivityFragment = new AddActivityFragment();
        return addActivityFragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_add_activity, container, false);
        initInstance(rootview);
        return rootview;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        serviceDao = new ServiceDao(WebAPI.URL);
        if(savedInstanceState != null) {
            imageList = savedInstanceState.getParcelableArrayList("image");
            if(imageList != null) {
                adapter.setImages(imageList);
                adapter.notifyDataSetChanged();

            }

        }
    }

    private void initInstance(View rootview) {
        toolbarTitleTextview = (TextView) rootview.findViewById(R.id.toolbar_title);
        rightMenuImageview = (ImageView) rootview.findViewById(R.id.rightMenu);
        cameraLayout = (FrameLayout) rootview.findViewById(R.id.cameraLayout);
        imageLayout = (FrameLayout) rootview.findViewById(R.id.imageLayout);
        videoLayout = (FrameLayout) rootview.findViewById(R.id.videoLayout);
        mainContentLayout = (LinearLayout) rootview.findViewById(R.id.mainContent);

        galleryRecyclerView = (RecyclerView) rootview.findViewById(R.id.galleryRecyclerView);
        layoutManager = new GridLayoutManager(getActivity(),span);
        galleryRecyclerView.setLayoutManager(layoutManager);
        imageList = new ArrayList<Image>();
        adapter = new GalleryRecyclerViewAdapter(getActivity(), imageList);
        galleryRecyclerView.setAdapter(adapter);
        galleryRecyclerView.addItemDecoration(new GalleryItemDecoration(3));

        titleactivityEditText = (EditText) rootview.findViewById(R.id.activityTitleEditText);

        dateActivityTextview = (TextView) rootview.findViewById(R.id.activityDateTextview);
        final Calendar c = GregorianCalendar.getInstance(new Locale("th_TH"));

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String currentDate = String.format("%d/%d/%d", day, month, year+543);
        dateActivityTextview.setText(currentDate);
        dateActivityTextview.setOnClickListener(this);

        detailActivityEditText = (EditText) rootview.findViewById(R.id.activityDetailEditText);
        saveButton = (Button) rootview.findViewById(R.id.saveButton);
        progressbar = (ProgressBar) rootview.findViewById(R.id.progressBar);
        saveButton.setOnClickListener(this);
        cameraLayout.setOnClickListener(this);
        imageLayout.setOnClickListener(this);
        videoLayout.setOnClickListener(this);

        rightMenuImageview.setImageResource(R.drawable.ic_history_white_24dp);
        toolbarTitleTextview.setText(R.string.add_activity_toolbar_title);
        rightMenuImageview.setOnClickListener(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("image", (ArrayList<? extends Parcelable>) imageList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rightMenu:
                closeFragment();
                break;
            case R.id.cameraLayout:
                openCamera();
                break;
            case R.id.imageLayout:
                openGallery();
                break;
            case R.id.videoLayout:
                openVideoCam();
                break;
            case R.id.saveButton:
                addActivity();
                break;
            case R.id.activityDateTextview:
                openDateDialog();
                break;
        }
    }

    private void openDateDialog() {
        android.support.v4.app.DialogFragment dateDialog = DatePickerFragment.getInstance(getActivity(), dateActivityTextview);
        dateDialog.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    private void addActivity() {

        String title = titleactivityEditText.getText().toString();
        if (FormValidation.isEmpty(titleactivityEditText, getActivity())) return;
        mainContentLayout.setVisibility(View.GONE);
        progressbar.setVisibility(View.VISIBLE);
        String detail = detailActivityEditText.getText().toString();
        String date = dateActivityTextview.getText().toString();
        String oldateFormat = "dd/mm/yyyy";
        String newFormatDate = "yyyy-mm-dd";
        date = DateUtil.changeFormatDate(oldateFormat, newFormatDate, date);
        serviceDao.addActivity(ShareData.getUserProfile(), title, date, detail, imageList, this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   openCamera();

                }
                break;
            case REQUEST_STORAGE_PERMISSION_VIDEO:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openVideoCam();
                }

        }
    }
    private void openVideoCam() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION_CAMERA);
            return ;
        }
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (takeVideoIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_SELECT_IMAGE);

    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION_CAMERA);
            return ;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            File imageFile = null;
            try {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "PIC_" + timeStamp + "_";
                File storageDir = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                imageFile = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".jpg",         /* suffix */
                        storageDir      /* directory */
                );
                imageUrlTemp = imageFile.getAbsolutePath();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK) {
                    setImageToImageToGallery();
                }
                break;
            case REQUEST_SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    setImageToGallery(data);

                }
                break;
            case REQUEST_VIDEO_CAPTURE:
                if(resultCode == Activity.RESULT_OK) {
                    Uri vid = data.getData();
                    Image videoImage = new Image(FileUtil.getRealPathFromURI(vid, getActivity()));
                    videoImage.setMediaType("video");
                    imageList.add(videoImage);
                    adapter.notifyDataSetChanged();
                }
        }
    }
    private void setImageToGallery(Intent data) {
        if (data != null) {
            ClipData clipdata = data.getClipData();
            Image image = null;
            if(clipdata != null) {
                for (int i = 0; i < clipdata.getItemCount(); i++) {
                    ClipData.Item item = clipdata.getItemAt(i);
                    Uri uri = item.getUri();
                    image = new Image(FileUtil.getRealPathFromURI(uri, getActivity()));
                    image.setProtocol("file://");
                    imageList.add(image);
                }
            } else {
                Uri uri = data.getData();
                image = new Image(FileUtil.getRealPathFromURI(uri, getActivity()));
                image.setProtocol("file://");
                imageList.add(image);
            }
            adapter.notifyDataSetChanged();

        }
    }

    private void setImageToImageToGallery() {
        Image image = new Image(imageUrlTemp);
        image.setProtocol("file://");
        imageList.add(image);
        adapter.notifyDataSetChanged();
    }
    private void closeFragment() {
        getActivity().getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.from_right, R.anim.to_left, R.anim.from_left, R.anim.to_right)
                .replace(R.id.fragmentContainer, ActivityHistoryFragment.newInstance())
                .addToBackStack("HistoryFragment")
                .commit();
    }

    @Override
    public void onSuccess(String result) {

        try {
            JSONObject response = new JSONObject(result);
            if(response.getString("error").equals("0")) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(0, 0, R.anim.from_left, R.anim.to_right)
                        .replace(R.id.fragmentContainer, ActivityHistoryFragment.newInstance())
                        .addToBackStack("HistoryFragment")
                        .commit();
                progressbar.setVisibility(View.GONE);
            } else {
                progressbar.setVisibility(View.GONE);
                mainContentLayout.setVisibility(View.VISIBLE);
                WindowsUtil.defaultAlertDialog(getString(R.string.default_dialog_header), getString(R.string.default_message_dialog), getString(R.string.default_label_dialog_button), getActivity());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onLostConnection() {
        progressbar.setVisibility(View.GONE);
        mainContentLayout.setVisibility(View.VISIBLE);
        WindowsUtil.defaultAlertDialog(getString(R.string.default_dialog_header), getString(R.string.default_message_dialog), getString(R.string.default_label_dialog_button), getActivity());
    }

    @Override
    public void onUnreachHost() {
        progressbar.setVisibility(View.GONE);
        mainContentLayout.setVisibility(View.VISIBLE);
        WindowsUtil.defaultAlertDialog(getString(R.string.default_dialog_header), getString(R.string.default_message_dialog), getString(R.string.default_label_dialog_button), getActivity());
    }
}
