package fr.vbastien.mycoincollector.controller.coin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.MenuItem
import android.view.View

import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.db.AppDatabase
import fr.vbastien.mycoincollector.util.ItemClickSupport
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list_coin.*

class CoinListActivity : AppCompatActivity() {

    var countryId : Int? = null
    var coinAdapter: CoinAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_coin)
        setActionBar(ui_toolbar)

        if (actionBar != null) actionBar.setDisplayShowHomeEnabled(true)

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

        ItemClickSupport.addTo(ui_rv_coinlist).setOnItemClickListener { parent, view, position, id ->
            val coin = coinAdapter?.getItemAt(position)
            if (coin == null) {
                Snackbar.make(ui_cl_container, R.string.coin_view_error, Snackbar.LENGTH_LONG).show()
            } else {
                //TODO
            }
        }

        AppDatabase.getInMemoryDatabase(this).coinModel().findCoinsForCountry(countryId.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError { t ->
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
                    coinAdapter = CoinAdapter(this, coinList)
                    ui_rv_coinlist.adapter = coinAdapter
                }
                .subscribe()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}