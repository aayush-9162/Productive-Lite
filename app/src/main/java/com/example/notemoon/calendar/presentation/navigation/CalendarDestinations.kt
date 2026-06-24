package com.example.notemoon.calendar.presentation.navigation

/** Navigation argument keys for the Calendar module. */
const val EVENT_ID_ARG = "eventId"
const val EVENT_DATE_ARG = "date"

/** Sentinel meaning "no event" — the Add/Edit screen is in create mode. */
const val NO_EVENT_ID = -1L

/** Sentinel meaning "no preset date". */
const val NO_DATE = -1L

/** Route definitions for the Calendar module. */
object CalendarDestinations {

    const val CALENDAR = "calendar"

    const val ADD_EDIT_EVENT = "add_edit_event"
    const val ADD_EDIT_EVENT_ROUTE =
        "$ADD_EDIT_EVENT?$EVENT_ID_ARG={$EVENT_ID_ARG}&$EVENT_DATE_ARG={$EVENT_DATE_ARG}"

    const val EVENT_DETAILS = "event_details"
    const val EVENT_DETAILS_ROUTE = "$EVENT_DETAILS/{$EVENT_ID_ARG}"

    /** Create a new event, optionally pre-filling the day. */
    fun addEvent(date: Long = NO_DATE): String =
        "$ADD_EDIT_EVENT?$EVENT_ID_ARG=$NO_EVENT_ID&$EVENT_DATE_ARG=$date"

    fun editEvent(eventId: Long): String =
        "$ADD_EDIT_EVENT?$EVENT_ID_ARG=$eventId&$EVENT_DATE_ARG=$NO_DATE"

    fun eventDetails(eventId: Long): String = "$EVENT_DETAILS/$eventId"
}
