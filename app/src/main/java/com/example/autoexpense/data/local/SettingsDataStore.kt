package com.example.autoexpense.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.autoexpense.data.model.Category
import com.example.autoexpense.data.model.SubCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// 创建 DataStore 实例，名称叫 settings
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * 设置数据存储类
 * 用 DataStore 保存用户的配置：Webhook URL、分类等
 */
class SettingsDataStore(private val context: Context) {

    companion object {
        val WEBHOOK_URL_KEY = stringPreferencesKey("webhook_url")
        val SEND_MODE_KEY = stringPreferencesKey("send_mode")
        val FEISHU_APP_ID_KEY = stringPreferencesKey("feishu_app_id")
        val FEISHU_APP_SECRET_KEY = stringPreferencesKey("feishu_app_secret")
        val BITABLE_APP_TOKEN_KEY = stringPreferencesKey("bitable_app_token")
        val BITABLE_TABLE_ID_KEY = stringPreferencesKey("bitable_table_id")
        val BITABLE_NOTIFICATION_WEBHOOK_KEY = stringPreferencesKey("bitable_notification_webhook")
        val CATEGORIES_KEY = stringPreferencesKey("categories")
        private val CATEGORY_VERSION_KEY = intPreferencesKey("category_version")
        val SETUP_COMPLETED_KEY = booleanPreferencesKey("setup_completed")
        val APP_SKIN_KEY = stringPreferencesKey("app_skin")

        // 支出分类配置（15个）
        val DEFAULT_EXPENSE_CATEGORIES = listOf(
            Category("餐饮", listOf("早餐", "午餐", "晚餐", "外卖", "饮料", "零食").map { SubCategory(it) }),
            Category("休闲娱乐", listOf("电影", "游戏", "旅游", "会员", "KTV", "彩票").map { SubCategory(it) }),
            Category("购物", listOf("衣服", "数码", "美妆", "家居", "日用", "超市购物").map { SubCategory(it) }),
            Category("交通", listOf("共享单车", "公交", "打车", "地铁", "火车", "高铁", "飞机").map { SubCategory(it) }),
            Category("人情社交", listOf("职场", "亲戚", "朋友", "红包", "礼物", "聚餐").map { SubCategory(it) }),
            Category("宠物", listOf("宠物粮", "零食", "玩具", "医疗", "保险").map { SubCategory(it) }),
            Category("养娃", listOf("奶粉", "尿布", "玩具", "教育", "医疗").map { SubCategory(it) }),
            Category("运动健身", listOf("会员", "场地费", "器械", "服饰配件").map { SubCategory(it) }),
            Category("住房", listOf("房贷", "水电燃气", "物业", "家具", "保险", "维修", "房租").map { SubCategory(it) }),
            Category("爱车", listOf("加油", "保养", "维修", "保险", "停车").map { SubCategory(it) }),
            Category("医疗健康", listOf("挂号", "药品", "住院", "体检", "保险").map { SubCategory(it) }),
            Category("学习教育", listOf("书籍", "课程", "培训", "考试").map { SubCategory(it) }),
            Category("其他", listOf("其他支出").map { SubCategory(it) })
        )

        // 收入分类配置（8个）
        val DEFAULT_INCOME_CATEGORIES = listOf(
            Category("工资", listOf("月薪", "奖金", "补贴").map { SubCategory(it) }),
            Category("兼职", listOf("临时工", "自由职业").map { SubCategory(it) }),
            Category("投资理财", listOf("基金", "股票", "利息", "分红").map { SubCategory(it) }),
            Category("人情社交", listOf("红包", "转账", "礼物").map { SubCategory(it) }),
            Category("报销", listOf("差旅", "办公").map { SubCategory(it) }),
            Category("卖二手", listOf("闲鱼", "转转", "其他").map { SubCategory(it) }),
            Category("生活费", listOf("父母", "配偶").map { SubCategory(it) }),
            Category("其他", listOf("退款", "中奖", "其他收入").map { SubCategory(it) })
        )

        // 默认分类配置（兼容旧版，包含所有分类）
        val DEFAULT_CATEGORIES = DEFAULT_EXPENSE_CATEGORIES + DEFAULT_INCOME_CATEGORIES
    }

