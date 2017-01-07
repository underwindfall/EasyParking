package fr.re21.easypark.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


import android.support.v4.widget.SlidingPaneLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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


import java.util.ArrayList;

import fr.re21.easypark.R;
import fr.re21.easypark.customInterface.ServerResponseInterface;
import fr.re21.easypark.entity.ClosedParking;
import fr.re21.easypark.entity.EntityList;

import static android.view.View.VISIBLE;

/**
 * Created by maxime on 08/05/15.
 */
public class FreePlaceFragment extends Fragment implements OnMapReadyCallback, ServerResponseInterface, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, View.OnClickListener {

    private SupportMapFragment map;
    private GoogleMap googleMap;
    private SlidingUpPanelLayout slidingPaneLayout;
    private ArrayList<Marker> closedParkingMarkerList;
    private LinearLayout slidingContainer;
    private RelativeLayout loadingContainer;
    private TextView slidingTitle, slidingPlace, slidingAddr, slidingHourWeek, slidingHourSunday, privateParking;
    private FloatingActionButton slidingFab, positionFab;
    private ImageView cardPay, cashPay, coinsPay;
    private ClosedParking closedParking;


    private final double lat=48.29881172611295, lng=4.0776872634887695;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //identification des elements
        View view = inflater.inflate(R.layout.fragment_free_place,container, false);

        slidingPaneLayout = (SlidingUpPanelLayout) view.findViewById(R.id.free_place_sliding_layout);

        slidingContainer = (LinearLayout) view.findViewById(R.id.free_place_sliding_container);
        slidingTitle = (TextView) view.findViewById(R.id.free_place_sliding_title);
        slidingPlace = (TextView) view.findViewById(R.id.free_place_sliding_place);
        slidingAddr = (TextView) view.findViewById(R.id.free_place_sliding_addr);
        slidingFab= (FloatingActionButton) view.findViewById(R.id.free_place_sliding_fab);
        positionFab= (FloatingActionButton) view.findViewById(R.id.free_place_position_fab);
        slidingFab.setOnClickListener(this);
        positionFab.setOnClickListener(this);
        slidingHourWeek = (TextView) view.findViewById(R.id.free_place_week_hour);
        slidingHourSunday = (TextView) view.findViewById(R.id.free_place_sunday_hour);
        privateParking = (TextView) view.findViewById(R.id.free_place_private);

        cardPay = (ImageView) view.findViewById(R.id.free_place_card);
        cashPay = (ImageView) view.findViewById(R.id.free_place_cash);
        coinsPay = (ImageView) view.findViewById(R.id.free_place_coins);

        loadingContainer = (RelativeLayout) view.findViewById(R.id.loadingContainer);

