package tpbl.tpblappl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class EditPresOutingActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_pres_outing);

        int posList = getIntent().getIntExtra("Pos", 0);

        OutingClass outing = DonneeAppl.GetInstance().ListOuting.get(posList);

        outing.Name = "Tutu";

        TextView tv_Id = (TextView) findViewById(R.id.TV_Id);
        TextView tv_Title = (TextView) findViewById(R.id.TV_Title);

        tv_Id.setText( Integer.toString( outing.Id ) );
        tv_Title.setText( outing.Name );
    }
}
