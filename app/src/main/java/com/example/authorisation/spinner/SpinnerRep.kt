package com.example.authorisation.spinner

import android.content.Context
import com.example.authorisation.R

//TODO пока не работает
//Планировалось что я буду подгружать отсюда данные для спинера, но что то несложилось(на значения не смотрите)

class SpinnerRep {
    fun getSpinner(context: Context): List<Model>{
        return buildList {
            add(Model("normal", null))
            add(Model("low", R.drawable.arrow))
            add(Model("high", R.color.red))
        }
    }
}