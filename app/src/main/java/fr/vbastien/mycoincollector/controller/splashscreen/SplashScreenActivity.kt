package fr.vbastien.mycoincollector.controller.splashscreen

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.google.firebase.database.*
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
    private var countryCount = 0;
    private var timeOutHandler : Handler? = null
    private var timeOutRunnable : Runnable? = null

    fun onCountryInserted() {
        startApplication()
    }

    fun onCountryInsertError(error: Throwable) {
        Crashlytics.logException(error)
        Snackbar.make(ui_cl_container, R.string.app_init_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, { insertFallbackCountriesIntoDataSource() })
    }

    fun onCountryLoadError(error: Throwable) {
        Crashlytics.logException(error)
        Snackbar.make(ui_cl_container, R.string.app_init_error, Snackbar.LENGTH_INDEFINITE).setAction(R.string.retry, { countCountryInDataSource() })
    }

    fun onCountryLoaded(countries: Int) {
        countryCount = countries
        loadCountryFromRemoteDatabase()
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
                    } else if (countryCount == 0) {
                        insertFallbackCountriesIntoDataSource()
                    } else {
                        startApplication()
                    }
                } catch (e: Exception) {
                    Crashlytics.logException(e)
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
        disposableList.add(Completable.fromAction { countryList.forEach { country -> AppDatabase.getInMemoryDatabase(this).countryModel().insertCountry(country) } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete { onCountryInserted() }
                .doOnError { t : Throwable ->
                    Crashlytics.logException(t)
                    onCountryInsertError(t)
                }
                .subscribe())
    }

    fun insertFallbackCountriesIntoDataSource() {
        insertCountriesIntoDataSource(CountryBusiness.countries)
    }

    fun countCountryInDataSource() {
        disposableList.add(AppDatabase.getInMemoryDatabase(this).countryModel().countCountries()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnError { t: Throwable ->
                    Crashlytics.logException(t)
                    onCountryLoadError(t)
                }
                .doOnSuccess { count : Int -> onCountryLoaded(count) }
                .subscribe())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    }

    override fun onStart() {
        super.onStart()
        countCountryInDataSource()
        startTimeOutTimer()
    }

    override fun onStop() {
        super.onStop()
        disposableList.forEach { disposable -> if (!disposable.isDisposed) disposable.dispose() }
        if (timeOutRunnable != null) {
            timeOutHandler?.removeCallbacks(timeOutRunnable)
        }
    }

    private fun startApplication() {
        val i : Intent = Intent(this, CountryListActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun startTimeOutTimer() {
        try {
            timeOutHandler = Handler()
            timeOutRunnable = Runnable { startApplication() }
            timeOutHandler!!.postDelayed(timeOutRunnable, 2000)
        } catch (e : Exception) {
            Crashlytics.logException(e)
            e.printStackTrace()
        }
    }
}
