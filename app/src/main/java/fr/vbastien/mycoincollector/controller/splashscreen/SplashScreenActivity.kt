package fr.vbastien.mycoincollector.controller.splashscreen

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.business.CountryBusiness
import fr.vbastien.mycoincollector.controller.country.CountryListActivity
import fr.vbastien.mycoincollector.db.AppDatabase
import fr.vbastien.mycoincollector.db.Country
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {

    private var disposableList : MutableList<Disposable> = mutableListOf()
    private var countryCount = 0

    private fun onCountryInserted() {
        startApplication()
    }

    private fun onCountryInsertError(error: Throwable) {
        Crashlytics.logException(error)
        error.printStackTrace()
        Snackbar.make(ui_cl_container, R.string.app_init_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, { insertCountriesIntoDataSource() })
    }

    private fun onCountryLoadError(error: Throwable) {
        Crashlytics.logException(error)
        error.printStackTrace()
        Snackbar.make(ui_cl_container, R.string.app_init_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, { countCountryInDataSource() })
    }

    private fun onCountryLoaded(countries: Int) {
        countryCount = countries
        if (countryCount == 0) {
            insertCountriesIntoDataSource()
        }
    }

    private fun insertCountriesIntoDataSource(countryList : List<Country>) {
        disposableList.add(Completable.fromAction { countryList.forEach { country -> AppDatabase.getInMemoryDatabase(this).countryModel().insertCountry(country) } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onCountryInserted, { t: Throwable ->
                    Crashlytics.logException(t)
                    onCountryInsertError(t)
                }))
    }

    private fun insertCountriesIntoDataSource() {
        insertCountriesIntoDataSource(CountryBusiness.countries)
    }

    private fun countCountryInDataSource() {
        disposableList.add(AppDatabase.getInMemoryDatabase(this).countryModel().countCountries()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::onCountryLoaded, { t: Throwable ->
                    Crashlytics.logException(t)
                    onCountryLoadError(t)
                }))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun onStart() {
        super.onStart()
        countCountryInDataSource()
    }

    override fun onStop() {
        super.onStop()
        disposableList.forEach { disposable -> if (!disposable.isDisposed) disposable.dispose() }
    }

    private fun startApplication() {
        val i = Intent(this, CountryListActivity::class.java)
        startActivity(i)
        finish()
    }

}
