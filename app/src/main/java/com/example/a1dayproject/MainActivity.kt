package com.example.a1dayproject

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.core.os.HandlerCompat
import androidx.core.view.size
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.example.a1dayproject.db.UserEntity
import java.nio.file.Files.size
import java.security.SecureRandom
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.Executors.newCachedThreadPool
import java.util.concurrent.Executors.newSingleThreadExecutor
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(),MainRecyclerViewAdapter.RowClickListener {
    private lateinit var mainRVA:MainRecyclerViewAdapter
    lateinit var viewModel:MainActivityViewModel
    var a = 0

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<TextView>(R.id.background)
        imageView.setBackgroundResource(R.drawable.defalt_background)

        val recyclerView = findViewById<RecyclerView>(R.id.MainRV)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            mainRVA = MainRecyclerViewAdapter(this@MainActivity)
            adapter = mainRVA
            val divider = DividerItemDecoration(applicationContext,VERTICAL)
            addItemDecoration(divider)
        }

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.getAllUsersObservers().observe(this) {
            mainRVA.setListData(ArrayList(it))
            mainRVA.notifyDataSetChanged()

            var checkCnt = 0
            val cnt = mainRVA.items.size
            val parentsText = findViewById<TextView>(R.id.background)
            for (i in 0 until cnt) {
                if (mainRVA.items[i].check == true){
                    checkCnt += 1
                }
            }
            var parent = checkCnt * 100 / cnt
            parentsText.text = "$parent%"
        }

    }
    //チェックボタンが押下で走る
    fun parentsCheck() {
        val handler = HandlerCompat.createAsync(mainLooper)
        val background = findViewById<TextView>(R.id.background)
        val backgroundReceiver = CheckInfoBackgroundReceiver(handler, background)
        val executeService = Executors.newSingleThreadExecutor()
        executeService.submit(backgroundReceiver)
    }
    private inner class CheckInfoBackgroundReceiver(handler: Handler,view: TextView):Runnable{
        private val _handler = handler
        private val _viewText = view
        private val maxColor = 150
        var checkCnt = 0
        private var cnt = mainRVA.items.size
        //チェックされている割合を取得
        var parent = 0
        var mCounter = 0

        override fun run() {
            try {
                for (i in 0 until cnt){
                    if (mainRVA.items[i].check) {
                        checkCnt += 1
                    }
                }
                while (mCounter < 50){
                    // Threadによる処理の中ではUIを操作することができないので、
                    // Handlerを用いてUIスレッドに行わせる処理を記述する
                    parent = checkCnt * 100 / cnt
                    var red = (maxColor * parent / 100)
                    var blue =maxColor - (maxColor * parent / 100)

                    _handler.post {
                        val secureRandom = SecureRandom().nextInt(100)
                        // この部分はUIスレッドで動作する
                        _viewText.text = secureRandom.toString()
                        _viewText.setBackgroundColor(Color.rgb(red,75,blue))
                    }
                    // ここで時間稼ぎ
                    Thread.sleep(20)
                    mCounter++
                    a++
                    println("$a : $mCounter")
                }
                if (mCounter == 50) {
                    _viewText.text ="$parent%"
                    println("50になったよ")
                }
            } catch (e: Exception) {
            }
        }

    }

    fun runWorker(){

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
        val intent: Intent
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