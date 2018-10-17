package e.inf_b.geocollect;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;

    public CustomInfoWindowAdapter(Context ctx){
        context = ctx;
    }


    @Override
    public View getInfoContents(final Marker m) {
        //Carga layout personalizado.
        View v = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.infowindow_layout, null);
        TextView nombre =(TextView)v.findViewById(R.id.info_window_nombre);
        TextView detalle =(TextView)v.findViewById(R.id.info_window_placas);
        TextView estado =(TextView)v.findViewById(R.id.info_window_estado);
        nombre.setText(m.getTitle());
        estado.setText(m.getSnippet());
                InfoWindowData infoWindowData = (InfoWindowData) m.getTag();

        detalle.setText(infoWindowData.getDetalle());
        return v;
    }

    @Override
    public View getInfoWindow(Marker m) {
        return null;
    }

}
