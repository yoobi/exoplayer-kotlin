package io.github.yoobi.rvitempreview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ItemDecoration(private val padding: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val totalSpanCount = getTotalSpanCount(parent)
        val column = position % totalSpanCount

        outRect.left = padding - column * padding / totalSpanCount // padding - column * ((1f / totalSpanCount) * padding)
        outRect.right = (column + 1) * padding / totalSpanCount // (column + 1) * ((1f / totalSpanCount) * padding)
        outRect.bottom = padding
        if (position < totalSpanCount) outRect.top = padding

    }

    private fun getTotalSpanCount(parent: RecyclerView): Int {
        return (parent.layoutManager as? GridLayoutManager)?.spanCount ?: 1
    }

}