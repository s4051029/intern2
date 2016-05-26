package com.mirrorchannelth.internship.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.mirrorchannelth.internship.R;
import com.mirrorchannelth.internship.config.Constant;
import com.mirrorchannelth.internship.dialog.StudentClusterDialog;
import com.mirrorchannelth.internship.helper.MultiDrawable;
import com.mirrorchannelth.internship.model.Student;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rooney on 4/12/2016.
 */
public class StudentMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener
        , ClusterManager.OnClusterClickListener<StudentMapFragment.AppClusterItem>
        , ClusterManager.OnClusterItemClickListener<StudentMapFragment.AppClusterItem> {
    private View rootView = null;
    private GoogleMap map = null;
    private ArrayList<AppClusterItem> clusterItemsList = null;
    private final int kMapZoomLevel = Constant.MAP_ZOOM;
    private ArrayList<Student> studentList = null;

    private ClusterManager<AppClusterItem> mClusterManager = null;

    private ImageLoader imageLoaderProfile = null;
    public DisplayImageOptions options = null;

    private SeekBar seekBar = null;
    private TextView progressLabel = null;
    private int progress = 1;
    private double lat = 0, lng = 0;
    private int mapZoom = Constant.MAP_ZOOM;
    private int position = 0;

    private boolean isFragmentCreated = false;

    private OnFragmentActionListener onFragmentActionListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) {
            ViewGroup viewGroup = (ViewGroup) rootView.getParent();
            if (viewGroup != null) {
                viewGroup.removeView(rootView);
            }
        }
        try {
            rootView = inflater.inflate(R.layout.fragment_student_map, container, false);
        } catch (InflateException ex) {
            //ex.printStackTrace();
        } finally {
            initToolbar();
            seekBar = (SeekBar) rootView.findViewById(R.id.seekBar);
            progressLabel = (TextView) rootView.findViewById(R.id.progressLabel);
            setSeekBarListeners();
            seekBar.setProgress(progress - 1);
            seekBar.setMax(Constant.MAP_DISTANCE - 1);
            // /setSeekBarListeners();
            configImageCache();
            initImageCache();
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }
        return rootView;
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        TextView textTitle = (TextView) rootView.findViewById(R.id.toolbar_title);
        textTitle.setText(getString(R.string.student_map_title));
        //((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_toolbar_logo);
    }

    private void initImageCache() {
        imageLoaderProfile = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .postProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bmp) {
                        return Bitmap.createScaledBitmap(bmp, 150, 150, false);
                    }
                })
                .build();
    }

    private void configImageCache() {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (imageLoader.isInited()) {
            imageLoader.destroy();
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getActivity())
                .memoryCacheSizePercentage(13)
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                //.writeDebugLogs()
                .defaultDisplayImageOptions(options)
                .build();
        imageLoader.init(config);
    }

    private void setSeekBarListeners() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue + 1;
                progressLabel.setText(getString(R.string.student_map_distance_km, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progressLabel.setText(getString(R.string.student_map_distance_km, progress));

                //  Save distance to search data
                //SearchPreference searchPreference = SearchPreference.getInstance(getActivity());
                //searchPreference.setValueInt(SearchPreference.DISTANCE, progress);

                //  Refresh map & list
                //onFragmentActionListener.onRefresh();
            }
        });
    }

    public void setOnFragmentActionListener(OnFragmentActionListener onFragmentActionListener) {
        this.onFragmentActionListener = onFragmentActionListener;
    }

    public interface OnFragmentActionListener {
        public void onRefresh();
        //public void onRemoveSeeker(Seeker seeker);
    }

    /*private void requestMap() {
        LatLng point = new LatLng(lat, lng);
        mapZoom = Constant.MAP_ZOOM - (progress / 4);
        mapZoom += 1;
        if (progress == 20) {
            mapZoom -= 1;
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, mapZoom ));


        //	Define request data
        String url = WebAPI.URL;
        //String employerId = appPreference.getValueString(AppPreference.USER_ID);
        //String language = appPreference.getValueString(AppPreference.LANGUAGE);

        //	Start request
        Connection connection = new Connection(url);
        connection.setDecrypt(false);
        //connection.addPostData("language", language);
        //connection.addPostData("employerId", employerId);
        connection.addPostData("distance", Integer.toString(progress));
        connection.addPostData("lat", Double.toString(lat));
        connection.addPostData("lng", Double.toString(lng));
        //connection.addPostData("tags", tags);
        connection.setOnConnectionCallBackListener(new Connection.OnConnectionCallBackListener() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject resultJObj = new JSONObject(result);
                    if (seekerList == null) {
                        //seekerList = new ArrayList<>();
                        clusterItemsList = new ArrayList<>();
                    } else {
                        map.clear();
                        //seekerList.clear();
                        clusterItemsList.clear();
                        mClusterManager.clearItems();
                        //createUserMarkerWithRadius();
                    }
                    JSONArray resultsJArr = resultJObj.getJSONArray("result");
                    int size = resultsJArr.length();
                    for (int i = 0; i < size; i++) {
                        JSONObject menuJObj = resultsJArr.getJSONObject(i);
                        String id = menuJObj.getString("id");
                        String name = menuJObj.getString("name");
                        String subtype = menuJObj.getString("subtype");
                        String gender = menuJObj.getString("gender");
                        String education = menuJObj.getString("education");
                        String position = menuJObj.getString("position");
                        int age = menuJObj.getInt("age");
                        int salary = menuJObj.getInt("salary");
                        String experience = menuJObj.getString("experience");
                        String description = menuJObj.getString("description");
                        String profileImageUrl = menuJObj.getString("profile_url");
                        String coverImageUrl = menuJObj.getString("cover_url");
                        String videoUrl = menuJObj.getString("video_url");
                        double lng = menuJObj.getDouble("lng");
                        double lat = menuJObj.getDouble("lat");
                        double distance = menuJObj.getDouble("distance");
                        String address = menuJObj.getString("address");
                        String tags = menuJObj.getString("tags");
                        String actualAddress = menuJObj.getString("actualAddress");

                        String roleTags = menuJObj.getString("roleTags");
                        String benefitTags = menuJObj.getString("benefitTags");
                        String cultureTags = menuJObj.getString("cultureTags");
                        String managementTags = menuJObj.getString("managementTags");
                        String interestTags = menuJObj.getString("interestTags");

                        String facebookId = menuJObj.getString("facebook");
                        String lineId = menuJObj.getString("line");
                        String phone = menuJObj.getString("phone");

                        //  Create instance
                        //seekerList.add(seeker);
                    }
                    redrawMarker();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLostConnection() {
                //Snackbar.make(coordinatorLayout, R.string.network_lost_connection, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onUnreachHost() {
            }
        });
        connection.execute();
    } */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setOnMarkerDragListener(this);
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        lat = 13.784829;
        lng = 100.570544;
        LatLng point = new LatLng(lat, lng);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, kMapZoomLevel));

        //  Set up cluster
        setUpClustering();

        //  Create user's marker
        createUserMarkerWithRadius();

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        if (!isFragmentCreated) {
            isFragmentCreated = true;
            clusterItemsList = new ArrayList<>();
            redrawMarker();
        }
    }

    private void setUpClustering() {
        // Initialize the manager with the context and the map.
        mClusterManager = new ClusterManager<>(getActivity(), map);
        mClusterManager.setRenderer(new PersonRenderer());
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);

        // Point the map's listeners at the listeners implemented by the cluster manager.
        map.setOnCameraChangeListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
    }

    @Override
    public boolean onClusterClick(Cluster<AppClusterItem> cluster) {
        //float zoom = map.getCameraPosition().zoom;
        //if (zoom == map.getMaxZoomLevel()) {
        ArrayList<Student> clusterSeekerList = new ArrayList<>();
        int size = cluster.getSize();
        List<AppClusterItem> clusterList = (List<AppClusterItem>) cluster.getItems();
        for (int i = 0; i < size; i++) {
            AppClusterItem item = clusterList.get(i);
            clusterSeekerList.add(studentList.get(item.getIndex()));
        }

        //  Open dialog
        StudentClusterDialog studentClusterDialog = new StudentClusterDialog();
        studentClusterDialog.setStudentList(clusterSeekerList);
        studentClusterDialog.setOnClusterActionListener(new StudentClusterDialog.OnClusterActionListener() {
            @Override
            public void onClusterAction(Student student) {
                position = studentList.indexOf(student);
                //changeToDetailSeekerActivity();
            }
        });
        studentClusterDialog.show(getFragmentManager(), StudentClusterDialog.TAG);
        //}
        return true;
    }

    @Override
    public boolean onClusterItemClick(AppClusterItem appClusterItem) {
        onClusterMarkerClick(appClusterItem);
        return true;
    }

    private boolean onClusterMarkerClick(AppClusterItem appClusterItem) {
        int index = clusterItemsList.indexOf(appClusterItem);
        if (index != -1) {
            position = index;
            //changeToDetailSeekerActivity();
        }
        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        //  Get new coordinate from marker
        LatLng point = marker.getPosition();
        lat = point.latitude;
        lng = point.longitude;

        //  Clear map & add all marker
        redrawMarker();
    }

    private void redrawMarker() {
        if (2 > 0) {
            new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] params) {
                    try {
                        int size = 2; //studentList.size();
                        for (int i = 0; i < size; i++) {
                            //final Student student = studentList.get(i);

                            //  Get imageProfile
                            Bitmap profileBitmap = imageLoaderProfile.loadImageSync("http://www.tanteifile.com/diary/2005/08/21_01/image/03.jpg", options);

                            // Image Circle
                            final Bitmap circleBitmap = getRoundedRectBitmap(profileBitmap);

                            if (map != null) {
                                //  Create marker
                                AppClusterItem offsetItem = new AppClusterItem(13.784829 + Double.valueOf(i), 100.570544 - Double.valueOf(i), circleBitmap, i);
                                mClusterManager.addItem(offsetItem);
                                clusterItemsList.add(offsetItem);
                            }
                        }
                        createUserMarkerWithRadius();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    mClusterManager.cluster();
                }
            }.execute(null, null, null);
        } else {
            createUserMarkerWithRadius();
        }
    }

    private Bitmap getRoundedRectBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(150,
                150, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    private void createUserMarkerWithRadius() {
        //  Create user's marker
        final LatLng point = new LatLng(lat, lng);
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                try {
                    map.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_marker))
                            .position(point)
                            .draggable(true));

                    CircleOptions circleOptions = new CircleOptions()
                            .center(new LatLng(lat, lng))
                            .radius(progress * 1000)
                            .strokeColor(Color.TRANSPARENT)
                            .fillColor(getResources().getColor(R.color.student_map_radius)); // In meters
                    map.addCircle(circleOptions);

                    map.animateCamera(CameraUpdateFactory.zoomTo(map.getCameraPosition().zoom), 500, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    class AppClusterItem implements ClusterItem {

        private final LatLng mPosition;
        private final Bitmap profilePhoto;
        private final int index;

        public AppClusterItem(double latitude, double longitude, Bitmap pictureResource, int index) {
            mPosition = new LatLng(latitude, longitude);
            profilePhoto = pictureResource;
            this.index = index;
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }

        public int getIndex() {
            return index;
        }
    }

    private class PersonRenderer extends DefaultClusterRenderer<AppClusterItem> {
        private final IconGenerator mIconGenerator = new IconGenerator(getActivity());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getActivity());
        private final ImageView mImageView;
        private final int mDimension;

        public PersonRenderer() {
            super(getActivity(), map, mClusterManager);

            View multiProfile = getActivity().getLayoutInflater().inflate(R.layout.item_cluster_marker, null);
            mClusterIconGenerator.setContentView(multiProfile);

            mImageView = new ImageView(getActivity().getApplicationContext());
            mDimension = (int) getResources().getDimension(R.dimen.student_map_cluster_item_width);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
            int padding = (int) getResources().getDimension(R.dimen.student_map_cluster_item_text_padding);
            mImageView.setPadding(padding, padding, padding, padding);
            mIconGenerator.setContentView(mImageView);
        }

        @Override
        protected void onBeforeClusterItemRendered(AppClusterItem person, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
            mImageView.setImageBitmap((person.profilePhoto));
            Bitmap icon = mIconGenerator.makeIcon();
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title("");
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<AppClusterItem> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
            List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
            int width = mDimension;
            int height = mDimension;

            for (AppClusterItem p : cluster.getItems()) {
                //  Draw 4 at most.
                if (profilePhotos.size() == 1) break;
                Drawable drawable = new BitmapDrawable(getResources(), p.profilePhoto);
                drawable.setBounds(0, 0, width, height);
                profilePhotos.add(drawable);
            }
            MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
            multiDrawable.setBounds(0, 0, width, height);

            String size = null;
            if (cluster.getSize() > 1000) {
                size = (cluster.getSize() / 1000) + "K";
            } else {
                size = String.valueOf(cluster.getSize());
            }

            Bitmap icon = mClusterIconGenerator.makeIcon(size);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }
}
