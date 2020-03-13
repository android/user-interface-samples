package com.example.android.bubbles

import android.content.ContentProvider
import android.content.ContentValues
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.android.bubbles.data.Contact

class AssetFileProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun getType(uri: Uri): String? {
        val segments = uri.pathSegments
        return when (segments[0]) {
            "icon" -> MimeTypeMap.getSingleton().getMimeTypeFromExtension("jpg")
            else -> "application/octet-stream"
        }
    }

    override fun openAssetFile(uri: Uri, mode: String): AssetFileDescriptor? {
        val segments = uri.pathSegments
        return when (segments[0]) {
            "icon" -> {
                val id = segments[1].toLong()
                Contact.CONTACTS.find { it.id == id }?.let { contact ->
                    context?.resources?.assets?.openFd(contact.icon)
                }
            }
            else -> null
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        throw UnsupportedOperationException("No query")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("No insert")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        throw UnsupportedOperationException("No update")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException("No delete")
    }
}
