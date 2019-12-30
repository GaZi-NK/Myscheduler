package com.example.myscheduler
//モデルクラス⇒データを格納するクラス
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class Schedule : RealmObject() {
    @PrimaryKey
    var id: Long = 0
    var date: Date = Date()
    var title: String = ""
    var detail: String = ""
}