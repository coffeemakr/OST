package ch.unstable.ost;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import ch.unstable.ost.api.model.Section;
import ch.unstable.ost.views.lists.station.SectionsStopsListAdapter;

public class SectionDetailFragment extends Fragment {

    private static final String KEY_SECTION = "SectionDetailFragment.KEY_SECTION";
    private Section mSection;
    private SectionsStopsListAdapter mStopsListAdapter;

    public static SectionDetailFragment newInstance(Section section) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_SECTION, section);
        SectionDetailFragment fragment = new SectionDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSection = savedInstanceState.getParcelable(KEY_SECTION);
        } else {
            mSection = getArguments().getParcelable(KEY_SECTION);
        }
        if (mSection == null) {
            throw new IllegalStateException("section not set");
        }
        mStopsListAdapter = new SectionsStopsListAdapter();
        mStopsListAdapter.setElements(mSection.getStops());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_SECTION, mSection);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_journey_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView stationsList = view.findViewById(R.id.stationsList);
        stationsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        stationsList.setAdapter(mStopsListAdapter);
    }
}
