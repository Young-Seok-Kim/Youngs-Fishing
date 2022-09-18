package com.youngs.common

import android.Manifest
import android.text.InputFilter
import java.util.regex.Pattern

object Define {

    val BASE_URL_HTTP_DEBUG : String = "http://192.168.0.15:8080/YoungsFishing/"
//    val BASE_URL_HTTPS_RELEASE : String = "https://awsyoungsbook.duckdns.org/"

    val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
}