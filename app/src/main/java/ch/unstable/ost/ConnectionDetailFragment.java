package ch.unstable.ost;


import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ch.unstable.ost.api.transport.model.Connection;


public class ConnectionDetailFragment extends Fragment {


    private static final String KEY_CONNECTION = "KEY_CONNECTION";
    private Connection mConnection;
    private RecyclerView mSectionsList;
    private SectionListAdapter mSectionListAdapter;

    public ConnectionDetailFragment() {
        // Required empty public constructor
    }


    public static ConnectionDetailFragment newInstance(Connection connection) {
        if(connection == null) {
            throw new NullPointerException("connection is null");
        }
        Bundle arguments = new Bundle();
        arguments.putParcelable(KEY_CONNECTION, connection);
        ConnectionDetailFragment detailFragment = new ConnectionDetailFragment();
        detailFragment.setArguments(arguments);
        return detailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            mConnection = savedInstanceState.getParcelable(KEY_CONNECTION);
        } else {
            mConnection = getArguments().getParcelable(KEY_CONNECTION);
        }
        mSectionListAdapter = new SectionListAdapter();

        mSectionListAdapter.setSections(mConnection.getSections());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_CONNECTION, mConnection);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_connection_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSectionsList = (RecyclerView) view.findViewById(R.id.sectionsList);
        mSectionsList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mSectionsList.setAdapter(mSectionListAdapter);
    }
}
