package tpbl.tpblappl;

import android.os.Parcelable;
import android.os.Parcel;
import android.support.annotation.NonNull;



public class UserClass implements Parcelable {

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
        }

    public  UserClass(String name, int id)
    {
        Name = name;
        Id = id;
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
