package ch.unstable.ost;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ch.unstable.ost.api.transport.model.Connection;

public class ConnectionDetailActivity extends AppCompatActivity {

    public static final String EXTRA_CONNECTION = "EXTRA_CONNECTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_detail);

        if(getSupportFragmentManager().findFragmentById(R.id.fragment_container) == null) {
            Connection connection = getIntent().getParcelableExtra(EXTRA_CONNECTION);
            ConnectionDetailFragment fragment = ConnectionDetailFragment.newInstance(connection);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
