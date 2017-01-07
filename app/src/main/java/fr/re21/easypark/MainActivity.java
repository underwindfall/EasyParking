package fr.re21.easypark;


import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import fr.re21.easypark.fragments.FreePlaceFragment;
import fr.re21.easypark.fragments.HomeFragment;
import fr.re21.easypark.fragments.MyPlaceFragment;
import fr.re21.easypark.fragments.PoliceFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerCallbacks, OnMapReadyCallback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    private ArrayList<Fragment> fragmentList;
    private int fragmentPos;

    private SupportMapFragment map;
    private GoogleMap googleMap;
    private MaterialDialog mt;

    private final double lat=48.29881172611295, lng=4.0776872634887695;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //liste des fragments
        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new FreePlaceFragment());
        fragmentList.add(new MyPlaceFragment());
        fragmentList.add(new PoliceFragment());
        fragmentPos=-1;

        //identification du xml
        setContentView(R.layout.activity_main);
        //identification toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        //ajout du drawer. Contrsuit a partir de l'exemple https://github.com/kanytu/Android-studio-material-template
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
        mNavigationDrawerFragment.setUserData("EasyPark", "", BitmapFactory.decodeResource(getResources(), R.drawable.avatar));



    }

    /**
     * Dialoge qui s'affiche lorsque l'on clique sur signaler police
     */
    public void policeSeenDialog(){
        //si le dialog n'existe pas on le creer
        if(mt==null) {
            mt = new MaterialDialog.Builder(this)
                    .title(R.string.dialog_police_seen)
                    .customView(R.layout.dialog_layout, false)
                    .positiveText(R.string.dialog_police_seen_YES)
                    .negativeText(R.string.dialog_police_seen_NO)
                    .positiveColorRes(R.color.myPrimaryColor)
                    .negativeColorRes(R.color.myPrimaryColor)
                    .show();
        }else{
            //sinon on l'affiche
            mt.show();
        }

        //si la carte n'est pas créer, on le fait
       if(map == null) {
           map = (SupportMapFragment) getSupportFragmentManager()
                   .findFragmentById(R.id.police_seen_map);
           map.getMapAsync(this);
       }
    }

    /**
     * lorsque l'on clique sur un bouton du drawer, on change de fragment
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        changeFragment(position);
    }

    /**
     * quand on clique sur le bouton retour,
     * on ferme le drawer,
     * puis on revient à la page d'accueil, puis on quitte l'app
     */
    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen()){
            mNavigationDrawerFragment.closeDrawer();
        } else {
            if(fragmentPos==0){
                super.onBackPressed();
            } else {
                changeFragment(0);
                mNavigationDrawerFragment.setDrawerPosition(0);
            }
        }

    }

    /**
     * change la position surligner dans le drawer
     * @param position
     */
    public void changeDrawerPosition(int position){
        mNavigationDrawerFragment.setDrawerPosition(position);
    }

    /**
     * change le fragment apparent
     * @param position
     */
    public void changeFragment(int position){

        if(fragmentPos!=position){//si on ne  choisis pas le fragment déjà apparent
            //changement du fragment
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(R.animator.slide_in, R.animator.slide_out); //animation
            transaction.replace(R.id.container, fragmentList.get(position));
            transaction.commit();
            //changement du titre dans la toolbar
            if(position==0){
                if(mToolbar!=null) mToolbar.setTitle(R.string.fragment_home);
            } else if(position==1){
                if(mToolbar!=null) mToolbar.setTitle(R.string.fragment_free_place);
            } else if(position==2){
                if(mToolbar!=null) mToolbar.setTitle(R.string.fragment_my_place);
            } else if(position==3){
                if(mToolbar!=null) mToolbar.setTitle(R.string.fragment_police);
            }
            fragmentPos=position;
            this.invalidateOptionsMenu(); //met à jour lesmenu de la toolbar
        }
    }

    /**
     * paramètre la map google quand le dialog apparait (demo)
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        googleMap.setMyLocationEnabled(false);//affiche la location
        googleMap.getUiSettings().setMapToolbarEnabled(false); //enleve le bouton de menu
        googleMap.getUiSettings().setMyLocationButtonEnabled(false); //enleve lebouton de la place

        //positionne la carte
        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat, lng)).zoom(16).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //creer un cercle de 50m
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(lat, lng))   //set center
                .radius(50)   //set radius in meters
                .fillColor(getResources().getColor(R.color.circle_solid))  //default
                .strokeColor(getResources().getColor(R.color.circle_stroke))
                .strokeWidth(5);
        googleMap.addCircle(circleOptions);
    }


    /**
     * ajoute le menu a la toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()  && (fragmentPos>=1 && fragmentPos<=3)) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * gère les clique sur le menu
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //action sur le bouton refresh lors des fragment 1 2 et 3
        if (id == R.id.action_refresh ) {
            if(fragmentPos==1){
                ((FreePlaceFragment) fragmentList.get(1)).loadInfo();
            } else if(fragmentPos==2){
                //demo
            } else if (fragmentPos==3){
                //demo
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
