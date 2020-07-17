package ch.unstable.ost.views.lists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.MainThread
import androidx.recyclerview.widget.RecyclerView

import com.google.common.base.Preconditions

import java.util.ArrayList

import io.reactivex.functions.Consumer

import com.google.common.base.Verify.verifyNotNull

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
        return verifyNotNull(mElements[position], "element is null")
    }

    fun setElements(elements: Collection<E>) {

        Preconditions.checkNotNull(elements, "elements is null")
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
