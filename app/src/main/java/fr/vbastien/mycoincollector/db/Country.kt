package fr.vbastien.mycoincollector.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Relation
import android.content.Context
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.util.StringUtil

/**
 * Created by vbastien on 03/07/2017.
 */
@Entity(tableName = "country")
data class Country (
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "country_id")
        var countryId : Int = 0,
        var name: String = ""
)