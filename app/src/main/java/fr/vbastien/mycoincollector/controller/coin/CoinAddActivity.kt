package fr.vbastien.mycoincollector.controller.coin

import android.content.Context
import android.content.Intent
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
import android.widget.Toolbar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.asyncloader.AsyncCountryLoader
import fr.vbastien.mycoincollector.controller.country.FlagDrawableFactory
import fr.vbastien.mycoincollector.db.Country
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_coin_add.*
import java.util.*

class CoinAddActivity : AppCompatActivity(), AsyncCountryLoader.AsyncCountryLoaderListener<List<Country>> {

    private var countryList : List<Country> = mutableListOf()
    private var countryAdapter : CountrySpinnerAdapter? = null

    override fun onCountryLoaded(countries: List<Country>) {
        this.countryList = countries
        initView()
    }

    override fun onCountryLoadError(error: Throwable) {
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
    }

    override fun onStart() {
        super.onStart()
        if (intent != null && intent.hasExtra("coin_id")) {
            // TODO
        } else {
            ui_ll_content.visibility = View.INVISIBLE
            ui_pb_loading.visibility = View.VISIBLE
            // TODO add a fake view
            disposableList.add(AsyncCountryLoader.loadCountriesFromDataSource(this, this))
        }
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
