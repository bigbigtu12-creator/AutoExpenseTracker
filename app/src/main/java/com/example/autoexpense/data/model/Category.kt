package com.example.autoexpense.data.model

import kotlinx.serialization.Serializable

/**
 * 二级分类
 */
@Serializable
data class SubCategory(
    val name: String
)

/**
 * 一级分类
 */
@Serializable
data class Category(
    val name: String,
    val subCategories: List<SubCategory> = emptyList(),
    val transactionType: String? = null
)
