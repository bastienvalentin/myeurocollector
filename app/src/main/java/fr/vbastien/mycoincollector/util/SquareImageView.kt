package fr.vbastien.mycoincollector.util

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

/**
 * Created by vbastien on 28/08/2017.
 */
class SquareImageView : ImageView {

    constructor(context : Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (measuredWidth > measuredHeight) {
            setMeasuredDimension(measuredHeight, measuredHeight);
        } else {
            setMeasuredDimension(measuredWidth, measuredWidth);
        }
    }
}