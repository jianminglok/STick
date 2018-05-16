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
import android.media.Image;
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
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.Logger;
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

import lokjianming.stick.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SpeechDelegate {
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    public Timer timer;

    private boolean m_exit;

    public Timer timer2;

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

    private boolean m_VoiceSet;
    private boolean m_VoiceSet2;
    private boolean m_VoiceSet3;
    private int m_voiceCount;
    private boolean m_LocationChoose;
    private boolean m_LocationChoose2;
    private boolean m_proceed;

    private String m_proceedData;
    private String m_VoiceDestination;

    Boolean m_boolean = false;
    private SpeechProgressView progress;
    public Set<BluetoothDevice> pairedDevices;

    private CardView mCard;

    private String m_Text;
    private TextView m_textView;
    private EditText m_editText;

    private Thread workerThread;

    private String coordinate1;

    private SharedPreferences.Editor editor;
    private SharedPreferences settings;

    private String ret;

    private final static String TAG = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m_exit = false;
        ret = "";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);

        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);

        settings = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        coordinate1 = settings.getString("coordinate1", "0");

        m_textView = findViewById(R.id.info_text3);

        if(!coordinate1.isEmpty()) {
            m_textView.setText(coordinate1);
        }

        editor = settings.edit();

        Speech.init(this, getPackageName());

        CardView mCard = findViewById(R.id.card_view);
        mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);

                if(!coordinate1.isEmpty() && ret.isEmpty()) {
                    Log.e(TAG, "Coordinate is not empty");
                    userInputDialogEditText.setText(coordinate1);
                } else if(!ret.isEmpty()) {
                    userInputDialogEditText.setText(ret);
                }   else {
                    Log.e(TAG, "Coordinate is empty");
                }

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                m_Text = userInputDialogEditText.getText().toString();
                                editor.putString("coordinate1", m_Text);
                                editor.apply();
                                ret = settings.getString("coordinate1", "0");
                                m_textView.setText(ret);
                                userInputDialogEditText.setText(ret);
                            }
                        })

                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });

                final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();

                alertDialogAndroid.setOnShowListener( new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface arg0) {
                        alertDialogAndroid.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.md_black_1000));
                        alertDialogAndroid.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.md_black_1000));
                    }
                });

                alertDialogAndroid.show();
            }
        });

        if (firstStart) {
            requestPermissions();
            initMapFragment();

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


        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
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

        TextView modelTextview = (TextView) findViewById(R.id.start);

    }

    /**
     * Only when the app's target SDK is 23 or higher, it requests each dangerous permissions it
     * needs when the app is running.
     */
    private void requestPermissions() {

        final List<String> requiredSDKPermissions = new ArrayList<String>();
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


        Logger.setLogLevel(Logger.LogLevel.DEBUG);

        int[] colors = {
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.darker_gray),
                ContextCompat.getColor(this, android.R.color.black),
                ContextCompat.getColor(this, android.R.color.holo_orange_dark),
                ContextCompat.getColor(this, android.R.color.holo_red_dark)
        };
        progress.setColors(colors);

            /* Initialize the MapFragment, results will be given via the called back. */

                            findViewById(R.id.assistant_background).setVisibility(View.VISIBLE);
                            Log.e(TAG, "this is null");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
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
            }
        }, 1000);

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

        } else {
            if(m_VoiceDestination.equals("") && result.equals("config") && !m_VoiceSet2) {
                Speech.getInstance().stopListening();
                findViewById(R.id.assistant_background).setVisibility(View.INVISIBLE);
                findViewById(R.id.start).setVisibility(View.INVISIBLE);
            }
            if (m_VoiceDestination.equals("") && !result.equals("config") && !result.equals("Yes") && !result.equals("No") && !m_VoiceSet2 && !m_VoiceSet3) {
                m_VoiceDestination = result;

                    Speech.getInstance().say("The destination you have selected is " + result + " ,is that correct?", new TextToSpeechCallback() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onCompleted() {
                            m_VoiceSet = true;
                            m_VoiceSet2 = false;
                            onRecordAudioPermissionGranted();

                        }

                        @Override
                        public void onError() {

                        }
                    });

            }

            if (result.equals("yes") && !m_VoiceDestination.equals("") && !m_VoiceSet2 || result.equals("Yes") && !m_VoiceDestination.equals("") && !m_VoiceSet2) {
                m_VoiceSet = false;
                m_VoiceSet3 = false;
                m_voiceCount = 5;
                Speech.getInstance().stopListening();
                findViewById(R.id.assistant_background).setVisibility(View.INVISIBLE);
                findViewById(R.id.start).setVisibility(View.INVISIBLE);

            }
            if (result.equals("no") && !m_VoiceDestination.equals("") && !m_VoiceSet2 || result.equals("No") && !m_VoiceDestination.equals("") && !m_VoiceSet2) {
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
        Speech.getInstance().shutdown();
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