package com.eundmswlji.tacoling.presentation.ui.decoration

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalItemDecoration(
    private val top: Int,
    private val bottom: Int,
    private val between: Int,
    private val left: Int,
    private val right: Int
) : RecyclerView.ItemDecoration() {

    constructor(size: Int) : this(size, size, size, size, size)
    constructor(vertical: Int, horizontal: Int) : this(vertical, vertical, vertical, horizontal, horizontal)

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val lastPosition = state.itemCount - 1

        when (position) {
            0 -> {
                outRect.top = top
                outRect.bottom = if(state.itemCount==1) bottom else between
            }
            lastPosition -> outRect.bottom = bottom
            else -> outRect.bottom = between
        }

        outRect.left = left
        outRect.right = left
    }
}