    /**
     * 保存飞书 Webhook URL
     */
    suspend fun saveWebhookUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[WEBHOOK_URL_KEY] = url
        }
    }

    /**
     * 读取飞书 Webhook URL
     */
    val webhookUrl: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[WEBHOOK_URL_KEY] ?: ""
        }

    suspend fun saveSendMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[SEND_MODE_KEY] = mode
        }
    }

    val sendMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SEND_MODE_KEY] ?: "feishu_bot"
        }

    val appSkin: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[APP_SKIN_KEY] ?: "caramel" }

    suspend fun saveAppSkin(value: String) {
        context.dataStore.edit { preferences -> preferences[APP_SKIN_KEY] = value }
    }

    suspend fun saveFeishuBitableConfig(
        appId: String,
        appSecret: String,
        appToken: String,
        tableId: String,
        notificationWebhook: String
    ) {
        context.dataStore.edit { preferences ->
            preferences[FEISHU_APP_ID_KEY] = appId
            preferences[FEISHU_APP_SECRET_KEY] = appSecret
            preferences[BITABLE_APP_TOKEN_KEY] = appToken
            preferences[BITABLE_TABLE_ID_KEY] = tableId
            preferences[BITABLE_NOTIFICATION_WEBHOOK_KEY] = notificationWebhook
        }
    }

    val feishuAppId: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[FEISHU_APP_ID_KEY] ?: "" }

    val feishuAppSecret: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[FEISHU_APP_SECRET_KEY] ?: "" }

    val bitableAppToken: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[BITABLE_APP_TOKEN_KEY] ?: "" }

    val bitableTableId: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[BITABLE_TABLE_ID_KEY] ?: "" }

    val bitableNotificationWebhook: Flow<String> = context.dataStore.data
        .map { preferences -> preferences[BITABLE_NOTIFICATION_WEBHOOK_KEY] ?: "" }

    /**
     * 保存配置完成状态
     */
    suspend fun saveSetupCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SETUP_COMPLETED_KEY] = completed
        }
    }

    /**
     * 读取配置完成状态
     */
    val isSetupCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SETUP_COMPLETED_KEY] ?: false
        }

    /**
     * 保存分类配置（转成 JSON 字符串存储）
     */
    suspend fun saveCategories(categories: List<Category>) {
        val json = Json.encodeToString(categories)
        context.dataStore.edit { preferences ->
            preferences[CATEGORIES_KEY] = json
        }
    }

    suspend fun rememberSubCategory(categoryName: String, subCategoryName: String, transactionType: String) {
        val name = subCategoryName.trim()
        if (name.isEmpty()) return
        migrateCategories()
        context.dataStore.edit { preferences ->
            val current = preferences[CATEGORIES_KEY]?.let {
                runCatching { Json.decodeFromString<List<Category>>(it) }.getOrNull()
            } ?: DEFAULT_CATEGORIES
            val matches = current.indices.filter { current[it].name == categoryName }
            val index = if (transactionType == "income" || transactionType == "refund")
                matches.lastOrNull() else matches.firstOrNull()
            if (index == null || current[index].subCategories.any { it.name == name }) return@edit
            val updated = current.toMutableList()
            updated[index] = updated[index].copy(subCategories = updated[index].subCategories + SubCategory(name))
            preferences[CATEGORIES_KEY] = Json.encodeToString(updated)
        }
    }

    /**
     * 读取分类配置（从 JSON 字符串解析）
     */
    val categories: Flow<List<Category>> = context.dataStore.data
        .onStart { migrateCategories() }
        .map { preferences ->
            val json = preferences[CATEGORIES_KEY]
            if (json.isNullOrEmpty()) {
                DEFAULT_CATEGORIES
            } else {
                try {
                    Json.decodeFromString(json)
                } catch (e: Exception) {
                    DEFAULT_CATEGORIES
                }
            }
        }

    private suspend fun migrateCategories() {
        context.dataStore.edit { preferences ->
            if ((preferences[CATEGORY_VERSION_KEY] ?: 0) >= 1) return@edit
            val old = preferences[CATEGORIES_KEY]?.let {
                runCatching { Json.decodeFromString<List<Category>>(it) }.getOrNull()
            }
            if (old != null) {
                val replacements = mapOf(
                    "宠物" to (listOf("猫粮", "狗粮", "玩具", "医疗") to listOf("宠物粮", "零食", "玩具", "医疗", "保险")),
                    "运动健身" to (listOf("健身", "球类", "游泳", "跑步") to listOf("会员", "场地费", "器械", "服饰配件")),
                    "住房" to (listOf("房租", "房贷", "水电", "物业", "家具") to listOf("房贷", "水电燃气", "物业", "家具", "保险", "维修", "房租")),
                    "医疗健康" to (listOf("挂号", "药品", "体检", "保险") to listOf("挂号", "药品", "住院", "体检", "保险")),
                    "休闲娱乐" to (emptyList<String>() to listOf("彩票")),
                    "购物" to (emptyList<String>() to listOf("超市购物"))
                )
                val movedCustom = old.filter { it.name == "日用品" || it.name == "买菜" }
                    .flatMap { category ->
                        val builtIns = if (category.name == "日用品")
                            setOf("洗护", "清洁", "纸巾", "厨房")
                        else setOf("蔬菜", "肉类", "海鲜", "蛋奶")
                        category.subCategories.filterNot { it.name in builtIns }
                    }
                val updated = old.filterNot { it.name == "日用品" || it.name == "买菜" }
                    .map { category ->
                        val replacement = replacements[category.name] ?: return@map category
                        val (obsolete, requested) = replacement
                        val movedNames = if (category.name == "购物") movedCustom.map { it.name } else emptyList()
                        val names = (requested + category.subCategories.map { it.name }.filterNot { it in obsolete } + movedNames)
                            .distinct()
                        category.copy(subCategories = names.map(::SubCategory))
                    }
                preferences[CATEGORIES_KEY] = Json.encodeToString(updated)
            }
            preferences[CATEGORY_VERSION_KEY] = 1
        }
    }
}
