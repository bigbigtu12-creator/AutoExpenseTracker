package com.example.autoexpense.data.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

object FeishuApiService {

    private const val TAG = "FeishuApiService"
    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    data class BitableConfig(
        val appId: String,
        val appSecret: String,
        val appToken: String,
        val tableId: String
    ) {
        val isComplete: Boolean
            get() = appId.isNotBlank() &&
                    appSecret.isNotBlank() &&
                    appToken.isNotBlank() &&
                    tableId.isNotBlank()
    }

    data class SendResult(
        val success: Boolean,
        val message: String = ""
    )

    private data class FieldListResult(
        val success: Boolean,
        val fieldTypes: Map<String, Int> = emptyMap(),
        val message: String = ""
    )

    suspend fun sendExpense(
        webhookUrl: String,
        amount: Double,
        paymentMethod: String,
        category: String,
        subCategory: String,
        merchant: String,
        transactionType: String = "expense"
    ): Boolean {
        return sendExpenseDetailed(
            webhookUrl = webhookUrl,
            amount = amount,
            paymentMethod = paymentMethod,
            category = category,
            subCategory = subCategory,
            merchant = merchant,
            transactionType = transactionType
        ).success
    }

    suspend fun sendExpenseDetailed(
        webhookUrl: String,
        amount: Double,
        paymentMethod: String,
        category: String,
        subCategory: String,
        merchant: String,
        transactionType: String = "expense"
    ): SendResult = withContext(Dispatchers.IO) {
        if (webhookUrl.isBlank()) {
            return@withContext SendResult(false, "Webhook URL is empty")
        }
        if (!isTrustedFeishuWebhookUrl(webhookUrl)) {
            return@withContext SendResult(
                false,
                "\u8bf7\u4f7f\u7528\u98de\u4e66\u7fa4\u673a\u5668\u4eba\u7684\u5b98\u65b9 Webhook \u5730\u5740"
            )
        }

        try {
            val message = buildTextMessage(
                amount = amount,
                paymentMethod = paymentMethod,
                category = category,
                subCategory = subCategory,
                merchant = merchant,
                transactionType = transactionType
            )

            val json = JSONObject().apply {
                put("msg_type", "text")
                put("content", JSONObject().apply {
                    put("text", message)
                })
            }

            val request = Request.Builder()
                .url(webhookUrl)
                .post(json.toString().toRequestBody(jsonMediaType))
                .build()

            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@use SendResult(false, "Webhook HTTP ${response.code}")
                }

                try {
                    val jsonResponse = JSONObject(responseBody.ifBlank { "{}" })
                    if (!jsonResponse.has("code")) {
                        return@use SendResult(true, "Webhook sent")
                    }
                    val code = jsonResponse.optInt("code", -1)
                    val msg = jsonResponse.optString("msg", "")
                    SendResult(code == 0, if (code == 0) "Webhook sent" else "Webhook code=$code $msg")
                } catch (e: Exception) {
                    SendResult(false, "Webhook response parse failed: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Webhook send failed")
            SendResult(false, "Webhook request failed")
        }
    }

    suspend fun writeExpenseToBitable(
        config: BitableConfig,
        amount: Double,
        paymentMethod: String,
        category: String,
        subCategory: String,
        merchant: String,
        transactionType: String = "expense",
        rawText: String = "",
        requestId: String = UUID.randomUUID().toString()
    ): Boolean {
        return writeExpenseToBitableDetailed(
            config = config,
            amount = amount,
            paymentMethod = paymentMethod,
            category = category,
            subCategory = subCategory,
            merchant = merchant,
            transactionType = transactionType,
            rawText = rawText,
            requestId = requestId
        ).success
    }

