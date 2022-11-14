package com.example.a1dayproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle import android.view.MenuItem
import androidx.appcompat.app.ActionBar

class EditMode : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_mode)

        //optionBar 戻る
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //optionBar タイトル
        supportActionBar?.title = "EditMode..."
    }




    //optionBar 押下処理
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var returnVal = true
        //android.R.id.home : Android SDKで用意されたR値を使用する。
        if(item.itemId == android.R.id.home){
            finish()
        }
        else{
            returnVal = super.onOptionsItemSelected(item)
        }
        return returnVal
    }
}