package com.winwang.lifecycle.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by WinWang on 2022/8/18
 * Description:
 **/
class LifecyclePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("=====================================================")
        println("=============Applifecycle plugin running=============")
        println("=====================================================")
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new LifecycleTransform(project))

    }

}