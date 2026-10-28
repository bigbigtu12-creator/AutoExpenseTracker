package com.example.autoexpense.ui

import com.example.autoexpense.data.model.Category

/** The persisted list keeps expense categories before income categories. */
fun isIncomeCategory(categories: List<Category>, index: Int): Boolean {
    val category = categories[index]
    category.transactionType?.let { return it == "income" }
    val name = category.name
    if (name in setOf("工资", "兼职", "投资理财", "报销", "卖二手", "生活费")) return true
    if (name in setOf("人情社交", "其他")) {
        if (categories.count { it.name == name } > 1)
            return categories.take(index).any { it.name == name }
    }
    val incomeOnlySubcategories = setOf(
        "月薪", "奖金", "补贴", "临时工", "自由职业", "基金", "股票", "利息", "分红",
        "转账", "差旅", "办公", "闲鱼", "转转", "父母", "配偶", "退款", "中奖", "其他收入"
    )
    return category.subCategories.any { it.name in incomeOnlySubcategories }
}
