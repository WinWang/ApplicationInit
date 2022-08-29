package com.winwang.api

import android.content.Context
import android.util.Log
import com.winwang.api.utils.ClassUtils
import java.util.*

/**
 * Created by WinWang on 2022/8/17
 * Description:外部Application对应生命周期调用调用
 **/
object AppLifecycleManager {

    var DEBUG = true

    /**
     * 存放Applifecycle注解的列表
     */
    private val appLifecycleList = arrayListOf<IApplifecycle>()

    /**
     * 是否初始化标志，防止多次初始化调用异常
     */
    private var hasInit = false


    private var gradlePluginFlag = false

    /**
     * 通过插件加载 IAppLifecycle 类-ASM扫描找到加载的标志
     */
    @JvmStatic
    fun loadAppLifecycle() {

    }


    /**
     * AppProcessor添加扫描到的调用初始化类
     */
    @JvmStatic
    fun registerAppLifecycle(applifecycle: IApplifecycle) {
        gradlePluginFlag = true
        appLifecycleList.add(applifecycle)
    }

    @JvmStatic
    fun registerAppLifecycle(className: String?) {
        runCatching {
            className?.run {
                val newInstance = Class.forName(this).getConstructor().newInstance()
                if (newInstance is IApplifecycle) {
                    registerAppLifecycle(newInstance)
                }
            }
        }
    }


    fun init(context: Context) {
        if (hasInit) {
            return
        }
        hasInit = true
        loadAppLifecycle()
        if (!gradlePluginFlag) {
            Log.d("AppLifecycle", "需要扫描所有类...")
            scanClassFile(context)
        } else {
            Log.d("AppLifecycle", "插件里已自动注册...")
        }
        //针对properties依赖判断是否需要
        Collections.sort(appLifecycleList, AppLifecycleComparator())
        appLifecycleList.forEach {
            it.onCreate(context)
        }
    }

    /**
     * 扫描出固定包名下，实现了IAppLike接口的代理类
     *
     * @param context
     */
    private fun scanClassFile(context: Context) {
        try {
            val set: Set<String> = ClassUtils.getFileNameByPackageName(context, "com.winwang.applifecycle.apt.proxy")
            if (set != null) {
                for (className in set) {
                    if (DEBUG) {
                        Log.d("WinWangAppLifeCycle", className)
                    }
                    try {
                        val obj = Class.forName(className).newInstance()
                        if (obj is IApplifecycle) {
                            appLifecycleList.add(obj as IApplifecycle)
                        }
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

/**
 * 优先级字段properties比较器
 */
class AppLifecycleComparator : Comparator<IApplifecycle> {
    override fun compare(p0: IApplifecycle?, p1: IApplifecycle?): Int {
        val order1 = p0?.properties ?: 1
        val order2 = p1?.properties ?: 1
        return order2 - order1
    }
}