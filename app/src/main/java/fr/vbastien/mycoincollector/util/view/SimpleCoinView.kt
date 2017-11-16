package fr.vbastien.mycoincollector.util.view

import android.content.Context
import android.content.res.TypedArray
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.widget.LinearLayout
import com.crashlytics.android.Crashlytics
import fr.vbastien.mycoincollector.R
import kotlinx.android.synthetic.main.item_simple_coin.view.*

/**
 * Created by boulotperso on 13/11/2017.
 */
class SimpleCoinView : LinearLayout {
    private var possess = false;

    constructor(context: Context) : this(context, null) {
        init(context, null)
    }
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        init(context, attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs : AttributeSet?) {
        inflate(context, R.layout.item_simple_coin, this)
        if (attrs != null) {
            val a : TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SimpleCoinView, 0, 0)
            try {
                ui_tv_simple_value.text = a.getString(R.styleable.SimpleCoinView_coin_facial_value)
                possess = a.getBoolean(R.styleable.SimpleCoinView_possess, false)
                setPossess(possess)
            } catch (e: Exception) {
                Crashlytics.logException(e)
                e.printStackTrace()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }

    public fun setPossess(possess : Boolean) {
        this.possess = possess
        if (possess) {
            ui_iv_simple_shape.setImageResource(R.drawable.round_shape)
            ui_tv_simple_value.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        } else {
            ui_iv_simple_shape.setImageResource(R.drawable.dot_circle_shape)
            ui_tv_simple_value.setTextColor(ContextCompat.getColor(context, R.color.material_light_white))
        }
    }

}