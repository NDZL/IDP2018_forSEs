package emdkprofiles.ndzl.com;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.symbol.emdk.EMDKBase;
import com.symbol.emdk.EMDKException;
import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.ProfileManager;

import static android.content.Intent.ACTION_VIEW;


public class MainActivity extends Activity
        implements EMDKManager.EMDKListener,
                   EMDKManager.StatusListener,
                   ProfileManager.DataListener{

    private ProfileManager profileManager = null;

    private EMDKManager emdkManager = null;

    private String errorName = "";
    private String errorType = "";
    private String errorDescription = "";
    private String status = "";

    private Button bwf;

    Button btSleep;
    Button btClock;
    Button btErrProfile;
    TextView tvD;

    String profileToBeApplied = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvD = (TextView)findViewById(R.id.tvDisplay);

        btSleep = (Button)findViewById(R.id.btSleep);
        btSleep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                profileToBeApplied= "SLEEP";
                EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), MainActivity.this);
            }
        });

        btClock = (Button)findViewById(R.id.btClock);
        btClock.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                profileToBeApplied= "SETCLOCK";
                EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), MainActivity.this);
            }
        });

        btErrProfile = (Button)findViewById(R.id.btProfErr);
        btErrProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                profileToBeApplied= "XYZ-ERROR";
                EMDKResults results = EMDKManager.getEMDKManager(getApplicationContext(), MainActivity.this);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onOpened(EMDKManager emdkManager) {

        this.emdkManager = emdkManager;

        try {
            emdkManager.getInstanceAsync(EMDKManager.FEATURE_TYPE.PROFILE,
                    MainActivity.this);
        } catch (EMDKException e) {
            e.printStackTrace();
        }
        catch(Exception ex){

        }

        //ApplyEMDKprofile();  //this is the "synced" way to apply a profile
        //-> in the "async" way the execution is passed to "onStatus" when the object Profile is ready to go


        //finish();
        //System.exit(0);

    }


    private void ApplyEMDKprofile(){
        if (profileManager != null) {
            String[] modifyData = new String[1];

            final EMDKResults results = profileManager.processProfileAsync(profileToBeApplied,
                    ProfileManager.PROFILE_FLAG.SET, modifyData);

            String sty = results.statusCode.toString();
            tvD.setText("Result: "+sty +" ");
        }
    }



    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //Clean up the objects created by EMDK manager
        emdkManager.release();
    }


    @Override
    public void onClosed() {

    }

    @Override
    public void onStatus(EMDKManager.StatusData statusData, EMDKBase emdkBase) {
        if(statusData.getResult() == EMDKResults.STATUS_CODE.SUCCESS) {
            if(statusData.getFeatureType() == EMDKManager.FEATURE_TYPE.PROFILE)
            {
                profileManager = (ProfileManager)emdkBase;
                profileManager.addDataListener(this);
                ApplyEMDKprofile();
                    //finish();
                    //System.exit(0);
            }
        }
    }


    @Override
    public void onData(ProfileManager.ResultData resultData) {
        //processProfileAsync callback method
        EMDKResults result = resultData.getResult();
        if(result.statusCode == EMDKResults.STATUS_CODE.CHECK_XML) {
            String responseXML = result.getStatusString();
            Toast.makeText(MainActivity.this, "RESPONSE="+responseXML, Toast.LENGTH_LONG).show();
        } else if(result.statusCode != EMDKResults.STATUS_CODE.SUCCESS) {
            Toast.makeText(MainActivity.this, "ERROR IN PROFILE APPLICATION", Toast.LENGTH_LONG).show();
        }
    }
}
