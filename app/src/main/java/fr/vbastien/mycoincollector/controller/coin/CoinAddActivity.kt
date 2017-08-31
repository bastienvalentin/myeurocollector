package fr.vbastien.mycoincollector.controller.coin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.controller.country.FlagDrawableFactory
import fr.vbastien.mycoincollector.db.AppDatabase
import fr.vbastien.mycoincollector.db.Coin
import fr.vbastien.mycoincollector.db.Country
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_coin_add.*
import java.text.Normalizer
import java.util.*
import kotlin.Comparator

class CoinAddActivity : AppCompatActivity() {

    private var countryList : List<Country> = mutableListOf()
    private var countryAdapter : CountrySpinnerAdapter? = null
    private var imageUri : Uri? = null

    fun onCountryLoaded(countries: List<Country>) {
        this.countryList = countries
        initView()
    }

    fun onCountryLoadError(error: Throwable) {
        Snackbar.make(ui_cl_cointainer, R.string.loading_error, Snackbar.LENGTH_LONG);
    }

    private var disposableList : MutableList<Disposable> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_add)
        setActionBar(ui_toolbar)

        ui_ll_coin_picture.setOnClickListener {
            CropImage.activity(null)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAllowFlipping(false)
                    .setAllowCounterRotation(false)
                    .setFixAspectRatio(true)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        ui_bt_add_coin.setOnClickListener { onAddButtonClick() }
    }

    private fun onAddButtonClick() {
        val selectedCountry : Country = ui_sp_country.selectedItem as Country
        val coinValue : Double? = ui_et_coin_value.text.toString().toDoubleOrNull()
        if (coinValue == null) {
            ui_et_coin_value.error = getString(R.string.mandatory_field)
            return
        }
        if (coinValue < 0) {
            ui_et_coin_value.error = getString(R.string.coin_value_must_be_positive)
            return
        }
        val description = ui_et_coin_description.text.toString()
        val coin = Coin()
        coin.countryId = selectedCountry.countryId
        coin.value = coinValue
        coin.description = description
        coin.img = imageUri.toString()

        disposableList.add(Completable.fromAction { AppDatabase.getInMemoryDatabase(this).coinModel().insertCoin(coin) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    Snackbar.make(ui_cl_cointainer, R.string.coin_added, Snackbar.LENGTH_SHORT).show()
                    resetFields()
                }
                .doOnError { t : Throwable -> Snackbar.make(ui_cl_cointainer, R.string.coin_addition_error, Snackbar.LENGTH_SHORT) }
                .subscribe())
    }

    fun resetFields() {
        ui_sp_country.setSelection(0)
        ui_et_coin_value.setText("")
        ui_et_coin_description.setText("")
        ui_iv_coin_picture.visibility = View.GONE
        ui_ll_coin_picture.visibility = View.VISIBLE
    }

    override fun onStart() {
        super.onStart()
        if (intent != null && intent.hasExtra("coin_id")) {
            // TODO
        } else {
            ui_ll_content.visibility = View.INVISIBLE
            ui_pb_loading.visibility = View.VISIBLE
            // TODO add a fake view
            disposableList.add(AppDatabase.getInMemoryDatabase(this).countryModel().findCountries()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnError { t : Throwable -> onCountryLoadError(t) }
                    .doOnSuccess { countries : List<Country> ->
                        sortCountriesByLocaleName(countries)
                        onCountryLoaded(countries)
                    }
                    .subscribe())
        }
    }

    private fun sortCountriesByLocaleName(countries : List<Country>) {
        countries.forEach { country -> country.localeName = Locale("", country.code).getDisplayCountry() }
        Collections.sort(countries, object : Comparator<Country> {
            override fun compare(p0: Country, p1: Country): Int {
                val p0localNameStripped = Normalizer.normalize(p0.localeName, Normalizer.Form.NFD)
                p0localNameStripped.replace("[^\\p{ASCII}]", "")

                val p1localNameStripped = Normalizer.normalize(p1.localeName, Normalizer.Form.NFD)
                p1localNameStripped.replace("[^\\p{ASCII}]", "")

                return p0localNameStripped.compareTo(p1localNameStripped)
            }
        })
    }

    private fun initView() {
        ui_ll_content.visibility = View.VISIBLE
        ui_pb_loading.visibility = View.GONE
        countryAdapter = CountrySpinnerAdapter(this, R.layout.item_country_list, countryList)
        ui_sp_country.adapter = countryAdapter

        countryAdapter!!.notifyDataSetChanged()

        ui_sp_country.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

        }
    }

    override fun onStop() {
        disposableList.forEach { disposable -> if (!disposable.isDisposed()) disposable.dispose() }
        super.onStop()
    }

    internal class CountrySpinnerAdapter(context :Context, resourceId : Int, countryList : List<Country>)
        : ArrayAdapter<Country>(context, resourceId, countryList) {

        var layoutInflater : LayoutInflater
        var resourceId : Int

        init {
            this.layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            this.resourceId = resourceId
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return fillView(position, convertView, parent)
        }

        fun fillView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val holder: ViewHolder
            val convertViewLocal : View

            if (convertView == null){
                convertViewLocal = layoutInflater.inflate(resourceId, null)
                holder = ViewHolder()

                holder.countryName = convertViewLocal as TextView?

                convertViewLocal.setTag(holder)//error in this line

            } else {
                holder = convertView.tag as ViewHolder
                convertViewLocal = convertView
            }

            val item = getItem(position)

            holder.countryName?.text = Locale("", item?.code).getDisplayCountry()

            val countryFlagRes = FlagDrawableFactory.getFlagFor(item?.code)
            if (countryFlagRes != null) {
                val countryFlag = ContextCompat.getDrawable(context, countryFlagRes)
                holder.countryName?.setCompoundDrawablesWithIntrinsicBounds(countryFlag, null, null, null)
            } else {
                holder.countryName?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            convertViewLocal.isClickable = false
            convertViewLocal.isFocusable = false

            return convertViewLocal
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return fillView(position, convertView, parent)
        }

        internal class ViewHolder {
            var countryName: TextView? = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                ui_iv_coin_picture.setImageURI(result.uri)
                ui_ll_coin_picture.visibility = View.GONE
                ui_iv_coin_picture.visibility = View.VISIBLE

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Snackbar.make(ui_cl_cointainer, R.string.edit_picture_failed, Snackbar.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
