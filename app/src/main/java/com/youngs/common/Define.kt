package com.youngs.common

import android.Manifest
import android.text.InputFilter
import java.util.regex.Pattern

object Define {

    const val BASE_URL_HTTP_DEBUG : String = "http://121.134.147.60:8080/YoungsFishing/"
    const val BASE_URL_HTTPS_RELEASE : String = "https://awsyoungsbook.duckdns.org/"
    const val KAKAO_NATIVE_KEY = "b78c7e68fc97d3d4c2a57c8cc1074f6e"

    const val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
}