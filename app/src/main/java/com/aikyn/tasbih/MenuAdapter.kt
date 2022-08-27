package com.aikyn.calculator

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aikyn.tasbih.ListItem
import com.aikyn.tasbih.MainActivity
import com.aikyn.tasbih.R

class MenuAdapter (listArray: ArrayList<ListItem>, context: Context): RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    var array = listArray
    var cont = context

    //Создаем то что будем привязывать
    class ViewHolder (view: View): RecyclerView.ViewHolder(view) {
        val tasbih_count = view.findViewById<TextView>(R.id.tasbih_count)!!
        val description = view.findViewById<TextView>(R.id.description)!!

        fun bind(listItem: ListItem, pos: Int, cont: Context){
            tasbih_count.text = listItem.tasbih_count
            description.text = listItem.description
            itemView.setOnClickListener {
                val intent = Intent(cont, MainActivity::class.java)
                intent.putExtra("count", listItem.tasbih_count)
                intent.putExtra("description", listItem.description)
                intent.putExtra("position", pos+1)
                cont.startActivity(intent)
            }
        }
    }

    //Заполняем шаблоны
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(cont)
        return ViewHolder(inflater.inflate(R.layout.item_layout, parent, false))
    }

    //Привязываем все и что то делаем
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(array.get(position), position, cont)
    }

    //Сколько элементов
    override fun getItemCount(): Int {
        return array.size
    }
}