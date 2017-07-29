package ch.unstable.ost;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.unstable.ost.api.transport.TransportAPI;
import ch.unstable.ost.api.transport.model.Location;


public class StandardNavigationFragment extends BaseNavigationFragment {


    private AppCompatAutoCompleteTextView startView;
    private AppCompatAutoCompleteTextView destinationView;
    private TransportAPI timetableAPI = new TransportAPI();
    private TextWatcher textWatcher;
    private Button searchButton;
    private View.OnClickListener mOnSearchClickListener;
    private TextView.OnEditorActionListener mOnEditTextListener;


    public StandardNavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mOnSearchClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRouteSubmitted();
            }
        };

        mOnEditTextListener = new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onRouteSubmitted();
                    return true;
                }
                return false;
            }
        };
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isRouteSelected()) {
                    setButtonEnabled(true);
                } else {
                    setButtonEnabled(false);
                }
            }
        };
    }

    private void onRouteSubmitted() {
        selectRoute(startView.getText().toString(), destinationView.getText().toString());
    }

    private boolean isRouteSelected() {
        if (startView == null || destinationView == null) {
            return false;
        }
        return startView.getText().length() > 0 && destinationView.getText().length() > 0;
    }

    private void setButtonEnabled(boolean enabled) {
        if(searchButton != null) {
            searchButton.setEnabled(enabled);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_standart_navigation, container, false);
        startView = (AppCompatAutoCompleteTextView) view.findViewById(R.id.start);
        startView.setAdapter(new AutoCompletionAdapter(getContext(), timetableAPI));
        startView.addTextChangedListener(textWatcher);

        destinationView = (AppCompatAutoCompleteTextView) view.findViewById(R.id.destination);
        destinationView.setAdapter(new AutoCompletionAdapter(getContext(), timetableAPI));
        destinationView.addTextChangedListener(textWatcher);

        searchButton = (Button) view.findViewById(R.id.button_seach_route);
        searchButton.setOnClickListener(mOnSearchClickListener);
        return view;
    }

    private static class AutoCompletionAdapter extends ArrayAdapter<String> implements Filterable {
        public final TransportAPI transportAPI;
        private List<String> resultList;

        public AutoCompletionAdapter(@NonNull Context context, TransportAPI transportAPI) {
            super(context, android.R.layout.simple_list_item_1, android.R.id.text1);
            this.transportAPI = transportAPI;
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    List<String> results;
                    if (constraint != null) {

                        // Retrieve the autocomplete results.
                        try {
                            List<Location> locations = transportAPI.getLocationsByQuery(constraint.toString());
                            results = new ArrayList<>(locations.size());
                            for(Location location: locations) {
                                results.add(location.getName());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            results = Collections.emptyList();
                        }

                        // Assign the data to the FilterResults
                        filterResults.values = results;
                        filterResults.count = results.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        resultList = (List<String>) results.values;
                        notifyDataSetChanged();
                    } else {
                        resultList = Collections.emptyList();
                        notifyDataSetInvalidated();
                    }
                }
            };
        }
    }
}
