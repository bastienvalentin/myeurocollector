package fr.vbastien.mycoincollector.controller.coin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.squareup.picasso.Picasso

import fr.vbastien.mycoincollector.R
import fr.vbastien.mycoincollector.db.AppDatabase
import fr.vbastien.mycoincollector.db.Coin
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_coin_add.*
import kotlinx.android.synthetic.main.activity_coin_detail.*
import org.reactivestreams.Subscriber
import java.util.function.BiConsumer

class CoinDetailActivity : AppCompatActivity() {

    private var coinId: Int? = -1
    private var disposableList : MutableList<Disposable> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_detail)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab.setOnClickListener { view ->
            val editIntent = Intent(this@CoinDetailActivity, CoinAddActivity::class.java)
            editIntent.putExtra("coin_id", coinId)
            startActivity(editIntent)
        }

        coinId = intent?.getIntExtra("coin_id", -1)

    }

    override fun onStart() {
        super.onStart()
        disposableList.add(AppDatabase.getInMemoryDatabase(this).coinModel().findCoinWithId(coinId.toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::initView,
                        this::onCoinLoadError
                )
        )
    }

    fun onCoinLoadError(error: Throwable) {
        Crashlytics.logException(error)
        Toast.makeText(this, R.string.loading_error, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun initView(coin: Coin) {
        if (!TextUtils.isEmpty(coin.img)) {
            Picasso.with(this).load(coin.img).resize(1024, 1024).centerInside().into(image)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
