package com.winwang.annotation


/**
 * Created by WinWang on 2022/8/15
 * Description:声明自定义App加载的注解
 **/
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@kotlin.annotation.Target(AnnotationTarget.CLASS)
annotation class AppLifecycle(
    //默认该注解执行的优先级
    val properties: Int = 1
)

