package com.example.autoexpense

import android.app.Application

/**
 * Application 类
 * 整个 App 启动时第一个初始化的类
 */
class AutoExpenseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 应用启动时的初始化可以放在这里
    }
}
