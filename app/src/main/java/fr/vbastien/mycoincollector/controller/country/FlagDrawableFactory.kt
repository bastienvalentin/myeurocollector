package fr.vbastien.mycoincollector.controller.country

import fr.vbastien.mycoincollector.R

/**
 * Created by vbastien on 03/07/2017.
 */
class FlagDrawableFactory {
    companion object Factory {
        val flagMap = mapOf<String, Int>("france" to R.drawable.flag_france, "germany" to R.drawable.flag_germany)

        fun getFlagFor(countryName : String?): Int? {
            when (countryName?.toLowerCase()) {
                in flagMap -> return flagMap.get(countryName?.toLowerCase())
                else -> return null
            }
        }
    }
}