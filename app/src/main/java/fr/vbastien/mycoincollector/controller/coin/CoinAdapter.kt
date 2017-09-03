package fr.vbastien.mycoincollector.controller.coin

import android.support.v7.widget.RecyclerView
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.db.Coin
import fr.vbastien.mycoincollector.util.SquareImageView


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

        holder?.coinDesc?.text = coin.description
        holder?.coinValue?.text = coin.value.toString()
        if (TextUtils.isEmpty(coin.img)) return
        holder?.coinPicture?.setImageURI(Uri.parse(coin.img))
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var coinPicture = v.findViewById<SquareImageView>(R.id.ui_iv_coin_picture)
        var coinDesc = v.findViewById<TextView>(R.id.ui_tv_coin_desc)
        var coinValue = v.findViewById<TextView>(R.id.ui_tv_coin_value)
    }
}