package fr.vbastien.mycoincollector.db

import android.arch.persistence.room.*

/**
 * Created by vbastien on 03/07/2017.
 */
@Entity(tableName = "country")
data class Country (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "country_id")
        var countryId : Int = 0,
        var name: String = "",
        var code: String = "",
        var since: String = "",
        @Ignore
        var localeName: String = ""
)