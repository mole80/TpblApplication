package tpbl.tpblappl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
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

public class  OutingActivity extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> _outingsHashList;

    private static String _urlGetOuting = "http://www.tpbl.ch/service/getSortieAppl.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_OUTING = "sortie";
    private static final String TAG_ID = "Id";
    private static final String TAG_NAME = "Titre";

    JSONArray _jSonArrayOuting = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_products);

        Bundle extras = getIntent().getExtras();
        UserClass u = getIntent().getParcelableExtra("user");

        // Hashmap for ListView
        _outingsHashList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new LoadAllOuting().execute();

        // Get listview
        ListView lv = getListView();

        // on seleting single item
        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem
                String oid = ((TextView) view.findViewById(R.id.TV_Id)).getText()
                        .toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        EditPresOutingActivity.class);
                // sending pid to next activity
                in.putExtra("Pos", position);

                // starting new activity and expecting some response back
                startActivityForResult(in, 100);
            }
        });

    }

    // Response from Edit Product Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if result code 100
        if (resultCode == 100) {
            // if result code 100 is received
            // means user edited/deleted product
            // reload this screen again
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }

    }

    class LoadAllOuting extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OutingActivity.this);
            pDialog.setMessage("Loading sortie. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(_urlGetOuting, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Load outing: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    _jSonArrayOuting = json.getJSONArray(TAG_OUTING);

                    for (int i = 0; i < _jSonArrayOuting.length(); i++) {
                        JSONObject c = _jSonArrayOuting.getJSONObject(i);

                        int id = c.getInt(TAG_ID);
                        String name = c.getString(TAG_NAME);

                        DonneeAppl.GetInstance().ListOuting.add(new OutingClass(id, name));
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_ID, Integer.toString(id));
                        map.put(TAG_NAME, name);
                        _outingsHashList.add(map);
                    }
                } else {
                    Intent i = new Intent(getApplicationContext(),
                            NewOutingActivity.class);
                    // Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread

            ArrayList<OutingClass> l = DonneeAppl.GetInstance().ListOuting;
            OutingAdapter ad = new OutingAdapter(OutingActivity.this, l);
            setListAdapter(ad);

            /*runOnUiThread(new Runnable() {
                public void run() {

                    ListAdapter adapter = new SimpleAdapter(
                            OutingActivity.this, _outingsHashList,
                            R.layout.list_item, new String[] { TAG_ID,
                            TAG_NAME},
                            new int[] { R.id.pid, R.id.name });
                    // updating listview
                    setListAdapter(adapter);
                }
            });*/

        }

    }
}
