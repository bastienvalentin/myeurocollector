package fr.vbastien.mycoincollector.controller.country

import android.arch.lifecycle.LifecycleActivity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.RequestCodes
import fr.vbastien.mycoincollector.controller.coin.CoinAddActivity
import fr.vbastien.mycoincollector.db.AppDatabase
import fr.vbastien.mycoincollector.db.Country
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_list_country.*

class CountryListActivity : LifecycleActivity() {

    private var disposableList : MutableList<Disposable> = mutableListOf()

    fun onCountryLoaded(countries: List<Country>) {
        showCountries(countries)
        ui_tv_country_placeholder.visibility = View.GONE
        ui_rv_countrylist.visibility = View.VISIBLE
    }

    fun onCountryLoadError(error: Throwable) {
        Snackbar.make(ui_cl_container, R.string.loading_error, Snackbar.LENGTH_SHORT).show()
    }

    var countryAdapter : CountryAdapter? = null
    var countryList : List<Country> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_country)
        setActionBar(ui_toolbar)

        loadCountries()
        countryAdapter = CountryAdapter(this, countryList)
        ui_rv_countrylist.adapter = countryAdapter

        countryAdapter?.notifyDataSetChanged()

        ui_fab_add_coin.setOnClickListener { loadAddCoinCountry() }
        ui_bt_add_coin.setOnClickListener { loadAddCoinCountry() }
    }

    fun loadAddCoinCountry() {
        val i : Intent = Intent(this, CoinAddActivity::class.java)
        startActivityForResult(i, RequestCodes.ADD_COIN_REQUEST_CODE)
    }

    fun loadCountries() {
        ui_tv_country_placeholder.visibility = View.VISIBLE
        ui_rv_countrylist.visibility = View.GONE
        ui_rl_emptyview.visibility = View.GONE

        disposableList.add(AppDatabase.getInMemoryDatabase(this).countryModel().findCountriesWithCoin()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError { t: Throwable -> onCountryLoadError(t) }
                .doOnSuccess { countries: List<Country> -> onCountryLoaded(countries) }
                .subscribe())
    }

    fun showCountries(countries : List<Country>?) {
        if (countries == null || countries.isEmpty()) {
            ui_tv_country_placeholder.visibility = View.GONE
            ui_rv_countrylist.visibility = View.GONE
            ui_rl_emptyview.visibility = View.VISIBLE
        } else {
            countryAdapter?.countryList = countries

            countryAdapter?.notifyDataSetChanged()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_country, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onStop() {
        super.onStop()
        disposableList.forEach { disposable -> if (!disposable.isDisposed) disposable.dispose() }
    }
}
