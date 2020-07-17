package ch.unstable.ost.api.search.types;

import ch.unstable.ost.api.model.Connection;

/**
 * Created on 24.02.18.
 */
public class ConnectionsList {
    public Connection[] connections;

    public ConnectionsList(Connection[] connections) {
        this.connections = connections;
    }
}
