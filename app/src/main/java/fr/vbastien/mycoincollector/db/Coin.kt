package fr.vbastien.mycoincollector.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import android.content.Context
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.util.StringUtil

/**
 * Created by vbastien on 03/07/2017.
 */
@Entity(tableName = "coin",
        foreignKeys = arrayOf(ForeignKey(entity = Country::class,
        parentColumns = arrayOf("country_id"),
        childColumns = arrayOf("coin_id"),
        onDelete = ForeignKey.CASCADE)))
data class Coin (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "coin_id")
    var coinId : Int = 0,

    @ColumnInfo(name = "country_id")
    var countryId : Int = 0,

    var name: String = "",
    var value: Double = 0.0,
    var img: String? = null,
    var description: String? = null
)