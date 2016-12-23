package tpbl.tpblappl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OutingAdapter  extends BaseAdapter{
    private  List<OutingClass> _listOuting;
    private Context _context;
    private LayoutInflater _inflater;

    public OutingAdapter( Context context, List<OutingClass> list )
    {
        _context = context;
        _listOuting = list;
        _inflater = LayoutInflater.from(_context);
    }

    public int getCount(){
        return _listOuting.size();
    }

    public OutingClass getItem(int position){
        return _listOuting.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LinearLayout layoutItem;

        if( convertView == null ){
            layoutItem = (LinearLayout)_inflater.inflate(R.layout.outing_layout, parent, false);
        }
        else {
            layoutItem = (LinearLayout) convertView;
        }

        TextView tv_Id = (TextView)layoutItem.findViewById(R.id.TV_Id);
        TextView tv_Title = (TextView)layoutItem.findViewById(R.id.TV_Title);

        tv_Id.setText( Integer.toString( _listOuting.get(position).Id ) );
        tv_Title.setText(_listOuting.get(position).Name);

        if( _listOuting.get(position).StatePres == eStatePresOuting.Absent )
            layoutItem.setBackgroundColor(Color.MAGENTA);
        else
            layoutItem.setBackgroundColor(Color.BLUE);

        return layoutItem;
    }
}
