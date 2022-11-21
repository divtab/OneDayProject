package com.example.a1dayproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.example.a1dayproject.db.UserEntity

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
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditMode)
            recyclerViewAdapter = RecyclerViewAdapter(this@EditMode)
            adapter = recyclerViewAdapter
            val divider = DividerItemDecoration(applicationContext, VERTICAL)
            addItemDecoration(divider)
        }

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.getAllUsersObservers().observe(this, Observer {
            recyclerViewAdapter.setListData(ArrayList(it))
            recyclerViewAdapter.notifyDataSetChanged()
        })


        val saveButton = findViewById<Button>(R.id.saveBtn)
        saveButton.setOnClickListener {
            val project  = findViewById<EditText>(R.id.etProject)
            if(saveButton.text.equals("Save")) {
                val user = UserEntity(0, project.text.toString())
                viewModel.insertUserInfo(user)
            } else {
                val user = UserEntity(project.getTag(project.id).toString().toInt(), project.text.toString())
                viewModel.updateUserInfo(user)
                saveButton.setText("Save")
            }
            project.setText("")
        }
    }

    //保存ボタン押下時
//    fun onSaveBtn(view: View){
//        val saveBtn:Button = findViewById(R.id.saveBtn)
//        var etProject:EditText = findViewById(R.id.etProject)
//
//        //プロジェクト欄の内容を取得。
//        val note : String = etProject.text.toString()
//
//        //プロジェクト欄を白紙に戻す。
//        etProject.setText("")
//
//    }
    override fun onDeleteUserClickListener(user: UserEntity) {
        viewModel.deleteUserInfo(user)
    }

    override fun onItemClickListener(user: UserEntity) {
        val project  = findViewById<EditText>(R.id.etProject)
        val saveButton = findViewById<Button>(R.id.saveBtn)
        project.setText(user.name)
        project.setTag(project.id, user.id)
        saveButton.setText("Update")
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