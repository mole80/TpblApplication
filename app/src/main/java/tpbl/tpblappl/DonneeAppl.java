package tpbl.tpblappl;

import java.util.ArrayList;

public class DonneeAppl {

    public ArrayList<SortieClass> ListSortie = new ArrayList<>();

    private DonneeAppl()
    {}

    private static DonneeAppl INSTANCE = new DonneeAppl();

    public static DonneeAppl getInstance()
    {
        return INSTANCE;
    }
}
