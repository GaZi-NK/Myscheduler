package com.example.myscheduler
//アダプタークラス。RecycleViewに1行分のコンテンツを表示させる⇒セルの生成、表示
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter
import android.text.format.DateFormat

class ScheduleAdapter(data: OrderedRealmCollection<Schedule>) :
    RealmRecyclerViewAdapter<Schedule, ScheduleAdapter.ViewHolder>(data, true){

    private var listener: ((Long?) -> Unit)? = null //関数型変数⇒idを受け取ってUnitで返す

    //listener変数に関数を格納
    fun setOnItemClickListener(listener: (Long?) -> Unit){
        this.listener = listener
    }

    init{
        setHasStableIds(true)
    }
/*   ↓インナークラス　RealmRecyclerViewAdapterのジェネリクスの第二引数で使用
                       セルに表示するビューを保持するためのクラス　　　　　　　 */
    class ViewHolder(cell: View) : RecyclerView.ViewHolder(cell){
        val date: TextView = cell.findViewById(android.R.id.text1)
        val title: TextView = cell.findViewById(android.R.id.text2)
    }
//   ↓セルが必要になるたびに呼び出される
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }
//   ↓データを取り出して表示
    override fun onBindViewHolder(holder: ScheduleAdapter.ViewHolder, position: Int) {
        val schedule: Schedule? = getItem(position)
        holder.date.text = DateFormat.format("yyyy/MM/dd", schedule?.date)
        holder.title.text = schedule?.title
        //セルに使用しているビューがタップされた時のイベント
        holder.itemView.setOnClickListener{
            listener?.invoke(schedule?.id)  //invoke⇒関数型を実行する特殊なメソッド
        }
    }

    override fun getItemId(position: Int): Long {
        return getItem(position)?.id ?: 0
    }
}