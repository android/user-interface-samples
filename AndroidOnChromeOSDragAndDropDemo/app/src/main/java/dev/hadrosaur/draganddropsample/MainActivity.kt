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
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.DragEvent
import android.view.DragEvent.*
import android.view.View
import android.view.View.DRAG_FLAG_GLOBAL
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import dev.hadrosaur.draganddropsample.databinding.ActivityMainBinding
import java.io.FileInputStream
import java.io.FileNotFoundException

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        val LOG_TAG = "DragDropSample"
        fun logd(message: String) {
            Log.d(LOG_TAG, message)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val mainConstraintLayout = binding.root
        setContentView(mainConstraintLayout)

        binding.textDragItem.setOnLongClickListener {
            val textView = it as TextView
            val dragContent = "Dragged Text: ${textView.text}"

            //Set the drag content and type
            val item = ClipData.Item(dragContent)
            val dragData = ClipData(dragContent, arrayOf(MIMETYPE_TEXT_PLAIN), item)

            //Set the visual look of the dragged object
            //Can be extended and customized. We use the default here.
            val dragShadow = View.DragShadowBuilder(textView)

            // Starts the drag, note: global flag allows for cross-application drag
            textView.startDragAndDrop(dragData, dragShadow, null, DRAG_FLAG_GLOBAL)

            false
        }

        binding.textDropTarget.setOnDragListener(DropTargetListener())
    }

    inner class DropTargetListener : View.OnDragListener {
        override fun onDrag(view: View, event: DragEvent): Boolean {
            return when(event.action) {
                ACTION_DRAG_STARTED -> {
                    if (event.clipDescription.hasMimeType(MIMETYPE_TEXT_PLAIN)
                        || event.clipDescription.hasMimeType("image/png")) {

                        // Greenify background colour so user knows this is a target
                        view.setBackgroundColor(getColor(android.R.color.holo_green_dark))
                        return true
                    }
                    false
                }
                ACTION_DRAG_ENTERED -> {
                    // More intense green background colour when item is over top of target
                    view.setBackgroundColor(getColor(android.R.color.holo_green_light))
                    true
                }
                ACTION_DRAG_LOCATION -> { true }
                ACTION_DRAG_EXITED -> {
                    // Less intense green background colour when item not over target
                    view.setBackgroundColor(getColor(android.R.color.holo_green_dark))
                    true
                }
                ACTION_DRAG_ENDED -> {
                    // Back to default colour when drag is not in process
                    view.setBackgroundColor(getColor(android.R.color.holo_purple))
                    true
                }
                ACTION_DROP -> {
                    requestDragAndDropPermissions(event) //Allow items from other applications
                    val item = event.clipData.getItemAt(0)

                    if (event.clipDescription.hasMimeType(MIMETYPE_TEXT_PLAIN)) {
                        if (item.text != null) {
                            // Plain text item, show it on screen
                            binding.textDropTarget.setTextSize(COMPLEX_UNIT_SP, 22f)
                            binding.textDropTarget.text = item.text
                        } else {
                            // This is a plain text file. Read some characters and show them
                            // Use ContentResolver to resolve the URI
                            val contentUri = item.uri
                            var parcelFileDescriptor: ParcelFileDescriptor? = null
                            try {
                                parcelFileDescriptor =
                                    contentResolver.openFileDescriptor(contentUri, "r")
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                                logd("File not found.")
                                return false
                            }

                            if (parcelFileDescriptor == null) {
                                logd("Error: could not load file: ${contentUri.toString()}")
                                binding.textDropTarget.text = "Error: could not load file: ${contentUri.toString()}"
                            } else {
                                // Got the file descriptor, now read the file
                                val fileDescriptor = parcelFileDescriptor.fileDescriptor
                                val MAX_LENGTH = 5000
                                val bytes = ByteArray(MAX_LENGTH)

                                // Read the first MAX_LENGTH bytes of the file
                                try {
                                    val `in` = FileInputStream(fileDescriptor)
                                    try {
                                        `in`.read(bytes, 0, MAX_LENGTH)
                                    } finally {
                                        `in`.close()
                                    }
                                } catch (e: Exception) {
                                    logd("Unable to read file: ${e.message}")
                                }

                                // Show CHARS_TO_READ chars in the UI
                                val contents = String(bytes)
                                val CHARS_TO_READ = 200
                                val content_length =
                                    if (contents.length > CHARS_TO_READ) CHARS_TO_READ else 0
                                binding.textDropTarget.setTextSize(COMPLEX_UNIT_SP, 15f)
                                binding.textDropTarget.text = contents.substring(0, content_length)
                            }
                        }
                    } else if (event.clipDescription.hasMimeType("image/png")) {
                        // PNG image received. Do something with it (for now just show a message)
                        binding.textDropTarget.setTextSize(COMPLEX_UNIT_SP, 22f)
                        binding.textDropTarget.text = "You have dropped a PNG file"
                    }
                    true
                }
                else -> {
                    logd("Unknown dragListener action.")
                    false
                }
            }
        }
    }
}