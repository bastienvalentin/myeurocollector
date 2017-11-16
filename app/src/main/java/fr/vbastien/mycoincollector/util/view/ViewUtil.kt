package fr.vbastien.mycoincollector.util.view

import android.content.Context

/**
 * Created by vbastien on 16/09/2017.
 */
class ViewUtil {
    companion object {
        fun dpToPx(context: Context, dp : Int) : Int {
            return (context.resources.displayMetrics.density * dp + 0.5f).toInt()
        }
    }
}