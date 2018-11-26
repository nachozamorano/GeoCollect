package e.inf_b.geocollect;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class Mapa extends FragmentActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    Marker m;
    String canal = "my_channel_01";
    private Context mContext=Mapa.this;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        Toolbar tb = findViewById(R.id.toolbar);
        tb.setSubtitle("Bienvenido GeoCollector");
        enableMyLocationIfPermitted();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(latitude,
                    longitude));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);

            googleMap.moveCamera(center);
            googleMap.animateCamera(zoom);
        }
        enableMyLocationIfPermitted();
        mMap.setOnMyLocationClickListener(onMyLocationClickListener);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(16);
        Double [] Latitud ={-33.013207,-33.012096,-33.012813, -33.011019,-33.01124 ,-32.771897};
        Double [] Longitud ={-71.54271,-71.543596, -71.545426,-71.543577,-71.545321, -71.53498};
        String [] traslado ={"13/11/2018","14/11/2018","16/11/2018","18/11/2018","10/12/2018","1/12/2018"};
        String [] Estado = {"Activo","Fuera de servicio","Fuera de servicio","Fuera de servicio","Activo","Activo"};
        MarkerOptions markerOptions = new MarkerOptions();
        for(int i=0; i<=5;i++) {
            if (Estado[i].equals("Activo")) {
                final LatLng punto1 = new LatLng(Latitud[i], Longitud[i]);
                markerOptions = new MarkerOptions();
                markerOptions.position(punto1)
                        .title("Batea")
                        .snippet("Estado:" + " " + Estado[i])
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            } else {
                final LatLng punto1 = new LatLng(Latitud[i], Longitud[i]);
                markerOptions = new MarkerOptions();
                markerOptions.position(punto1)
                        .title("Batea")
                        .snippet("Estado:" + " " + Estado[i])
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
            InfoWindowData info = new InfoWindowData();
            info.setDetalle("Fecha de traslado: "+ traslado[i]);
            CustomInfoWindowAdapter customInfoWindow = new CustomInfoWindowAdapter(this);
            mMap.setInfoWindowAdapter(customInfoWindow);
            m = mMap.addMarker(markerOptions);
            m.setTag(info);
            m.showInfoWindow();
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(final Marker marker) {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(Mapa.this);
                    dialogo1.setTitle("Mensaje");
                    dialogo1.setMessage("¿Quiere ir a este punto?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            aceptar(marker);
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogo1, int id) {
                            cancelar();
                        }
                    });
                    dialogo1.show();

                }
            });
        }

    }
    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
            new GoogleMap.OnMyLocationClickListener() {
                @Override
                public void onMyLocationClick(@NonNull Location location) {

                    mMap.setMinZoomPreference(16);

                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(new LatLng(location.getLatitude(),
                            location.getLongitude()));

                    mMap.addCircle(circleOptions);
                }
            };
    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }
    private void showDefaultLocation() {
        Toast.makeText(this, "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(47.6739881, -122.121512);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }

        }
    }
    public void aceptar(Marker marker) {
        int notificationId = 3;

        Intent viewIntent = new Intent(this, Mapa.class);
        viewIntent.putExtra("NotiID", "Notification ID is " + notificationId);

        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW);
        Uri geoUri = Uri.parse("google.navigation:q="+marker.getPosition().latitude+","+marker.getPosition().longitude);
        mapIntent.setData(geoUri);
        PendingIntent mapPendingIntent =
                PendingIntent.getActivity(this, 0, mapIntent, 0);

        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.common_google_signin_btn_icon_dark_focused,
                        "Ruta", mapPendingIntent)
                        .build();


        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, canal)
                        .setSmallIcon(R.mipmap.icono)
                        .setContentTitle("Notificación Geocollect")
                        .setContentText("Toque la notificación del reloj")
                        .setSubText("Ir a la batea")
                        .setContentIntent(viewPendingIntent)
                        .setChannelId(canal)
                        .extend(new WearableExtender().addAction(action))
                        .setLocalOnly(false);

        mostrarNotificacion(notificationId, notificationBuilder.build());
        Intent serviceIntent = new Intent(Mapa.this, MyMessagingService.class);
        serviceIntent.setAction(MyMessagingService.SEND_MESSAGE_ACTION);
        startService(serviceIntent);
    }

    public void cancelar() {
    }

    public void mostrarNotificacion(int id, Notification notificacion) {
        NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "my channel";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(canal, name, importance);
            channel.setDescription(description);

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(id, notificacion);

    }
    private Intent getMessageReadIntent(int id) {
        return new Intent().setAction("GeoCollect_read")
                .putExtra("conversation_id", id);
    }
    private Intent getMessageReplyIntent(int conversationId) {
        return new Intent().setAction("GeoCollect_reply")
                .putExtra("conversation_id", conversationId);
    }

}

