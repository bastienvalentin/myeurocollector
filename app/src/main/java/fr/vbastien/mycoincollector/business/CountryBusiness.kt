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

        var countries : List<Country> = mutableListOf(
                Country(1, "france", "fr", "01/01/1999"),
                Country(2, "germany", "de", "01/01/1999"),
                Country(3, "austria", "at", "01/01/1999"),
                Country(4, "belgium", "be", "01/01/1999"),
                Country(5, "spain", "es", "01/01/1999"),
                Country(6, "finland", "fi", "01/01/1999"),
                Country(7, "ireland", "ie", "01/01/1999"),
                Country(8, "italy", "it", "01/01/1999"),
                Country(9, "luxembourg", "lu", "01/01/1999"),
                Country(10, "netherlands", "nl", "01/01/1999"),
                Country(11, "portugal", "pt", "01/01/1999"),
                Country(12, "greece", "gr", "01/01/2001"),
                Country(13, "slovenia", "si", "01/01/2007"),
                Country(14, "cyprus", "cy", "01/01/2008"),
                Country(15, "malta", "mt", "01/01/2008"),
                Country(16, "slovakia", "sk", "01/01/2009"),
                Country(17, "estonia", "ee", "01/01/2011"),
                Country(18, "latvia", "lv", "01/01/2014"),
                Country(19, "lithuania", "lt", "01/01/2015"),
                Country(20, "andorra", "ad", "30/06/2011"),
                Country(21, "monaco", "mc", ""),
                Country(22, "san_marino", "sm", ""),
                Country(23, "vatican", "va", ""),
                Country(24, "montenegro", "me", "01/01/2002"),
                Country(25, "kosovo", "xk", "01/01/2002")
        )

        fun parseCountryListFromMap(mappedCountries : Map<String, Map<String, String>>) : List<Country> {
            val countryList : MutableList<Country> = mutableListOf()
            mappedCountries.keys.forEach { key ->  countryList.add(parseCountryFromMap(mappedCountries.get(key)!!))}
            return countryList
        }

        fun parseCountryFromMap(mappedCountry : Map<String, String>) : Country {
            return Country(mappedCountry.get("id")!!.toInt(), mappedCountry.get("label")!!.toString(), mappedCountry.get("name")!!.toString(), if (mappedCountry.get("since") != null) mappedCountry.get("since").toString() else "")
        }
    }
}