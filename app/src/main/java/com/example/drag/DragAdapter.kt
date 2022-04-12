package com.example.drag

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.core.view.DragStartHelper
import androidx.draganddrop.DropHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.drag.databinding.ItemViewBinding

class DragAdapter(val viewModel: ViewModel) : ListAdapter<Task, DragAdapter.DragHolder>(DiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DragHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DragHolder(binding)
    }

    override fun onBindViewHolder(holder: DragHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DragHolder(private val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Task) {
            with(binding) {
                title.text = item.name
                content.text = item.content
            }
            drag()
            dropHelper()
        }

        fun drag() {
            DragStartHelper(itemView) { view, _ ->
                val title = binding.title.text
                val content = binding.content.text
                // Create the ClipData to be shared
                val dragClipData = ClipData.newPlainText(/*label*/"Task", title)

                Log.d("TAG", "DragStartHelper ${title}")
                // Use the default drag shadow
                val dragShadowBuilder = View.DragShadowBuilder(view)
                MainActivity.logD("DragStartHelper")
                // Initiate the drag. Note the DRAG_FLAG_GLOBAL, which allows for drag events to be listened
                // to by apps other than the source app.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(dragClipData, dragShadowBuilder, null, View.DRAG_FLAG_GLOBAL)
                }else{
                    view.startDrag(dragClipData,dragShadowBuilder,view,0)
                }
            }.attach()
        }

        fun dropHelper() {
            DropHelper.configureView(
                binding.root.context as Activity,
                itemView,
                arrayOf(
                    ClipDescription.MIMETYPE_TEXT_PLAIN,
                    "image/*",
                    "application/x-arc-uri-list" // Support external items on Chrome OS Android 9
                ),
                DropHelper.Options.Builder()
                    .setHighlightColor(Color.RED)
                    // Match the radius of the view's background drawable
                    .setHighlightCornerRadiusPx(binding.root.resources.getDimensionPixelSize(R.dimen.drop_target_corner_radius))
                    .build()
            ) { _, payload ->

                // For the purposes of this demo, only handle the first ClipData.Item

                val item = payload.clip.getItemAt(0)
                Log.d("TAG", "item.text ${item.text}, ${payload.clip.itemCount}")
                val (_, remaining) = payload.partition { it == item }

                when {
                    payload.clip.description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) ->
                        handlePlainTextDrop(item)
                    else -> MainActivity.logD("dd")
                    //handleImageDrop(item)
                }

                // Allow the system to handle any remaining ClipData.Item objects if applicable
                remaining
            }
        }

        fun handlePlainTextDrop(item: ClipData.Item) {
            // The text is contained in the ClipData.Item
            if (item.text != null) {
                binding.title.text = item.text
            }
            else {
                // The text is in a file pointed to by the ClipData.Item
//                val parcelFileDescriptor: ParcelFileDescriptor? = try {
//                    contentResolver.openFileDescriptor(item.uri, "r")
//                } catch (e: FileNotFoundException) {
//                    e.printStackTrace()
//                    MainActivity.logE("FileNotFound")
//                    return
//                }

//                if (parcelFileDescriptor == null) {
//                    MainActivity.logE("Could not load file")
//                    binding.textDropTarget.text =
//                        resources.getString(R.string.drop_error, item.uri.toString())
//                } else {
//                    val fileDescriptor = parcelFileDescriptor.fileDescriptor
//                    val bytes = ByteArray(MainActivity.MAX_LENGTH)
//
//                    try {
//                        FileInputStream(fileDescriptor).use {
//                            it.read(bytes, 0, MainActivity.MAX_LENGTH)
//                        }
//                    } catch (e: java.lang.Exception) {
//                        MainActivity.logE("Unable to read file: ${e.message}")
//                    }
//
//                    binding.textDropTarget.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
//                    binding.textDropTarget.text = getString(R.string.drop_text, String(bytes))
//                }
            }
        }
    }



}