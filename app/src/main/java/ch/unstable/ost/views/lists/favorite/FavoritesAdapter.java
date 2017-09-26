package ch.unstable.ost.views.lists.favorite;

import android.support.annotation.NonNull;
import android.view.View;

import ch.unstable.ost.R;
import ch.unstable.ost.api.model.Connection;
import ch.unstable.ost.database.model.FavoriteConnection;
import ch.unstable.ost.views.lists.connection.ConnectionBinder;
import ch.unstable.ost.views.lists.query.QueryBinder;
import ch.unstable.ost.views.lists.SingleTypeSimplerAdapter;

public class FavoritesAdapter extends SingleTypeSimplerAdapter<FavoriteConnection, FavoritesViewHolder> {

    public FavoritesAdapter() {
        super();
        setHasStableIds(true);
    }

    @Override
    public int getLayout() {
        return R.layout.item_favorite_connection;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesViewHolder holder, @NonNull FavoriteConnection element, int position) {
        Connection connection = element.getConnection();
        ConnectionBinder.bindConnection(connection, holder);
        QueryBinder.INSTANCE.bindFromToText(holder.getFromToText(), connection);
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(@NonNull View itemView) {
        return new FavoritesViewHolder(itemView);
    }


    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }
}
