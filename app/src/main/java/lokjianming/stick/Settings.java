/*
 * MIT License
 *
 * Copyright (c) 2017 Jan Heinrich Reimer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package lokjianming.stick;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    private CardView mCard;

    private String m_Text;
    private String m_Text2;
    private String m_Text3;
    private TextView m_textView;
    private TextView m_textView2;
    private TextView m_textView3;
    private EditText m_editText;

    private SharedPreferences.Editor editor;
    private SharedPreferences settings;

    private String ret;
    private String ret2;
    private String ret3;

    private final static String TAG = "1";

    private String blood;
    private String allergy;
    private String age;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(toolbar);

        settings = PreferenceManager.getDefaultSharedPreferences(Settings.this);
        blood = settings.getString("blood", "0");
        age = settings.getString("age", "0");
        allergy = settings.getString("allergy", "0");


        editor = settings.edit();

        m_textView = findViewById(R.id.info_text3);
        m_textView2 = findViewById(R.id.info_text6);
        m_textView3 = findViewById(R.id.info_text9);


        if(!blood.isEmpty()) {
            m_textView.setText(blood);
        }

        if(!age.isEmpty()) {
            m_textView2.setText(age);
        }

        if(!allergy.isEmpty()) {
            m_textView3.setText(allergy);
        }

        CardView mCard = findViewById(R.id.card_view);
        mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Settings.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Settings.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                m_Text = userInputDialogEditText.getText().toString();
                                editor.putString("blood", m_Text);
                                editor.apply();
                                ret = settings.getString("blood", "0");
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

        CardView mCard2 = findViewById(R.id.card_view2);
        mCard2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Settings.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Settings.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                m_Text2 = userInputDialogEditText.getText().toString();
                                editor.putString("age", m_Text2);
                                editor.apply();
                                ret2 = settings.getString("age", "0");
                                m_textView2.setText(ret2);
                                userInputDialogEditText.setText(ret2);
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

        CardView mCard3 = findViewById(R.id.card_view3);
        mCard3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Settings.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.user_input_dialog_box, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Settings.this, R.style.Theme_AppCompat_Light_Dialog_Alert);
                alertDialogBuilderUserInput.setView(mView);

                final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                // ToDo get user input here
                                m_Text3 = userInputDialogEditText.getText().toString();
                                editor.putString("allergy", m_Text3);
                                editor.apply();
                                ret3 = settings.getString("allergy", "0");
                                m_textView3.setText(ret3);
                                userInputDialogEditText.setText(ret3);
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
    }

}