package fr.vbastien.mycoincollector.controller.country

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.asyncloader.AsyncCountryLoader
import fr.vbastien.mycoincollector.db.AppDatabase
import fr.vbastien.mycoincollector.db.Country
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_country.*
import java.lang.Exception

class CountryActivity : LifecycleActivity(), AsyncCountryLoader.AsyncCountryLoaderListener<List<Country>> {

    private var disposableList : MutableList<Disposable> = mutableListOf()

    override fun onCountryLoaded(countries: List<Country>) {
        showCountries(countries)
        ui_tv_country_placeholder.visibility = View.GONE
        ui_rv_countrylist.visibility = View.VISIBLE
    }

    override fun onCountryLoadError(error: Throwable) {
        Snackbar.make(ui_cl_container, R.string.loading_error, Snackbar.LENGTH_SHORT).show()
    }

    var countryAdapter : CountryAdapter? = null
    var countryList : List<Country> = mutableListOf()

    var countriesViewModel : AllCountryViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_country)
        val toolbar = findViewById(R.id.ui_toolbar) as Toolbar
        setActionBar(toolbar)

        loadCountries()
        countriesViewModel = ViewModelProviders.of(this).get(AllCountryViewModel::class.java)
        countryAdapter = CountryAdapter(this, countryList)
        ui_rv_countrylist.adapter = countryAdapter

        countryAdapter?.notifyDataSetChanged()

    }

    fun loadCountries() {
        ui_tv_country_placeholder.visibility = View.VISIBLE
        ui_rv_countrylist.visibility = View.GONE
        ui_rl_emptyview.visibility = View.GONE

        disposableList.add(AsyncCountryLoader.loadCountriesFromDataSource(this, this))
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
