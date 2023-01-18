package com.example.a1dayproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.example.a1dayproject.db.RoomAppDb
import com.example.a1dayproject.db.UserEntity

class EditMode : AppCompatActivity(),RecyclerViewAdapter.RowClickListener{
    lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var viewModel: MainActivityViewModel
    var moveJudge:Boolean = false
    var btnMode:String = "save"
    //データベースの取得
    private val dataBase = RoomAppDb.getAppDatabase(this)
    //UserDaoの取得
    private val userDao = dataBase?.userDao()


    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_mode)



        val saveButton = findViewById<Button>(R.id.saveBtn)
        saveButton.background = resources.getDrawable(R.drawable.background_selector, null)
        val returnButton = findViewById<Button>(R.id.returnButton)


        //optionBar 戻る
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //optionBar タイトル
        supportActionBar?.title = "EditMode..."
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAdapter)


        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@EditMode)
            recyclerViewAdapter = RecyclerViewAdapter(this@EditMode)
            adapter = recyclerViewAdapter
//            //アイテムの区切り線
//            val divider = DividerItemDecoration(applicationContext, VERTICAL)
//            addItemDecoration(divider
//            setHasFixedSize(true)
        }

//        val itemDecoration: RecyclerView.ItemDecoration =
//            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
//        recyclerView.addItemDecoration(itemDecoration)


        val adapter = recyclerView.adapter as RecyclerViewAdapter

        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
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

                    if (moveJudge){
                        val returnButton = findViewById<Button>(R.id.returnButton)
                        returnButton.isEnabled = true
                        val project  = findViewById<EditText>(R.id.etProject)
                        project.setText("**並び順を保存しますか?**")
                        saveButton.text = "保存"
                        saveButton.background = resources.getDrawable(R.drawable.background_selector, null)
                        returnButton.background = resources.getDrawable(R.drawable.btn_return,null)

                        moveJudge = false

                        //　⇑⇑　上のアイテムを下にムーブした際
                        if (fromPos < toPos){

                            val a = recyclerViewAdapter.items[fromPos]
                            recyclerViewAdapter.items[fromPos] = recyclerViewAdapter.items[toPos]
                            recyclerViewAdapter.items[toPos] = a
                        //　⇓⇓　下のアイテムを上にムーブした際
                        }else{
                            val a = recyclerViewAdapter.items[toPos]
                            recyclerViewAdapter.items[toPos] = recyclerViewAdapter.items[fromPos]
                            recyclerViewAdapter.items[fromPos] = a
                        }

                        btnMode = "sort"

                    }
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,direction: Int) {
//                    .removeAt(viewHolder.adapterPosition)
//                    adapter.notifyItemRemoved(viewHolder.adapterPosition)
                }
                // 1. 行が選択された時に、このコールバックが呼ばれる。ここで行をハイライトする。
                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                // 2. 行が選択解除された時 (ドロップされた時) このコールバックが呼ばれる。ハイライトを解除する。
                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)

                    viewHolder?.itemView?.alpha = 1.0f
                }
            })

        mIth.attachToRecyclerView(recyclerView)

        fun startDragging(viewHolder: RecyclerView.ViewHolder) {
            mIth.startDrag(viewHolder)
        }

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.getAllUsersObservers().observe(this) {
            recyclerViewAdapter.setListData(ArrayList(it))
            recyclerViewAdapter.notifyDataSetChanged()
        }

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
                    for (i in 0 until cnt) {
                        val a = userDao?.getAllUserInfo()?.get(i)?.id
                        val b:Int = a!!

                        recyclerViewAdapter.items[i] = UserEntity(
                            b,
                            recyclerViewAdapter.items[i].name,
                            recyclerViewAdapter.items[i].check
                        )
                        val user = recyclerViewAdapter.items[i]
                        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
                        viewModel.updateUserInfo(user)
                    }
                    val toast = Toast.makeText(this,"並び順を保存しました。",Toast.LENGTH_LONG)
                    toast.show()

                    val returnBtn = findViewById<Button>(R.id.returnButton)
                    returnBtn.isEnabled = false
//                    val project = findViewById<TextView>(R.id.etProject)
                    project.setText("")
                    btnMode = "save"

                }
            }
        }
        returnButton.setOnClickListener {

            val cnt = recyclerViewAdapter.items.size

            //　×　item[0]にid=11,takuma,trueを保存する
            //　〇　UserEntityのidが一致するitemsにname,checkを保存する。
            for (i in 0 until cnt) {
                val a = userDao?.getAllUserInfo()?.get(i)?.id

                recyclerViewAdapter.items[i] = UserEntity(
                    recyclerViewAdapter.items[i].id,
                    recyclerViewAdapter.items[i].name,
                    recyclerViewAdapter.items[i].check
                )
                val user = recyclerViewAdapter.items[i]
                viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
                viewModel.updateUserInfo(user)
            }
            val toast = Toast.makeText(this,"並び順を元に戻しました。",Toast.LENGTH_LONG)
            toast.show()

            btnMode = "save"
            returnButton.isEnabled = false
            project.setText("")

        }
   }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onDeleteUserClickListener(user: UserEntity) {
        val project  = findViewById<EditText>(R.id.etProject)
        val saveButton = findViewById<Button>(R.id.saveBtn)
        val returnButton = findViewById<Button>(R.id.returnButton)
        saveButton.background = resources.getDrawable(R.drawable.btn_del,null)
        saveButton.text = "保存"
        project.setText("")
        returnButton.isEnabled = false
        viewModel.deleteUserInfo(user)

    }

    @SuppressLint("UseCompatLoadingForDrawables")
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



}


