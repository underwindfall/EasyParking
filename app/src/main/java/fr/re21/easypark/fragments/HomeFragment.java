package fr.re21.easypark.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import fr.re21.easypark.MainActivity;
import fr.re21.easypark.R;

/**
 * Created by maxime on 08/05/15.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    private CardView freePlace, myPlace, police, policeSeen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //identification des elements
        View view = inflater.inflate(R.layout.fragment_home,container, false);

        freePlace = (CardView) view.findViewById(R.id.home_button_free_place);
        myPlace = (CardView) view.findViewById(R.id.home_button_my_place);
        police = (CardView) view.findViewById(R.id.home_button_police);
        policeSeen = (CardView) view.findViewById(R.id.home_button_police_seen);
        freePlace.setOnClickListener(this);
        myPlace.setOnClickListener(this);
        police.setOnClickListener(this);
        policeSeen.setOnClickListener(this);
        return view;
    }

    /**
     * detection des cliques
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.equals(freePlace)){//bouton des places libres
            ((MainActivity) getActivity()).changeFragment(1);
            ((MainActivity) getActivity()).changeDrawerPosition(1);
        } else if(v.equals(myPlace)){ //bouton de "ma place"
            ((MainActivity) getActivity()).changeFragment(2);
            ((MainActivity) getActivity()).changeDrawerPosition(2);
        } else if(v.equals(police)){ //bouton de la place de la police
            ((MainActivity) getActivity()).changeFragment(3);
            ((MainActivity) getActivity()).changeDrawerPosition(3);
        } else if(v.equals(policeSeen)){ //bouton pour notifier la police
            ((MainActivity) getActivity()).policeSeenDialog();
        }
    }
}