    suspend fun writeExpenseToBitableDetailed(
        config: BitableConfig,
        amount: Double,
        paymentMethod: String,
        category: String,
        subCategory: String,
        merchant: String,
        transactionType: String = "expense",
        rawText: String = "",
        requestId: String = UUID.randomUUID().toString()
    ): SendResult = withContext(Dispatchers.IO) {
        if (!config.isComplete) {
            return@withContext SendResult(false, "Bitable config is incomplete")
        }

        try {
            val tokenResult = getTenantAccessTokenDetailed(config.appId, config.appSecret)
            if (!tokenResult.success) {
                return@withContext tokenResult
            }
            val token = tokenResult.message

            val fieldListResult = getBitableFieldNamesDetailed(config, token)
            if (!fieldListResult.success) {
                return@withContext SendResult(false, fieldListResult.message)
            }

            val availableFields = fieldListResult.fieldTypes
            val requiredFields = listOf(
                "\u65f6\u95f4",
                "\u7c7b\u578b",
                "\u91d1\u989d",
                "\u652f\u4ed8\u65b9\u5f0f",
                "\u4e00\u7ea7\u5206\u7c7b",
                "\u4e8c\u7ea7\u5206\u7c7b"
            )
            val missingRequiredFields = requiredFields.filterNot(availableFields::containsKey)
            if (missingRequiredFields.isNotEmpty()) {
                return@withContext SendResult(
                    false,
                    "\u8868\u683c\u7f3a\u5c11\u5fc5\u9700\u5b57\u6bb5\uff1a${missingRequiredFields.joinToString("\u3001")}"
                )
            }

            if (availableFields["\u91d1\u989d"] != 2) {
                return@withContext SendResult(false, "\u5b57\u6bb5\u201c\u91d1\u989d\u201d\u5fc5\u987b\u8bbe\u7f6e\u4e3a\u6570\u5b57\u7c7b\u578b")
            }

            val occurredAtMillis = System.currentTimeMillis()
            val occurredAtText = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
                .format(Date(occurredAtMillis))
            val occurredAtValue: Any = if (availableFields["\u65f6\u95f4"] == 5) {
                occurredAtMillis
            } else {
                occurredAtText
            }
            val desiredFields = linkedMapOf<String, Any>(
                "\u65f6\u95f4" to occurredAtValue,
                "\u7c7b\u578b" to transactionTypeLabel(transactionType),
                "\u91d1\u989d" to amount,
                "\u5546\u5bb6" to merchant.ifBlank { "\u672a\u77e5\u5546\u5bb6" },
                "\u652f\u4ed8\u65b9\u5f0f" to paymentMethod,
                "\u4e00\u7ea7\u5206\u7c7b" to category,
                "\u4e8c\u7ea7\u5206\u7c7b" to subCategory,
                "\u5907\u6ce8" to merchant,
                "\u6765\u6e90" to "App",
                "\u552f\u4e00\u952e" to requestId,
                "\u539f\u59cb\u6d88\u606f" to rawText
            )

            val fields = JSONObject().apply {
                desiredFields.forEach { (name, value) ->
                    if (availableFields.containsKey(name)) {
                        put(name, value)
                    }
                }
            }

            val body = JSONObject().apply {
                put("fields", fields)
            }

            createBitableRecordDetailed(config, token, requestId, body)
        } catch (e: Exception) {
            Log.e(TAG, "Bitable write failed")
            SendResult(false, "Bitable request failed")
        }
    }

