package fr.vbastien.mycoincollector.controller.coin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.MenuItem
import android.view.View
import com.crashlytics.android.Crashlytics

import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.RequestCodes
import fr.vbastien.mycoincollector.db.AppDatabase
import fr.vbastien.mycoincollector.db.Coin
import fr.vbastien.mycoincollector.util.view.ItemClickSupport
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list_coin.*
import kotlinx.android.synthetic.main.bar_simple_coin.*

class CoinListActivity : AppCompatActivity() {

    var countryId : Int? = null
    var coinAdapter: CoinAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_coin)
        setSupportActionBar(ui_toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (supportActionBar != null) supportActionBar?.setDisplayShowHomeEnabled(true)

        if (savedInstanceState != null) {
            countryId = savedInstanceState.getInt("country_id")
        } else {
            if (intent == null || intent.getIntExtra("country_id", -1) == -1) return
            countryId = intent.getIntExtra("country_id", -1)
        }

        if (countryId == null) {
            Snackbar.make(ui_cl_container, R.string.country_view_error, Snackbar.LENGTH_LONG).show()
            finish()
        }

        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        ui_rv_coinlist.layoutManager = layoutManager

        ItemClickSupport.addTo(ui_rv_coinlist).setOnItemClickListener { parent, view, position, id ->
            val coin = coinAdapter?.getItemAt(position)
            if (coin == null) {
                Snackbar.make(ui_cl_container, R.string.coin_view_error, Snackbar.LENGTH_LONG).show()
            } else {
                loadAddCoinCountry(coin.coinId)
            }
        }

        ui_fab_add_coin.setOnClickListener({ _ -> loadAddCoinCountry(null) })

        coinAdapter = CoinAdapter(this, mutableListOf())
        ui_rv_coinlist.adapter = coinAdapter
    }

    override fun onStart() {
        super.onStart()
        AppDatabase.getInMemoryDatabase(this).coinModel().findCoinsForCountry(countryId.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError { t ->
                    Crashlytics.logException(t)
                    t.printStackTrace()
                    Snackbar.make(ui_cl_container, R.string.country_view_error, Snackbar.LENGTH_LONG).show()
                    finish()
                }
                .doOnSuccess { coinList ->
                    if (coinList.isEmpty()) {
                        Snackbar.make(ui_cl_container, R.string.country_view_error, Snackbar.LENGTH_LONG).show()
                        finish()
                    }
                    ui_tv_country_placeholder.visibility = View.GONE

                    coinAdapter?.coinList = coinList
                    coinAdapter?.notifyDataSetChanged()
                    displaySimpleCoin(coinList)
                }
                .subscribe()
    }

    private fun displaySimpleCoin(coinList : List<Coin>) {
        for (coin in coinList) {
            when (coin.value) {
                2.0 -> ui_tv_two_euros.setPossess(true)
                1.0 -> ui_tv_one_euro.setPossess(true)
                0.5 -> ui_tv_fifty_cent.setPossess(true)
                0.2 -> ui_tv_twenty_cent.setPossess(true)
                0.1 -> ui_tv_ten_cent.setPossess(true)
                0.05 -> ui_tv_five_cent.setPossess(true)
                0.02 -> ui_tv_two_cent.setPossess(true)
                0.01 -> ui_tv_one_cent.setPossess(true)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("country_id", countryId!!)
    }

    fun loadAddCoinCountry(coinId: Int?) {
        val i : Intent = Intent(this, CoinAddActivity::class.java)
        if (coinId != null) {
            i.putExtra("coin_id", coinId)
        } else {
            i.putExtra("country_id", countryId!!)
        }
        startActivityForResult(i, RequestCodes.ADD_COIN_REQUEST_CODE)
    }
}
