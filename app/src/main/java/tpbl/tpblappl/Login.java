package tpbl.tpblappl;

import android.content.SharedPreferences;
import android.icu.text.TimeZoneFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimeZone;

import android.view.Menu;
import android.view.MenuItem;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;


public class Login extends Activity
{
    public final static String OLD_USER_NAME = "Old username";

    JSONParser jParser = new JSONParser();

    // Lien vers votre page php sur votre serveur
    private static final String	LOGIN_URL	=  "http://www.tpbl.ch/service/testLogin.php";

    private static final String TAG_RESULT = "result";
    private  static  final String TAG_NOM = "Nom";
    private  static  final String TAG_USER = "user";
    private  static  final String TAG_ID = "Id";

    //TODO : Faire mieux pour gèrer les paramètres de login
    String _user;
    String _pass;

    UserClass u = null;
    JSONArray log = null;

    String _tokenFire;

    public ProgressDialog progressDialog;

    private EditText UserEditText;

    private EditText PassEditText;

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }*/

    void SetOldUser(String name)
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(OLD_USER_NAME, name);
        editor.commit();
    }

    String GetOldUsername()
    {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
       return pref.getString(OLD_USER_NAME, "");
    }

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        TimeZone.setDefault(TimeZone.getTimeZone("GMT+1"));

        try
        {
            _tokenFire = FirebaseInstanceId.getInstance().getToken();
            UpdateToken.execute();
            //Toast.makeText(this, "Current token ["+token+"]",
            //        Toast.LENGTH_LONG).show();
        }
        catch (Exception ex)
        {
            String s = ex.getMessage();
        }

        // initialisation d'une progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        // Récupération des éléments de la vue définis dans le xml
        UserEditText = (EditText) findViewById(R.id.username);

        PassEditText = (EditText) findViewById(R.id.password);
        Button button = (Button) findViewById(R.id.okbutton);

        UserEditText.setText( GetOldUsername() );

        // Définition du listener du bouton
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                int usersize = UserEditText.getText().length();
                int passsize = PassEditText.getText().length();
                // si les deux champs sont remplis
                if (usersize > 0 && passsize > 0)
                {
                    progressDialog.show();
                    String user = UserEditText.getText().toString();
                    String pass = PassEditText.getText().toString();

                    _user = user;
                    _pass = UserClass.ConvertMd5(pass);

                    SetOldUser(user);

                    // On appelle la fonction doLogin qui va communiquer avec le PHP
                    new CheckLogin().execute(user,pass);
                    String s = user;
                }
                else
                    createDialog("Error", "Please enter Username and Password");
            }
        });

        button = (Button) findViewById(R.id.cancelbutton);
        // Création du listener du bouton cancel (on sort de l'appli)
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                quit(false, null);
            }
        });
    }

    private void quit(boolean success, Intent i)
    {
        // On envoie un résultat qui va permettre de quitter l'appli
        setResult((success) ? Activity.RESULT_OK : Activity.RESULT_CANCELED, i);
        finish();
    }

    private void createDialog(String title, String text)
    {
        // Création d'une popup affichant un message
        AlertDialog ad = new AlertDialog.Builder(this)
                .setPositiveButton("Ok", null).setTitle(title).setMessage(text)
                .create();
        ad.show();
    }

    void ShowToastToken()
    {
        Toast.makeText(this, "Current token ["+_tokenFire+"]", Toast.LENGTH_LONG).show();
    }

    class UpdateToken extends AsyncTask<String, String, String>
   {
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
       }

       @Override
       protected void onPostExecute(String s) {
           progressDialog.dismiss();

           ShowToastToken();
       }

       @Override
       protected String doInBackground(String... strings) {
           // Building Parameters

           UserClass cu = DonneeAppl.GetInstance().CurrentUser;

           List<NameValuePair> params = new ArrayList<NameValuePair>();
           params.add(new BasicNameValuePair("id", Integer.toString( cu.Id ) ));
           params.add(new BasicNameValuePair("tokenFirebase", cu.FirebaseToken ));
           params.add(new BasicNameValuePair("token", cu.Token ));
           // getting JSON string from URL
           JSONObject json = jParser.makeHttpRequest("http://www.tpbl.ch/service/token.php", "POST", params);

           // Check your log cat for JSON reponse
           Log.d("Login : ", json.toString());

           try {
               // Checking for SUCCESS TAG
               int success = json.getInt(TAG_RESULT);

               if (success == 1) {
               }
               else {
                   return null;
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }

           return "";
       }
   }


   class CheckLogin extends AsyncTask<String, String, UserClass>
   {
       @Override
       protected void onPreExecute() {
           super.onPreExecute();
       }

       @Override
       protected void onPostExecute(UserClass s) {
           progressDialog.dismiss();

           DonneeAppl.GetInstance().CurrentUser = new UserClass( s.Name, s.Id );

           if( s != null ) {
               Intent ii = new Intent(getApplicationContext(), OutingActivity.class);
               ii.putExtra("user", s);
               ii.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(ii);
           }
           else
               createDialog("Error", "Contrôler le mot de passe et le nom");
       }

       @Override
       protected UserClass doInBackground(String... strings) {
           // Building Parameters

           u = null;

           List<NameValuePair> params = new ArrayList<NameValuePair>();
           params.add(new BasicNameValuePair("user", _user ));
           params.add(new BasicNameValuePair("pass", _pass ));
           // getting JSON string from URL
           JSONObject json = jParser.makeHttpRequest(LOGIN_URL, "POST", params);

           // Check your log cat for JSON reponse
           Log.d("Login : ", json.toString());

           try {
               // Checking for SUCCESS TAG
               int success = json.getInt(TAG_RESULT);

               if (success == 1) {
                   log = json.getJSONArray(TAG_USER);

                   for (int i = 0; i < log.length(); i++) {
                       JSONObject c = log.getJSONObject(i);

                       // Storing each json item in variable
                       Integer id = c.getInt(TAG_ID);
                       String name = c.getString(TAG_NOM);

                      u = new UserClass( name, id );
                   }
               }
               else {
                   return null;
               }
           } catch (JSONException e) {
               e.printStackTrace();
           }

           return u;
       }
   }
}
