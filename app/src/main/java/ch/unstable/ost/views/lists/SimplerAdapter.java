package ch.unstable.ost.views.lists;

import android.support.annotation.LayoutRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.functions.Consumer;

import static com.google.common.base.Verify.verifyNotNull;

public abstract class SimplerAdapter<E, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH>
        implements Consumer<List<E>> {

    private final ArrayList<E> mElements = new ArrayList<>();

    @LayoutRes
    public abstract int getLayout(int i);

    @Override
    public VH onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(getLayout(i), viewGroup, false);
        return onCreateViewHolderFromView(itemView, i);
    }

    public abstract VH onCreateViewHolderFromView(View itemView, int i);


    @NonNull
    protected E getItem(int position) {
        return verifyNotNull(mElements.get(position), "element is null");
    }

    public void setElements(Collection<E> elements) {
        //noinspection ResultOfMethodCallIgnored
        Preconditions.checkNotNull(elements, "elements is null");
        this.mElements.clear();
        this.mElements.addAll(elements);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mElements.size();
    }

    @MainThread
    @Override
    public void accept(List<E> es) {
        setElements(es);
    }
}
