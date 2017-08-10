package fr.vbastien.mycoincollector.controller.country

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import fr.vbastien.mycoincollector.db.AppDatabase
import fr.vbastien.mycoincollector.db.Country

/**
 * Created by vbastien on 04/07/2017.
 */
class AllCountryViewModel(application : Application) : AndroidViewModel(application) {
    var countries: LiveData<List<Country>> = AppDatabase.getInMemoryDatabase(application.applicationContext).countryModel().findCountriesLiveData()
}