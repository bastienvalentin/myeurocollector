package fr.vbastien.mycoincollector.util

import android.content.Context

/**
 * Created by vbastien on 03/07/2017.
 */
class StringUtil {
    companion object StringUtil {
        fun getStringIdentifier(context : Context, name: String): Int? {
            return context.getResources().getIdentifier(name, "string", context.getPackageName());
        }
    }
}