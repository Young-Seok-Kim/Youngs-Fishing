package com.youngs.common

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.gson.JsonObject
import com.youngs.common.network.NetworkConnect
import com.youngs.common.network.NetworkProgressDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


object YoungsFunction {
    fun stringArrayToJson(jsonString : String ) : JSONArray
    {
        val jsonObject = JSONObject(jsonString)

        val resultJson : JSONArray = jsonObject.get("RESULT_LIST") as JSONArray

        if(jsonObject.get("RESULT_LIST").toString() == "[]")
            resultJson.put("")

        return resultJson
    }

    fun stringIntToJson(jsonString: String): Int {
        val jsonObject = JSONObject(jsonString)

        return jsonObject.get("RESULT_LIST").toString().toInt()
    }

    /**
     * 현재 날짜 및 시간을 리턴한다.
     * param ex "yyyyMMdd"  "yyyy-MM-dd" "HH:mm" ...
     */
    fun getNowDate(format: String? = null): String {
        return SimpleDateFormat(if (format.isNullOrEmpty()) "yyyy-MM-dd" else format).format(Date())
    }

    /**
     * Date형식을 입력하면 yyyy-MM-dd 형식의 String으로 반환해준다.
     */
    fun getDate(date: Date): String {
        val simple: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")

        return simple.format(date)
    }

    /*
    단순 OK 버튼
     */
    fun messageBoxOK(context: Context, title : String, Message : String){
        val messageBox = AlertDialog.Builder(context)
        .setTitle(title)
        .setMessage(Message)
        .setPositiveButton("확인") {
                _: DialogInterface, _: Int ->
        }
        .setCancelable(false)
        .show()
    }

    /*
    OK 버튼을 누르면 특정 액션이 동작하도록
     */
    fun messageBoxOKAction(context: Context, title : String, Message : String, OKAction : () -> Unit){
        val messageBox = AlertDialog.Builder(context)
        messageBox.setTitle(title)
        .setMessage(Message)
        .setPositiveButton("확인") {
                _: DialogInterface, _: Int ->
            OKAction()
        }
        .setCancelable(false)
        .show()
    }
    fun messageBoxOKCancelAction(context: Context, title : String, Message : String, OKAction : () -> Unit, cancelAction : () -> Unit){
        val messageBox = AlertDialog.Builder(context)
        messageBox.setTitle(title)
        .setMessage(Message)
        .setPositiveButton("확인") {
                _: DialogInterface, _: Int ->
            OKAction()
        }
        .setNegativeButton("취소"){
                _: DialogInterface, _: Int ->
            cancelAction()
        }
        .setCancelable(false)
        .show()
    }

    /*
     01012345678과 같은 형식으로 파라미터를 넣으면 국제번호로 값을 return 해준다.
     */
    fun phoneNumber82(msg : String) : String{

        val firstNumber : String = msg.substring(0,3)
        var phoneEdit = msg.substring(3)

        when(firstNumber){
            "010" -> phoneEdit = "+8210$phoneEdit"
            "011" -> phoneEdit = "+8211$phoneEdit"
            "016" -> phoneEdit = "+8216$phoneEdit"
            "017" -> phoneEdit = "+8217$phoneEdit"
            "018" -> phoneEdit = "+8218$phoneEdit"
            "019" -> phoneEdit = "+8219$phoneEdit"
            "106" -> phoneEdit = "+82106$phoneEdit"
        }
        Log.d("국가코드로 변경된 번호 ",phoneEdit)
        return phoneEdit
    }

    fun getStringFromJson(jsonObject: JSONObject, columnName: String, defaultValue: String?): String? {
        if (jsonObject.has(columnName)) {
            var string = jsonObject.getString(columnName)
            if (string.equals("null", ignoreCase = true)) {
                string = ""
            }
            return string
        } else {
            return defaultValue
        }
    }
    fun getIntFromJson(jsonObject: JSONObject, columnName: String, defaultValue: Int?): Int? {
        return if (jsonObject.has(columnName)) {
            jsonObject.getInt(columnName)
        } else {
            defaultValue
        }
    }



    fun setDate(context: Context, textView: TextView, divider: String) {
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]

        DatePickerDialog(
            context,
            { _, yearSelected, monthOfYear, dayOfMonth ->
                val date: String
                val dialogMonth: String = if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString() + ""
                val dialogDay: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString() + ""
                date = yearSelected.toString() + divider + dialogMonth + divider + dialogDay
                textView.text = date
            },
            year,
            month,
            day
        ).show()
    }

    private fun setTime(context: Context?, tv: TextView, divider: String) {
        val cal = Calendar.getInstance()
        val hour = cal[Calendar.HOUR_OF_DAY]
        val minute = cal[Calendar.MINUTE]
        TimePickerDialog(
            context,
            { _, hourOfDay, min ->
                val time: String
                val dialogHour: String = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString() + ""
                val dialogMinute: String = if (min < 10) "0$min" else min.toString() + ""
                time = dialogHour + divider + dialogMinute + divider + "00"
                tv.text = time
            },
            hour,
            minute,
            true
        ).show()
    }


}