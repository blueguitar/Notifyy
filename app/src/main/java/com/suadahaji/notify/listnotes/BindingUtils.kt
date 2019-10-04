package com.suadahaji.notify.listnotes

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.suadahaji.notify.database.Note
import java.text.SimpleDateFormat

@BindingAdapter("noteTitle")
fun TextView.setNoteTitle(item: Note?) {
    item?.let {
        text = item.noteTitle
    }
}

@BindingAdapter("noteContent")
fun TextView.setNoteContent(item: Note?) {
    item?.let {
        text = item.noteContent
    }
}

@BindingAdapter("noteDate")
fun TextView.setNoteDate(item: Note?) {
    item?.let {
        text = convertLongToDateString(item.updatedDate)
    }
}

@BindingAdapter("noteTime")
fun TextView.setNoteTime(item: Note?) {
    item?.let {
        text = convertLongToTimeString(item.updatedDate)
    }
}

@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("dd-MMM-yyyy")
        .format(systemTime).toString()
}

@SuppressLint("SimpleTimeFormat")
fun convertLongToTimeString(systemTime: Long): String {
    return SimpleDateFormat("hh:mm a")
        .format(systemTime).toString()
}
