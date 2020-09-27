package jp.techacademy.sakai.naonari.qa_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_question_detail.*
import kotlinx.android.synthetic.main.list_question_detail.*

class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var mQuestion: Question
    private lateinit var mAdapter: QuestionDetailListAdapter
    private lateinit var mAnswerRef: DatabaseReference
    private lateinit var mfavoriteRef: DatabaseReference
    private var favorite = false

    private val mEventListener = object : ChildEventListener {
        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            val answerUid = dataSnapshot.key ?: ""

            for (answer in mQuestion.answers) {
                //同じAnswerUidのものが存在しているときは何もしない
                if (answerUid == answer.answerUid) {
                    return
                }
            }

            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""

            val answer = Answer(body, name, uid, answerUid)
            mQuestion.answers.add(answer)
            mAdapter.notifyDataSetChanged()
        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onChildRemoved(dataSnapshot: DataSnapshot) {

        }

        override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

        }

        override fun onCancelled(error: DatabaseError) {

        }
    }

    private val mFavotiteEventLisner = object : ValueEventListener {

        override fun onDataChange(dataSnapshot: DataSnapshot) {
            favorite = (dataSnapshot.getValue() ?: false) as Boolean
            Log.d("QA_App", "$favorite")
            if (favorite == true) {
                like_it.setImageResource(R.drawable.fav_yes)
            } else {
                like_it.setImageResource(R.drawable.fav_no)
            }
        }

        override fun onCancelled(error: DatabaseError) {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question_detail)

        //渡ってきたQuestionのオブジェクトを保持する
        val extras = intent.extras
        mQuestion = extras!!.get("question") as Question

        title = mQuestion.title



        fab.setOnClickListener {
            //ログイン済みのユーザーを取得する
            val user = FirebaseAuth.getInstance().currentUser

            if (user == null) {
                //ログインしていなければログイン画面に遷移させる
                val intent = Intent(applicationContext, LoginActivity::class.java)
                startActivity(intent)
            } else {
                //Questionを渡して回答作成画面を起動する
                val intent = Intent(applicationContext, AnswerSendActivity::class.java)
                intent.putExtra("question", mQuestion)
                startActivity(intent)
            }
        }

        val dataBaseReference = FirebaseDatabase.getInstance().reference
        mAnswerRef = dataBaseReference.child(ContentsPATH).child(mQuestion.genre.toString())
            .child(mQuestion.questionUid).child(
                AnswersPATH
            )
        mAnswerRef.addChildEventListener(mEventListener)


    }

    override fun onResume() {
        super.onResume()

        val dataBaseReference = FirebaseDatabase.getInstance().reference

        //ListViewの準備
        mAdapter = QuestionDetailListAdapter(this, mQuestion)
        listView.adapter = mAdapter
        mAdapter.notifyDataSetChanged()

        //user
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {

            // UID
            var uid = FirebaseAuth.getInstance().currentUser!!.uid

             Log.d("QA_App", "$uid")
            if (uid != null) {
                mfavoriteRef =
                    dataBaseReference.child(FavoritePATH).child(uid).child(mQuestion.questionUid)
                mfavoriteRef!!.addValueEventListener(mFavotiteEventLisner)

            }

            listView.setOnItemClickListener { parent, view, position, id ->

                if (uid != null) {
                    favorite = !favorite
                    mfavoriteRef.setValue(favorite)
                }

            }
        }


    }
}