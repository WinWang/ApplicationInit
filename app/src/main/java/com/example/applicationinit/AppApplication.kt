package com.example.applicationinit

import android.app.Application
import com.winwang.api.AppLifecycleManager

/**
 * Created by WinWang on 2022/8/16
 * Description:
 **/

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppLifecycleManager.init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        println(">>>>>>>>>>>>>>退出应用")
    }


}