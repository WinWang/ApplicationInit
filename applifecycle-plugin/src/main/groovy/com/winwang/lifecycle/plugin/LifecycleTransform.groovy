package com.winwang.lifecycle.plugin


import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

/**
 * Created by WinWang on 2022/8/18
 * Description:
 **/
class LifecycleTransform extends Transform {

    Project project

    LifecycleTransform(Project project) {
        this.project = project
    }

    /**
     * Transform的名称
     * @return
     */
    @Override
    String getName() {
        return "AppLifecycleTransform"
    }

    /**
     * Transform作用类型
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * Transform作用域
     * @return
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    /**
     * 是否增量执行
     * @return
     */
    @Override
    boolean isIncremental() {
        return false
    }

    /**
     * 转换器执行操作
     * @param transformInvocation
     * @throws TransformException* @throws InterruptedException* @throws IOException
     */
    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        def appLifecycleProxyClassList = []
        def outputProvider = transformInvocation.outputProvider
        transformInvocation.inputs.each { input ->
            input.directoryInputs.each { directoryInput ->
                if (directoryInput.file.isDirectory()) {
                    directoryInput.file.eachFileRecurse { File file ->
                        //形如 AppLifecycle$$****$$Proxy.class 的类，是我们要找的目标class
                        if (ScanUtil.isTargetProxyClass(file)) {
                            println("AppLifecycle-ASM匹配到的名字>>>>$file")
                            appLifecycleProxyClassList.add(file.name)
                        }
                    }
                }

                def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            input.jarInputs.each { jarInput ->
                def jarName = jarInput.name
                def md5 = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                def dest = outputProvider.getContentLocation(jarName + md5, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
                    //处理jar包里的代码
                    File src = jarInput.file
                    if (ScanUtil.shouldProcessPreDexJar(src.absolutePath)) {
                        List<String> list = ScanUtil.scanJar(src, dest)
                        if (list != null) {
                            appLifecycleProxyClassList.addAll(list)
                        }
                    }
                }
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
        appLifecycleProxyClassList.forEach({ fileName ->
            println "Applifecycle列表>>file name = " + fileName
        })
        println "\n包含AppLifeCycleManager类的jar文件"
        println ScanUtil.FILE_CONTAINS_INIT_CLASS.getAbsolutePath()
        println "==========开始自动注册=========="
        new AppLifecycleCodeInjector(appLifecycleProxyClassList).execute()
        println "AppLifecycle-transform finish----------------<<<<<<<\n"
    }
}