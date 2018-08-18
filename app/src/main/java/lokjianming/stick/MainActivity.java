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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

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
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.io.File;
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

public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private MainActivity m_mapFragmentView;

    private BluetoothAdapter mBluetoothAdapter;
    private PositioningManager.OnPositionChangedListener positionListener;

    private int m_routeLength;

    private String m_proceedData;

    private boolean m_exit;

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
    Boolean m_boolean = false;
    private TextView origin;
    private FloatingActionButton searchrequestBtn;
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

    private Button sendBtn;

    private SharedPreferences settings;
    private String blood;
    private String allergy;
    private String age;

    private static final String VOICE_LANG_CODE = "en-US";
    private static final String VOICE_LANG_CODE_ALT = "es-ES";
    private static final String VOICE_MARC_CODE = "eng";

    public static final String PREF_KEY_FIRST_START = "lokjianming.stick.PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_INTRO = 1;

    private ActivityMainBinding binding;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m_exit = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);

        settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        blood = settings.getString("blood", "0");
        age = settings.getString("age", "0");
        allergy = settings.getString("allergy", "0");

        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);

        if (firstStart) {
            requestPermissions();
            Intent intent = new Intent(this, Intro.class);
            startActivityForResult(intent, REQUEST_CODE_INTRO);

        } else {
            initMapFragment();
        }

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.drawable.black)
                .withSavedInstance(savedInstanceState)
                .build();

        sendBtn = (Button) findViewById(R.id.start);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendMessage();
            }
        });


        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIcon(GoogleMaterial.Icon.gmd_home).withIdentifier(1).withSelectable(true),
                        new PrimaryDrawerItem().withName("Settings").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(2).withSelectable(true)
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
        requiredSDKPermissions.add(Manifest.permission.SEND_SMS);

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

        /* Locate the mapFragment UI element */
        origin = (TextView) findViewById(R.id.start);

        m_mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapfragment);

        // Set up disk cache path for the map service for this application
        boolean success = com.here.android.mpa.common.MapSettings.setIsolatedDiskCacheRootPath(
                getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                "lokjianming.stick.MapService");

        if (!success) {
            Toast.makeText(getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_LONG);
        } else {
            /* Initialize the MapFragment, results will be given via the called back. */
            m_mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {

                    if (error == Error.NONE) {
                        m_map = m_mapFragment.getMap();

                        getPosition();

                        if(getIntent().getData() == null) {
                            Log.e(TAG, "this is null");
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
                            Log.e(TAG, "message sent");
                        } else {
                            getPosition();
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
        if(positioningManager != null) {
            getPosition();
        }
        if(m_navigationManager != null) {
            m_navigationManager.resume();
        }
    }

    // To pause positioning listener
    @Override
    public void onPause() {
        if(m_navigationManager != null) {
            m_navigationManager.pause();
        }
        super.onPause();
        paused = true;
    }

    private void sendMessage() {
        blood = settings.getString("blood", "0");
        age = settings.getString("age", "0");
        allergy = settings.getString("allergy", "0");
        String string1 = String.valueOf(geoCoordinate1.getLatitude()) + "," + String.valueOf(geoCoordinate1.getLongitude());
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+60109829252", null, "I need help urgently! \nMy Location: " + string1 + "\nhttps://maps.google.com/maps?q=loc:" + string1 + "\nBlood group: " + blood + "\nAge: " + age + "\nAllergies: " + allergy, null, null);
        Log.e(TAG, "message sent");
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
                    m_map.setZoomLevel(17.2);
                    Log.e(TAG,"Hey, we just got position");
                    geoCoordinate1 = position.getCoordinate();
                    Log.e(TAG, geoCoordinate1.toString());
                    if(geoCoordinate1 == null) {
                        getPosition();
                    } else if(geoCoordinate1.toString().contains("Alt: 0.0")) {
                        getPosition();
                    }
                    // Display position indicator
                    m_map.getPositionIndicator().setVisible(true);
                    if(m_proceed) {
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


        m_map.addMapObject(mapMarker);
        m_mapObjectList.add(mapMarker);
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
        workerThread = new Thread(() -> {
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

                                String m_data = data;

                                handler.post(() -> {
                                    Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                                    sendMessage();
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

    void promptSMS(String string) {
        if(!string.isEmpty() && string.equals("1")) {
            sendMessage();
        }
    }

    void closeBT() throws IOException {
        stopWorker = true;
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
        Toast.makeText(MainActivity.this, "Bluetooth Closed", Toast.LENGTH_SHORT).show();
    }

    protected void onDestroy() {
        super.onDestroy();
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
