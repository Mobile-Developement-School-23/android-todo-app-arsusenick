package com.example.authorisation.spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.authorisation.R

//Логика для кастом спинера который я сделал чтобы у меня помимо текста ещё была картинка
// (хотел чтобы и цвет текста менялся но что то пошло не так)
class CustomDropDownAdapter(val context: Context, var dataSource: List<Model>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.spinner, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }
//заполнение полей
        vh.label.text = dataSource[position].name

        val pic = dataSource[position].pic
        if (pic == null){
            vh.picture.visibility = View.GONE
        }
        else{
            vh.picture.setImageResource(pic)
        }

        return view
    }

    override fun getItem(position: Int): Any? {
        return dataSource[position];
    }

    override fun getCount(): Int {
        return dataSource.size;
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

//инициализация полей
    private class ItemHolder(row: View?) {
        val label: TextView
        val picture: ImageView

        init {
            label = row?.findViewById(R.id.textick) as TextView
            picture = row.findViewById(R.id.ico) as ImageView
        }
    }

}