package ch.unstable.ost;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ch.unstable.ost.api.model.impl.Connection;
import ch.unstable.ost.api.model.impl.Section;
import ch.unstable.ost.preference.SettingsActivity;
import ch.unstable.ost.theme.ThemedActivity;

public class ConnectionDetailActivity extends ThemedActivity implements ConnectionDetailFragment.OnConnectionDetailInteractionListener {

    public static final String EXTRA_CONNECTION = "EXTRA_CONNECTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            Connection connection = getIntent().getParcelableExtra(EXTRA_CONNECTION);
            ConnectionDetailFragment fragment = ConnectionDetailFragment.newInstance(connection);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onSectionSelected(@NonNull Section section) {
        SectionDetailFragment fragment = SectionDetailFragment.newInstance(section);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
        }
        return false;
    }
}
