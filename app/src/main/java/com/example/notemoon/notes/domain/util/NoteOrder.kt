package com.example.notemoon.notes.domain.util

/** Direction a list of notes can be sorted in. */
enum class OrderType {
    Ascending,
    Descending
}

/**
 * The field notes are sorted by, combined with an [OrderType].
 *
 * Covers the three sort options required by the Notes module:
 * Date Created, Last Modified and Title.
 */
sealed class NoteOrder(val orderType: OrderType) {
    class Title(orderType: OrderType) : NoteOrder(orderType)
    class DateCreated(orderType: OrderType) : NoteOrder(orderType)
    class LastModified(orderType: OrderType) : NoteOrder(orderType)

    /** Returns the same sort field with a (possibly) different [OrderType]. */
    fun copy(orderType: OrderType): NoteOrder {
        return when (this) {
            is Title -> Title(orderType)
            is DateCreated -> DateCreated(orderType)
            is LastModified -> LastModified(orderType)
        }
    }
}
