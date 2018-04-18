package fr.vbastien.mycoincollector.features.country

import fr.vbastien.mycoincollector.R

/**
 * Created by vbastien on 03/07/2017.
 */
class FlagDrawableFactory {
    companion object Factory {
        val flagMap = mapOf<String, Int>(
                "fr" to R.drawable.flag_france,
                "de" to R.drawable.flag_germany,
                "at" to R.drawable.flag_austria,
                "be" to R.drawable.flag_belgium,
                "es" to R.drawable.flag_spain,
                "fi" to R.drawable.flag_finland,
                "ie" to R.drawable.flag_ireland,
                "it" to R.drawable.flag_italy,
                "lu" to R.drawable.flag_luxembourg,
                "nl" to R.drawable.flag_netherlands,
                "pt" to R.drawable.flag_portugal,
                "gr" to R.drawable.flag_greece,
                "si" to R.drawable.flag_slovenia,
                "cy" to R.drawable.flag_cyprus,
                "mt" to R.drawable.flag_malta,
                "sk" to R.drawable.flag_slovakia,
                "ee" to R.drawable.flag_estonia,
                "lv" to R.drawable.flag_latvia,
                "lt" to R.drawable.flag_lithuania,
                "ad" to R.drawable.flag_andorra,
                "mc" to R.drawable.flag_monaco,
                "sm" to R.drawable.flag_san_marino,
                "va" to R.drawable.flag_vatican_city,
                "me" to R.drawable.flag_montenegro,
                "xk" to R.drawable.flag_kosovo,
                "dk" to R.drawable.flag_denmark
                )

        fun getFlagFor(countryCode : String?): Int? {
            when (countryCode?.toLowerCase()) {
                in flagMap -> return flagMap.get(countryCode?.toLowerCase())
                else -> return null
            }
        }
    }
}