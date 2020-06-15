package com.example.maintainingcar.view

import android.app.Activity
import android.os.Handler
import android.os.Message

class DialogHandler(private val activity: Activity): Handler() {
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        val title = msg.data.getString("TITLE")?:"不足为道"
        val msg = msg.data.getString("MSG")?:"都没带人，咋整@@"
        DetailDialog.Builder(activity).setTitle(title).setMsg(msg).build().show()
    }
}