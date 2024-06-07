package com.example.todonotion.util


// Notification Channel constants

// Name of Notification Channel for notifications of background work
val NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Miscellaneous Notifications"
const val NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
val NOTIFICATION_TITLE: CharSequence = "Authentication"
const val CHANNEL_ID = "AUTH_NOTIFICATION"
const val NOTIFICATION_ID = 1

// The name of the image manipulation work
const val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"


const val ADD_ACTION = "ADD"
const val EDIT_ACTION = "EDIT"
const val DELETE_ACTION = "DELETE"
// Other keys
const val OUTPUT_PATH = "blur_filter_outputs"
const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
const val TAG_OUTPUT = "OUTPUT"
const val KEY_BLUR_LEVEL = "KEY_BLUR_LEVEL"

const val DELAY_TIME_MILLIS: Long = 3000