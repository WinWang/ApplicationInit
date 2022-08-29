package com.winwang.processor

import com.google.auto.service.AutoService
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import com.winwang.annotation.AppLifecycle
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Modifier
import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Created by WinWang on 2022/8/16
 * Description:注解处理器逻辑Processor
 **/
@AutoService(Processor::class)
class ApplicationProcessor : AbstractProcessor() {

    /**
     * 注解处理器初始化的方法
     */
    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        //注解处理器提供输出方法
        processingEnv?.messager?.printMessage(Diagnostic.Kind.NOTE, "初始化AppLifecycle>>>>>")
    }

    /**
     * 需要扫描的注解，可以将需要的注解都添加到hashSet里面
     */
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val hashSet: HashSet<String> = HashSet()
        hashSet.add(AppLifecycle::class.java.canonicalName)
        return hashSet
    }

    /**
     * 编译版本设置，通用写法
     */
    override fun getSupportedSourceVersion(): SourceVersion {
        return processingEnv.sourceVersion
    }

    /**
     * 扫描注解的回调
     */
    override fun process(annotations: MutableSet<out TypeElement>?, roundEnvironment: RoundEnvironment?): Boolean {
        //获取AppLifecycle注解的类
        val elements = roundEnvironment?.getElementsAnnotatedWith(AppLifecycle::class.java)
        elements?.forEachIndexed { _, element ->
            val simpleName = element.simpleName
            //打印获取的注解的类名
            processingEnv.messager?.printMessage(Diagnostic.Kind.NOTE, "获取注解类>>>>$simpleName")
            //检查扫描到加载了注解的类是否实现了Applifecycle注解
            val typeElement = element as TypeElement
            val interfaces = typeElement.interfaces
            if (interfaces.isNullOrEmpty()) {
                throw RuntimeException("${typeElement.qualifiedName} must implements interface com.winwang.api.IApplifecycle")
            }
            //检测添加注解累是否实现IApplifecycle接口
            var checkInterfaceFlag = false
            for (mirror in interfaces) {
                if ("com.winwang.api.IApplifecycle" == mirror.toString()) {
                    checkInterfaceFlag = true
                } else {
                    checkInterfaceFlag = false
                    break
                }
            }
            if (!checkInterfaceFlag) {
                throw RuntimeException("${typeElement.qualifiedName} must implements interface com.winwang.api.IApplifecycle")
            }
            val javaCode = generateJavaCode(element)
            try {
                val jfo = processingEnv.filer.createSourceFile("AppLifecycle$$${element.simpleName}$\$Proxy")
                val writer = jfo.openWriter()
                writer.write(javaCode)
                writer.flush()
                writer.close()
            } catch (e: Exception) {

            }

        }



        return false
    }

    /**
     * 通过JavaPoet生成的类对Android包的依赖太大，故改用直接拼接生成代码逻辑
     */
    private fun generateJavaByJavaPoet(simpleName: Name?) {
        //需要生成的类名--也可以生成注解、枚举等
        val classBuilder = TypeSpec.classBuilder("AppLifecycle$$$simpleName")
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

        val method = MethodSpec.methodBuilder("onCreate")
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.VOID)
            .addAnnotation(Override::class.java)
            .addStatement("\$T.out.println(\$S)", System::class.java, "Hello,JavaPoet!")
            .build()
        classBuilder.addMethod(method)

        val javaFile = JavaFile.builder("com.winwang.applifecycle", classBuilder.build()).build()
        runCatching {
            javaFile.writeTo(processingEnv.filer)
        }
    }

    /**
     * 改用代码生成，因为考虑到javaPoet生成涉及到Context，processor包是一个javaLibrary，
     * 无法引用到包含IApplifecycle带有Context的Android Library，故舍弃JavaPoet生成class文件的方式
     */
    private fun generateJavaCode(element: TypeElement): String {
        //获取注解配置properties参数
        val annotation = element.getAnnotation(AppLifecycle::class.java)
        val properties = annotation.properties

        val sb = StringBuilder()
        //设置包名
        sb.append("package ").append("com.winwang.applifecycle.apt.proxy").append(";\n\n")

        //设置import部分
        sb.append("import android.content.Context;\n")
        sb.append("import com.winwang.api.IApplifecycle;\n")
        sb.append("import ").append(element.qualifiedName).append(";\n\n")

        sb.append("public class ").append("AppLifecycle$$${element.simpleName}$\$Proxy")
            .append(" implements ").append("IApplifecycle").append(" {\n\n")

        //设置变量
        sb.append("  private ").append(element.simpleName.toString()).append(" mAppLifecycle;\n\n")

        //构造函数
        sb.append("  public ").append("AppLifecycle$$${element.simpleName}$\$Proxy").append("() {\n")
        sb.append("     mAppLifecycle = new ").append(element.simpleName.toString()).append("();\n")
        sb.append("  }\n\n")

        //onCreate()方法
        sb.append("  @Override\n")
        sb.append("  public void onCreate(Context context) {\n")
        sb.append("    mAppLifecycle.onCreate(context);\n")
        sb.append("  }\n\n")

        //重写properties变量
        sb.append("  @Override\n")
        sb.append("  public int getProperties() {\n")
        sb.append("     return $properties;\n")
        sb.append("  }\n\n")

        sb.append("\n}")
        return sb.toString()
    }


}