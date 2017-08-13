package ch.unstable.ost.api.transport;

import java.io.IOException;

import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.api.model.ConnectionQuery;

public interface ConnectionAPI {

    int getPageMax();

    int getPageMin();

    Connection[] getConnections(ConnectionQuery connectionQuery, int page) throws IOException;
}
