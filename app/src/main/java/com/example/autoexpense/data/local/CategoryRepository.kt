package com.example.autoexpense.data.local

import android.content.Context
import com.example.autoexpense.data.model.Category
import kotlinx.coroutines.flow.first

/**
 * 分类数据仓库
 * 封装 SettingsDataStore，提供同步/异步获取分类的方法
 */
class CategoryRepository(context: Context) {

    private val settingsDataStore = SettingsDataStore(context)

    /**
     * 获取分类列表（异步 Flow）
     */
    fun getCategories() = settingsDataStore.categories

    /**
     * 获取分类列表（同步阻塞，用于服务中）
     */
    suspend fun getCategoriesSync(): List<Category> {
        return settingsDataStore.categories.first()
    }

    /**
     * 保存分类列表
     */
    suspend fun saveCategories(categories: List<Category>) {
        settingsDataStore.saveCategories(categories)
    }

    suspend fun rememberSubCategory(category: String, subCategory: String, transactionType: String) {
        settingsDataStore.rememberSubCategory(category, subCategory, transactionType)
    }

    /**
     * 获取默认分类
     */
    fun getDefaultCategories(): List<Category> {
        return SettingsDataStore.DEFAULT_CATEGORIES
    }
}
