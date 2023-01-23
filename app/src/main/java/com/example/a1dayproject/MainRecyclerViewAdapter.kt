package com.example.a1dayproject

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.a1dayproject.db.UserEntity

class MainRecyclerViewAdapter (private val listener:MainActivity): RecyclerView.Adapter<MainRecyclerViewAdapter.MainViewHolderItem>(){
    var items = ArrayList<UserEntity>()

    fun setListData(data: ArrayList<UserEntity>) {
        this.items = data
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainRecyclerViewAdapter.MainViewHolderItem {
        val itemXml = LayoutInflater.from(parent.context).inflate(R.layout.main_one_layout,parent,false)

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
    inner class MainViewHolderItem(view: View, private val listener: MainActivity): RecyclerView.ViewHolder(view) {

        private val checkBox = view.findViewById<CheckBox>(R.id.checkBox)
        private val mainTv = view.findViewById<TextView>(R.id.miantv)



        fun bind(data: UserEntity) {
            mainTv.text = data.name
            checkBox.isChecked = data.check

            //チェックボックス押下により取り消し線を引く
            invoke(mainTv,checkBox.isChecked)

            //チェックボックス押下時に状態をRoomに登録
            checkBox.setOnClickListener {

                //チェックボックスを押下した結果trueの時
                if (checkBox.isChecked){
                    listener.onCheckBoxClickTrue(data)
                    listener.parentsCheck()
                }
                //チェックボックスを押下した結果falseの時
                else{
                    listener.onCheckBoxClickfalse(data)
                    listener.parentsCheck()

                }


            }


        }
    }

    interface RowClickListener{
        fun onCheckBoxClickTrue(user: UserEntity)
        fun onCheckBoxClickfalse(user: UserEntity)
        fun onItemClickListener(user: UserEntity)
    }


    //TextViewに取り消し線を付ける
    fun invoke(textView: TextView, checked: Boolean) {
        if (checked) {
            textView.apply {
                setTextColor(Color.LTGRAY)
                paint.flags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                paint.isAntiAlias = true
            }
        } else {
            textView.apply {
                setTextColor(Color.BLACK)
                paint.flags = Paint.ANTI_ALIAS_FLAG
                paint.isAntiAlias = false
            }
        }
    }



}