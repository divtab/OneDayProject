package com.example.a1dayproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
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

    //保存ボタン押下時
    fun onSaveBtn(view: View){
        val saveBtn:Button = findViewById(R.id.saveBtn)
        var etProject:EditText = findViewById(R.id.etProject)

        //プロジェクト欄の内容を取得。
        val note : String = etProject.text.toString()

        //プロジェクト欄を白紙に戻す。
        etProject.setText("")

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