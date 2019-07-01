package de.gruppe10.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class AnalyseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyse);
        Toast.makeText(getApplicationContext(), "Willkommen im ThingSpeak Analyse Modus", Toast.LENGTH_SHORT).show();


        WebView webG = findViewById(R.id.wG);
        WebView webD = findViewById(R.id.wD);
        WebView webT = findViewById(R.id.wT);
        WebView webL = findViewById(R.id.wL);

        WebSettings webGSet = webG.getSettings();
        WebSettings webDSet = webD.getSettings();
        WebSettings webTSet = webT.getSettings();
        WebSettings webLSet = webL.getSettings();

        webGSet.setBuiltInZoomControls(true);
        webDSet.setBuiltInZoomControls(true);
        webTSet.setBuiltInZoomControls(true);
        webLSet.setBuiltInZoomControls(true);

        webGSet.setJavaScriptEnabled(true);
        webDSet.setJavaScriptEnabled(true);
        webTSet.setJavaScriptEnabled(true);
        webLSet.setJavaScriptEnabled(true);

        webG.loadUrl("https://thingspeak.com/channels/791372/charts/1?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&title=Geschwindigkeit&type=line");
        webD.loadUrl("https://thingspeak.com/channels/791372/charts/2?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&max=10000&title=Drehzahl&type=line");
        webT.loadUrl("https://thingspeak.com/channels/791372/charts/3?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&title=Motortemperatur&type=line&yaxis=°C");
        webL.loadUrl("https://thingspeak.com/channels/791372/charts/4?bgcolor=%23ffffff&color=%23d62020&dynamic=true&results=60&title=Motorlast&type=line&yaxis=%25");


    }
}
