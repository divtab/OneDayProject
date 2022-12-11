package com.example.a1dayproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.a1dayproject.db.UserEntity


class MainActivity : AppCompatActivity(),MainRecyclerViewAdapter.RowClickListener {
    lateinit var mainRVA:MainRecyclerViewAdapter
    lateinit var viewModel:MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
    //ここから再開する


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




}