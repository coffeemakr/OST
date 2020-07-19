package ch.unstable.ost.views.lists


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.functions.Consumer
import java.util.*

abstract class SingleTypeSimplerAdapter<E, VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>(), Consumer<List<E>> {

    private val mElements = ArrayList<E>()

    @get:LayoutRes
    abstract val layout: Int

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): VH {
        val inflater = LayoutInflater.from(viewGroup.context)
        val itemView = inflater.inflate(layout, viewGroup, false)
        return onCreateViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        onBindViewHolder(viewHolder, getItem(position), position)
    }

    abstract fun onBindViewHolder(viewHolder: VH, element: E, position: Int)

    abstract fun onCreateViewHolder(itemView: View): VH


    protected fun getItem(position: Int): E {
        return mElements[position]!!
    }

    fun setElements(@NonNull elements: Collection<E>) {
        this.mElements.clear()
        this.mElements.addAll(elements)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mElements.size
    }

    @MainThread
    override fun accept(es: List<E>) {
        setElements(es)
    }
}
