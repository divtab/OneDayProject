package com.example.a1dayproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a1dayproject.db.UserEntity

class MainRecyclerViewAdapter (val listener:MainActivity): RecyclerView.Adapter<MainRecyclerViewAdapter.MainViewHolderItem>(){
    var items = ArrayList<UserEntity>()

    fun setListData(data: ArrayList<UserEntity>) {
        this.items = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainRecyclerViewAdapter.MainViewHolderItem {
        val itemXml = LayoutInflater.from(parent.context).inflate(R.layout.,parent,false)
        return MainViewHolderItem(itemXml, listener)
    }
    override fun onBindViewHolder(holder: MainViewHolderItem, position: Int) {
        holder.itemView.setOnClickListener {
            listener.onItemClickListener(items[position])
        }
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    //ViewHolder
    inner class MainViewHolderItem(view: View, val listener: MainActivity): RecyclerView.ViewHolder(view) {

        val checkBox = view.findViewById<TextView>(R.id.checkBox)

        fun bind(data: UserEntity) {
            checkBox.text = data.name
//            deleteUserID.setOnClickListener {
//                listener.onDeleteUserClickListener(data)
//            }
        }
    }

    interface RowClickListener{
        fun onDeleteUserClickListener(user: UserEntity)
        fun onItemClickListener(user: UserEntity)
    }



}