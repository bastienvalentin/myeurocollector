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
                Country(1, "france", "01/01/1999"),
                Country(2, "germany", "01/01/1999"),
                Country(3, "austria", "01/01/1999"),
                Country(4, "belgium", "01/01/1999"),
                Country(5, "spain", "01/01/1999"),
                Country(6, "finland", "01/01/1999"),
                Country(7, "ireland", "01/01/1999"),
                Country(8, "italy", "01/01/1999"),
                Country(9, "luxembourg", "01/01/1999"),
                Country(10, "netherlands", "01/01/1999"),
                Country(11, "portugal", "01/01/1999"),
                Country(12, "greece", "01/01/2001"),
                Country(13, "slovenia", "01/01/2007"),
                Country(14, "cyprus", "01/01/2008"),
                Country(15, "malta", "01/01/2008"),
                Country(16, "slovakia", "01/01/2009"),
                Country(17, "estonia", "01/01/2011"),
                Country(18, "latvia", "01/01/2014"),
                Country(19, "lithuania", "01/01/2015"),
                Country(20, "andorra", "30/06/2011"),
                Country(21, "monaco", ""),
                Country(22, "san_marino", ""),
                Country(23, "vatican", ""),
                Country(24, "montenegro", "01/01/2002"),
                Country(25, "kosovo", "01/01/2002")
        )

        fun parseCountryListFromMap(mappedCountries : Map<String, Map<String, String>>) : List<Country> {
            val countryList : MutableList<Country> = mutableListOf()
            mappedCountries.keys.forEach { key ->  countryList.add(parseCountryFromMap(mappedCountries.get(key)!!))}
            return countryList
        }

        fun parseCountryFromMap(mappedCountry : Map<String, String>) : Country {
            return Country(mappedCountry.get("id")!!.toInt(), mappedCountry.get("name")!!.toString(), if (mappedCountry.get("since") != null) mappedCountry.get("since").toString() else "")
        }
    }
}