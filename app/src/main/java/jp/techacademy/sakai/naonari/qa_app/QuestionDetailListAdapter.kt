package jp.techacademy.sakai.naonari.qa_app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class QuestionDetailListAdapter(context: QuestionDetailActivity, private val mQuestion: Question):BaseAdapter() {
    companion object{
        private val TYPE_QUESTION = 0
        private val TYPE_ANSWER = 1
    }
    private var mLayoutInflater: LayoutInflater? = null

    init {
        mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
       return  1 + mQuestion.answers.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0){
            TYPE_QUESTION
        } else{
            TYPE_ANSWER
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Any {
        return mQuestion
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView

        if (getItemViewType(position)== TYPE_QUESTION){
            if (convertView == null){
                convertView = mLayoutInflater!!.inflate(R.layout.list_question_detail,parent,false)!!
            }

            //ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            //ログインしていれば、like_itをvisible
            if(user != null){
                val likeIt =  convertView.findViewById<View>(R.id.like_it) as ImageView
                likeIt.visibility = View.VISIBLE
            }

            val body = mQuestion.body
            val name = mQuestion.name
            val primarykey = mQuestion.primaryKey

            val bodyTextView = convertView.findViewById<View>(R.id.bodyTextView) as TextView
            bodyTextView.text = body

            val nameTextView = convertView.findViewById<View>(R.id.nameTextView) as TextView
            nameTextView.text = name

            val questionId = convertView.findViewById<View>(R.id.questionid) as TextView
            questionId.text = "ID:"+"$primarykey"

            val fivorite = convertView.findViewById<View>(R.id.like_it) as ImageView
            if (mQuestion.favorite == true){
            fivorite.setImageResource(R.drawable.fav_yes)
            }else{fivorite.setImageResource(R.drawable.fav_no)}

            val bytes = mQuestion.imageBytes
            if (bytes.isNotEmpty()){
                val image = BitmapFactory.decodeByteArray(bytes,0,bytes.size).copy(Bitmap.Config.ARGB_8888,true)
                val imageView = convertView.findViewById<View>(R.id.imageView) as ImageView
                imageView.setImageBitmap(image)
            }
        }else{
            if(convertView == null){
                convertView = mLayoutInflater!!.inflate(R.layout.list_answer,parent,false)!!
            }

            val answer = mQuestion.answers[position -1]
            val body = answer.body
            val name = answer.name

            val bodyTextView = convertView.findViewById<View>(R.id.bodyTextView) as TextView
            bodyTextView.text = body

            val nameTextView = convertView.findViewById<View>(R.id.nameTextView) as TextView
            nameTextView.text = name
        }

        return convertView
    }



}