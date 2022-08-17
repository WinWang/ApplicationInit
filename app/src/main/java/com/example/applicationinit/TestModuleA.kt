package com.example.applicationinit

import android.content.Context
import com.winwang.annotation.AppLifecycle
import com.winwang.api.IApplifecycle

/**
 * Created by WinWang on 2022/8/16
 * Description:
 **/


@AppLifecycle(properties = 2)
class TestModuleA : IApplifecycle {

    override fun onCreate(context: Context) {
        println("执行A项目初始化")
    }
}