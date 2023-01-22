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
import androidx.core.view.size
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import com.example.a1dayproject.db.RoomAppDb
import com.example.a1dayproject.db.UserDao
import com.example.a1dayproject.db.UserEntity
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.SecureRandom
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity(),MainRecyclerViewAdapter.RowClickListener {
    private lateinit var mainRVA:MainRecyclerViewAdapter
    lateinit var viewModel:MainActivityViewModel

    internal var mHandler = Handler()
    internal var mCounter: Int = 0


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
            println("aaaa"+size)
        }
        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.getAllUsersObservers().observe(this) {
            mainRVA.setListData(ArrayList(it))
            mainRVA.notifyDataSetChanged()
        }


    }
    fun parentsCheck() {
        var checkCnt = 0
        val cnt = mainRVA.items.size
        val parentsText = findViewById<TextView>(R.id.background)
        for (i in 0 until cnt) {
            if (mainRVA.items[i].check == true){
                checkCnt += 1
            }
        }

        val thread = Thread(Runnable {
            try {
                mCounter = 0
                while (mCounter < 70) {
                    // Threadによる処理の中ではUIを操作することができないので、
                    // Handlerを用いてUIスレッドに行わせる処理を記述する
                    mHandler.post {
                        val secureRandom = SecureRandom().nextInt(100)
                        // この部分はUIスレッドで動作する
                        parentsText.text = secureRandom.toString()
                    }
                    // ここで時間稼ぎ
                    Thread.sleep(20)
                    mCounter++
                }
                if(mCounter == 70) {
                    var parent = checkCnt * 100 / cnt
                    parentsText.text = "$parent%"
                }

            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        })
        thread.start()


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