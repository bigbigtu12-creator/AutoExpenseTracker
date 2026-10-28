package com.example.autoexpense.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.autoexpense.R
import com.example.autoexpense.ui.ExpenseConfirmActivity

class ManualEntryWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val openManualEntry = Intent(context, ExpenseConfirmActivity::class.java).apply {
                action = ACTION_MANUAL_ENTRY
                putExtra("amount", 0.0)
                putExtra("paymentMethod", context.getString(R.string.widget_manual_payment_method))
                putExtra("rawText", "")
                putExtra("transactionType", "expense")
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                openManualEntry,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // The desktop shortcut keeps its original artwork across app skins.
            val views = RemoteViews(context.packageName, R.layout.widget_manual_entry).apply {
                setOnClickPendingIntent(R.id.widget_manual_entry_root, pendingIntent)
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private companion object {
        const val ACTION_MANUAL_ENTRY = "com.example.autoexpense.action.MANUAL_ENTRY"
    }
}
