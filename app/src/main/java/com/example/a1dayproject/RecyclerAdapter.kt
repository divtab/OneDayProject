package com.example.a1dayproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolderItem>() {

    var items = ArrayList<UserEh>

    //1行だけのレイアウト
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val itemXml = LayoutInflater.from(parent.context).inflate(R.layout.one_layout,parent,false)
        return ViewHolderItem(itemXml)
    }

    //position番目のデータをレイアウトに表示させるようにセット
    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        val currentItem = todolist[position]
        holder.tvHolder.text = currentItem.databaseName
    }
    //リストサイズ
    override fun getItemCount(): Int {
        return todolist.size
    }


    //ViewHolder
    inner class ViewHolderItem(view:View):RecyclerView.ViewHolder(view){
        lateinit var tvName:TextView
        init {
            tvName = view.findViewById(R.id.tvName)
        }
    }

}