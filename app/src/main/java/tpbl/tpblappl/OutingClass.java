package tpbl.tpblappl;

enum eStatePresOuting
{
    Present,
    Absent,
    NotSure,
}

public class OutingClass
{
    public OutingClass(int id, String name)
    {
       Id = id;
        Name = name;
    }

    int Id;
    String Name;
    eStatePresOuting StatePres;
}
