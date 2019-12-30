package com.example.myscheduler

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_schedule_edit.*
import java.lang.IllegalArgumentException
import android.text.format.DateFormat
import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class ScheduleEditActivity : AppCompatActivity() {
    //レルムインスタンスの宣言
    private lateinit var realm: Realm

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_edit)
        //レルムインスタンスの初期化
        realm = Realm.getDefaultInstance()

        val scheduleId = intent?.getLongExtra("schedule_id", -1L) //schedule_idが取得できなかった場合-1を返す
        if (scheduleId != -1L) {        //更新処理
            val schedule = realm.where<Schedule>()
                .equalTo("id", scheduleId).findFirst() //引数のidと等しい一行のScheduleを返す
            dateEdit.setText(DateFormat.format("yyyy/MM/dd", schedule?.date))
            titleEdit.setText(schedule?.title)
            detailEdit.setText(schedule?.title)
            delete.visibility = View.VISIBLE //削除ボタンの表示
        } else {
            delete.visibility = View.INVISIBLE //更新時以外は削除ボタンは非表示
        }

        //保存ボタン押下時の操作⇒データベースへスケジュール登録
        save.setOnClickListener { view: View ->
            when (scheduleId) {
                -1L -> {//新規作成時の保存ボタン押下時の処理
                    //データベースへに対する処理はexecuteTransactionを使用
                    realm.executeTransaction { db: Realm ->
                        val maxId = db.where<Schedule>().max("id")  //Scheduleのidフィールドの最大値を取得
                        val nextId = (maxId?.toLong() ?: 0L) + 1                //idの最大値＋1を新規作成
                        val schedule = db.createObject<Schedule>(nextId) //データを一行追加⇒scheduleオブジェクト返す
                        var sb = StringBuilder()
                        sb.append(dateEdit.text.toString())
                        sb.insert(4,"/")
                        sb.insert(7,"/")
                        val date = sb.toString().toDate("yyyy/MM/dd") //ここからscheduleオブジェクトに値を格納.
                        if (date != null) schedule.date = date
                        schedule.title = titleEdit.text.toString()
                        schedule.detail = detailEdit.text.toString()
                    }
                    Snackbar.make(view, "追加しました", Snackbar.LENGTH_SHORT)
                        .setAction("戻る") { finish() }
                        .setActionTextColor(Color.YELLOW)
                        .show()
                }
                else -> {//更新時の保存ボタン押下時の処理
                    realm.executeTransaction { db: Realm ->
                        val schedule = db.where<Schedule>()
                            .equalTo("id", scheduleId).findFirst()  //引数のidと等しい一行のScheduleを返す
                        val date = dateEdit.text.toString().toDate("yyyy/MM/dd")  //日付の更新
                        if (date != null) schedule?.date = date
                        schedule?.title = titleEdit.text.toString()  //タイトルの更新
                        schedule?.detail = detailEdit.text.toString() //詳細の更新

                    }
                    Snackbar.make(view, "修正しました", Snackbar.LENGTH_SHORT)
                        .setAction("戻る") { finish() }
                        .setActionTextColor(Color.YELLOW)
                        .show()
                }
            }
        }

//        削除ボタン押下時の処理
        delete.setOnClickListener{view: View ->
            realm.executeTransaction{ db: Realm ->
                db.where<Schedule>().equalTo("id", scheduleId)
                    ?.findFirst() //削除するセル(1行分のデータ)の取得
                    ?.deleteFromRealm() //削除
            }
            Snackbar.make(view, "削除しました",Snackbar.LENGTH_SHORT)
                .setAction("戻る"){finish()}
                .setActionTextColor(Color.YELLOW)
                .show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun String.toDate(pattern: String): Date?{
        return try {
            SimpleDateFormat(pattern).parse(this)
        } catch (e: IllegalArgumentException){
            return null
        } catch (e: ParseException){
            return null
        }
    }
}
