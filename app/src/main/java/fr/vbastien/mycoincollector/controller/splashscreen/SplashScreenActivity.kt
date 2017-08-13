package fr.vbastien.mycoincollector.controller.splashscreen

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View

import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.asyncloader.AsyncCountryLoader
import fr.vbastien.mycoincollector.business.CountryBusiness
import fr.vbastien.mycoincollector.controller.country.CountryActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity(), AsyncCountryLoader.AsyncCountryLoaderListener<Int>, AsyncCountryLoader.AsyncCountryInsertListener {

    private var disposableList : MutableList<Disposable> = mutableListOf()

    override fun onCountryInserted() {
        startApplication()
    }

    override fun onCountryInsertError(error: Throwable) {
        Snackbar.make(ui_cl_container, R.string.app_init_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, { insertCountriesIntoDataSource() })
    }

    override fun onCountryLoadError(error: Throwable) {
        Snackbar.make(ui_cl_container, R.string.app_init_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, { countCountryInDataSource() })
    }

    override fun onCountryLoaded(countries: Int) {
        if (countries == 0) {
            insertCountriesIntoDataSource()
        } else {
            startApplication()
        }
    }

    fun insertCountriesIntoDataSource() {
        disposableList.add(AsyncCountryLoader.insertCountryIntoDataSource(this, this, CountryBusiness.countries))
    }

    fun countCountryInDataSource() {
        disposableList.add(AsyncCountryLoader.loadCountryCountFromDataSource(this, this))
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
        val i : Intent = Intent(this, CountryActivity::class.java)
        startActivity(i)
    }
}
