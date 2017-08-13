package fr.vbastien.mycoincollector.asyncloader

import android.content.Context
import fr.vbastien.mycoincollector.db.AppDatabase
import fr.vbastien.mycoincollector.db.Country
import fr.vbastien.mycoincollector.db.CountryDao
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

/**
 * Created by vbastien on 12/08/2017.
 */
class AsyncCountryLoader {
    interface AsyncCountryLoaderListener<T> {
        fun onCountryLoaded(countries : T)
        fun onCountryLoadError(error : Throwable)
    }

    interface AsyncCountryInsertListener {
        fun onCountryInserted()
        fun onCountryInsertError(error : Throwable)
    }

    companion object {
        fun getCountryLoaderObservable(context: Context) : Observable<List<Country>> {
            return Observable.create({ listener ->
                try {
                    val countries = AppDatabase.getInMemoryDatabase(context).countryModel().findCountries()
                    listener.onNext(countries)
                    listener.onComplete()
                } catch (e : Exception) {
                    e.printStackTrace();
                    listener.onError(e)
                }
            })
        }

        fun loadCountriesFromDataSource(context: Context, listener : AsyncCountryLoaderListener<List<Country>>) : Disposable {
            return getCountryLoaderObservable(context)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext({ countries -> listener.onCountryLoaded(countries) })
                    .doOnError({ error -> listener.onCountryLoadError(error) })
                    .subscribe()
        }

        fun getCountryCountLoaderObservable(context: Context) : Observable<Int> {
            return Observable.create({ listener ->
                try {
                    val countryCount = AppDatabase.getInMemoryDatabase(context).countryModel().countCountries()
                    listener.onNext(countryCount)
                    listener.onComplete()
                } catch (e : Exception) {
                    e.printStackTrace();
                    listener.onError(e)
                }
            })
        }

        fun loadCountryCountFromDataSource(context: Context, listener : AsyncCountryLoaderListener<Int>) : Disposable {
            return getCountryCountLoaderObservable(context)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext({ countryCount -> listener.onCountryLoaded(countryCount) })
                    .doOnError({ error -> listener.onCountryLoadError(error) })
                    .subscribe()
        }

        fun getAddCountryToDataSourceObservable(context: Context, countryList : List<Country>) : Observable<Boolean> {
            return Observable.create({ listener ->
                try {
                    val countryDao : CountryDao = AppDatabase.getInMemoryDatabase(context).countryModel()
                    countryList.forEach { country -> countryDao.insertCountry(country) }
                    listener.onNext(true)
                    listener.onComplete()
                } catch (e : Exception) {
                    e.printStackTrace();
                    listener.onError(e)
                }
            })
        }

        fun insertCountryIntoDataSource(context: Context, listener : AsyncCountryInsertListener, countryList : List<Country>) : Disposable {
            return getAddCountryToDataSourceObservable(context, countryList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext({ countryCount -> listener.onCountryInserted() })
                    .doOnError({ error -> listener.onCountryInsertError(error) })
                    .subscribe()
        }
    }

}