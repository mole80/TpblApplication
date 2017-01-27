package tpbl.tpblappl;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        try
        {
            //TODO : Create a method to send token and not duplicate code with login
            JSONParser jParser = new JSONParser();

            DonneeAppl d = DonneeAppl.GetInstance();
            UserClass cu = d.CurrentUser;

            String tok = FirebaseInstanceId.getInstance().getToken();

            if( cu != null ) {
                cu.FirebaseToken = tok;

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id", Integer.toString(cu.Id)));
                params.add(new BasicNameValuePair("tokenFirebase", cu.FirebaseToken));
                params.add(new BasicNameValuePair("token", cu.Token));

                JSONObject json = jParser.makeHttpRequest("http://www.tpbl.ch/service/token.php", "POST", params);

                if (json != null)
                    Log.d("Token : ", json.toString());

                d.TokenMustBeUpdated = false;
            }
            else{
                d.TokenMustBeUpdated = true;
                Log.d(TAG, "Refreshed token: " + tok);
            }
//           int success = json.getInt("result");
        }
        catch (Exception ex)
        {
            String s = ex.getMessage();
        }
    }
}
