package com.winwang.api

import android.content.Context

/**
 * Created by WinWang on 2022/8/16
 * Description:定义App初始化的Lifecycle基类
 **/
interface IApplifecycle {

    fun onCreate(context: Context)

}