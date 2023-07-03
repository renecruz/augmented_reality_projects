package mx.uaemex.arapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRAS_KEY_ACTIVITY_TITLE_STRING = "activityTitle";
    public static final String EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL = "activityArchitectWorldUrl";
    public static final String EXTRAS_KEY_ACTIVITY_IR = "activityIr";
    public static final String EXTRAS_KEY_ACTIVITY_GEO = "activityGeo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Button btnStartCam = (Button) findViewById(R.id.btnStartCam);
        btnStartCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launCamActivity();
            }
        });
    }

    private void launCamActivity() {
        String className = "mx.uaemex.arapp.SampleCamActivity"; //name of class to load

        try {
            final Intent intent = new Intent(this, Class.forName(className));
            intent.putExtra(EXTRAS_KEY_ACTIVITY_TITLE_STRING,
                    "1.1 Image On Target");

            intent.putExtra(EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL,
                    "Elementos/1_Client$Recognition_1_Image$On$Target/index.html"); //route of index file to load

            intent.putExtra(EXTRAS_KEY_ACTIVITY_IR,
                    true);

            intent.putExtra(EXTRAS_KEY_ACTIVITY_GEO,
                    false);

                /* launch activity */
            this.startActivity(intent);
        } catch (Exception e) {
			/*
			 * may never occur, as long as all SampleActivities exist and are
			 * listed in manifest
			 */
            Toast.makeText(this, className + "\nnot defined/accessible",
                    Toast.LENGTH_SHORT).show();
            Log.e("ERROR->", e.toString());
        }
    }







}
