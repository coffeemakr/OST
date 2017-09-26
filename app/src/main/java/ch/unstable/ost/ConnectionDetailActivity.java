package ch.unstable.ost;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.Section;
import ch.unstable.ost.preference.SettingsActivity;
import ch.unstable.ost.theme.ThemedActivity;
import ch.unstable.ost.utils.NavHelper;

public class ConnectionDetailActivity extends ThemedActivity implements ConnectionDetailFragment.OnConnectionDetailInteractionListener {

    public static final String EXTRA_CONNECTION = "EXTRA_CONNECTION";
    public static final String EXTRA_FAVORITE_ID = "EXTRA_FAVORITE_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            Connection connection = getIntent().getParcelableExtra(EXTRA_CONNECTION);
            long favoriteId = getIntent().getLongExtra(EXTRA_FAVORITE_ID, 0L);
            ConnectionDetailFragment fragment = ConnectionDetailFragment.newInstance(connection, favoriteId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @MainThread
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
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_about:
                NavHelper.INSTANCE.openAbout(this);
                return true;
            default:
                return false;
        }
    }
}
