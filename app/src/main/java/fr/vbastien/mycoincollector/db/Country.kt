package fr.vbastien.mycoincollector.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.util.StringUtil

/**
 * Created by vbastien on 03/07/2017.
 */
@Entity
class Country {
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
    }
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null

    var name: String? = null
}