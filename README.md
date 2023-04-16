# ApplicationInit

组件化项目初始化工具--通过注解，方便实现组建隔离情况下各个模块SDK初始化调用时序的问题，通过APT生成模块调用初始化代码，通过注解参数propertie控制注解初始化模块初始化优先级

#使用方式：($applifecycleVersion="v1.0.3")

1、添加依赖：

1-1：在项目根目录build添加gradle-plugin插件依赖（）

```
classpath "com.github.WinWang.ApplicationInit:applifecycle-plugin:$appLifecycleVersion"
```

1-2：在需要使用的module添加以下依赖

```
implementation "com.github.WinWang.ApplicationInit:applifecycle-api:$appLifecycleVersion"
```

```
kapt "com.github.WinWang.ApplicationInit:applifecycle-processor:$appLifecycleVersion"
```

2、在需要跨模块启动的Module使用Gradle Plugin：

```
apply plugin: 'com.winwang.plugin.lifecycle'
```

3、代码使用：

1）在Application onCrate()调用初始化 （当然也可不必在Application的onCreate调用，具体看业务初始化时机）

```
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //初始化App启动框架
        AppLifecycleManager.init(this)
    }

}
```

2、定义各个模块启动Module

```
@AppLifecycle(properties = 2)
class TestLib2 : IApplifecycle {
    override fun onCreate(context: Context) {
        Log.d("Applifecycle", "加载TestLib2")
    }
}
```

其中的注解重的properties变量表示该模块的启动调用优先级，数字越大调用优先级越高

具体应用demo可参照customView项目中集成测试使用

[WinWang/customView: 前段时间闲来无事写的一些自定义控件和测试的一些实例 (github.com)](https://github.com/WinWang/customView)


## 其他一些学习练手的项目

**Flutter版本**的音乐播放App链接(getx+retrofit+dio)：https://github.com/WinWang/music_listener <br>

**React版本**的开眼App链接(React18+React-Vant+Mobx+axios)：https://github.com/WinWang/react-oepn-eye <br>

**Vue2版本**WanAndroid链接(Vue2+vuex+vant+axios)：https://github.com/WinWang/Vue-WanAndroid <br>

**Vue3版本**WanAndroid链接(vue3+typeScript+pinia+vant+vite)：https://github.com/WinWang/Vue3-wanAndroid

**Android组件化项目**ReadingGallery链接(jetpack+kotlin+koin+couroutine)：https://github.com/WinWang/ReadingGallery <br>
