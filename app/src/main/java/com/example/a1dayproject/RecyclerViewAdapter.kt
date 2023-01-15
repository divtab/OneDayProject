package com.example.a1dayproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.a1dayproject.db.UserEntity
import kotlin.properties.Delegates

class RecyclerViewAdapter(private val listener: EditMode): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolderItem>() {
    var items = ArrayList<UserEntity>()

    //ViewHolder
    inner class ViewHolderItem(view: View, val listener: EditMode): RecyclerView.ViewHolder(view) {

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val deleteUserID = view.findViewById<ImageView>(R.id.deleteUserID)

        fun bind(data: UserEntity) {
            tvName.text = data.name
            deleteUserID.setOnClickListener {
                listener.onDeleteUserClickListener(data)
            }
        }
    }

    fun setListData(data: ArrayList<UserEntity>) {
        this.items = data
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): RecyclerViewAdapter.ViewHolderItem {
        val itemXml = LayoutInflater.from(parent.context).inflate(R.layout.one_layout,parent,false)
        return ViewHolderItem(itemXml, listener)
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolderItem, position: Int){
        holder.itemView.setOnClickListener {
            listener.onItemClickListener(items[position])
        }
        holder.bind(items[position])
    }



    override fun getItemCount(): Int {
        return items.size
    }



    interface RowClickListener{
        fun onDeleteUserClickListener(user: UserEntity)
        fun onItemClickListener(user: UserEntity)

    }
}

