package de.gruppe10.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private ImageButton bt_info;
    private ImageButton bt_fahr;
    private ImageButton bt_analyse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bt_info = findViewById(R.id.bt_info);
        bt_fahr = findViewById(R.id.bt_car);
        bt_analyse = findViewById(R.id.bt_analyse);


    }

    public void changeInfo(View v) {
        Intent intent = new Intent(this, InfoActivity.class);
        startActivity(intent);
    }

    public void changeFahr(View view) {
        Intent intent = new Intent(this, FahrActitvity.class);
        startActivity(intent);
    }

    public void changeAnalyse(View view) {
        Context context = getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConn = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConn) {
            Intent intent = new Intent(this, AnalyseActivity.class);
            startActivity(intent);
        } else {
            Toast toast = Toast.makeText(context, "Prüfe Internetverbindung!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
