package tpbl.tpblappl;

import android.os.Parcelable;
import android.os.Parcel;

import java.math.BigInteger;
import java.security.MessageDigest;
import android.support.annotation.NonNull;



public class UserClass implements Parcelable {

    String Token;
    int Id;
    String Name;

    public static final android.os.Parcelable.Creator<UserClass>CREATOR=new Parcelable.Creator<UserClass>(){
        @Override
        public UserClass createFromParcel(Parcel in){
            return new UserClass(in);
        }

        @Override
        public UserClass[]newArray(int size){
            return new UserClass[size];
        }
    };

    public UserClass(Parcel in)
        {
            Name = in.readString();
            Id = in.readInt();
            CreateToken();
        }

    public  UserClass(String name, int id)
    {
        Name = name;
        Id = id;
        CreateToken();
    }


    void CreateToken()
    {
        Token = ConvertMd5(Name + "tutu123" + Integer.toString(Id) );
    }

    String ConvertMd5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return password;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Name);
        parcel.writeInt(Id);
    }
}
