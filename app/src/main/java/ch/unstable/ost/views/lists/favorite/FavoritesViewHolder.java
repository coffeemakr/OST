package ch.unstable.ost.views.lists.favorite;

import android.view.View;
import android.widget.TextView;

import ch.unstable.ost.R;
import ch.unstable.ost.views.lists.connection.ConnectionViewHolder;


public class FavoritesViewHolder extends ConnectionViewHolder {
    public final TextView fromToText;

    public FavoritesViewHolder(View itemView) {
        super(itemView);
        fromToText = itemView.findViewById(R.id.from_to_text);
    }
}
