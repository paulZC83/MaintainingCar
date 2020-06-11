package com.example.maintainingcar.view

import android.app.Activity
import android.app.Dialog
import android.content.Context
import com.example.maintainingcar.R
import kotlinx.android.synthetic.main.dialog_in_detail_layout.*

class DetailDialog : Dialog {

    constructor(context: Context, title:String, msg:String) :super(context, R.style.MyUsualDialog){
        setContentView(R.layout.dialog_in_detail_layout)
        setCanceledOnTouchOutside(true)
        tv_title.text = title
        tv_message_in.text = msg
    }



    class Builder(val context: Activity) {
        private var title = ""
        private var message = ""
        fun setTitle(ti:String):Builder{
            title = ti
            return this
        }
        fun setMsg(msg:String):Builder{
            message = msg
            return this
        }
        fun build():DetailDialog{
            return DetailDialog(context, title, message)
        }
    }
}