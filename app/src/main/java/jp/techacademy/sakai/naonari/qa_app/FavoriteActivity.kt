package jp.techacademy.sakai.naonari.qa_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Base64
import android.util.Log
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.list_question_detail.*

class FavoriteActivity : AppCompatActivity() {

    private lateinit var mDatabaseReference: DatabaseReference
    private lateinit var mListView: ListView
    private lateinit var mQuestionArrayList: ArrayList<Question>
    private lateinit var mHobbyQuestionArrayList: ArrayList<Question>
    private lateinit var mLifeQuestionArrayList: ArrayList<Question>
    private lateinit var mHealthQuestionArrayList: ArrayList<Question>
    private lateinit var mComputerQuestionArrayList: ArrayList<Question>
    private lateinit var mAdapter: QuestionsListAdapter
    private var mHobbyRef: DatabaseReference? = null
    private var mLifeRef: DatabaseReference? = null
    private var mHealthRef: DatabaseReference? = null
    private var mComputerRef: DatabaseReference? = null
    private var mFavoriteRef: DatabaseReference? = null
    private var favorite: Boolean = false
    private var questionUidList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)


        // ListViewの準備
        mListView = findViewById(R.id.favlistView)
        mAdapter = QuestionsListAdapter(this)
        mHobbyQuestionArrayList = ArrayList<Question>()
        mAdapter.notifyDataSetChanged()

        mListView.setOnItemClickListener { parent, view, position, id ->
            //Questionのインスタンスを渡して質問詳細画面を起動する
            val intent = Intent(applicationContext, QuestionDetailActivity::class.java)
            intent.putExtra("question", mQuestionArrayList[position])
            startActivity(intent)
        }

        mAdapter.setQuestionArrayList(mHobbyQuestionArrayList)
        mListView.adapter = mAdapter


    }

    override fun onResume() {
        super.onResume()

        //Firebase
        mDatabaseReference = FirebaseDatabase.getInstance().reference

//        mHobbyRef = mDatabaseReference.child(ContentsPATH).child(1.toString())
//        mHobbyRef!!.addChildEventListener(mHobbyEventLisner)

//        mLifeRef = mDatabaseReference.child(ContentsPATH).child(2.toString())
//        mLifeRef!!.addChildEventListener(mLifeEventLisner)
//
//        mHealthRef = mDatabaseReference.child(ContentsPATH).child(3.toString())
//        mHealthRef!!.addChildEventListener(mHealthEventLisner)
//
//        mComputerRef = mDatabaseReference.child(ContentsPATH).child(4.toString())
//        mComputerRef!!.addChildEventListener(mComputerEventLisner)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // UID
            var uid = FirebaseAuth.getInstance().currentUser!!.uid
            mFavoriteRef = mDatabaseReference.child(FavoritePATH).child(uid)
            mFavoriteRef!!.addChildEventListener(mFavotiteEventLisner)
        }
    }
    private val mFavotiteEventLisner = object : ChildEventListener {


        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

            Log.d("QA_app", "動作確認fav")
            val questionUid = dataSnapshot.getValue()
//            questionUidList.add(questionUid)

            Log.d("QA_app", "あああ$questionUid")


        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

        override fun onChildRemoved(p0: DataSnapshot) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onCancelled(p0: DatabaseError) {

        }


    }

    private val mHobbyEventLisner = object : ChildEventListener {

        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {

            Log.d("QA_app", "動作確認")
            val map = dataSnapshot.value as Map<String, String>
            val title = map["title"] ?: ""
            val body = map["body"] ?: ""
            val name = map["name"] ?: ""
            val uid = map["uid"] ?: ""
            val imageString = map["image"] ?: ""
            val bytes =
                if (imageString.isNotEmpty()) {
                    Base64.decode(imageString, Base64.DEFAULT)
                } else {
                    byteArrayOf()
                }

            val answerArrayList = ArrayList<Answer>()
            val answerMap = map["answers"] as Map<String, String>?
            if (answerMap != null) {
                for (key in answerMap.keys) {
                    val temp = answerMap[key] as Map<String, String>
                    val answerBody = temp["body"] ?: ""
                    val answerName = temp["name"] ?: ""
                    val answerUid = temp["uid"] ?: ""
                    val answer = Answer(answerBody, answerName, answerUid, key)
                    answerArrayList.add(answer)
                }
            }

            val question = Question(
                title, body, name, uid, dataSnapshot.key ?: "",
                1, bytes, answerArrayList
            )

            mHobbyQuestionArrayList.add(question)
            mAdapter.notifyDataSetChanged()


        }

        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
            val map = dataSnapshot.value as Map<String, String>

            //変更があったQuestionを探す
            for (question in mHobbyQuestionArrayList) {
                if (dataSnapshot.key.equals(question.questionUid)) {
                    //このアプリで変更がある可能性があるのは回答(Answer)のみ
                    question.answers.clear()
                    val answerMap = map["answers"] as Map<String, String>?
                    if (answerMap != null) {
                        for (key in answerMap.keys) {
                            val temp = answerMap[key] as Map<String, String>
                            val answerBody = temp["body"] ?: ""
                            val answerName = temp["name"] ?: ""
                            val answerUid = temp["uid"] ?: ""
                            val answer = Answer(answerBody, answerName, answerUid, key)
                            question.answers.add(answer)
                        }
                    }
                }
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {

        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {

        }

        override fun onCancelled(p0: DatabaseError) {

        }
    }

//    private val mLifeEventLisner = object : ChildEventListener {
//        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
//            val map = dataSnapshot.value as Map<String, String>
//            val title = map["title"] ?: ""
//            val body = map["body"] ?: ""
//            val name = map["name"] ?: ""
//            val uid = map["uid"] ?: ""
//            val imageString = map["image"] ?: ""
//            val bytes =
//                if (imageString.isNotEmpty()) {
//                    Base64.decode(imageString, Base64.DEFAULT)
//                } else {
//                    byteArrayOf()
//                }
//
//            val answerArrayList = ArrayList<Answer>()
//            val answerMap = map["answers"] as Map<String, String>?
//            if (answerMap != null) {
//                for (key in answerMap.keys) {
//                    val temp = answerMap[key] as Map<String, String>
//                    val answerBody = temp["body"] ?: ""
//                    val answerName = temp["name"] ?: ""
//                    val answerUid = temp["uid"] ?: ""
//                    val answer = Answer(answerBody, answerName, answerUid, key)
//                    answerArrayList.add(answer)
//                }
//            }
//
//            val question = Question(
//                title, body, name, uid, dataSnapshot.key ?: "",
//                2, bytes, answerArrayList
//            )
//            mLifeQuestionArrayList.add(question)
//        }
//
//        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
//            val map = dataSnapshot.value as Map<String, String>
//
//            //変更があったQuestionを探す
//            for (question in mLifeQuestionArrayList) {
//                if (dataSnapshot.key.equals(question.questionUid)) {
//                    //このアプリで変更がある可能性があるのは回答(Answer)のみ
//                    question.answers.clear()
//                    val answerMap = map["answers"] as Map<String, String>?
//                    if (answerMap != null) {
//                        for (key in answerMap.keys) {
//                            val temp = answerMap[key] as Map<String, String>
//                            val answerBody = temp["body"] ?: ""
//                            val answerName = temp["name"] ?: ""
//                            val answerUid = temp["uid"] ?: ""
//                            val answer = Answer(answerBody, answerName, answerUid, key)
//                            question.answers.add(answer)
//                        }
//                    }
//                }
//            }
//        }
//
//        override fun onChildRemoved(p0: DataSnapshot) {
//
//        }
//
//        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//        }
//
//        override fun onCancelled(p0: DatabaseError) {
//
//        }
//    }

    //    private val mHealthEventLisner = object : ChildEventListener {
//        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
//            val map = dataSnapshot.value as Map<String, String>
//            val title = map["title"] ?: ""
//            val body = map["body"] ?: ""
//            val name = map["name"] ?: ""
//            val uid = map["uid"] ?: ""
//            val imageString = map["image"] ?: ""
//            val bytes =
//                if (imageString.isNotEmpty()) {
//                    Base64.decode(imageString, Base64.DEFAULT)
//                } else {
//                    byteArrayOf()
//                }
//
//            val answerArrayList = ArrayList<Answer>()
//            val answerMap = map["answers"] as Map<String, String>?
//            if (answerMap != null) {
//                for (key in answerMap.keys) {
//                    val temp = answerMap[key] as Map<String, String>
//                    val answerBody = temp["body"] ?: ""
//                    val answerName = temp["name"] ?: ""
//                    val answerUid = temp["uid"] ?: ""
//                    val answer = Answer(answerBody, answerName, answerUid, key)
//                    answerArrayList.add(answer)
//                }
//            }
//
//            val question = Question(
//                title, body, name, uid, dataSnapshot.key ?: "",
//                3, bytes, answerArrayList
//            )
//            mHealthQuestionArrayList.add(question)
//        }
//
//        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
//            val map = dataSnapshot.value as Map<String, String>
//
//            //変更があったQuestionを探す
//            for (question in mHealthQuestionArrayList) {
//                if (dataSnapshot.key.equals(question.questionUid)) {
//                    //このアプリで変更がある可能性があるのは回答(Answer)のみ
//                    question.answers.clear()
//                    val answerMap = map["answers"] as Map<String, String>?
//                    if (answerMap != null) {
//                        for (key in answerMap.keys) {
//                            val temp = answerMap[key] as Map<String, String>
//                            val answerBody = temp["body"] ?: ""
//                            val answerName = temp["name"] ?: ""
//                            val answerUid = temp["uid"] ?: ""
//                            val answer = Answer(answerBody, answerName, answerUid, key)
//                            question.answers.add(answer)
//                        }
//                    }
//                }
//            }
//        }
//
//        override fun onChildRemoved(p0: DataSnapshot) {
//
//        }
//
//        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//        }
//
//        override fun onCancelled(p0: DatabaseError) {
//
//        }
//    }
//
//    private val mComputerEventLisner = object : ChildEventListener {
//        override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
//            val map = dataSnapshot.value as Map<String, String>
//            val title = map["title"] ?: ""
//            val body = map["body"] ?: ""
//            val name = map["name"] ?: ""
//            val uid = map["uid"] ?: ""
//            val imageString = map["image"] ?: ""
//            val bytes =
//                if (imageString.isNotEmpty()) {
//                    Base64.decode(imageString, Base64.DEFAULT)
//                } else {
//                    byteArrayOf()
//                }
//
//            val answerArrayList = ArrayList<Answer>()
//            val answerMap = map["answers"] as Map<String, String>?
//            if (answerMap != null) {
//                for (key in answerMap.keys) {
//                    val temp = answerMap[key] as Map<String, String>
//                    val answerBody = temp["body"] ?: ""
//                    val answerName = temp["name"] ?: ""
//                    val answerUid = temp["uid"] ?: ""
//                    val answer = Answer(answerBody, answerName, answerUid, key)
//                    answerArrayList.add(answer)
//                }
//            }
//
//            val question = Question(
//                title, body, name, uid, dataSnapshot.key ?: "",
//                4, bytes, answerArrayList
//            )
//            mComputerQuestionArrayList.add(question)
//        }
//
//        override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
//            val map = dataSnapshot.value as Map<String, String>
//
//            //変更があったQuestionを探す
//            for (question in mComputerQuestionArrayList) {
//                if (dataSnapshot.key.equals(question.questionUid)) {
//                    //このアプリで変更がある可能性があるのは回答(Answer)のみ
//                    question.answers.clear()
//                    val answerMap = map["answers"] as Map<String, String>?
//                    if (answerMap != null) {
//                        for (key in answerMap.keys) {
//                            val temp = answerMap[key] as Map<String, String>
//                            val answerBody = temp["body"] ?: ""
//                            val answerName = temp["name"] ?: ""
//                            val answerUid = temp["uid"] ?: ""
//                            val answer = Answer(answerBody, answerName, answerUid, key)
//                            question.answers.add(answer)
//                        }
//                    }
//                }
//            }
//        }
//
//        override fun onChildRemoved(p0: DataSnapshot) {
//
//        }
//
//        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
//
//        }
//
//        override fun onCancelled(p0: DatabaseError) {
//
//        }
//    }
//

}