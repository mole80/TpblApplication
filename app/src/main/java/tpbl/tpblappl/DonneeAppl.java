package tpbl.tpblappl;

import java.util.ArrayList;

public class DonneeAppl {

    public ArrayList<OutingClass> ListOuting = new ArrayList<>();
    public UserClass CurrentUser = null;

    private DonneeAppl()
    {}

    private static DonneeAppl INSTANCE = new DonneeAppl();

    public static DonneeAppl GetInstance()
    {
        return INSTANCE;
    }
}
