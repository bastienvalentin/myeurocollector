package fr.vbastien.mycoincollector.controller.country

import android.arch.lifecycle.LifecycleActivity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.crashlytics.android.Crashlytics
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.RequestCodes
import fr.vbastien.mycoincollector.controller.coin.CoinAddActivity
import fr.vbastien.mycoincollector.controller.coin.CoinListActivity
import fr.vbastien.mycoincollector.db.AppDatabase
import fr.vbastien.mycoincollector.db.Country
import fr.vbastien.mycoincollector.util.ItemClickSupport
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
        Crashlytics.logException(error)
        Snackbar.make(ui_cl_container, R.string.loading_error, Snackbar.LENGTH_SHORT).show()
    }

    var countryAdapter : CountryAdapter? = null
    var countryList : List<Country> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_country)
        setActionBar(ui_toolbar)

        countryAdapter = CountryAdapter(this, countryList)
        ui_rv_countrylist.adapter = countryAdapter

        ItemClickSupport.addTo(ui_rv_countrylist).setOnItemClickListener { parent, view, position, id ->
            val country = countryAdapter!!.getItemAt(position)
            if (country == null) {
                Snackbar.make(ui_cl_container, R.string.country_view_error, Snackbar.LENGTH_LONG).show()
                return@setOnItemClickListener
            }
            val intent = Intent(this, CoinListActivity::class.java)
            intent.putExtra("country_id", country.countryId)
            startActivity(intent)
        }

        countryAdapter?.notifyDataSetChanged()

        ui_fab_add_coin.setOnClickListener { loadAddCoinCountry() }
        ui_bt_add_coin.setOnClickListener { loadAddCoinCountry() }
    }

    override fun onStart() {
        super.onStart()
        loadCountries()
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
                .subscribe(
                        {countries: List<Country> -> onCountryLoaded(countries)},
                        {
                            t: Throwable ->
                            Crashlytics.logException(t)
                            onCountryLoadError(t)
                        }
                ))
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
