package com.youngs.common.network

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.youngs.youngsfishing.BuildConfig
import com.youngs.youngsfishing.R


class YoungsProgressBar(context: Context) : Dialog(context) {
    init {
        setContentView(R.layout.dialog_progress)
        if (BuildConfig.DEBUG.not())
            setCancelable(false) // 주변을 클릭하면 종료되지 않도록함

        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    /*
        * private val youngsProgressBar: YoungsProgressBar by lazy { YoungsProgressBar(this@EmployeeActivity) }
        * youngsProgressBar.show() // 사용할때사용
        * youngsProgressBar.dismiss() // 종료할때사용
    * */
}