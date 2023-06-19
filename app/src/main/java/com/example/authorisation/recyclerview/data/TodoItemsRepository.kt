package com.example.authorisation.recyclerview.data

import android.content.Context
import androidx.fragment.app.FragmentActivity
//Репозиторий с захардкодиными значениями --
class TodoItemsRepository {

    fun getTasks(context: Context?): List<TaskPreview>{
        return buildList {
            val date = "10/08/2023"
            val text = "Скушать булочку, попить водичку, ММММ блаженство"
            val text1 = "Cходить в магазин"
            val text2 = "Узнать как можно купить машину не покупая машину и при это остаться при деньгах и на свободе"
            val text3 = "Посмотреть телевизор"
            val text4 = "Скушать сгущенку и яблоко, и конфеты, и ботинки, и собаку, и кошку, окрошку, сережку, максимку, димку раунд..."

            add(TaskPreview("0",1,text,
                date, true, date, null))

            add(TaskPreview("1",1,text,
                null, true, date, null))

            add(TaskPreview("2",2,text1,
            null, true, date, null))

            add(TaskPreview("3",0,text2,
                null, true, date, null))

            add(TaskPreview("4",1,text3,
                null, true, date, null))

            add(TaskPreview("5",1,text4,
                null, true, date, null))

            add(TaskPreview("6",2,text2,
                null, true, date, null))

            add(TaskPreview("7",1,text4,
                null, true, date, null))

            add(TaskPreview("8",0,text,
                null, true, date, null))

            add(TaskPreview("9",0,text1,
                null, true, date, null))

            add(TaskPreview("10",2,text3,
                null, true, date, null))

            add(TaskPreview("11",0,text,
                null, false, date, null))

            add(TaskPreview("12",1,text4,
                null, false, date, null))

            add(TaskPreview("13",2,text2,
                null, false, date, null))

            add(TaskPreview("14",0,text,
                null, false, date, null))

            add(TaskPreview("15",1,text3,
                null, true, date, null))

            add(TaskPreview("end",3,text1,
                null, false, date, null))


        }
    }
}