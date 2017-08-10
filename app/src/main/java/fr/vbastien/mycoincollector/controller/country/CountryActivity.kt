package fr.vbastien.mycoincollector.controller.country

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toolbar
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.db.AppDatabase
import fr.vbastien.mycoincollector.db.Country
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_country.*
import java.lang.Exception

class CountryActivity : LifecycleActivity() {

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
//        countryList = AppDatabase.getInMemoryDatabase(applicationContext).countryModel().findCountries();
        countryAdapter = CountryAdapter(this, countryList)
        ui_rv_countrylist.adapter = countryAdapter

        countryAdapter?.notifyDataSetChanged()

    }

    fun loadCountries() {
        ui_tv_country_placeholder.visibility = View.VISIBLE
        ui_rv_countrylist.visibility = View.GONE

        getCountryLoaderObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext({ countries ->
                    showCountries(countries)
                    ui_tv_country_placeholder.visibility = View.GONE
                    ui_rv_countrylist.visibility = View.VISIBLE
                })
                .subscribe()
    }

    fun getCountryLoaderObservable() : Observable<List<Country>> {
        return Observable.create({ listener ->
            try {
                val countries = AppDatabase.getInMemoryDatabase(this).countryModel().findCountries()
                listener.onNext(countries)
                listener.onComplete()
            } catch (e : Exception) {
                e.printStackTrace();
            }
        })
    }

    fun showCountries(countries : List<Country>?) {
        countryAdapter?.countryList = countries ?: mutableListOf()

        countryAdapter?.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_country, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}
