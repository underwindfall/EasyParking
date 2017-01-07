package fr.re21.easypark.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import fr.re21.easypark.R;

/**
 * Created by maxime on 08/05/15.
 */
public class PoliceFragment extends Fragment implements OnMapReadyCallback {

    private SupportMapFragment map;
    private GoogleMap googleMap;

    private final double lat=48.29881172611295, lng=4.0776872634887695;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //identification des élements
        View view = inflater.inflate(R.layout.fragment_police,container, false);

        map = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.police_map);
        map.getMapAsync(this);
        return view;
    }

    /**
     * init la carte lorsque qu'elle apparait
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        googleMap.setMyLocationEnabled(true);
        //reglage de la map
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        //centre la map sur un point (démo)
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat, lng)).zoom(16).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        drawCircle();
    }

    /**
     * trace des cercle de 50m sur la map (démo)
     */
    private void drawCircle(){
        //création des cercle
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(lat, lng))   //set center
                .radius(50)   //set radius in meters
                .fillColor(getResources().getColor(R.color.circle_solid))  //default
                .strokeColor(getResources().getColor(R.color.circle_stroke))
                .strokeWidth(5);


        CircleOptions circleOptions2 = new CircleOptions()
                .center(new LatLng(48.299502, 4.076786))   //set center
                .radius(50)   //set radius in meters
                .fillColor(getResources().getColor(R.color.circle_solid2))  //default
                .strokeColor(getResources().getColor(R.color.circle_stroke2))
                .strokeWidth(5);

        CircleOptions circleOptions3 = new CircleOptions()
                .center(new LatLng(48.299814, 4.075423))   //set center
                .radius(50)   //set radius in meters
                .fillColor(getResources().getColor(R.color.circle_solid3))  //default
                .strokeColor(getResources().getColor(R.color.circle_stroke3))
                .strokeWidth(5);

        //ajout des cercles
        googleMap.addCircle(circleOptions);
        googleMap.addCircle(circleOptions2);
        googleMap.addCircle(circleOptions3);
    }
}
