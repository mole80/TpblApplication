package tpbl.tpblappl;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import android.view.LayoutInflater;
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
import android.app.AlertDialog;

public class EditPresOutingActivity extends Activity{

    public final static String ID_DEFAULT_CALENDAR = "Default calendar id";

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    ArrayList<HashMap<String, String>> _outingsHashList;
    private static String _urlUpdateOuting = "http://www.tpbl.ch/service/RepSortieAppl.php";

    int _userId;
    UserClass _user;
    OutingClass _outing;

    CalendarClass _calendars[];

    void SetDefaultId(int id)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(ID_DEFAULT_CALENDAR, id);
        editor.commit();
    }

    int GetDefaultId()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        return pref.getInt(ID_DEFAULT_CALENDAR, -1);
    }

    public void EndOfTask(){
        Intent result = new Intent();
        setResult(100, result);
        finish();
    }

    String[] GetEvents(Date date ){

        String[] outings = null;

        try {
            String[] proj = new String[]{
                    CalendarContract.Instances.EVENT_ID,
                    CalendarContract.Instances.BEGIN,
                    CalendarContract.Instances.TITLE,
                    CalendarContract.Instances.END
            };

            Calendar begin = Calendar.getInstance();
            begin.setTime(date);
            begin.set(Calendar.HOUR_OF_DAY, 0);
            Calendar end = Calendar.getInstance();
            end.setTime(date);
            end.set(Calendar.HOUR_OF_DAY, 23);
            long beginMili = begin.getTimeInMillis();
            long endMili = end.getTimeInMillis();

            Cursor cur = null;
            ContentResolver cr = getContentResolver();

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, beginMili);
            ContentUris.appendId(builder, endMili);

            /*String sel = CalendarContract.Instances.EVENT_ID + " =?";
            String[] selArgs = new String[]{"3763"};
            cur = cr.query(builder.build(), proj, sel, selArgs, null);*/

            cur = cr.query(builder.build(), proj, null, null, null);

            outings = new String[cur.getCount()];
            int cpt = 0;

            while (cur.moveToNext()) {
                String title = null;
                long eventID = 0;
                long beginVal = 0;

                eventID = cur.getLong(0);
                title = cur.getString(2);
                beginVal = cur.getLong(1);
                long endVal = cur.getLong(3);

                Calendar caS = Calendar.getInstance();
                caS.setTimeInMillis(beginVal);
                int dayS = caS.get(Calendar.DAY_OF_MONTH);

                Calendar ca = Calendar.getInstance();
                ca.setTimeInMillis(endVal);
                int day = ca.get(Calendar.DAY_OF_MONTH);
                int hour = ca.get(Calendar.HOUR_OF_DAY);

                outings[cpt] = title;
                cpt++;
            }
        }
        catch (Exception ex )
        {
            String a = ex.getMessage();
        }

        return outings;
    }


    boolean EventsExist()
    {
        String[] outings = null;

        try {
            String[] proj = new String[]{
                    CalendarContract.Instances.EVENT_ID,
                    CalendarContract.Instances.BEGIN,
                    CalendarContract.Instances.TITLE
            };

            Calendar begin = Calendar.getInstance();
            begin.setTime(_outing.OutingDate);
            begin.set(Calendar.HOUR_OF_DAY, 1);
            Calendar end = Calendar.getInstance();
            end.setTime(_outing.OutingDate);
            end.set(Calendar.HOUR_OF_DAY, 23);
            long beginMili = begin.getTimeInMillis();
            long endMili = end.getTimeInMillis();

            Cursor cur = null;
            ContentResolver cr = getContentResolver();

            Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(builder, beginMili);
            ContentUris.appendId(builder, endMili);

            String sel = CalendarContract.Instances.TITLE + " =?";
            String[] selArgs = new String[]{_outing.Name};
            cur = cr.query(builder.build(), proj, sel, selArgs, null);

            if( cur.getCount() > 0 )
                return true;
        }
        catch (Exception ex )
        {
            String a = ex.getMessage();
        }

        return false;
    }


    int GetIdCalendar(String name)
    {
        for( CalendarClass c : _calendars )
        {
            if( c.Name.equals( name ) )
                return Integer.parseInt( c.Id );
        }

        return 0;
    }


    void UpdateListCalendars(){
        String[] proj = new String[]{
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
                };

        Uri uriCal = CalendarContract.Calendars.CONTENT_URI;

        try {

            ContentResolver cr = getContentResolver();

            Cursor cur = cr.query(uriCal, proj, null, null, null);

            if (cur.moveToFirst()) {

                _calendars = new CalendarClass[cur.getCount()];

                String l_calName;
                String l_calId;

                int l_cnt = 0;
                int l_nameCol = cur.getColumnIndex(proj[1]);
                int l_idCol = cur.getColumnIndex(proj[0]);

                do {
                    l_calName = cur.getString(l_nameCol);
                    l_calId = cur.getString(l_idCol);

                    _calendars[l_cnt] = new CalendarClass(l_calName, l_calId);
                    ++l_cnt;
                } while (cur.moveToNext());

            }
        }
        catch( Exception e )
        {
            String mess = e.getMessage();
        }
    }

    void AskAddCalendar()
    {
        boolean exist = EventsExist();
        if( !exist ) {
            UpdateListCalendars();
            showDialog(0);
        }
        else
            new UpdatePresOuting().execute();
    }

    void AddOutingToCalendar(String calandarName)
    {
        try {
            int id = GetIdCalendar(calandarName);

            if( id != GetDefaultId() )
                SetDefaultId(id);

            Calendar begin = Calendar.getInstance();
            begin.setTime(_outing.OutingDate);
            begin.set(Calendar.HOUR_OF_DAY, 3);
            Calendar end = Calendar.getInstance();
            end.setTime(_outing.OutingDate);
            end.set(Calendar.HOUR_OF_DAY, 23);
            long beginMili = begin.getTimeInMillis();
            long endMili = end.getTimeInMillis();

            Cursor cur = null;
            ContentResolver cr = getContentResolver();

            ContentValues cv = new ContentValues();

            cv.put(CalendarContract.Events.DTSTART, beginMili);
            cv.put(CalendarContract.Events.DTEND, endMili);

            //TODO : Si l'heure est dans la sortie, il est possible d'ajuster les infos
            if( true ) {
                cv.put(CalendarContract.Events.ALL_DAY, 1);
            }

            cv.put(CalendarContract.Events.TITLE, _outing.Name);
            cv.put(CalendarContract.Events.DESCRIPTION, _outing.Infos);
            cv.put(CalendarContract.Events.CALENDAR_ID, id);
            cv.put(CalendarContract.Events.EVENT_LOCATION, _outing.Location);

            TimeZone tz = TimeZone.getDefault();
            cv.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getID());

            Uri ue = CalendarContract.Events.CONTENT_URI;
            Uri ue1 = CalendarContract.Instances.CONTENT_URI;

            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, cv);
        }
        catch (SecurityException se)
        {
            int a = 0;
        }
        catch (Exception ex)
        {
            String m = ex.getMessage();
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder( this );

        LayoutInflater inflater = this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_outing_calendar, null);

        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.RG_CalendarChoice);

        for( int k=0; k < _calendars.length; k++ ){
            RadioButton rb = new RadioButton( this );
            rb.setText( _calendars[k].Name );
            if( _calendars[k].Id.equals( Integer.toString( GetDefaultId() )) )
                rb.setChecked(true);
            rg.addView(rb);
        }

        builder.setView(view)

                .setTitle("Calendrier")

                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        for( int k=0; k< _calendars.length; k++ )
                        {
                            RadioButton rb = (RadioButton) rg.getChildAt(k);
                            if( rb.isChecked() ) {
                                AddOutingToCalendar(rb.getText().toString());
                                break;
                            }
                        }

                        new UpdatePresOuting().execute();
                    }
                })

                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //LoginDialogFragment.this.getDialog().cancel();
                        new UpdatePresOuting().execute();
                    }
                });
        return builder.create();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_pres_outing);

        int posList = getIntent().getIntExtra("Pos", 0);
        _userId = getIntent().getIntExtra("userId", -1);
        _user = getIntent().getParcelableExtra("user");

        _outing = DonneeAppl.GetInstance().ListOuting.get(posList);

        String[] arrayOutings = GetEvents(_outing.OutingDate);

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

        ListView lv_events = (ListView) findViewById( R.id.LV_Events );
        List<String> list_outing = new ArrayList<String>();

        if( arrayOutings.length > 0 ) {
            for (String out : arrayOutings) {
                list_outing.add(out);
            }
        }
        else {
            list_outing.add("LIBRE");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list_outing);
        lv_events.setAdapter(adapter);

        Button btnPres = (Button)findViewById(R.id.btnPresent);
        Button btnAbsent = (Button)findViewById(R.id.btnAbsent);
        Button btnNotSure = (Button)findViewById(R.id.btnNotSure);

        if( _outing.State != eState.Edition )
        {
            btnNotSure.setVisibility( View.GONE );
            if ( _outing.StatePres != eStatePresOuting.NoAnswer )
            {
                btnAbsent.setVisibility(View.GONE);
                btnPres.setVisibility(View.GONE);
            }
        }

        btnPres.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                _outing.StatePres = eStatePresOuting.Present;
                AskAddCalendar();
//                new UpdatePresOuting().execute();
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
                AskAddCalendar();
//                new UpdatePresOuting().execute();
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
            params.add(new BasicNameValuePair("IdP", Integer.toString( _user.Id) ));
            params.add(new BasicNameValuePair("IdS", Integer.toString( _outing.Id ) ));
            params.add(new BasicNameValuePair("Pres",  _outing.GetPresForSite() ));
            params.add(new BasicNameValuePair("Token",  _user.Token ));

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
