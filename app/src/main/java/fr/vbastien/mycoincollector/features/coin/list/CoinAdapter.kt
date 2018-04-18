package fr.vbastien.mycoincollector.features.coin.list

import android.support.v7.widget.RecyclerView
import android.content.Context
import android.content.res.ColorStateList
import android.net.Uri
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatImageView
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.db.Coin
import fr.vbastien.mycoincollector.util.view.ViewUtil


/**
 * Created by vbastien on 03/09/2017.
 */
class CoinAdapter(var context: Context, var coinList: List<Coin>) : RecyclerView.Adapter<CoinAdapter.ViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent?.context).inflate(R.layout.item_coin_list, parent, false)
        val viewHolder = ViewHolder(v)
        return viewHolder
    }

    fun getItemAt(pos: Int) : Coin? {
        if (pos > coinList.size) return null
        return coinList.get(pos)
    }

    override fun getItemCount(): Int {
        return coinList.size
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        val coin = getItemAt(position) ?: return

        if (TextUtils.isEmpty(coin.description)) {
            holder?.coinDesc?.visibility = View.GONE
        } else {
            holder?.coinDesc?.text = coin.description
            holder?.coinDesc?.visibility = View.VISIBLE
        }

        holder?.coinValue?.text = coin.value.toString()
        if (TextUtils.isEmpty(coin.img)) {
            holder?.coinPicture?.setImageResource(R.drawable.coin_empty)
            holder?.coinPicture?.supportImageTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.material_grey_400))
            val padding = ViewUtil.dpToPx(context, 32)
            holder?.coinPicture?.setPadding(padding, padding, padding, padding)
        } else {
            Picasso.with(context).load(Uri.parse(coin.img)).resize(500, 500).centerInside().into(holder?.coinPicture)
            holder?.coinPicture?.setPadding(0, 0, 0, 0)
        }
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var coinPicture = v.findViewById<AppCompatImageView>(R.id.ui_iv_coin_picture)
        var coinDesc = v.findViewById<TextView>(R.id.ui_tv_coin_desc)
        var coinValue = v.findViewById<TextView>(R.id.ui_tv_coin_value)
    }
}