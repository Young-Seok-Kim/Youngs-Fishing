package com.youngs.youngsfishing.fishinformation

import java.time.LocalDate

data class FishInformationModel(
    val fish_no : String
    ,val fish_name : String
    ,var isChecked : Boolean = false
)