    private fun getTenantAccessTokenDetailed(appId: String, appSecret: String): SendResult {
        val body = JSONObject().apply {
            put("app_id", appId)
            put("app_secret", appSecret)
        }

        val request = Request.Builder()
            .url("https://open.feishu.cn/open-apis/auth/v3/tenant_access_token/internal")
            .post(body.toString().toRequestBody(jsonMediaType))
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@use SendResult(false, "Token HTTP ${response.code}")
                }
                val jsonResponse = JSONObject(responseBody.ifBlank { "{}" })
                val code = jsonResponse.optInt("code", -1)
                val msg = jsonResponse.optString("msg", "")
                if (code != 0) {
                    SendResult(false, "Token code=$code $msg")
                } else {
                    SendResult(true, jsonResponse.optString("tenant_access_token", ""))
                }
            }
        } catch (e: Exception) {
            SendResult(false, "Token request failed")
        }
    }

    private fun getBitableFieldNamesDetailed(
        config: BitableConfig,
        token: String
    ): FieldListResult {
        val url = "https://open.feishu.cn/open-apis/bitable/v1/apps/${config.appToken}/tables/${config.tableId}/fields?page_size=100"
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .get()
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                val responseBody = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    return@use FieldListResult(false, message = "Fields HTTP ${response.code}")
                }

                val jsonResponse = JSONObject(responseBody.ifBlank { "{}" })
                val code = jsonResponse.optInt("code", -1)
                val msg = jsonResponse.optString("msg", "")
                if (code != 0) {
                    return@use FieldListResult(false, message = "Fields code=$code $msg")
                }

                val items = jsonResponse.optJSONObject("data")?.optJSONArray("items")
                val fields = buildMap {
                    if (items != null) {
                        for (index in 0 until items.length()) {
                            val item = items.optJSONObject(index) ?: continue
                            val name = item.optString("field_name")
                            if (name.isNotBlank()) put(name, item.optInt("type", -1))
                        }
                    }
                }
                FieldListResult(true, fields)
            }
        } catch (e: Exception) {
            FieldListResult(false, message = "Fields request failed")
        }
    }

    private suspend fun createBitableRecordDetailed(
        config: BitableConfig,
        token: String,
        requestId: String,
        body: JSONObject
    ): SendResult {
        val url = "https://open.feishu.cn/open-apis/bitable/v1/apps/${config.appToken}/tables/${config.tableId}/records?client_token=$requestId"
        var lastError = "\u5199\u5165\u5931\u8d25"

        repeat(3) { attempt ->
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $token")
                .post(body.toString().toRequestBody(jsonMediaType))
                .build()

            try {
                client.newCall(request).execute().use { response ->
                    val responseBody = response.body?.string().orEmpty()
                    if (!response.isSuccessful) {
                        lastError = "Bitable HTTP ${response.code}"
                        if (response.code !in 500..599 || attempt == 2) {
                            return SendResult(false, lastError)
                        }
                    } else {
                        val jsonResponse = JSONObject(responseBody.ifBlank { "{}" })
                        val code = jsonResponse.optInt("code", -1)
                        val msg = jsonResponse.optString("msg", "")
                        when {
                            code == 0 -> return SendResult(true, "\u5df2\u5199\u5165\u98de\u4e66\u4ea4\u6613\u6d41\u6c34")
                            code == 1254608 -> return SendResult(true, "\u8be5\u7b14\u8bb0\u5f55\u5df2\u63d0\u4ea4\uff0c\u672a\u91cd\u590d\u5199\u5165")
                            code in setOf(1254290, 1254291, 1255040, 1254607) && attempt < 2 -> {
                                lastError = "Bitable code=$code $msg"
                            }
                            else -> return SendResult(false, "Bitable code=$code $msg")
                        }
                    }
                }
            } catch (e: IOException) {
                lastError = "\u7f51\u7edc\u8bf7\u6c42\u5931\u8d25"
                if (attempt == 2) return SendResult(false, lastError)
            }

            delay(500L * (attempt + 1))
        }

        return SendResult(false, lastError)
    }

    private fun buildTextMessage(
        amount: Double,
        paymentMethod: String,
        category: String,
        subCategory: String,
        merchant: String,
        transactionType: String
    ): String {
        val typeLabel = when (transactionType) {
            "income" -> "\u6536\u5165"
            "refund" -> "\u9000\u6b3e"
            else -> "\u652f\u51fa"
        }
        return buildString {
            append("\u8bb0\u8d26\u6210\u529f\n")
            append("\u7c7b\u578b\uff1a")
            append(typeLabel)
            append("\n\u91d1\u989d\uff1a\u00a5")
            append(String.format(Locale.CHINA, "%.2f", amount))
            append("\n\u5546\u5bb6\uff1a")
            append(merchant.ifBlank { "\u672a\u586b\u5199" })
            append("\n\u652f\u4ed8\u65b9\u5f0f\uff1a")
            append(paymentMethod)
            if (category.isNotBlank()) {
                append("\n\u5206\u7c7b\uff1a")
                append(category)
                if (subCategory.isNotBlank()) {
                    append(" / ")
                    append(subCategory)
                }
            }
        }
    }

    private fun isTrustedFeishuWebhookUrl(value: String): Boolean {
        val url = value.trim().toHttpUrlOrNull() ?: return false
        val trustedHost = url.host == "open.feishu.cn" || url.host == "open.larksuite.com"
        return url.isHttps && trustedHost && url.encodedPath.startsWith("/open-apis/bot/v2/hook/")
    }

    private fun transactionTypeLabel(transactionType: String): String {
        return when (transactionType) {
            "income" -> "\u6536\u5165"
            "refund" -> "\u9000\u6b3e"
            else -> "\u652f\u51fa"
        }
    }

}
