package ch.unstable.ost;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Date;

import ch.unstable.ost.api.transport.model.ConnectionQuery;

public class NavigationStartActivity extends AppCompatActivity
        implements BaseNavigationFragment.OnRouteSelectionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new StandardNavigationFragment())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onRouteSelected(ConnectionQuery query) {
        Intent intent = new Intent(this, ConnectionListActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(ConnectionListActivity.EXTRA_QUERY, query);
        startActivity(intent);
        finish();
    }
}
