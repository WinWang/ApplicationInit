package com.example.applicationinit.ui.theme

import android.content.Context
import com.winwang.annotation.AppLifecycle
import com.winwang.api.IApplifecycle

/**
 * Created by WinWang on 2022/8/17
 * Description:
 **/
@AppLifecycle(properties = 10)
class TestModuleA : IApplifecycle {
    override fun onCreate(context: Context) {

    }
}