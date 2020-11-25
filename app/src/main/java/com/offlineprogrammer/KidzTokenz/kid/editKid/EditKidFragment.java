package com.offlineprogrammer.KidzTokenz.kid.editKid;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.offlineprogrammer.KidzTokenz.R;
import com.offlineprogrammer.KidzTokenz.kid.Kid;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditKidFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditKidFragment extends Fragment {


    private static final String ARG_PARAM1 = "selectedKid";
    private static final int CAMERA_REQUEST = 1888;
    public Context context;
    RecyclerView monstersRecyclerView;
    List<Monster> monsters;
    Monster oMonster;
    MonstersAdapter monstersAdapter;
    private Kid m_selectedKid;
    

    public EditKidFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param selectedKid Parameter 1.
     * @return A new instance of fragment EditKidFragment.
     */

    public static EditKidFragment newInstance(Kid selectedKid) {
        EditKidFragment fragment = new EditKidFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, selectedKid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            m_selectedKid = getArguments().getParcelable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_kid, container, false);
    }

    public void onAttach(Context context2) {
        super.onAttach(context2);
        this.context = context2;
    }

    public void onResume() {
        super.onResume();
    }

    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        if (this.m_selectedKid == null) {
//            ((KidActivity) this.context).setOkAndFinish();
        } else {
            initViews(view);
        }
    }

    private void initViews(View view) {

        monstersRecyclerView = view.findViewById(R.id.monster_image_recyclerview);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(context, 2);
        monstersRecyclerView.setLayoutManager(mGridLayoutManager);
        int largePadding = getResources().getDimensionPixelSize(R.dimen.ktz_kidz_grid_spacing);
        int smallPadding = getResources().getDimensionPixelSize(R.dimen.ktz_kidz_grid_spacing_small);
        monstersRecyclerView.addItemDecoration(new MonstersGridItemDecoration(largePadding, smallPadding));
        monsters = new ArrayList<>();
        oMonster = new Monster("M1", "m1");
        monsters.add(oMonster);
        oMonster = new Monster("M2", "m2");
        monsters.add(oMonster);

        oMonster = new Monster("M3", "m3");
        monsters.add(oMonster);

        oMonster = new Monster("M4", "m4");
        monsters.add(oMonster);

        oMonster = new Monster("M5", "m5");
        monsters.add(oMonster);

        oMonster = new Monster("M6", "m6");
        monsters.add(oMonster);

        oMonster = new Monster("M7", "m7");
        monsters.add(oMonster);

        oMonster = new Monster("M8", "m8");
        monsters.add(oMonster);

        oMonster = new Monster("M9", "m9");
        monsters.add(oMonster);

        oMonster = new Monster("M10", "m10");
        monsters.add(oMonster);

        oMonster = new Monster("M11", "m11");
        monsters.add(oMonster);


        monstersAdapter = new MonstersAdapter(context, monsters);
        monstersRecyclerView.setAdapter(monstersAdapter);


    }

}