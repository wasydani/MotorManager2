package de.gruppe10.myapplication;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;

public class FahrActitvity extends AppCompatActivity {

    private long mAnzParcels = 0L;

    private BTManager mBTManager;
    private BTMsgHandler mBTHandler;
    private TextView mBluetoothStatus;
    private TextView mTextAnzParcels;
    private Switch mConnect;
    private ListView mDeviceList;
    private LinearLayout mLL;

    private ImageView status;

    private JSONObject fahrData;
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

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fahr_actitvity);
        Toast.makeText(getApplicationContext(), "Willkommen im Fahrmodus", Toast.LENGTH_SHORT).show();


        //Bluetooth TextViews
        mBluetoothStatus = findViewById(R.id.bluetoothStatus2);
        mTextAnzParcels = findViewById(R.id.text_anzParcels);
        mConnect = findViewById(R.id.switch1);
        mDeviceList = findViewById(R.id.listView);
        mLL = findViewById(R.id.mlinearLayout);

        //Status Image
        status = findViewById(R.id.im_status);
        final Drawable warn = getDrawable(R.drawable.ic_warn);
        final Drawable tick = getDrawable(R.drawable.ic_good);
        final Drawable stop = getDrawable(R.drawable.ic_stop);

        setConnectButtons(false);

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

               /* if (test.equals("V")){
                    mTempo.setText(show);
                }*/

                if (test.equals("L")) { //check if received msg is Motorlast
                    if (check < 40) {
                        status.setImageDrawable(tick);
                    } else if (check > 41 && check < 70) {
                        status.setImageDrawable(warn);
                    } else if (check > 72) {
                        status.setImageDrawable(stop);
                    }
                }


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
                    mDeviceList.setVisibility(View.GONE); //if connected disable BT List
                }

                if (mBTManager != null) {
                    mBTManager.cancel();
                    setConnectButtons(false);
                }
            }
        });

        //we don't want see the keyboard emulator on start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
    }

    public class RequestAsync extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            try {
                return RequestHandler.sendPost("https://api.thingspeak.com/update?api_key=P515K36DJWEDMKAI", fahrData);
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

