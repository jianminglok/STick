/*
 * Created by Lok Jian Ming, with reference to HERE Europe B.V's and other miscellaneous sources on GitHub.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lokjianming.stick;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoBoundingBox;
import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.GeoPosition;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.PositioningManager;
import com.here.android.mpa.guidance.NavigationManager;
import com.here.android.mpa.guidance.VoiceCatalog;
import com.here.android.mpa.guidance.VoicePackage;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapFragment;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapRoute;
import com.here.android.mpa.routing.CoreRouter;
import com.here.android.mpa.routing.Maneuver;
import com.here.android.mpa.routing.Route;
import com.here.android.mpa.routing.RouteOptions;
import com.here.android.mpa.routing.RoutePlan;
import com.here.android.mpa.routing.RouteResult;
import com.here.android.mpa.routing.RouteWaypoint;
import com.here.android.mpa.routing.Router;
import com.here.android.mpa.routing.RoutingError;
import com.here.android.mpa.search.DiscoveryRequest;
import com.here.android.mpa.search.DiscoveryResult;
import com.here.android.mpa.search.DiscoveryResultPage;
import com.here.android.mpa.search.ErrorCode;
import com.here.android.mpa.search.PlaceLink;
import com.here.android.mpa.search.ResultListener;
import com.here.android.mpa.search.SearchRequest;
import com.mikepenz.fastadapter.commons.utils.RecyclerViewCacheUtil;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import net.gotev.speech.DelayedOperation;
import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Logger;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.TextToSpeechCallback;
import net.gotev.speech.ui.SpeechProgressView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import im.delight.android.location.SimpleLocation;
import lokjianming.stick.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SpeechDelegate {
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private MainActivity m_mapFragmentView;

    private BluetoothAdapter mBluetoothAdapter;
    private PositioningManager.OnPositionChangedListener positionListener;

    private int m_routeLength;

    private String m_proceedData;

    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;
    private SimpleLocation location;
    private String m_firstManeuverAudio;
    private String m_firstManeuverAudioFinal;
    public DiscoveryRequest discoveryRequest;
    public Integer[] arrayList;

    private Double d;

    public static List<DiscoveryResult> s_ResultList;

    private MapFragment m_mapFragment;
    private Map m_map;
    private Route m_route;
    private static Button m_placeDetailButton;
    private NavigationManager m_navigationManager;
    private static List<MapObject> m_mapObjectList = new ArrayList<>();
    private AudioManager audio;
    private TextToSpeech ttobj;
    private TextToSpeech ttobj2;
    Boolean m_boolean = false;
    private SpeechProgressView progress;
    private TextView origin;
    private FloatingActionButton searchrequestBtn;
    private DelayedOperation delayedStopListening;
    private boolean m_VoiceSet;
    private boolean m_VoiceSet2;
    private boolean m_VoiceSet3;
    private int m_voiceCount;
    private boolean m_LocationChoose;
    private int m_resultCount;
    private ArrayList<String> m_test;
    private String m_resultCountFinal;
    private String m_resultLetter;
    private int m_resultCount2;
    private boolean m_proceed;
    private MapMarker mapMarker;
    private MapMarker mapMarker2;
    private int m_testresult;
    private boolean m_LocationChoose2;
    private String m_LocationChooseDestination;
    private String m_LocationChooseVicinity;
    public Set<BluetoothDevice> pairedDevices;
    private Double m_SecondLargest;
    private Double m_ThirdLargest;
    private Double m_Largest;
    private Double m_Distance;
    private boolean m_LargeCount;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    private String m_nextTurn;
    private String m_nextRoadName;
    private String m_nextTurnString;
    private String m_nextPreposition;
    private String m_NextDistance;
    private String m_VoiceDestination;
    private String m_firstManeuver;
    private String newString;
    private Image img;

    private final static String TAG = "1";

    public GeoCoordinate geoCoordinate;

    public GeoCoordinate geoCoordinate1;

    public PositioningManager positioningManager;

    private Handler mHandler = new Handler();

    private MapRoute mapRoute = null;

    public Timer timer;

    public Timer timer2;
    ArrayList<String> results;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer drawer = null;

    public Boolean paused;

    private static final String VOICE_LANG_CODE = "en-US";
    private static final String VOICE_LANG_CODE_ALT = "es-ES";
    private static final String VOICE_MARC_CODE = "eng";

    public static final String PREF_KEY_FIRST_START = "lokjianming.stick.PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_INTRO = 1;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);

        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);

        if (firstStart) {
            requestPermissions();
            Intent intent = new Intent(this, Intro.class);
            startActivityForResult(intent, REQUEST_CODE_INTRO);

        } else {
            initMapFragment();
            downloadVoiceCatalog();
        }

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.black)
                .withSavedInstance(savedInstanceState)
                .build();


        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withSelectable(true),
                        new PrimaryDrawerItem().withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(2).withSelectable(true),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName("About this app").withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(3).withSelectable(true)
                )// add the items we want to use with our Drawer
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem

                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                drawer.setSelection(1, false);
                                intent = new Intent(MainActivity.this, MainActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                drawer.setSelection(2, false);
                                intent = new Intent(MainActivity.this, Settings.class);
                            } else if (drawerItem.getIdentifier() == 3) {
                                drawer.setSelection(3, false);
                                intent = new Intent(MainActivity.this, About.class);
                            }
                            if (intent != null) {
                                MainActivity.this.startActivity(intent);
                            }
                        }

                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //only set the active selection or active profile if we do not recreate the activity
        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 3 (About)
            drawer.setSelection(1, false);
        }

        //if you have many different types of DrawerItems you can magically pre-cache those items to get a better scroll performance
        //make sure to init the cache after the DrawerBuilder was created as this will first clear the cache to make sure no old elements are in
        //RecyclerViewCacheUtil.getInstance().withCacheSize(2).init(result);
        new RecyclerViewCacheUtil<IDrawerItem>().withCacheSize(2).apply(drawer.getRecyclerView(), drawer.getDrawerItems());

        TextView modelTextview = (TextView) findViewById(R.id.start);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, false)
                        .apply();
                initMapFragment();
                downloadVoiceCatalog();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, true)
                        .apply();
                //User cancelled the intro so we'll finish this activity too.
                finish();
            }
        }
    }

    /**
     * Only when the app's target SDK is 23 or higher, it requests each dangerous permissions it
     * needs when the app is running.
     */
    private void requestPermissions() {

        final List<String> requiredSDKPermissions = new ArrayList<String>();
        requiredSDKPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        requiredSDKPermissions.add(Manifest.permission.BLUETOOTH);
        requiredSDKPermissions.add(Manifest.permission.RECORD_AUDIO);
        requiredSDKPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        requiredSDKPermissions.add(Manifest.permission.INTERNET);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_WIFI_STATE);
        requiredSDKPermissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        requiredSDKPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        ActivityCompat.requestPermissions(this,
                requiredSDKPermissions.toArray(new String[requiredSDKPermissions.size()]),
                REQUEST_CODE_ASK_PERMISSIONS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS: {
                for (int index = 0; index < permissions.length; index++) {
                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
                        /**
                         * If the user turned down the permission request in the past and chose the
                         * Don't ask again option in the permission request system dialog.
                         */
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                                permissions[index])) {
                            Toast.makeText(this,
                                    "Required permission " + permissions[index] + " not granted. "
                                            + "Please go to settings and turn on for sample app",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(this,
                                    "Required permission " + permissions[index] + " not granted",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }

                /**
                 * All permission requests are being handled.Create map fragment view.Please note
                 * the HERE SDK requires all permissions defined above to operate properly.
                 */
                m_mapFragmentView = new MainActivity();
                break;

            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void onSpeakClick() {
        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        } else {

            try {


                Speech.getInstance().startListening(progress, MainActivity.this);

            } catch (SpeechRecognitionNotAvailable exc) {
                showSpeechNotSupportedDialog();

            } catch (GoogleVoiceTypingDisabledException exc) {
                showEnableGoogleVoiceTyping();
            }

        }
    }


    private void initMapFragment() {
        location = new SimpleLocation(this);

        try {
            findBT();
            openBT();
        }
        catch (IOException ex) {}
        m_LargeCount = false;
        m_voiceCount = 5;
        m_proceedData = "";
        m_VoiceSet = false;
        m_VoiceSet2 = false;
        m_VoiceSet3 = false;
        m_LocationChoose = false;
        m_LocationChoose2 = false;
        m_proceed = true;
        m_VoiceDestination = "";
        findViewById(R.id.assistant_background).setVisibility(View.INVISIBLE);

        progress = (SpeechProgressView) findViewById(R.id.progress);

        Speech.init(MainActivity.this, getPackageName());
        Logger.setLogLevel(Logger.LogLevel.DEBUG);

        int[] colors = {
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.darker_gray),
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.holo_orange_dark),
                ContextCompat.getColor(this, android.R.color.holo_red_dark)
        };
        progress.setColors(colors);
        /* Locate the mapFragment UI element */
        origin = (TextView) findViewById(R.id.start);

        m_mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapfragment);

        if (m_mapFragment != null) {
            /* Initialize the MapFragment, results will be given via the called back. */
            m_mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                    if (error == Error.NONE) {
                        m_map = m_mapFragment.getMap();

                        getPosition();

                        if(getIntent().getData() == null && pairedDevices.contains(mmDevice)) {
                            findViewById(R.id.assistant_background).setVisibility(View.VISIBLE);
                            Log.e(TAG, "this is null");

                            Speech.getInstance().say("Where would you like to go today?", new TextToSpeechCallback() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onCompleted() {
                                    try {
                                        Speech.getInstance().startListening(progress, MainActivity.this);
                                        Speech.getInstance().setStopListeningAfterInactivity(100000);
                                        Speech.getInstance().setTransitionMinimumDelay(2000);
                                    } catch (SpeechRecognitionNotAvailable exc) {
                                        showSpeechNotSupportedDialog();

                                    } catch (GoogleVoiceTypingDisabledException exc) {
                                        showEnableGoogleVoiceTyping();
                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            });
                        } else {
                            Speech.getInstance().say("Please pair your phone with STick first", new TextToSpeechCallback() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onCompleted() {
                                    final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                    ComponentName cn = new ComponentName("com.android.settings",
                                            "com.android.settings.bluetooth.BluetoothSettings");
                                    intent.setComponent(cn);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity( intent);
                                }

                                @Override
                                public void onError() {

                                }
                            });
                        }

                        mapMarker = new MapMarker();
                        mapMarker2 = new MapMarker();

                        img = new Image();
                        try {
                            img.setImageResource(R.drawable.marker);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        mapMarker.setIcon(img);
                        mapMarker2.setIcon(img);

                        if (geoCoordinate1 != null) {

                            m_map.setCenter(geoCoordinate1, Map.Animation.LINEAR);
                            Log.e(TAG,geoCoordinate1.toString());
                            Log.e(TAG, "set");
                        } else {
                            m_map.setCenter(new GeoCoordinate(5.9804, 116.0735),
                                    Map.Animation.NONE);

                            m_map.setZoomLevel(13.2);
                        }

                        /*
                         * Get the NavigationManager instance.It is responsible for providing voice
                         * and visual instructions while driving and walking
                         */
                        m_navigationManager = NavigationManager.getInstance();

                        positioningManager = PositioningManager.getInstance();

                        paused = false;

                        if (geoCoordinate1 == null) {

                            getPosition();

                        }
                    } else {
                        Toast.makeText(MainActivity.this,
                                "ERROR: Cannot initialize Map with error " + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


    // Resume positioning listener on wake up
    @Override
    public void onResume() {
        super.onResume();
        paused = false;
        if (positioningManager != null) {
            positioningManager.start(
                    PositioningManager.LocationMethod.GPS_NETWORK);
            if (positioningManager.hasValidPosition(PositioningManager.LocationMethod.GPS_NETWORK)) {
                Log.e(TAG, "still got position");
            } else {
                getPosition();
            }
        }
    }

    // To pause positioning listener
    @Override
    public void onPause() {
        if (positioningManager != null) {
            positioningManager.stop();
        } else {
            Log.e(TAG, "This is what that's not working");
        }
        super.onPause();
        paused = true;
    }

    private void searchDestination() {
        if (getIntent().getData() != null) {
            Uri intentUri = getIntent().getData();
            String paramValue = intentUri.toString();

            newString = intentUri.getQueryParameter("daddr");
            Log.e(TAG, newString);
            Log.e(TAG, "param value 2 is " + newString);
            if(newString.matches(".*[a-zA-Z]+.*")) {
                Log.e(TAG, "Search started");
                SearchRequest searchRequest2 = new SearchRequest(newString).setSearchCenter(geoCoordinate1);
                searchRequest2.setCollectionSize(3);
                searchRequest2.execute(new SearchRequestListener());
            } else {
                Log.e(TAG, "no search started");
                String LatLng[] = newString.split(",");
                double latitude = Double.parseDouble(LatLng[0]);
                double longitude = Double.parseDouble(LatLng[1]);
                geoCoordinate = new GeoCoordinate(latitude, longitude);
                if (geoCoordinate1 != null) {
                    Log.e(TAG, "Hey, we just got location 2");
                    m_map.getPositionIndicator().setVisible(false);
                    mapMarker2.setCoordinate(geoCoordinate1);
                    m_map.addMapObject(mapMarker2);
                    m_mapObjectList.add(mapMarker2);
                    m_map.setCenter(geoCoordinate1, Map.Animation.LINEAR);
                    m_map.setZoomLevel(13.2, Map.Animation.LINEAR);
                    getDirections();
                    if (geoCoordinate != null) {
                        mapMarker.setCoordinate(geoCoordinate);
                        m_map.addMapObject(mapMarker);
                        m_mapObjectList.add(mapMarker);
                    } else {
                        Log.e(TAG, "error geocoordinate 2");
                    }

                }
            }


        }
    }

    private void getPosition() {

        // Register positioning listener
        positionListener = new PositioningManager.OnPositionChangedListener() {

            @Override
            public void onPositionUpdated(PositioningManager.LocationMethod method, GeoPosition position, boolean isMapMatched) {
                // set the center only when the app is in the foreground
                // to reduce CPU consumption
                if (!paused) {
                    m_map.setCenter(position.getCoordinate(), Map.Animation.NONE);
                    Log.e(TAG,"Hey, we just got position");
                    geoCoordinate1 = position.getCoordinate();
                    if(geoCoordinate1 == null) {
                        getPosition();
                    }
                    // Display position indicator
                    m_map.getPositionIndicator().setVisible(true);
                    if(m_proceed) {
                        searchDestination();
                        m_proceed = false;
                    }
                }
            }
            @Override
            public void onPositionFixChanged(PositioningManager.LocationMethod method, PositioningManager.LocationStatus status) {

            }
        };

        PositioningManager.getInstance().addListener(new WeakReference<PositioningManager.OnPositionChangedListener>(positionListener));

        PositioningManager.getInstance().start(PositioningManager.LocationMethod.GPS_NETWORK);
    }

    public void cleanMap() {
        if (!m_mapObjectList.isEmpty()) {
            m_map.removeMapObjects(m_mapObjectList);
            m_mapObjectList.clear();
        }
    }

    private class SearchRequestListener implements ResultListener<DiscoveryResultPage> {
        @Override
        public void onCompleted(DiscoveryResultPage discoveryResultPage, ErrorCode errorCode) {
            if (errorCode == ErrorCode.NONE) {

                /*
                 * The result is a DiscoveryResultPage object which represents a paginated
                 * collection of items.The items can be either a PlaceLink or DiscoveryLink.The
                 * PlaceLink can be used to retrieve place details by firing another
                 * PlaceRequest,while the DiscoveryLink is designed to be used to fire another
                 * DiscoveryRequest to obtain more refined results.
                 */
                s_ResultList = discoveryResultPage.getItems();
                Iterator<DiscoveryResult> i = s_ResultList.iterator();
                List<Integer> s_DistanceList = new ArrayList<>();
                Iterator<Integer> ii = s_DistanceList.iterator();
                for (Iterator<DiscoveryResult> iter = s_ResultList.iterator(); iter.hasNext();) {

                    /*
                     * Add a marker for each result of PlaceLink type.For best usability, map can be
                     * also adjusted to display all markers.This can be done by merging the bounding
                     * box of each result and then zoom the map to the merged one.
                     */
                    DiscoveryResult item = iter.next();

                    if (item.getResultType() == DiscoveryResult.ResultType.PLACE) {

                        if(m_LocationChoose2) {
                            PlaceLink placeLink = (PlaceLink) item;
                            if(placeLink.getTitle().equals(m_LocationChooseDestination) && placeLink.getVicinity().contains(m_LocationChooseVicinity)) {

                                addMarkerAtPlace(placeLink);
                                geoCoordinate = placeLink.getPosition();

                                if (geoCoordinate != null) {
                                    Log.e(TAG, String.valueOf(geoCoordinate));
                                } else {
                                    Log.e(TAG, "error geocoordinate");
                                }
                            } else {
                                iter.remove();
                                Log.e(TAG, "the list size is " + s_ResultList.size());
                            }
                        } else {

                            PlaceLink placeLink = (PlaceLink) item;

                            s_DistanceList.add((int) placeLink.getDistance());

                            if(!iter.hasNext()) {
                                arrayList = getThreeLowest(s_DistanceList);
                            }

                            if(arrayList != null) {
                                for (Iterator<DiscoveryResult> iter2 = s_ResultList.iterator(); iter2.hasNext();) {

                                    DiscoveryResult item2 = iter2.next();

                                    PlaceLink placeLink2 = (PlaceLink) item2;

                                    d = placeLink2.getDistance();

                                    Log.e(TAG, "the distance list contains all " + s_DistanceList.toString());

                                    if (Arrays.asList(arrayList).contains(d.intValue()) && d.intValue() < 20000) {
                                        Log.e(TAG, item.getTitle());

                                        Log.e(TAG, "the distance list contains lowest three " + Arrays.toString(arrayList));

                                        Log.e(TAG, "The distance is " + d.intValue());

                                        Log.e(TAG, "The distance list contains " + s_DistanceList.size());
                                        Log.e(TAG, "the list size is " + s_ResultList.size());

                                        addMarkerAtPlace(placeLink2);

                                        geoCoordinate = placeLink2.getPosition();

                                        if (geoCoordinate != null) {
                                            Log.e(TAG, String.valueOf(geoCoordinate));
                                        } else {
                                            Log.e(TAG, "error geocoordinate");
                                        }
                                    } else {
                                        iter2.remove();
                                        Log.e(TAG, "the list size is " + s_ResultList.size());
                                    }
                                }
                            }

                        }


                    }
                }

            } else {
                Toast.makeText(MainActivity.this,
                        "ERROR:Discovery search request returned return error code+ " + errorCode,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private static Integer[] getThreeLowest(List<Integer> array) {
        Integer[] lowestValues = new Integer[3];
        Arrays.fill(lowestValues, Integer.MAX_VALUE);

        for(Integer n : array) {
            if(n < lowestValues[2]) {
                lowestValues[2] = n;
                Arrays.sort(lowestValues);
            }
        }
        return lowestValues;
    }

    private void addMarkerAtPlace(PlaceLink placeLink) {

        geoCoordinate = placeLink.getPosition();

        if (geoCoordinate1 != null) {
            m_map.getPositionIndicator().setVisible(false);
            mapMarker2.setCoordinate(geoCoordinate1);
            m_map.addMapObject(mapMarker2);
            m_mapObjectList.add(mapMarker2);
            m_map.setCenter(geoCoordinate1, Map.Animation.LINEAR);
            m_map.setZoomLevel(13.2, Map.Animation.LINEAR);

        } else {
            Log.e(TAG, "error2");
        }

        Log.e(TAG, "the value is " + String.valueOf(s_ResultList.size()));


        if (s_ResultList.size() == 1  || m_LocationChoose2) {
            Log.e(TAG, "the value is 1");
            getDirections();
        }

        if (s_ResultList.size() > 1 && !m_LocationChoose2) {
            findViewById(R.id.assistant_background).setVisibility(View.VISIBLE);
            m_LocationChoose = true;

            Speech.getInstance().say("Please chooose from the following options", new TextToSpeechCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onCompleted() {

                    m_resultLetter = "A";
                    m_resultCount = m_resultLetter.charAt(0);
                    m_resultCount2 = 0;

                    if (m_resultCount2 == 0) {
                        m_resultCountFinal = "first";
                    }
                    if (m_resultCount2 == 1) {
                        m_resultCountFinal = "second";
                    }
                    if (m_resultCount2 == 2) {
                        m_resultCountFinal = "third";
                    }

                    Speech.getInstance().say(m_resultCountFinal + "option " + "," + s_ResultList.get(m_resultCount2).getTitle() + "," + s_ResultList.get(m_resultCount2).getVicinity().replace("<br/>", ", "), new TextToSpeechCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onCompleted() {
                            m_resultLetter = String.valueOf((char) (m_resultCount + 1));
                            m_resultCount2 = m_resultCount2 + 1;

                            if (m_resultCount2 == 0) {
                                m_resultCountFinal = "first";
                            }
                            if (m_resultCount2 == 1) {
                                m_resultCountFinal = "second";
                            }
                            if (m_resultCount2 == 2) {
                                m_resultCountFinal = "third";
                            }

                            if (s_ResultList.get(m_resultCount2).getTitle() != null) {
                                readOutChoice(m_resultCount2);
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }


                @Override
                public void onError() {

                }
            });


        }

        m_map.addMapObject(mapMarker);
        m_mapObjectList.add(mapMarker);
    }

    public void readOutChoice(final int i) {

        Speech.getInstance().say(m_resultCountFinal + "option " + "," + s_ResultList.get(i).getTitle() + "," + s_ResultList.get(i).getVicinity().replace("<br/>", ", "), new TextToSpeechCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted() {
                m_resultCount = m_resultCount + 1;
                m_resultCount2 = m_resultCount2 + 1;

                if(m_resultCount2 == 0) {
                    m_resultCountFinal = "first";
                }
                if(m_resultCount2 == 1) {
                    m_resultCountFinal = "second";
                }
                if(m_resultCount2 == 2) {
                    m_resultCountFinal = "third";
                }

                m_resultLetter = String.valueOf( (char) (m_resultCount + 1));
                if (m_resultCount2 < s_ResultList.size()) {
                    readOutChoice(m_resultCount2);
                }
                if(m_resultCount2 == s_ResultList.size()) {
                    onRecordAudioPermissionGranted();
                }
            }

            @Override
            public void onError() {

            }
        });
    }

    // Functionality for taps of the "Get Directions" button
    public void getDirections() {
        if(geoCoordinate1 == null) {
            timer = new Timer();

            // This timer task will be executed every 1 sec.
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (geoCoordinate1 == null) {
                                getPosition();
                            } else {
                                timer.cancel();
                            }
                        }
                    });

                }
            }, 0, 1000);
        }
        if (geoCoordinate != null && geoCoordinate1 != null) {
            // 1. clear previous results
            if (m_map != null && mapRoute != null) {
                m_map.removeMapObject(mapRoute);
                mapRoute = null;
            }

            /* Initialize a CoreRouter */
            CoreRouter coreRouter = new CoreRouter();

            /* Initialize a RoutePlan */
            RoutePlan routePlan = new RoutePlan();

            RouteOptions routeOptions = new RouteOptions();
            routeOptions.setTransportMode(RouteOptions.TransportMode.PEDESTRIAN);
            routeOptions.setRouteType(RouteOptions.Type.FASTEST);
            routePlan.setRouteOptions(routeOptions);

            RouteWaypoint startPoint = new RouteWaypoint(new GeoCoordinate(geoCoordinate1));

            RouteWaypoint destination = new RouteWaypoint(new GeoCoordinate(geoCoordinate));

            /* Add both waypoints to the route plan */
            routePlan.addWaypoint(startPoint);
            routePlan.addWaypoint(destination);

            /* Trigger the route calculation,results will be called back via the listener */
            coreRouter.calculateRoute(routePlan,
                    new Router.Listener<List<RouteResult>, RoutingError>() {

                        @Override
                        public void onProgress(int i) {
                        /* The calculation progress can be retrieved in this callback. */
                        }

                        @Override
                        public void onCalculateRouteFinished(List<RouteResult> routeResults,
                                                             RoutingError routingError) {
                        /* Calculation is done.Let's handle the result */
                            if (routingError == RoutingError.NONE) {
                                if (routeResults.get(0).getRoute() != null) {

                                    m_route = routeResults.get(0).getRoute();
                                /* Create a MapRoute so that it can be placed on the map */
                                    MapRoute mapRoute = new MapRoute(routeResults.get(0).getRoute());

                                /* Show the maneuver number on top of the route */
                                    mapRoute.setManeuverNumberVisible(true);

                                /* Add the MapRoute to the map */
                                    m_map.addMapObject(mapRoute);

                                /*
                                 * We may also want to make sure the map view is orientated properly
                                 * so the entire route can be easily seen.
                                 */
                                    GeoBoundingBox m_geoBoundingBox = routeResults.get(0).getRoute().getBoundingBox();
                                    m_map.zoomTo(m_geoBoundingBox, Map.Animation.NONE,
                                            Map.MOVE_PRESERVE_ORIENTATION);

                                    m_firstManeuver = String.valueOf(m_route.getFirstManeuver().getTurn());
                                    m_nextRoadName = String.valueOf(m_route.getFirstManeuver().getRoadName());

                                    if(m_firstManeuver != null) {
                                        if(s_ResultList != null) {
                                            Log.e(TAG, s_ResultList.get(0).getTitle());
                                        }
                                        if (m_firstManeuver.contains("LEFT") || m_firstManeuver.contains("left")) {
                                            m_firstManeuverAudioFinal = "Turn left";
                                            if(!m_nextRoadName.isEmpty()) {
                                                m_firstManeuverAudioFinal = m_firstManeuverAudio + "into" + m_nextRoadName;
                                            }
                                        }
                                        if (m_firstManeuver.contains("RIGHT") || m_firstManeuver.contains("right")) {
                                            m_firstManeuverAudioFinal = "Turn right";
                                            if(!m_nextRoadName.isEmpty()) {
                                                m_firstManeuverAudioFinal = m_firstManeuverAudio + "into" + m_nextRoadName;
                                            }
                                        }
                                        if (m_firstManeuver.contains("UNDEFINED") || m_firstManeuver.contains("undefined")) {
                                            m_firstManeuverAudioFinal = "Make a u-turn";
                                            if(!m_nextRoadName.isEmpty()) {
                                                m_firstManeuverAudioFinal = m_firstManeuverAudio + "into" + m_nextRoadName;
                                            }
                                        }
                                        if (m_firstManeuver.contains("NO") || m_firstManeuver.contains("no")) {
                                            m_firstManeuverAudioFinal = "Go straight";
                                            if(!m_nextRoadName.isEmpty()) {
                                                m_firstManeuverAudioFinal = m_firstManeuverAudio + "on" + m_nextRoadName;
                                            }
                                        }

                                        Toast.makeText(MainActivity.this, "Nothing wrong with first maneuver", Toast.LENGTH_LONG).show();

                                        Speech.getInstance().say(m_firstManeuverAudioFinal, new TextToSpeechCallback() {
                                            @Override
                                            public void onStart() {

                                            }

                                            @Override
                                            public void onCompleted() {
                                                startNavigation(m_route);
                                            }

                                            @Override
                                            public void onError() {

                                            }
                                        });


                                        if(pairedDevices.contains(mmDevice)) {
                                            timer = new Timer();

                                            // This timer task will be executed every 1 sec.
                                            timer.schedule(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if(!m_proceedData.isEmpty() && m_proceedData.equals("ok") ) {
                                                                timer.cancel();
                                                            } else {
                                                                try {
                                                                    sendData(String.valueOf(m_route.getFirstManeuver().getAngle()));
                                                                    if (m_firstManeuver.contains("LEFT") || m_firstManeuver.contains("left")) {
                                                                        sendData("Lt");
                                                                    }
                                                                    if (m_firstManeuver.contains("RIGHT") || m_firstManeuver.contains("right")) {
                                                                        sendData("Rt");
                                                                    }
                                                                    if (m_firstManeuver.contains("UNDEFINED") || m_firstManeuver.contains("undefined")) {
                                                                        sendData("Rt");
                                                                    }
                                                                    if (m_firstManeuver.contains("NO") || m_firstManeuver.contains("no")) {
                                                                        sendData("FW");
                                                                    }
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                        }
                                                    });

                                                }
                                            }, 0, 1000);

                                        }
                                    } else {
                                        startNavigation(m_route);
                                        Toast.makeText(MainActivity.this, "First maneuver is undefined", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(MainActivity.this,
                                            "Error:route results returned is not valid",
                                            Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Error:route calculation returned error code: " + routingError,
                                        Toast.LENGTH_LONG).show();

                            }
                        }
                    });

        } else {
            Log.e(TAG, "null geoco");
        }
    }

    ;

    private void startNavigation(Route route) {
        if (m_map != null) {
        /* Display the position indicator on map */
            m_map.getPositionIndicator().setVisible(true);
        /* Configure Navigation manager to launch navigation on current map */
            m_navigationManager.setMap(m_map);
        /*
         * Start the turn-by-turn navigation.Please note if the transport mode of the passed-in
         * route is pedestrian, the NavigationManager automatically triggers the guidance which is
         * suitable for walking. Simulation and tracking modes can also be launched at this moment
         * by calling either simulate() or startTracking()
         */
            m_navigationManager.startNavigation(route);
            if(!String.valueOf(m_testresult).isEmpty()) {
                String newString = s_ResultList.get(m_testresult).getTitle();
            } else {
                String newString = s_ResultList.get(0).getTitle();
            }
            origin.setText("Selected Destination: " + newString);
            m_navigationManager.simulate(m_route, 20);
        /*
         * Set the map update mode to ROADVIEW.This will enable the automatic map movement based on
         * the current location.If user gestures are expected during the navigation, it's
         * recommended to set the map update mode to NONE first. Other supported update mode can be
         * found in HERE Android SDK API doc
         */
            m_navigationManager.setMapUpdateMode(NavigationManager.MapUpdateMode.ROADVIEW);

            m_navigationManager.setNaturalGuidanceMode(EnumSet.of(NavigationManager.NaturalGuidanceMode.TRAFFIC_LIGHT, NavigationManager.NaturalGuidanceMode.STOP_SIGN, NavigationManager.NaturalGuidanceMode.JUNCTION));

        /*
         * NavigationManager contains a number of listeners which we can use to monitor the
         * navigation status and getting relevant instructions.In this example, we will add 2
         * listeners for demo purpose,please refer to HERE Android SDK API documentation for details
         */
            addNavigationListeners();
        }
    }

    private void addNavigationListeners() {

        /*
         * Register a NavigationManagerEventListener to monitor the status change on
         * NavigationManager
         */

        m_navigationManager.addNavigationManagerEventListener(
                new WeakReference<NavigationManager.NavigationManagerEventListener>(
                        m_navigationManagerEventListener));

        /* Register a PositionListener to monitor the position updates */
        m_navigationManager.addPositionListener(
                new WeakReference<NavigationManager.PositionListener>(
                        m_positionListener));

        m_navigationManager.addNewInstructionEventListener(
                new WeakReference<NavigationManager.NewInstructionEventListener>(
                        m_instructionListener));

        m_navigationManager.addAudioFeedbackListener(
                new WeakReference<NavigationManager.AudioFeedbackListener>(
                        m_audioeventListener));
    }

    private NavigationManager.AudioFeedbackListener m_audioeventListener = new NavigationManager.AudioFeedbackListener() {
        @Override
        public void onAudioStart() {
            super.onAudioStart();
            ttobj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    // TODO Auto-generated method stub
                    if (status == TextToSpeech.SUCCESS) {
                        Locale locale = new Locale("en", "MY");
                        int result = ttobj.setLanguage(locale);

                        if (result == TextToSpeech.LANG_MISSING_DATA ||
                                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("error", "This Language is not supported");
                        }


                    } else
                        Log.e("error", "Initilization Failed!");
                }
            });

            ttobj2 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int status) {
                    // TODO Auto-generated method stub
                    if (status == TextToSpeech.SUCCESS) {
                        Locale locale = new Locale("id", "ID");
                        int result = ttobj2.setLanguage(locale);

                        if (result == TextToSpeech.LANG_MISSING_DATA ||
                                result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            Log.e("error", "This Language is not supported");
                        } else {
                            if (m_nextTurn != null & m_nextRoadName != null) {
                                if (m_nextTurn.contains("LEFT") || m_nextTurn.contains("left")) {
                                    m_nextTurnString = "Turn left";
                                    m_nextPreposition = "into";
                                }
                                if (m_nextTurn.contains("RIGHT") || m_nextTurn.contains("right")) {
                                    m_nextTurnString = "Turn right";
                                    m_nextPreposition = "into";
                                }
                                if (m_nextTurn.contains("UNDEFINED") || m_nextTurn.contains("undefined")) {
                                    m_nextTurnString = "Make a u-turn";
                                    m_nextPreposition = "into";
                                }
                                if (m_nextTurn.contains("NO") || m_nextTurn.contains("no")) {
                                    m_nextTurnString = "Continue going straight";
                                    m_nextPreposition = "on";
                                }
                                ttobj.speak(m_nextTurnString + m_nextPreposition, TextToSpeech.QUEUE_FLUSH, null);
                                ttobj2.speak(m_nextRoadName, TextToSpeech.QUEUE_FLUSH, null);
                                m_nextRoadName = null;
                                m_nextTurn = null;
                            }
                        }


                    } else
                        Log.e("error", "Initilization Failed!");
                }
            });
        }
    };

    private NavigationManager.PositionListener m_positionListener = new NavigationManager.PositionListener() {
        @Override
        public void onPositionUpdated(GeoPosition geoPosition) {
            /* Current position information can be retrieved in this callback */
            Log.e(TAG, String.valueOf(m_navigationManager.getNextManeuverDistance()));
        }
    };


    private NavigationManager.NavigationManagerEventListener m_navigationManagerEventListener = new NavigationManager.NavigationManagerEventListener() {
        @Override
        public void onRunningStateChanged() {
            Toast.makeText(MainActivity.this, "Running state changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNavigationModeChanged() {
            Toast.makeText(MainActivity.this, "Navigation mode changed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onEnded(NavigationManager.NavigationMode navigationMode) {
            Toast.makeText(MainActivity.this, navigationMode + " was ended", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMapUpdateModeChanged(NavigationManager.MapUpdateMode mapUpdateMode) {
            Toast.makeText(MainActivity.this, "Map update mode is changed to " + mapUpdateMode,
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRouteUpdated(Route route) {
            Toast.makeText(MainActivity.this, "Route updated", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCountryInfo(String s, String s1) {
            Toast.makeText(MainActivity.this, "Country info updated from " + s + " to " + s1,
                    Toast.LENGTH_SHORT).show();
        }
    };

    private NavigationManager.NewInstructionEventListener m_instructionListener = new NavigationManager.NewInstructionEventListener() {
        @Override
        public void onNewInstructionEvent() {

            final Maneuver maneuver = m_navigationManager.getNextManeuver();
            if (maneuver != null) {
                if (maneuver.getAction() == Maneuver.Action.END) {
                    timer = new Timer();

                    // This timer task will be executed every 1 sec.
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!m_proceedData.isEmpty() && m_proceedData.equals("ok")) {
                                        timer.cancel();
                                    } else {
                                        try {
                                            sendData("St");
                                            Toast.makeText(MainActivity.this, "Route completed", Toast.LENGTH_SHORT).show();
                                            ttobj.speak(String.valueOf(Maneuver.Action.END), TextToSpeech.QUEUE_FLUSH, null);
                                        } catch (IOException ex) {
                                            Toast.makeText(MainActivity.this, "Error sending data", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                        }
                    }, 0, 1000);
                } else {
                    Log.e(TAG, "The angle is " + String.valueOf(maneuver.getAngle()));
                    Log.e(TAG, String.valueOf(maneuver.getNextRoadName()));
                    Log.e(TAG, String.valueOf(maneuver.getDistanceToNextManeuver()));
                    Log.e(TAG, String.valueOf(maneuver.getTurn()));

                    m_nextRoadName = String.valueOf(maneuver.getNextRoadName());
                    m_nextTurn = String.valueOf(maneuver.getTurn());
                    m_NextDistance = String.valueOf(maneuver.getDistanceFromPreviousManeuver());
                    timer = new Timer();

                    // This timer task will be executed every 1 sec.
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!m_proceedData.isEmpty() && m_proceedData.equals("ok")) {
                                        if(pairedDevices.contains(mmDevice)) {
                                            try {

                                                sendData(String.valueOf(maneuver.getAngle()));

                                                if (m_nextTurn.contains("LEFT") || m_nextTurn.contains("left")) {
                                                    sendData("Lt");
                                                }
                                                if (m_nextTurn.contains("RIGHT") || m_nextTurn.contains("right")) {
                                                    sendData("Rt");
                                                }
                                                if (m_nextTurn.contains("UNDEFINED") || m_nextTurn.contains("undefined")) {
                                                    sendData("Rt");
                                                }
                                                if (m_nextTurn.contains("NO") || m_nextTurn.contains("no")) {
                                                    sendData("Fw");
                                                }
                                            } catch (IOException ex) {
                                                Toast.makeText(MainActivity.this, "Error sending data", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                }
                            });

                        }
                    }, 0, 1000);

                }


                //display current or next road information
                //display maneuver.getDistanceToNextManeuver()
            }
        }
    };

    private void downloadVoiceCatalog() {
        boolean result = VoiceCatalog.getInstance().downloadCatalog(new VoiceCatalog.OnDownloadDoneListener() {
            @Override
            public void onDownloadDone(VoiceCatalog.Error error) {
                //Toast.makeText(getApplicationContext(), "onDownloadDone: " + error.toString(), Toast.LENGTH_LONG).show();

                // Get the list of voice packages from the voice catalog list
                List<VoicePackage> voicePackages = VoiceCatalog.getInstance().getCatalogList();
                long id = -1;
                // select
                for (VoicePackage pacote : voicePackages) {
                    String language = pacote.getMarcCode();
                    if (language.compareToIgnoreCase("e") == 0) {
                        if (pacote.isTts()) {
                            id = pacote.getId();
                            break;
                        }
                    }
                }
                try {
                    if (!VoiceCatalog.getInstance().isLocalVoiceSkin(id)) {
                        final long finalId = id;
                        VoiceCatalog.getInstance().downloadVoice(id, new VoiceCatalog.OnDownloadDoneListener() {
                            @Override
                            public void onDownloadDone(VoiceCatalog.Error error) {
                                if (error == VoiceCatalog.Error.NONE) {
                                    //voice skin download successful

                                    // set the voice skin for use by navigation manager
                                    if (VoiceCatalog.getInstance().getLocalVoiceSkin(finalId) != null) {
                                        m_navigationManager.setVoiceSkin(VoiceCatalog.getInstance().getLocalVoiceSkin(finalId));
                                    } else {
                                        //Toast.makeText(mActivity.getApplicationContext(), "Navi manager set voice skin error.", Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    //Toast.makeText(mActivity.getApplicationContext(), "Voice skin download error.", Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    } else {
                        // set the voice skin for use by navigation manager
                        if (VoiceCatalog.getInstance().getLocalVoiceSkin(id) != null) {
                            m_navigationManager.setVoiceSkin(VoiceCatalog.getInstance().getLocalVoiceSkin(id));
                        } else {

                            //Toast.makeText(mActivity.getApplicationContext(), "Navi manager set voice skin error.", Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (Exception errordd) {

                }

            }
        });
    }

    private void onRecordAudioPermissionGranted() {
        try {
            Toast.makeText(MainActivity.this, "started", Toast.LENGTH_SHORT).show();
            Speech.getInstance().setStopListeningAfterInactivity(100000);
            Speech.getInstance().setTransitionMinimumDelay(2000);
            Speech.getInstance().startListening(progress, MainActivity.this);

        } catch (SpeechRecognitionNotAvailable exc) {
            showSpeechNotSupportedDialog();

        } catch (GoogleVoiceTypingDisabledException exc) {
            showEnableGoogleVoiceTyping();
        }
    }

    @Override
    public void onStartOfSpeech() {
        origin.setText("Waiting for input");
        Log.i("speech", "speech recognition is now active");
    }

    @Override
    public void onSpeechRmsChanged(float value) {
        Log.d(getClass().getSimpleName(), "Speech recognition rms is now " + value + "dB");
    }

    @Override
    public void onSpeechResult(final String result) {

        if (result.isEmpty()) {
            if (!m_VoiceSet2) {
                Speech.getInstance().say(getString(R.string.repeat), new TextToSpeechCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted() {
                        onRecordAudioPermissionGranted();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            if (m_VoiceSet2 && m_VoiceSet3) {

                Speech.getInstance().say(getString(R.string.repeat), new TextToSpeechCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted() {
                        onRecordAudioPermissionGranted();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            m_voiceCount = m_voiceCount - 1;
            if (m_voiceCount == 1) {
                Log.e(TAG, "Will repeat for " + m_voiceCount + " more time");
            }
            if (m_voiceCount > 1) {
                Log.e(TAG, "Will repeat for another " + m_voiceCount + " times");
            }


            if (m_voiceCount == 0) {
                if (m_VoiceSet2 && m_VoiceSet3) {
                    Speech.getInstance().say("Goodbye. Please open the app again if you wish to use it later.");
                    MainActivity.this.finish();
                    System.exit(0);
                }
                m_VoiceSet = true;
                m_VoiceSet2 = true;
                m_VoiceSet3 = true;
                m_voiceCount = 5;
                Log.e(TAG, "limit reached");
                Speech.getInstance().say("Would you like to continue?", new TextToSpeechCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted() {
                        onRecordAudioPermissionGranted();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            if (m_voiceCount == 2) {
                if (m_VoiceSet2 && m_VoiceSet3) {
                    Speech.getInstance().say("If you don't respond after another 2 tries, the app will close by itself.", new TextToSpeechCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onCompleted() {
                            onRecordAudioPermissionGranted();
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            }

        } else if (m_LocationChoose) {
            if (result.contains("First") || result.contains("first") || result.contains("Second") || result.contains("second") || result.contains("Third") || result.contains("third")) {

                findViewById(R.id.assistant_background).setVisibility(View.INVISIBLE);

                if(result.contains("First") || result.contains("first")) {
                    m_testresult = 0;
                }
                if(result.contains("Second") || result.contains("second")) {
                    m_testresult = 1;
                }
                if(result.contains("Third") || result.contains("third")) {
                    m_testresult = 2;
                }

                SearchRequest searchRequest2 = new SearchRequest(s_ResultList.get(m_testresult).getTitle()+" "+s_ResultList.get(m_testresult).getVicinity()).setSearchCenter(geoCoordinate1);
                searchRequest2.setCollectionSize(3);
                searchRequest2.execute(new SearchRequestListener());
                m_LocationChoose2 = true;
                if(m_LocationChoose2) {
                    m_LocationChooseDestination = s_ResultList.get(m_testresult).getTitle();
                    m_LocationChooseVicinity = s_ResultList.get(m_testresult).getVicinity();
                }
                m_LocationChoose = false;
            } else {
                m_voiceCount = 5;
                Speech.getInstance().say("Please choose between the first, second and third option", new TextToSpeechCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted() {
                        onRecordAudioPermissionGranted();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

        } else {
            if (m_VoiceDestination.equals("") && !result.equals("Yes") && !result.equals("No") && !m_VoiceSet2 && !m_VoiceSet3) {
                origin.setText("Selected Destination: " + result);
                m_VoiceDestination = result;
                if (!m_VoiceDestination.equals("")) {
                    Speech.getInstance().say("The destination you have selected is " + result + " ,is that correct?", new TextToSpeechCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onCompleted() {
                            onRecordAudioPermissionGranted();
                            m_VoiceSet = true;
                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            }

            if (result.equals("yes") && !m_VoiceDestination.equals("") && !m_VoiceSet2 || result.equals("Yes") && !m_VoiceDestination.equals("") && !m_VoiceSet2) {
                origin.setText("Choice: " + result);
                m_VoiceSet = false;
                m_VoiceSet3 = false;
                m_voiceCount = 5;
                Speech.getInstance().stopListening();
                findViewById(R.id.assistant_background).setVisibility(View.INVISIBLE);

                Log.e(TAG, "the search center is " + geoCoordinate1);


                SearchRequest searchRequest2 = new SearchRequest(m_VoiceDestination).setSearchCenter(geoCoordinate1);
                searchRequest2.execute(new SearchRequestListener());
            }
            if (result.equals("no") && !m_VoiceDestination.equals("") && !m_VoiceSet2 || result.equals("No") && !m_VoiceDestination.equals("") && !m_VoiceSet2) {
                origin.setText("Choice: " + result);
                m_VoiceDestination = "";
                m_VoiceSet = false;
                Speech.getInstance().say("Please choose your new destination", new TextToSpeechCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted() {
                        m_voiceCount = 5;
                        onRecordAudioPermissionGranted();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
            if (!result.equals("no") && !m_VoiceDestination.equals("") && m_VoiceSet && !m_VoiceSet2 || !result.equals("yes") && !m_VoiceDestination.equals("") && m_VoiceSet && !m_VoiceSet2) {
                Log.e(TAG, m_VoiceDestination);
                Speech.getInstance().say("Please choose between yes and no", new TextToSpeechCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted() {
                        onRecordAudioPermissionGranted();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            if (result.equals("yes") && m_VoiceDestination.equals("") && m_VoiceSet2) {
                m_VoiceSet = false;
                m_VoiceSet2 = false;
                m_VoiceSet3 = false;
                m_voiceCount = 5;
                Speech.getInstance().say("Where would you like to go today?", new TextToSpeechCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted() {
                        onRecordAudioPermissionGranted();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            if (result.equals("yes") && !m_VoiceDestination.equals("") && m_VoiceSet2) {
                m_VoiceSet = false;
                m_VoiceSet2 = false;
                m_VoiceSet3 = false;
                m_voiceCount = 5;
                Speech.getInstance().say("The destination you have selected is " + m_VoiceDestination + " ,is that correct?", new TextToSpeechCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted() {
                        onRecordAudioPermissionGranted();
                    }

                    @Override
                    public void onError() {

                    }
                });

            }

            if (result.equals("no") && m_VoiceDestination.equals("") && m_VoiceSet2) {
                Speech.getInstance().say("Goodbye");
                MainActivity.this.finish();
                System.exit(0);
            }

            if (!result.equals("no") && m_VoiceDestination.equals("") && m_VoiceSet2 || !result.equals("yes") && m_VoiceDestination.equals("") && m_VoiceSet2) {
                m_voiceCount = 5;
                Log.e(TAG, m_VoiceDestination);
                Speech.getInstance().say("Please choose between yes and no", new TextToSpeechCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onCompleted() {
                        onRecordAudioPermissionGranted();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }
        }
    }


    void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
        beginListenForData();
        Toast.makeText(MainActivity.this, "Bluetooth Opened", Toast.LENGTH_SHORT).show();
    }

    public void findBT() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null) {
            Toast.makeText(MainActivity.this, "No bluetooth adapter available", Toast.LENGTH_SHORT).show();
        }

        if(!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        pairedDevices = mBluetoothAdapter.getBondedDevices();
        mmDevice = mBluetoothAdapter.getRemoteDevice("98:D3:31:FB:41:07");
        if (pairedDevices.contains(mmDevice))
        {
            Toast.makeText(MainActivity.this,"Bluetooth Device Found, address: " + mmDevice.getAddress() ,Toast.LENGTH_LONG).show();
            Log.d("ArduinoBT", "BT is paired");
        }
        Toast.makeText(MainActivity.this, "Bluetooth Device Found", Toast.LENGTH_SHORT).show();
    }

    void beginListenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable() {
            public void run() {
                while(!Thread.currentThread().isInterrupted() && !stopWorker) {
                    try {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0) {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++) {
                                byte b = packetBytes[i];
                                if(b == delimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    m_proceedData = data;

                                    handler.post(new Runnable() {
                                        public void run() {
                                            Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex) {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void sendData(String i) throws IOException {
        //mmOutputStream.write(msg.getBytes());
        byte[] buffer = i.getBytes();
        try {
            mmOutputStream = mmSocket.getOutputStream();
            mmOutputStream.write(buffer);
            Log.d("message", i + " sent");
            Toast.makeText(MainActivity.this, "Data sent", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        Toast.makeText(MainActivity.this, "Bluetooth Closed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onSpeechPartialResults(List<String> results) {
        origin.setText("Analyzing");
    }


    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        SpeechUtil.redirectUserToGoogleAppOnPlayStore(MainActivity.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.speech_not_available)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.enable_google_voice_typing)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // do nothing
                    }
                })
                .show();
    }
    protected void onDestroy() {
        super.onDestroy();
        Speech.getInstance().unregisterDelegate();
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}