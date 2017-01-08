package tpbl.tpblappl;

import android.net.NetworkInfo;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

enum eStatePresOuting
{
    Present,
    Absent,
    NotSure,
    NoAnswer
}

public class OutingClass
{
    public OutingClass(int id, String name, String pres)
    {
       Id = id;
        Name = name;

        DateAsString = "";

        if( pres.equals("P")  )
            StatePres = eStatePresOuting.Present;
        else if( pres.equals("A") )
            StatePres = eStatePresOuting.Absent;
        else if( pres.equals("N") )
            StatePres =eStatePresOuting.NotSure;
        else
            StatePres = eStatePresOuting.NoAnswer;
    }

    public void SetDate(String date)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy:MM:dd");
        OutingDate = new Date();

        try {
            OutingDate = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateAsString = date;
    }

    public String GetPresForSite()
    {
        if( StatePres == eStatePresOuting.NotSure )
            return "N";
        else if ( StatePres == eStatePresOuting.Present )
            return "P";
        else if ( StatePres == eStatePresOuting.Absent )
            return "A";
        else
            return "";
    }

    public String GetDate()
    {
       return  DateFormat.format("dd-MM-yyyy", OutingDate).toString();
    }

    String InfosPrive;
    Date OutingDate;
    String Infos;
    String DateAsString;
    int Id;
    String Name;
    eStatePresOuting StatePres;
}
