package tpbl.tpblappl;

public class CalendarClass {
    public String Name;
    public String Id;

    public CalendarClass(String name, String id){
        Name = name;
        Id = id;
    }

    @Override
    public String toString() {
        return Name;
    }
}
