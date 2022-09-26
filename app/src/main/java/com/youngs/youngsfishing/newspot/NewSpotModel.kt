package com.youngs.youngsfishing.newspot

import java.time.LocalDate

data class NewSpotModel(
    val fish_no : String
    ,val fish_name : String
    ,var isChecked : Boolean = false
)