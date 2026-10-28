package com.example.autoexpense.service

import kotlin.math.roundToLong

/** Collects payment notifications arriving together, while ignoring duplicate listener events. */
class AlipayBurstAccumulator {
    data class Entry(val amount: Double, val rawText: String, val notificationKey: String?)
    data class Batch(val amount: Double, val rawText: String, val count: Int)

    private data class Seen(val amountCents: Long, val key: String?, val timeMs: Long)
    private val seen = mutableListOf<Seen>()
    private val pending = mutableListOf<Entry>()

    fun add(amount: Double, rawText: String, notificationKey: String?, nowMs: Long): Boolean {
        seen.removeAll { nowMs - it.timeMs > 8000L }
        val cents = (amount * 100).roundToLong()
        val duplicate = seen.any { previous ->
            when {
                notificationKey != null && previous.key != null -> notificationKey == previous.key
                else -> previous.amountCents == cents
            }
        }
        if (duplicate) {
            // Remember a newly observed notification key so a later repost is also ignored.
            if (notificationKey != null && seen.none { it.key == notificationKey }) {
                seen.add(Seen(cents, notificationKey, nowMs))
            }
            return false
        }
        seen.add(Seen(cents, notificationKey, nowMs))
        pending.add(Entry(amount, rawText, notificationKey))
        return pending.size == 1
    }

    fun drain(): Batch? {
        if (pending.isEmpty()) return null
        val entries = pending.toList()
        pending.clear()
        val cents = entries.sumOf { (it.amount * 100).roundToLong() }
        return Batch(cents / 100.0, entries.joinToString("\n") { it.rawText }.trim(), entries.size)
    }
}
