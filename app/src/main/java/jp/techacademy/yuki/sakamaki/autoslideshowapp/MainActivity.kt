package jp.techacademy.yuki.sakamaki.autoslideshowapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.provider.MediaStore
import android.content.ContentUris

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private var mHandler = Handler()
    var goukei: Long = 0
    var saisyo: Long = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
                goukei = Goukei()-1
                saisyo = Saisyo()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
        } else {
            getContentsInfo()
            goukei = Goukei()-1
            saisyo = Saisyo()
        }

        var i: Long = 0
        moveon_button.setOnClickListener {
            if(pause_button.text == "再生"){
                i = i+1
                moveOnContensInfo(goukei,saisyo,i)
            }
        }


        pause_button.setOnClickListener{
            if(pause_button.text == "再生"){
                pause_button.text = "停止"
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mHandler.post {
                            i = i+1
                            moveOnContensInfo(goukei,saisyo,i)
                        }
                    }
                }, 2000, 2000)

            }else{
                pause_button.text = "再生"
                mTimer!!.cancel()
            }
        }


        return_button.setOnClickListener{
            if(pause_button.text == "再生"){
                i = i-1
                moveOnContensInfo(goukei,saisyo,i)
            }
        }
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }




    private fun Goukei(): Long{
        var hoge: Long = 0
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )
        do {
            hoge = hoge + 1
        } while (cursor!!.moveToNext())
        cursor.close()
        return hoge
    }

    private fun Saisyo(): Long{
        var id: Long = 0
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )
        if (cursor!!.moveToFirst()) {
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            id = cursor.getLong(fieldIndex)
        }
        cursor.close()
        return id
    }

    private fun getContentsInfo(){
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )
        if (cursor!!.moveToFirst()) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
        }
        cursor.close()
    }










    private fun moveOnContensInfo(sum: Long,start: Long,last: Long){
        var id:Long = 0
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )
        if(last >= 0){
            id = start + last%sum
        }else if(last%sum == sum-sum){
            id = start
        }else{
            id = start + sum + last%sum
        }

        val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
        imageView.setImageURI(imageUri)
        /*Log.d("SUM",sum.toString())
        Log.d("START",start.toString())
        Log.d("LAST",last.toString())
        Log.d("URI",imageUri.toString())*/
    }

}