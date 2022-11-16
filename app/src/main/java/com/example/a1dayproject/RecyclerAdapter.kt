package com.example.a1dayproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a1dayproject.db.UserEntity


class RecyclerAdapter(val listener: EditMode): RecyclerView.Adapter<RecyclerAdapter.ViewHolderItem>() {

    var items = ArrayList<UserEntity>()

    fun setListData(data: ArrayList<UserEntity>) {
        this.items = data
    }



    //1行だけのレイアウト
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderItem {
        val itemXml = LayoutInflater.from(parent.context).inflate(R.layout.one_layout,parent,false)
        return ViewHolderItem(itemXml, listener)
    }

    //position番目のデータをレイアウトに表示させるようにセット
    override fun onBindViewHolder(holder: ViewHolderItem, position: Int) {
        holder.itemView.setOnClickListener {
            listener.onItemClickListener(items[position])
        }
        holder.bind(items[position])
    }
    //リストサイズ
    override fun getItemCount(): Int {
        return items.size
    }


    //ViewHolder
    class ViewHolderItem(view: View, val listener: RowClickListener): RecyclerView.ViewHolder(view) {

        val tvName = view.findViewById<TextView>(R.id.tvName)
        val deleteUserID = view.findViewById<ImageView>(R.id.deleteUserID)

        fun bind(data: UserEntity) {
            tvName.text = data.name
            deleteUserID.setOnClickListener {
                listener.onDeleteUserClickListener(data)
            }
        }
    }

    interface RowClickListener{
        fun onDeleteUserClickListener(user: UserEntity)
        fun onItemClickListener(user: UserEntity)
    }

}