        map = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.free_place_map);
        map.getMapAsync(this);

        //init de la liste
        closedParkingMarkerList = new ArrayList<>();
        //cache le sliding panel
        showPanel(false);

        return view;
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

        //position de la carte sur le centre de Troyes
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat, lng)).zoom(8).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(14);
        googleMap.animateCamera(zoom);

        loadInfo();
    }

    /**
     * charge les info depuis le serveur
     */
    public void loadInfo(){
        //vide la carte et la liste
        closedParkingMarkerList.clear();
        googleMap.clear();
        //cahce le sliding panel
        showPanel(false);
        //fait la requette
        ClosedParking.getCloseParkingList(EntityList.closedParkingList, this, getActivity());
        //affiche la progresse bar
        loadingContainer.setVisibility(VISIBLE);
    }

    /**
     * ajoute les marker à la carte
     * @param closedParkingArrayList
     */
    private void addMarkerList(ArrayList<ClosedParking> closedParkingArrayList){
        //chaque marker est stocker dans la liste
        for(ClosedParking parking : closedParkingArrayList){
            //creation du marker
            MarkerOptions marker = new MarkerOptions()
                    .position(new LatLng(parking.getLatitude(), parking.getLongitude()))
                    .title(parking.getName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            closedParkingMarkerList.add(googleMap.addMarker(marker));
        }
    }

    /**
     * appelé lorsque la requette de donnée a réussi
     * @param method
     * @param type
     */
    @Override
    public void onEventCompleted(int method, String type) {
        //cache la progress bar
        loadingContainer.setVisibility(View.INVISIBLE);
        if(method==ClosedParking.GET && type==ClosedParking.TYPE){
            //ajoute les marker
            addMarkerList(EntityList.closedParkingList);
        }
    }

    /**
     * appelé lorsque la requette de donnée a raté
     * @param method
     * @param type
     */
    @Override
    public void onEventFailed(int method, String type) {
        //cache la progress bar
        loadingContainer.setVisibility(View.INVISIBLE);
        //affiche un message d'erreur
        Toast.makeText(getActivity(), R.string.loading_error, Toast.LENGTH_LONG).show();

    }

    /**
     * lorsque l'on clique sur un marker
     * @param marker
     * @return
     */
    @Override
    public boolean onMarkerClick(Marker marker) {

        closedParking = null;
        //identification du marker avec la liste des parking
        for(ClosedParking parking : EntityList.closedParkingList){
            if(parking.getName().equals(marker.getTitle())){
                closedParking=parking;
                System.out.println(marker.getTitle());
                break;
            }
        }
        if(closedParking!=null){
            //change les info du sliding panel
            slidingContainer.setBackgroundResource(R.color.closed_parking_dark);
            slidingFab.setColorNormalResId(R.color.closed_parking_light);
            slidingFab.setColorPressedResId(R.color.closed_parking_dark);
            slidingTitle.setText(closedParking.getName());
            slidingPlace.setText(closedParking.getRemainPlace() + "/" + closedParking.getTotalPlace() + " Places Libre");
            slidingAddr.setText(closedParking.getAdresse()+" "+closedParking.getZipCode()+" "+closedParking.getCity());
            slidingHourWeek.setText(closedParking.getWeekHour());
            slidingHourSunday.setText(closedParking.getSundayHour());
            if(closedParking.isPrivatePark()==false){
                privateParking.setVisibility(View.GONE);
            } else {
                privateParking.setVisibility(VISIBLE);
            }
            if(closedParking.isCard()==false){
                cardPay.setVisibility(View.GONE);
            } else {
                cardPay.setVisibility(VISIBLE);
            }
            if(closedParking.isCash()==false){
                cashPay.setVisibility(View.GONE);
            } else {
                cashPay.setVisibility(VISIBLE);
            }
            if(closedParking.isCoins()==false){
                coinsPay.setVisibility(View.GONE);
            } else {
                coinsPay.setVisibility(VISIBLE);
            }
        }
        //affiche le panel
        showPanel(true);
        return false;
    }

    /**
     * affiche ou cache le sliding panel
     * @param show
     */
    public void showPanel(boolean show){
        if(show==true){//affiche
            slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            slidingFab.setVisibility(VISIBLE);
        } else {//cache
            slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
            slidingFab.setVisibility(View.GONE);
        }
    }

    /**
     * detect un clique sur la carte et cache le panel
     * @param latLng
     */
    @Override
    public void onMapClick(LatLng latLng) {
        showPanel(false);
    }

    /**
     * detect les clique sur les boutons
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(view.equals(positionFab) && googleMap!=null){//bouton affichage de la position
            //recupère la position et bouge la carte sur celle ci
            if(googleMap.getMyLocation()!=null){
                double lat = googleMap.getMyLocation().getLatitude();
                double lng = googleMap.getMyLocation().getLongitude();
                CameraUpdate center=
                        CameraUpdateFactory.newLatLng(new LatLng(lat,
                                lng));
                googleMap.animateCamera(center);
            } else {
                Toast.makeText(getActivity(), R.string.location_error, Toast.LENGTH_LONG).show();
            }

        } else if(view.equals(slidingFab)){//bouton de lancement du guidage
            if(closedParking!=null){
                //lance l'intent de guidage voiture via google map
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+closedParking.getLatitude()+","+closedParking.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        }

    }
}
