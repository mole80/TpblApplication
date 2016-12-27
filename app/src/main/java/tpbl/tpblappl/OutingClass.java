package tpbl.tpblappl;

enum eStatePresOuting
{
    Present,
    Absent,
    NotSure,
    NoAnswer;
}

public class OutingClass
{
    public OutingClass(int id, String name, String pres)
    {
       Id = id;
        Name = name;

        if( pres.equals("P")  )
            StatePres = eStatePresOuting.Present;
        else if( pres.equals("A") )
            StatePres = eStatePresOuting.Absent;
        else if( pres.equals("N") )
            StatePres =eStatePresOuting.NotSure;
        else
            StatePres = eStatePresOuting.NoAnswer;
    }

    int Id;
    String Name;
    eStatePresOuting StatePres;
}
