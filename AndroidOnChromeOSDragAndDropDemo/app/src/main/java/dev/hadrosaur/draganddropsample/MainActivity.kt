/*
 * Copyright (c) 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.hadrosaur.draganddropsample

import android.content.ClipData
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.Intent
import android.graphics.Bitmap.CompressFormat
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View
import android.view.View.DRAG_FLAG_GLOBAL
import android.view.View.DRAG_FLAG_GLOBAL_URI_READ
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import dev.hadrosaur.draganddropsample.databinding.ActivityMainBinding
import java.io.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        val LOG_TAG = "DragDropSample"
        fun logD(message: String) {
            Log.d(LOG_TAG, message)
        }

        fun logE(message: String) {
            Log.e(LOG_TAG, message)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val mainConstraintLayout = binding.root
        setContentView(mainConstraintLayout)

        binding.buttonNewTask.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            })
        }

        binding.textDragItem.setOnLongClickListener {
            val textView = it as TextView
            val dragContent = textView.text

            //Set the drag content and type
            val item = ClipData.Item(dragContent)
            val dragData = ClipData(dragContent, arrayOf(MIMETYPE_TEXT_PLAIN), item)

            //Set the visual look of the dragged object
            //Can be extended and customized. We use the default here.
            val dragShadow = View.DragShadowBuilder(textView)

            // Starts the drag, note: global flag allows for cross-application drag
            textView.startDragAndDrop(dragData, dragShadow, null, DRAG_FLAG_GLOBAL)
        }

        binding.imageDragItem.setOnLongClickListener {
            val textView = it as ImageView

            val imageFile = File(File(filesDir, "images"), "earth.png")

            if (!imageFile.exists()) {
                // Local file doesn't exist, create it
                File(filesDir, "images").mkdirs()
                // Write the file to local storage
                ByteArrayOutputStream().use { bos ->
                    textView.drawable.toBitmap()
                        .compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos)
                    FileOutputStream(imageFile).use { fos ->
                        fos.write(bos.toByteArray())
                        fos.flush()
                    }
                }
            }

            val dragContent =
                FileProvider.getUriForFile(
                    this,
                    "dev.hadrosaur.draganddropsample.images",
                    imageFile
                )

            //Set the drag content and type
            val item = ClipData.Item(dragContent)
            val dragData = ClipData("Image", arrayOf("image/png"), item)

            //Set the visual look of the dragged object
            //Can be extended and customized. We use the default here.
            val dragShadow = View.DragShadowBuilder(textView)

            // Starts the drag, note: global flag allows for cross-application drag
            textView.startDragAndDrop(
                dragData,
                dragShadow,
                null,
                DRAG_FLAG_GLOBAL.or(DRAG_FLAG_GLOBAL_URI_READ)
            )
        }

        binding.textDropTarget.setOnDragListener(DropTargetListener())

        binding.buttonClear.setOnClickListener {
            resetDropTarget()
        }
    }

    inner class DropTargetListener : View.OnDragListener {

        override fun onDrag(view: View, event: DragEvent): Boolean {
            return when (event.action) {
                ACTION_DRAG_STARTED -> {
                    event.clipDescription?.let { clip ->
                        if (clip.hasMimeType(MIMETYPE_TEXT_PLAIN)
                            || clip.hasMimeType("image/jpeg")
                            || clip.hasMimeType("image/png")
                        ) {
                            // Highlight background colour so user knows this is a target
                            binding.textDropTarget.background =
                                ContextCompat.getDrawable(
                                    this@MainActivity,
                                    R.drawable.bg_drop_highlight
                                )
                            return true
                        }
                        for (i in 0 until clip.mimeTypeCount) {
                            logD("Mime type not supported: ${clip.getMimeType(i)}")
                        }
                    } ?: run {
                        logE("Clip does not contain a description")
                    }
                    false
                }
                ACTION_DRAG_ENTERED -> {
                    // More intense background colour when item is over top of target
                    binding.textDropTarget.background =
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.bg_drop_enter)
                    true
                }
                ACTION_DRAG_LOCATION -> {
                    true
                }
                ACTION_DRAG_EXITED -> {
                    // Less intense background colour when item not over target
                    binding.textDropTarget.background =
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.bg_drop_highlight)
                    true
                }
                ACTION_DRAG_ENDED -> {
                    // Back to default colour when drag is not in process
                    binding.textDropTarget.background =
                        ContextCompat.getDrawable(this@MainActivity, R.drawable.bg_target_normal)
                    true
                }
                ACTION_DROP -> {
                    resetDropTarget()

                    requestDragAndDropPermissions(event) //Allow items from other applications
                    val item = event.clipData.getItemAt(0)

                    when {
                        event.clipDescription.hasMimeType(MIMETYPE_TEXT_PLAIN) -> {
                            val MAX_LENGTH = 200
                            if (item.text != null) {
                                // Plain text item, show it on screen
                                binding.textDropTarget.setTextSize(COMPLEX_UNIT_SP, 22f)
                                binding.textDropTarget.text = getString(
                                    R.string.drop_text,
                                    item.text.substring(
                                        0,
                                        item.text.length.coerceAtMost(MAX_LENGTH)
                                    )
                                )
                            } else {
                                // This is a plain text file. Read some characters and show them
                                // Use ContentResolver to resolve the URI
                                val contentUri = item.uri
                                val parcelFileDescriptor: ParcelFileDescriptor?
                                try {
                                    parcelFileDescriptor =
                                        contentResolver.openFileDescriptor(contentUri, "r")
                                } catch (e: FileNotFoundException) {
                                    e.printStackTrace()
                                    logD("File not found.")
                                    return false
                                }

                                if (parcelFileDescriptor == null) {
                                    logD("Error: could not load file: $contentUri")
                                    binding.textDropTarget.text =
                                        resources.getString(
                                            R.string.drop_error,
                                            contentUri.toString()
                                        )
                                } else {
                                    // Got the file descriptor, now read the file
                                    val fileDescriptor = parcelFileDescriptor.fileDescriptor
                                    val bytes = ByteArray(MAX_LENGTH)

                                    // Read the first MAX_LENGTH bytes of the file
                                    try {
                                        FileInputStream(fileDescriptor).use {
                                            it.read(bytes, 0, MAX_LENGTH)
                                        }
                                    } catch (e: Exception) {
                                        logD("Unable to read file: ${e.message}")
                                    }

                                    // Show CHARS_TO_READ chars in the UI
                                    val contents = String(bytes)

                                    // Show the read chars in drop target
                                    binding.textDropTarget.setTextSize(COMPLEX_UNIT_SP, 15f)
                                    binding.textDropTarget.text = getString(
                                        R.string.drop_text,
                                        contents.substring(
                                            0,
                                            contents.length.coerceAtMost(MAX_LENGTH)
                                        )
                                    )
                                }
                            }
                        }
                        event.clipDescription.hasMimeType("image/png") ||
                                event.clipDescription.hasMimeType("image/jpeg") -> {
                            item.uri?.let { uri ->
                                // PNG image received; display it
                                val size = 72.px
                                decodeSampledBitmapFromUri(
                                    contentResolver,
                                    uri,
                                    size,
                                    size
                                )?.let { bitmap ->
                                    binding.textDropTarget.text =
                                        getString(R.string.drop_image, bitmap.width, bitmap.height)
                                    val drawable = BitmapDrawable(resources, bitmap).apply {
                                        val ratio =
                                            intrinsicHeight.toFloat() / intrinsicWidth.toFloat()
                                        setBounds(0, 0, size, (size * ratio).toInt())
                                    }
                                    binding.textDropTarget.setCompoundDrawables(
                                        drawable,
                                        null,
                                        null,
                                        null
                                    )
                                }
                            } ?: run {
                                logE("Clip data is missing URI")
                            }
                        }
                    }
                    true
                }
                else -> {
                    logE("Unknown dragListener action.")
                    false
                }
            }
        }

    }

    private fun resetDropTarget() {
        binding.textDropTarget.setTextSize(COMPLEX_UNIT_SP, 22f)
        binding.textDropTarget.background =
            ContextCompat.getDrawable(this, R.drawable.bg_target_normal)
        binding.textDropTarget.text = resources.getString(R.string.drop_target)
        binding.textDropTarget.setCompoundDrawables(null, null, null, null)
    }

}
