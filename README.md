# ApplicationInit

组件化项目初始化工具--通过注解，方便实现组建隔离情况下各个模块SDK初始化调用时序的问题，通过APT生成模块调用初始化代码，通过注解参数propertie控制注解初始化模块初始化优先级

#使用方式：

1、添加依赖：

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
