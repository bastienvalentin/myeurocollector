package fr.vbastien.mycoincollector.features.country

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.db.Country
import java.util.*

/**
 * Created by vbastien on 03/07/2017.
 */
class CountryAdapter(var context: Context, var countryList: List<Country>) : RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_country_list, parent, false)
        val viewHolder = ViewHolder(v)
        return viewHolder
    }

    fun getItemAt(pos: Int) : Country? {
        if (pos > countryList.size) return null
        return countryList.get(pos)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val country : Country? = getItemAt(position)
        holder?.view?.text = Locale("", country?.code).getDisplayCountry()

        val chevron = ContextCompat.getDrawable(context, R.drawable.chevron_right);

        val countryFlagRes = FlagDrawableFactory.getFlagFor(country?.code)
        if (countryFlagRes != null) {
            val countryFlag = ContextCompat.getDrawable(context, countryFlagRes);
            holder?.view?.setCompoundDrawablesWithIntrinsicBounds(countryFlag, null, chevron, null)
        } else {
            holder?.view?.setCompoundDrawablesWithIntrinsicBounds(null, null, chevron, null)
        }


    }

    override fun getItemCount(): Int {
        return countryList.size;
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var view : TextView = v as TextView
    }
}