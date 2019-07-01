package de.gruppe10.myapplication;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity {

    private long mAnzParcels = 0L;

    private BTManager mBTManager;
    private BTMsgHandler mBTHandler;
    private TextView mBluetoothStatus;
    private TextView mTextAnzParcels;
    private Switch mConnect;
    private ListView mDeviceList;

    private TextView mKmh;
    private TextView mRpm;
    private TextView mTemp;
    private TextView mLast;
    private ImageView imTemp;
    private ImageView imMotor;
    private Button mPost;

    private LinearLayout mLL;
    //reaction after selection of an BT-Device to connect
    private AdapterView.OnItemClickListener myListClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            // Get the device MAC address, the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);

            mBluetoothStatus.setText(R.string.connecting);
            mConnect.setText(R.string.connecting);
            Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
            mDeviceList.setVisibility(View.INVISIBLE);
            mBTManager.connect(address);
            mConnect.setText(R.string.connect);
        }
    };
    private JSONObject infoData;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toast.makeText(getApplicationContext(), "Willkommen im Info Modus", Toast.LENGTH_SHORT).show();


        //TextView for speed and rpm
        mKmh = findViewById(R.id.textKmh);
        mRpm = findViewById(R.id.textRpm);
        mTemp = findViewById(R.id.textTemp);
        mLast = findViewById(R.id.textLast);
        //ImageView for motor and last
        imTemp = findViewById(R.id.im_temp);
        imMotor = findViewById(R.id.im_Motor);

        mPost = findViewById(R.id.button);

        //LinearLayout and TextView for BT and received parcels
        mLL = findViewById(R.id.mlinearLayout);
        mBluetoothStatus = findViewById(R.id.bluetoothStatus2);
        mTextAnzParcels = findViewById(R.id.text_anzParcels);
        mDeviceList = findViewById(R.id.listView);

        mConnect = findViewById(R.id.switch1);


        mKmh.setText(getString(R.string.nullKMH));
        mRpm.setText(getString(R.string.nullRPM));
        mTemp.setText(getString(R.string.nullGrad));
        mLast.setText(getString(R.string.nullProzent));

        setConnectButtons(false);

        initManager();


        //create a new BTManager to handle the connections to BTdevices
        try {
            mBTManager = new BTManager(this, mBTHandler);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }


        //click event to show the paired devices
        mConnect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (mDeviceList.getVisibility() != View.VISIBLE) {
                    pairedDevicesList();

                } else {
                    if (mBTManager != null) {
                        mBTManager.cancel();
                        setConnectButtons(false);
                    }
                    mDeviceList.setVisibility(View.GONE);
                }

                if (mBTManager != null) {
                    mBTManager.cancel();
                    setConnectButtons(false);
                }
            }
        });

        //we don't want see the keyboard emulator on start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RequestAsync().execute();
            }
        });

    }

    private void pairedDevicesList() {
        mDeviceList.setVisibility(View.VISIBLE);
        ArrayList<String> list = mBTManager.getPairedDeviceInfos();

        if (list.size() > 0) {
            final ArrayAdapter adapter;
            adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
            mDeviceList.setAdapter(adapter);
            mDeviceList.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
        } else {
            Toast.makeText(getApplicationContext(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
        }
    }

    public void setConnectButtons(boolean isConnected) {
        mConnect.setChecked(isConnected);

        if (!isConnected) {
            mBluetoothStatus.setText("-");
            mAnzParcels = 0L;
            mTextAnzParcels.setText(mAnzParcels + "");
            mConnect.setText(R.string.disabled);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mBTManager.cancel();
        //startActivity(new Intent(this, MainActivity.class));
    }

    @SuppressLint("HandlerLeak")
    public void initManager() {
        infoData = new JSONObject();
        mBTHandler = new BTMsgHandler() {
            @Override
            void receiveMessage(String msg) {
                mBluetoothStatus.setText(msg);
                mAnzParcels++;
                mTextAnzParcels.setText(mAnzParcels + "");
                String show = msg.substring(1);
                String test = "" + msg.charAt(0);
                show = show.trim();
                double check = Double.parseDouble(show);


                if (test.equals("V")) {
                    mKmh.setText(String.format("%s km/h", show));
                    try {
                        if (msg != null) {
                            infoData.put("field1", check);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (test.equals("D")) {
                    try {
                        if (msg != null) {
                            infoData.put("field2", check);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mRpm.setText(String.format("%s RPM", show));
                }

                if (test.equals("T")) {
                    mTemp.setText(String.format("%s °C", show));
                    try {
                        if (msg != null)
                            infoData.put("field3", check);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if ("L".equals(test)) {
                    mLast.setText(String.format("%s %%", show));
                    try {
                        if (msg != null) {
                            infoData.put("field4", check);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                new RequestAsync().execute();

            }

            @Override
            void receiveConnectStatus(boolean isConnected) {

                if (isConnected) {
                    mBluetoothStatus.setText(R.string.connect);
                    mLL.setVisibility(View.GONE);
                } else {
                    mBluetoothStatus.setText(R.string.conn_fail);
                }
                setConnectButtons(isConnected);
            }

            @Override
            void handleException(Exception e) {
                mBluetoothStatus.setText("-");
                setConnectButtons(false);
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

    }

    public class RequestAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                return RequestHandler.sendPost("https://api.thingspeak.com/update?api_key=P515K36DJWEDMKAI", infoData);
            } catch (Exception e) {
                return "Exception: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
        }


    }

}
