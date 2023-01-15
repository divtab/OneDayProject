package com.example.a1dayproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.example.a1dayproject.db.RoomAppDb
import com.example.a1dayproject.db.UserEntity

class EditMode() : AppCompatActivity(),RecyclerViewAdapter.RowClickListener{
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    lateinit var viewModel: MainActivityViewModel
    lateinit var user1:UserEntity
    var moveJudge:Boolean = false
    var btnMode:String = "save"
    var items = ArrayList<UserEntity>()
    val database = RoomAppDb.getAppDatabase(this)
    val userDao = database?.userDao()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_mode)

        val saveButton = findViewById<Button>(R.id.saveBtn)
        saveButton.background = resources.getDrawable(R.drawable.background_selector, null)

        //optionBar 戻る
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //optionBar タイトル
        supportActionBar?.title = "EditMode..."
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAdapter)
//        val dataSet: ArrayList<String> = arrayListOf()
//        var i = 0
//        while (i < 20) {
//            val str: String = java.lang.String.format(Locale.US, "Data_0%d", i)
//            dataSet.add(str)
//            i++
//        }

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
                ItemTouchHelper.END
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val fromPos = viewHolder.adapterPosition
                    val toPos = target.adapterPosition
                    adapter.notifyItemMoved(fromPos, toPos)
                    println("aaa")
                    println(fromPos)
                    println(toPos)
                    return true // true if moved, false otherwise
                }
                override fun onMoved(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    fromPos: Int,
                    target: RecyclerView.ViewHolder,
                    toPos: Int,
                    x: Int,
                    y: Int) {
                    moveJudge = true
                    val tvName = findViewById<TextView>(R.id.tvName)

                    val a = userDao?.getAllUserInfo()?.get(1)?.id
                    println("userDao"+a)


                    if (moveJudge == true){
                        val project  = findViewById<EditText>(R.id.etProject)
                        project.setText("**並び順を保存しますか?**")
                        saveButton.text = "保存"
                        saveButton.background = resources.getDrawable(R.drawable.background_selector, null)
                        blink(saveButton)
                        moveJudge = false

                        var test = recyclerViewAdapter.items

                        //　⇑⇑　上のアイテムを下にムーブした際
                        if (fromPos < toPos){

                            var a = recyclerViewAdapter.items[fromPos]
                            recyclerViewAdapter.items[fromPos] = recyclerViewAdapter.items[toPos]
                            recyclerViewAdapter.items[toPos] = a

                            var test2 = recyclerViewAdapter.items[0]
                            var test3 = recyclerViewAdapter.items[1]
                            var test = recyclerViewAdapter.items
                            println("リスト移動直後　↑↑ : "+ test)
                            println(test2)
                            println(test3)
                        //　⇓⇓　下のアイテムを上にムーブした際
                        }else{
                            var a = recyclerViewAdapter.items[toPos]
                            recyclerViewAdapter.items[toPos] = recyclerViewAdapter.items[fromPos]
                            recyclerViewAdapter.items[fromPos] = a

                            var test = recyclerViewAdapter.items
                            println("リスト移動直後 ⇓⇓　: "+ test)
                        }

                        btnMode = "sort"

                    }
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,direction: Int) {
//                    .removeAt(viewHolder.adapterPosition)
//                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
            })

        mIth.attachToRecyclerView(recyclerView)



        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getAllUsersObservers().observe(this, Observer {
            recyclerViewAdapter.setListData(ArrayList(it))
            recyclerViewAdapter.notifyDataSetChanged()
        })



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
            val a = saveButton.background
            //プロジェクト欄が空欄の時トースト
            if(project.text.toString() == ""){
                   val toast = Toast.makeText(this,"空欄では登録できません。",Toast.LENGTH_LONG)
                   toast.show()

            }else {
                //保存 or 変更
                if (btnMode == "save") {
                    //保存ボタンが押せる状況で、
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
                        //ボタン変更押下後、青色に変更。
                        saveButton.background = resources.getDrawable(R.drawable.background_selector, null)
                        saveButton.text = "保存"
                    }
                    project.setText("")

                //並び順を保存
                }else if (btnMode == "sort") {
                    //ボタンが”変更”のとき
                    val cnt = recyclerViewAdapter.items.size


                    //　×　item[0]にid=11,takuma,trueを保存する
                    //　〇　UserEntityのidが一致するitemsにname,checkを保存する。
                    // recyclerViewAdapter.items["0"]なぜ0でいいのかがわからない。iじゃないのか？　
//                    recyclerViewAdapter.items[0] = UserEntity(2,"tamafadsafds",true)
//                    var user = recyclerViewAdapter.items[0]
//                    println(recyclerViewAdapter.items)
//                    viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
//                    viewModel.updateUserInfo(user)


                    for (i in 0..cnt-1) {
                        val a = userDao?.getAllUserInfo()?.get(i)?.id
                        val b:Int = a!!
                        println("uuu")
                        println(b)

                        println(recyclerViewAdapter.items[i].name)
                        recyclerViewAdapter.items[i] = UserEntity(
                            b,
                            recyclerViewAdapter.items[i].name,
                            recyclerViewAdapter.items[i].check
                        )
                        println(recyclerViewAdapter.items[i])
                        var user = recyclerViewAdapter.items[i]
                        println("user"+user)
                        println(recyclerViewAdapter.items)
                        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
                        viewModel.updateUserInfo(user)
                    }


                    println("sortしました。")
                    restart()
                }
            }
        }
   }

    private fun restart() {
        val intent = Intent(this, EditMode::class.java)
        finishAndRemoveTask()
        startActivity(intent)
    }


    override fun onDeleteUserClickListener(user: UserEntity) {
        val project  = findViewById<EditText>(R.id.etProject)
        val saveButton = findViewById<Button>(R.id.saveBtn)
        saveButton.background = resources.getDrawable(R.drawable.btn_del,null)
        saveButton.text = "保存"
        project.setText("")

        viewModel.deleteUserInfo(user)
    }

    override fun onItemClickListener(user: UserEntity) {
        val project  = findViewById<EditText>(R.id.etProject)
        val saveButton = findViewById<Button>(R.id.saveBtn)
        saveButton.background = resources.getDrawable(R.drawable.btn_itemclick,null)
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
    private fun blink(view: View) {
        val handler = Handler()

        Thread(Runnable {
            val timeToBlink = 500
            try {
                Thread.sleep(timeToBlink.toLong())
            } catch (e: Exception) {
            }

            handler.post {
                if (view.alpha == 1f) {
                    view.alpha = 0.5f
                } else {
                    view.alpha = 1f
                }
                blink(view)
            }
        }).start()


    }
}


