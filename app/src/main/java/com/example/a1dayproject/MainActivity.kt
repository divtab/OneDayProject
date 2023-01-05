package com.example.a1dayproject

import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.example.a1dayproject.db.UserEntity


class MainActivity : AppCompatActivity(),MainRecyclerViewAdapter.RowClickListener {
    lateinit var mainRVA:MainRecyclerViewAdapter
    lateinit var viewModel:MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.MainRV)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            mainRVA = MainRecyclerViewAdapter(this@MainActivity)
            adapter = mainRVA
            val divider = DividerItemDecoration(applicationContext,VERTICAL)
            addItemDecoration(divider)
        }
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.getAllUsersObservers().observe(this, Observer {
            mainRVA.setListData(ArrayList(it))
            mainRVA.notifyDataSetChanged()
        })

    }



    //メニューを初めて表示するときに一度だけ呼び出される。
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }
    //オプションメニューの項目が選択されたときに呼ばれる。
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //戻り値用の変数を初期値trueで用意。
        var returnVal = true
        var intent: Intent
        //item.itemIdは、選択されたオプションメニューのid
        when(item.itemId){
            R.id.action_editMode -> {
                intent = Intent(this, EditMode::class.java)
                startActivity(intent)
            }
            else ->
                returnVal = super.onOptionsItemSelected(item)
        }
        return  returnVal
    }
    //
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



    override fun onCheckBoxClickTrue(user:UserEntity){
        user.check = true
        viewModel.updateUserInfo(user)
    }

    override fun onCheckBoxClickfalse(user:UserEntity){
        user.check = false
        viewModel.updateUserInfo(user)
    }

    override fun onItemClickListener(user: UserEntity) {

    }


}