package ch.unstable.ost.views.lists.query;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import ch.unstable.ost.R;

/**
 * View holder for a query history entry
 */
public class QueryViewHolder extends RecyclerView.ViewHolder {
    /**
     * Text field containing the description of the time restriction
     * e.g. "Departure 11:20"
     */
    final TextView date;
    /**
     * Text field containing the description of the route.
     * e.g "From Zürich to Basel SBB"
     */
    final TextView fromAndTo;

    public QueryViewHolder(View itemView) {
        super(itemView);
        this.date = itemView.findViewById(R.id.date_text);
        this.fromAndTo = itemView.findViewById(R.id.from_to_text);
    }
}
