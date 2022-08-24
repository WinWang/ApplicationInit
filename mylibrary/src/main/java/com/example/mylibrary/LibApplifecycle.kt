package com.example.mylibrary

import android.content.Context
import com.winwang.annotation.AppLifecycle
import com.winwang.api.IApplifecycle

/**
 * Created by WinWang on 2022/8/19
 * Description:
 **/
@AppLifecycle(properties = 100)
class LibApplifecycle : IApplifecycle {
    override fun onCreate(context: Context) {
        println("Lib初始化")
    }
}