package com.example.a1dayproject

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.example.a1dayproject.db.UserEntity
import java.util.*
import kotlin.collections.ArrayList

class EditMode : AppCompatActivity(),RecyclerViewAdapter.RowClickListener{
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var viewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_mode)

        //optionBar 戻る
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //optionBar タイトル
        supportActionBar?.title = "EditMode..."
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAdapter)
        val dataSet: ArrayList<String> = arrayListOf()
        var i = 0
        while (i < 20) {
            val str: String = java.lang.String.format(Locale.US, "Data_0%d", i)
            dataSet.add(str)
            i++
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditMode)
            recyclerViewAdapter = RecyclerViewAdapter(this@EditMode)
            adapter = recyclerViewAdapter
            //アイテムの区切り線
            val divider = DividerItemDecoration(applicationContext, VERTICAL)
            addItemDecoration(divider)
            //
            setHasFixedSize(true)
        }

        val itemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)


        var adapter = recyclerView.adapter as RecyclerViewAdapter

        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    adapter.notifyItemMoved(fromPos, toPos)
                    return true // true if moved, false otherwise
                }



                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,direction: Int) {
//                    dataSet.removeAt(viewHolder.adapterPosition)
//                    adapter.notifyItemRemoved(viewHolder.adapterPosition)

                }
            })

        mIth.attachToRecyclerView(recyclerView)



        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getAllUsersObservers().observe(this, Observer {
            recyclerViewAdapter.setListData(ArrayList(it))
            recyclerViewAdapter.notifyDataSetChanged()
        })

        val saveButton = findViewById<Button>(R.id.saveBtn)
        val project  = findViewById<EditText>(R.id.etProject)


//      テキストエディット空欄時、保存変更ボタンを使用不可にする。
        project.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun afterTextChanged(p0: Editable?) {
                saveButton.isEnabled = p0?.toString().equals("") != true
            }
        })


        //保存ボタン押下時
        saveButton.setOnClickListener {

            val project  = findViewById<EditText>(R.id.etProject)
            if(project.text.toString() == ""){
                   val toast = Toast.makeText(this,"空欄では登録できません。",Toast.LENGTH_LONG)
                   toast.show()
            }else {
                if (saveButton.text.equals("保存")) {
                    val user = UserEntity(0, project.text.toString(), false)
                    viewModel.insertUserInfo(user)
                } else {
                    //ボタンが”変更”のとき
                    val user = UserEntity(
                        project.getTag(project.id).toString().toInt(),
                        project.text.toString(),
                        false
                    )
                    viewModel.updateUserInfo(user)
                    saveButton.text = "保存"
                }
                project.setText("")
            }
        }
   }



    override fun onDeleteUserClickListener(user: UserEntity) {
        val project  = findViewById<EditText>(R.id.etProject)
        val saveButton = findViewById<Button>(R.id.saveBtn)
        saveButton.text = "保存"
        project.setText("")

        viewModel.deleteUserInfo(user)
    }

    override fun onItemClickListener(user: UserEntity) {
        val project  = findViewById<EditText>(R.id.etProject)
        val saveButton = findViewById<Button>(R.id.saveBtn)
        project.setText(user.name)
        project.setTag(project.id, user.id)
        saveButton.text = "変更"
    }

    //optionBar 押下処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var returnVal = true
        val intent = Intent(this, MainActivity::class.java)
        //android.R.id.home : Android SDKで用意されたR値を使用する。
        if(item.itemId == android.R.id.home){
            finish()
            startActivity(intent)
        }
        else{
            returnVal = super.onOptionsItemSelected(item)
        }
        return returnVal
    }
}