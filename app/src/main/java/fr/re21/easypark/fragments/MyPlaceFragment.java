package fr.re21.easypark.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import fr.re21.easypark.R;

/**
 * Created by maxime on 08/05/15.
 */
public class MyPlaceFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private SupportMapFragment map;
    private GoogleMap googleMap;
    private SlidingUpPanelLayout slidingPaneLayout;
    private LinearLayout slidingContainer;
    private TextView slidingTitle, slidingPlace, slidingAddr, slidingPrice;
    private FloatingActionButton slidingFab, positionFab;

    private final double lat=48.297053 , lng=4.076061;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //identification des élements
        View view = inflater.inflate(R.layout.fragment_my_place,container, false);

        slidingPaneLayout = (SlidingUpPanelLayout) view.findViewById(R.id.my_place_sliding_layout);
        slidingPaneLayout.setTouchEnabled(false);
        slidingContainer = (LinearLayout) view.findViewById(R.id.my_place_sliding_container);
        slidingTitle = (TextView) view.findViewById(R.id.my_place_sliding_title);
        slidingPlace = (TextView) view.findViewById(R.id.my_place_sliding_place);
        slidingAddr = (TextView) view.findViewById(R.id.my_place_sliding_addr);
        slidingPrice = (TextView) view.findViewById(R.id.my_place_price);
        slidingFab= (FloatingActionButton) view.findViewById(R.id.my_place_sliding_fab);
        positionFab= (FloatingActionButton) view.findViewById(R.id.my_place_position_fab);
        slidingFab.setOnClickListener(this);
        positionFab.setOnClickListener(this);

        map = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.my_place_map);
        map.getMapAsync(this);

        //cache le sliding panel
        showPanel(false);
        return view;
    }

    /**
     * detection des cliques
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(view.equals(positionFab) && googleMap!=null){//bouton affichage de la position
            if(googleMap.getMyLocation()!=null){
                //recupère la position et bouge la carte sur celle ci
                double lat = googleMap.getMyLocation().getLatitude();
                double lng = googleMap.getMyLocation().getLongitude();
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(lat,
                                lng));
                googleMap.animateCamera(center);
            } else {
                Toast.makeText(getActivity(), R.string.location_error, Toast.LENGTH_LONG).show();
            }
        } else if(view.equals(slidingFab)){//bouton de lancement du guidage par voiture
            //lance l'intent de guidage piéton via google map
            Uri gmmIntentUri = Uri.parse("google.navigation:q="+lat+","+lng+"&mode=w");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        }
    }

    /**
     * init la carte lorsque qu'elle apparait
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        showPanel(false);
        this.googleMap=googleMap;
        //reglage des param dela carte
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        //centre la carte sur la plce de la voiture
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat, lng)).zoom(18).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //ajoute le marker de la place
        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title("Ma Place")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        googleMap.addMarker(marker);

        //ajoute les info au sliding panel
        slidingTitle.setText("Ma place");
        slidingPlace.setText("10 minutes restantes");
        slidingAddr.setText("25 rue Emile Zola, 10000 Troyes");

        slidingContainer.setBackgroundResource(R.color.minute_stop_dark);
        slidingFab.setColorNormalResId(R.color.minute_stop_light);
        slidingFab.setColorPressedResId(R.color.minute_stop_dark);
        //affiche le sliding panel
        showPanel(true);
    }

    /**
     * affiche ou cache le sliding panel
     * @param show
     */
    public void showPanel(boolean show){
        if(show==true){//affiche
            slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            slidingFab.setVisibility(View.VISIBLE);
        } else {//cache
            slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            slidingFab.setVisibility(View.GONE);
        }
    }

    /**
     * detect un clique sur le marker et affiche le panel
     * @param marker
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        showPanel(true);
        return false;
    }

    /**
     * detect un clique sur la carte et cache le panel
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        showPanel(false);

    }
}
