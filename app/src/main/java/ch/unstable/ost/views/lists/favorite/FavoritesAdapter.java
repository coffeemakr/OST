package ch.unstable.ost.views.lists.favorite;

import android.view.View;

import ch.unstable.ost.R;
import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.database.model.FavoriteConnection;
import ch.unstable.ost.views.lists.connection.ConnectionBinder;
import ch.unstable.ost.views.lists.query.QueryBinder;
import ch.unstable.ost.views.lists.SimplerAdapter;

public class FavoritesAdapter extends SimplerAdapter<FavoriteConnection, FavoritesViewHolder> {

    public FavoritesAdapter() {
        super();
        setHasStableIds(true);
    }

    @Override
    public int getLayout(int i) {
        return R.layout.item_favorite_connection;
    }

    @Override
    public FavoritesViewHolder onCreateViewHolderFromView(View itemView, int i) {
        return new FavoritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoritesViewHolder holder, int i) {
        final Connection connection = getItem(i).getConnection();
        ConnectionBinder.bindConnection(connection, holder);
        QueryBinder.bindFromToText(holder.fromToText, connection);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }
}
