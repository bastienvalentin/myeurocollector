package fr.vbastien.mycoincollector.controller.coin

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.crashlytics.android.Crashlytics
import com.squareup.picasso.Picasso
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

class CoinAddActivity : AppCompatActivity() {

    private var countryList : List<Country> = mutableListOf()
    private var imageUri : Uri? = null
    private var countryId : Int = 0

    fun onCountryLoaded(countries: List<Country>) {
        this.countryList = countries
        initView()
    }

    fun onCountryLoadError(error: Throwable) {
        Crashlytics.logException(error)
        Snackbar.make(ui_cl_cointainer, R.string.loading_error, Snackbar.LENGTH_LONG)
    }

    private var disposableList : MutableList<Disposable> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_add)
        setActionBar(ui_toolbar)

        actionBar.setDisplayHomeAsUpEnabled(true)

        val pictureTakingListener = { _ : View ->
            CropImage.activity(null)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setAllowFlipping(false)
                    .setAllowCounterRotation(false)
                    .setFixAspectRatio(true)
                    .setAspectRatio(1, 1)
                    .start(this)
        }

        ui_ll_coin_picture.setOnClickListener(pictureTakingListener)
        ui_iv_coin_picture.setOnClickListener(pictureTakingListener)

        ui_bt_add_coin.setOnClickListener { onAddButtonClick() }

        if (savedInstanceState != null) {
            if (imageUri == null && !TextUtils.isEmpty(savedInstanceState.getString("imageUri"))) {
                imageUri = Uri.parse(savedInstanceState.getString("imageUri"))
            }
            countryId = savedInstanceState.getInt("countryId", 0)
        }

        if (intent != null && intent.hasExtra("coin_id")) {
            // TODO
        } else {
            ui_ll_content.visibility = View.INVISIBLE
            ui_pb_loading.visibility = View.VISIBLE
            // TODO add a fake view
            disposableList.add(AppDatabase.getInMemoryDatabase(this).countryModel().findCountries()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doOnError { t: Throwable -> onCountryLoadError(t) }
                    .doOnSuccess { countries: List<Country> ->
                        sortCountriesByLocaleName(countries)
                        onCountryLoaded(countries)
                    }
                    .subscribe())
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        if (imageUri != null) {
            displayImage(imageUri)
        } else {
            ui_iv_coin_picture.visibility = View.GONE
            ui_ll_coin_picture.visibility = View.VISIBLE
        }
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
        if (imageUri != null) {
            coin.img = imageUri.toString()
        } else {
            coin.img = null
        }

        disposableList.add(Completable.fromAction { AppDatabase.getInMemoryDatabase(this).coinModel().insertCoin(coin) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    Snackbar.make(ui_cl_cointainer, R.string.coin_added, Snackbar.LENGTH_SHORT).show()
                    resetFields()
                }
                .doOnError {
                    t : Throwable ->
                        Crashlytics.logException(t)
                        Snackbar.make(ui_cl_cointainer, R.string.coin_addition_error, Snackbar.LENGTH_SHORT)
                }
                .subscribe())
    }

    fun resetFields() {
        ui_sp_country.setSelection(0)
        ui_et_coin_value.setText("")
        ui_et_coin_description.setText("")
        ui_iv_coin_picture.visibility = View.GONE
        ui_ll_coin_picture.visibility = View.VISIBLE
        imageUri = null
    }

    private fun sortCountriesByLocaleName(countries : List<Country>) {
        countries.forEach { country -> country.localeName = Locale("", country.code).displayCountry }
        Collections.sort(countries) { p0, p1 ->
            val p0localNameStripped = Normalizer.normalize(p0.localeName, Normalizer.Form.NFD)
            p0localNameStripped.replace("[^\\p{ASCII}]", "")

            val p1localNameStripped = Normalizer.normalize(p1.localeName, Normalizer.Form.NFD)
            p1localNameStripped.replace("[^\\p{ASCII}]", "")

            p0localNameStripped.compareTo(p1localNameStripped)
        }
    }

    private fun initView() {
        ui_ll_content.visibility = View.VISIBLE
        ui_pb_loading.visibility = View.GONE
        val countryAdapter = CountrySpinnerAdapter(this, R.layout.item_country_list, countryList)
        ui_sp_country.adapter = countryAdapter

        countryAdapter.notifyDataSetChanged()

        ui_sp_country.setSelection(findIndexOfCountryWithId(countryId, countryAdapter))

        ui_sp_country.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            }

        }
    }

    override fun onDestroy() {
        disposableList.forEach { disposable -> if (!disposable.isDisposed) disposable.dispose() }
        super.onDestroy()
    }

    internal class CountrySpinnerAdapter(context :Context, var resourceId: Int, var countryList: List<Country>)
        : ArrayAdapter<Country>(context, resourceId, countryList) {

        var layoutInflater : LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            return fillView(position, convertView)
        }

        fun fillView(position: Int, convertView: View?): View {
            val holder: ViewHolder
            val convertViewLocal : View

            if (convertView == null){
                convertViewLocal = layoutInflater.inflate(resourceId, null)
                holder = ViewHolder()

                holder.countryName = convertViewLocal as TextView?

                convertViewLocal.tag = holder

            } else {
                holder = convertView.tag as ViewHolder
                convertViewLocal = convertView
            }

            val item = getItem(position)

            holder.countryName?.text = Locale("", item?.code).displayCountry

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
            return fillView(position, convertView)
        }

        internal class ViewHolder {
            var countryName: TextView? = null
        }
    }

    private fun displayImage(uri : Uri?) {
        if (uri == null) return
        Picasso.with(this).load(uri).resize(1024, 1024).centerInside().into(ui_iv_coin_picture)
        ui_ll_coin_picture.visibility = View.GONE
        ui_iv_coin_picture.visibility = View.VISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                displayImage(imageUri)

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Snackbar.make(ui_cl_cointainer, R.string.edit_picture_failed, Snackbar.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putString("imageUri", imageUri?.toString())
        outState?.putInt("countryId", (ui_sp_country.selectedItem as Country).countryId)
        super.onSaveInstanceState(outState)
    }

    private fun findIndexOfCountryWithId(countryId: Int, countryAdapter : CountrySpinnerAdapter) : Int {
        var i = 0
        countryAdapter.countryList.forEach { (countryId1) ->
            if (countryId1 == countryId) return i
            i++
        }
        return 0
    }

    private fun hasEdittedValue() : Boolean {
        return (!TextUtils.isEmpty(ui_et_coin_description.text)
                || !TextUtils.isEmpty(ui_et_coin_value.text)
                || imageUri != null)
    }

    private fun askExitConfirmation() {
        MaterialDialog.Builder(this)
                .title(R.string.abort_edition_dialog_title)
                .content(R.string.abort_edition_dialog_content)
                .positiveText(R.string.erase)
                .negativeText(R.string.continue_typing)
                .autoDismiss(true)
                .onPositive { _, _ -> finish() }
                .show()
    }

    private fun exitWithConfirmation() {
        if (hasEdittedValue()) {
            askExitConfirmation()
        } else {
            finish()
        }
    }

    override fun onBackPressed() {
        exitWithConfirmation()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            exitWithConfirmation()
        }
        return super.onOptionsItemSelected(item)
    }
}
