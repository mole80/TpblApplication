package tpbl.tpblappl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

        OutingClass out = _listOuting.get(position);

        // TODO : Check if ok with memory
        // Is use to refresh layout for each item to set the background color
        layoutItem = (LinearLayout)_inflater.inflate(R.layout.outing_layout, parent, false);
        /*if( convertView == null ){
            layoutItem = (LinearLayout)_inflater.inflate(R.layout.outing_layout, parent, false);
        }
        else {
            layoutItem = (LinearLayout) convertView;
        }*/

        RelativeLayout rl_outing = (RelativeLayout) layoutItem.findViewById(R.id.RL_outing);
        LinearLayout ll_outing = (LinearLayout) layoutItem.findViewById(R.id.LL_outingList);

        TextView tv_Title = (TextView)layoutItem.findViewById(R.id.TV_Title);
        TextView tv_pres = (TextView)layoutItem.findViewById(R.id.TV_Pres);
        TextView tv_Date = (TextView)layoutItem.findViewById(R.id.TV_Date);
        TextView tv_Location = (TextView)layoutItem.findViewById(R.id.TV_Location);

        //tv_Id.setText( Integer.toString( _listOuting.get(position).Id ) );

        tv_Location.setText( out.Location );

        String name = "";
        if( out.Type != eTypeOfOuting.Official ) {
            name += "P - ";
            int colInt = ContextCompat.getColor(_context, R.color.ColorPrivOuting);
            rl_outing.setBackgroundResource( R.color.ColorPrivOuting );
            //rl_outing.setBackgroundColor ( colInt );
            //ll_outing.setBackgroundColor( colInt );
        }

        name += out.Name;

        tv_Title.setText( name );
        tv_Date.setText( _listOuting.get(position).GetDate() );


        TextView tv_state = (TextView)layoutItem.findViewById(R.id.TV_stateOuting);
        if ( out.State == eState.Edition )
        {
            tv_state.setText("Sondage");
            tv_state.setTextColor(Color.YELLOW);
        }
        else if ( out.State == eState.Validate )
        {
            tv_state.setText("Valider");
            tv_state.setTextColor(Color.GREEN);
        }
        else if ( out.State == eState.Cancel )
        {
            tv_state.setText("Annulée");
            tv_state.setTextColor(Color.RED);
        }

        if( _listOuting.get(position).StatePres == eStatePresOuting.Absent )
            tv_pres.setText( "Absent" );
        else if( _listOuting.get(position).StatePres == eStatePresOuting.Present )
            tv_pres.setText( "Pres" );
        else if( _listOuting.get(position).StatePres == eStatePresOuting.NotSure )
            tv_pres.setText( "NS" );
        else
            tv_pres.setText( "----" );

        tv_pres.setTextColor(Color.BLACK);

        if( _listOuting.get(position).StatePres == eStatePresOuting.Absent )
            tv_pres.setBackgroundColor(Color.RED);
        else if( _listOuting.get(position).StatePres == eStatePresOuting.Present )
            tv_pres.setBackgroundColor(Color.GREEN);
        else if( _listOuting.get(position).StatePres == eStatePresOuting.NotSure )
            tv_pres.setBackgroundColor(Color.YELLOW);
        else
            tv_pres.setBackgroundColor(Color.WHITE);

        return layoutItem;
    }
}
