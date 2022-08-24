package com.winwang.api

import android.content.Context

/**
 * Created by WinWang on 2022/8/16
 * Description:定义App初始化的Lifecycle基类
 **/
interface IApplifecycle {

    /**
     * 初始化方法
     */
    fun onCreate(context: Context)

    /**
     * 初始化优先级-默认数字越大，优先级越大
     */
    val properties: Int
        get() = 1

}