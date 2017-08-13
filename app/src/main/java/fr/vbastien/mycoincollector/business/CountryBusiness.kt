package fr.vbastien.mycoincollector.business

import android.content.Context
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.db.Country
import fr.vbastien.mycoincollector.util.StringUtil

/**
 * Created by vbastien on 10/08/2017.
 */
class CountryBusiness {
    companion object {
        fun getTranslatedCountryName(context: Context, countryName : String?) : String {
            if (countryName == null) return context.getString(R.string.unknown_country)

            val stringRes = StringUtil.getStringIdentifier(context, countryName)
            if (stringRes == null || stringRes == 0) {
                return context.getString(R.string.unknown_country)
            } else {
                return context.getString(stringRes)
            }
        }

        var countries : List<Country> = mutableListOf(
                Country(1, "france"),
                Country(2, "germany")
        )
    }
}