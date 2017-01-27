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
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {

        try {
            JSONParser jParser = new JSONParser();
            UserClass cu = DonneeAppl.GetInstance().CurrentUser;
            cu.FirebaseToken = token;

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", Integer.toString(cu.Id)));
            params.add(new BasicNameValuePair("tokenFirebase", cu.FirebaseToken));
            params.add(new BasicNameValuePair("token", cu.Token));

            JSONObject json = jParser.makeHttpRequest("http://www.tpbl.ch/service/token.php", "POST", params);

            if( json != null )
                Log.d("Token : ", json.toString() );
        }
        catch (Exception ex)
        {
            Log.e("Error : ", ex.getMessage() );
        }
    }
}
