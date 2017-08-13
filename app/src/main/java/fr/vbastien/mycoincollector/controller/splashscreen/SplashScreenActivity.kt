package fr.vbastien.mycoincollector.controller.splashscreen

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.google.firebase.database.*
import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.asyncloader.AsyncCountryLoader
import fr.vbastien.mycoincollector.business.CountryBusiness
import fr.vbastien.mycoincollector.controller.country.CountryActivity
import fr.vbastien.mycoincollector.db.Country
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_splash_screen.*


class SplashScreenActivity : AppCompatActivity(), AsyncCountryLoader.AsyncCountryLoaderListener<Int>, AsyncCountryLoader.AsyncCountryInsertListener {

    private var disposableList : MutableList<Disposable> = mutableListOf()

    override fun onCountryInserted() {
        startApplication()
    }

    override fun onCountryInsertError(error: Throwable) {
        Snackbar.make(ui_cl_container, R.string.app_init_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, { insertFallbackCountriesIntoDataSource() })
    }

    override fun onCountryLoadError(error: Throwable) {
        Snackbar.make(ui_cl_container, R.string.app_init_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, { countCountryInDataSource() })
    }

    override fun onCountryLoaded(countries: Int) {
        if (countries == 0) {
            loadCountryFromRemoteDatabase()
        } else {
            startApplication()
        }
    }

    fun loadCountryFromRemoteDatabase() {
        val database = FirebaseDatabase.getInstance()
        val ref : DatabaseReference = database.getReference("countries")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    @Suppress("UNCHECKED_CAST")
                    val value: Map<String, Map<String, String>> = dataSnapshot.getValue(true) as Map<String, Map<String, String>>
                    val countryList = CountryBusiness.parseCountryListFromMap(value)
                    if (countryList.isNotEmpty()) {
                        insertCountriesIntoDataSource(countryList)
                    } else {
                        insertFallbackCountriesIntoDataSource()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    insertFallbackCountriesIntoDataSource()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value from remote database
                // we use fallback which is the country list already in the apk
                insertFallbackCountriesIntoDataSource()
            }
        })
    }

    fun insertCountriesIntoDataSource(countryList : List<Country>) {
        disposableList.add(AsyncCountryLoader.insertCountryIntoDataSource(this, this, countryList))
    }

    fun insertFallbackCountriesIntoDataSource() {
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
