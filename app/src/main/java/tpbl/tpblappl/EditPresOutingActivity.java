package tpbl.tpblappl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class EditPresOutingActivity extends Activity{

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> _outingsHashList;
    private static String _urlUpdateOuting = "http://www.tpbl.ch/service/RepSortieAppl.php";

    int _userId;
    OutingClass _outing;

    public void EndOfTask(){
        Intent result = new Intent();
        setResult(100, result);
        finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_pres_outing);

        int posList = getIntent().getIntExtra("Pos", 0);
        _userId = getIntent().getIntExtra("userId", -1);

        _outing = DonneeAppl.GetInstance().ListOuting.get(posList);

        TextView tv_Id = (TextView) findViewById(R.id.TV_Id);
        TextView tv_Title = (TextView) findViewById(R.id.TV_Title);

        TextView tv_Date = (TextView) findViewById(R.id.TV_Date);
        TextView tv_Infos = (TextView) findViewById(R.id.TV_Infos);
        TextView tv_InfosPrivee = (TextView) findViewById(R.id.TV_InfosPrive);

        tv_Id.setText( Integer.toString( _outing.Id ) );
        tv_Title.setText( _outing.Name );
        tv_Date.setText( _outing.GetDate() );
        tv_Infos.setText( _outing.Infos );
        tv_InfosPrivee.setText( _outing.InfosPrive );

        Button btnPres = (Button)findViewById(R.id.btnPresent);
        Button btnAbsent = (Button)findViewById(R.id.btnAbsent);
        Button btnNotSure = (Button)findViewById(R.id.btnNotSure);

        btnPres.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                _outing.StatePres = eStatePresOuting.Present;
                new UpdatePresOuting().execute();
            }
        });

        btnAbsent.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                _outing.StatePres = eStatePresOuting.Absent;
                new UpdatePresOuting().execute();
                int a = 0;
            }
        });

        btnNotSure.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                _outing.StatePres = eStatePresOuting.NotSure;
                new UpdatePresOuting().execute();
            }
        });
    }

    class UpdatePresOuting extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditPresOutingActivity.this);
            pDialog.setMessage("Update sortie. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("IdP", Integer.toString( _userId ) ));
            params.add(new BasicNameValuePair("IdS", Integer.toString( _outing.Id ) ));
            params.add(new BasicNameValuePair("Pres",  _outing.GetPresForSite() ));

            JSONObject json = jParser.makeHttpRequest(_urlUpdateOuting, "POST", params);

            // Check your log cat for JSON reponse
            Log.d("Update outing: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt( "result" );

                if (success == 1) {
                    /*Intent i = new Intent(getApplicationContext(),
                            NewOutingActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);*/
                }
                else {
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            EndOfTask();
            // updating UI from Background Thread
        }
    }
}
