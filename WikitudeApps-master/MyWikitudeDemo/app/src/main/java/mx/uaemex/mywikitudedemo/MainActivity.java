package mx.uaemex.mywikitudedemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wikitude.architect.ArchitectView;
import com.wikitude.architect.StartupConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private Map<Integer, List<SampleMeta>> samples;

    private Set<String> irSamples;
    private Set<String> geoSamples;

    public static final String EXTRAS_KEY_ACTIVITY_TITLE_STRING = "activityTitle";
    public static final String EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL = "activityArchitectWorldUrl";

    public static final String EXTRAS_KEY_ACTIVITY_IR = "activityIr";
    public static final String EXTRAS_KEY_ACTIVITY_GEO = "activityGeo";

    public static final String EXTRAS_KEY_ACTIVITIES_ARCHITECT_WORLD_URLS_ARRAY = "activitiesArchitectWorldUrls";
    public static final String EXTRAS_KEY_ACTIVITIES_TILES_ARRAY = "activitiesTitles";
    public static final String EXTRAS_KEY_ACTIVITIES_CLASSNAMES_ARRAY = "activitiesClassnames";

    public static final String EXTRAS_KEY_ACTIVITIES_IR_ARRAY = "activitiesIr";
    public static final String EXTRAS_KEY_ACTIVITIES_GEO_ARRAY = "activitiesGeo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        irSamples = getListFrom("samples/samples_ir.lst");
        geoSamples = getListFrom("samples/samples_geo.lst");

        setContentView(R.layout.activity_main);
        // ensure to clean cache when it is no longer required
        MainActivity.deleteDirectoryContent (ArchitectView.getCacheDirectoryAbsoluteFilePath(this));

        // extract names of samples from res/arrays
        final String[] values = this.getListLabels();
        Log.d("VALUES-MAIN------->", Arrays.toString(values));

        Button btnStartCam = (Button) findViewById(R.id.btnStartCam);
        btnStartCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launCamActivity();
            }
        });
    }

    private void launCamActivity() {
        String className = "mx.uaemex.mywikitudedemo.SampleCamActivity"; //name of class to load

        try {
            final Intent intent = new Intent(this, Class.forName(className));
            intent.putExtra(EXTRAS_KEY_ACTIVITY_TITLE_STRING,
                    "1.1 Image On Target");

            intent.putExtra(EXTRAS_KEY_ACTIVITY_ARCHITECT_WORLD_URL,
                    "samples/1_Client$Recognition_1_Image$On$Target/index.html"); //route of index file to load

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

    private Set<String> getListFrom(String fname) {
        HashSet<String> data = new HashSet<String>();
        try {
            BufferedReader burr = new BufferedReader(new InputStreamReader(getAssets().open(fname)));
            String line;
            while ((line = burr.readLine()) != null) {
                data.add(line);
            }
            burr.close();
        } catch (FileNotFoundException e) {
            Log.w("Wikitude SDK Samples", "Can't read list from file " + fname);
        } catch (IOException e) {
            Log.w("Wikitude SDK Samples", "Can't read list from file " + fname);
        }
        return data;
    }

    protected final String[] getListLabels() {
        boolean includeIR = (ArchitectView.getSupportedFeaturesForDevice(getApplicationContext()) & StartupConfiguration.Features.Tracking2D) != 0;
        boolean includeGeo = (ArchitectView.getSupportedFeaturesForDevice(getApplicationContext()) & StartupConfiguration.Features.Geo) != 0;

        samples = getActivitiesToLaunch(includeIR, includeGeo);
        final String[] labels = new String[samples.keySet().size()];
        for (int i = 0; i<labels.length; i++) {
            labels[i] = samples.get(i).get(0).categoryId + ". " + samples.get(i).get(0).categoryName.replace("$", " ");
        }
        return labels;
    }

    private List<SampleMeta> getActivitiesToLaunch(final int position){
        return samples.get(position);
    }

    private Map<Integer, List<SampleMeta>> getActivitiesToLaunch(boolean includeIR, boolean includeGeo){
        final Map<Integer, List<SampleMeta>> pos2activites = new HashMap<Integer, List<SampleMeta>>();

        String[] assetsIWant;

        try {
            assetsIWant = getAssets().list("samples"); //name of folder inside assets
            int pos = -1;
            String lastCategoryId = "";
            for(final String asset: assetsIWant) {
                if (!asset.substring(asset.length() - 4).contains(".")) {
                    try {
                        // don't include sample if it requires IR functionality on
                        // devices which don't support it.
                        boolean needIr = irSamples.contains(asset);
                        boolean needGeo = geoSamples.contains(asset);
                        if ((includeIR || !needIr) && (includeGeo || !needGeo)) {
                            SampleMeta sampleMeta = new SampleMeta(asset, needIr, needGeo);
                            if (!sampleMeta.categoryId.equals(lastCategoryId)) {
                                pos++;
                                pos2activites.put(pos, new ArrayList<SampleMeta>());
                            }
                            pos2activites.get(pos).add(sampleMeta);
                            Log.d("ActivitiesToLaunch--->", sampleMeta.toString());
                            lastCategoryId = sampleMeta.categoryId;
                        }
                    } catch (IllegalArgumentException e) {
                        // Log.e("Ignored Asset to load", asset + " invalid: "+ e.getMessage());
                    }
                }
            }

            return pos2activites;


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * deletes content of given directory
     * @param path
     */
    private static void deleteDirectoryContent(final String path) {
        try {
            final File dir = new File (path);
            if (dir.exists() && dir.isDirectory()) {
                final String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class SampleMeta {

        final String path, categoryName, sampleName, categoryId;
        final int sampleId;
        final boolean hasGeo, hasIr;

        public SampleMeta(String path, boolean hasIr, boolean hasGeo) {
            super();
            this.path = path;
            this.hasGeo = hasGeo;
            this.hasIr = hasIr;
            if (path.indexOf("_")<0) {
                throw new IllegalArgumentException("all files in asset folder must be folders and define category and subcategory as predefined (with underscore)");
            }
            this.categoryId = path.substring(0, path.indexOf("_"));
            path = path.substring(path.indexOf("_")+1);
            this.categoryName = path.substring(0, path.indexOf("_"));
            path = path.substring(path.indexOf("_")+1);
            this.sampleId = Integer.parseInt(path.substring(0, path.indexOf("_")));
            path = path.substring(path.indexOf("_")+1);
            this.sampleName = path;
        }

        @Override
        public String toString() {
            return "categoryId:" + this.categoryId + ", categoryName:" + this.categoryName + ", sampleId:" + this.sampleId +", sampleName: " + this.sampleName + ", path: " + this.path;
        }
    }
}
