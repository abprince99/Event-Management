package com.example.eventmanagement.events.ui.adapters

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.eventmanagement.databinding.EventListRowItemsBinding
import com.example.eventmanagement.events.data.models.Events
import java.util.Locale

class EventListAdapter(
    private val onEditClick: (Events) -> Unit,
    private val onDeleteClick: (Events) -> Unit
) : RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    private val events = mutableListOf<Events>()

    fun submitList(newEvents: List<Events>) {

        events.clear()
        events.addAll(newEvents)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EventViewHolder {

        val binding = EventListRowItemsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: EventViewHolder,
        position: Int
    ) {

        val event = events[position]
        holder.bind(
            event = event,
            onEditClick = onEditClick,
            onDeleteClick = onDeleteClick
        )
    }

    override fun getItemCount(): Int {
        return events.size
    }

    class EventViewHolder(
        private val binding: EventListRowItemsBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            event: Events,
            onEditClick: (Events) -> Unit,
            onDeleteClick: (Events) -> Unit
        ) {
            binding.tvEventTitle.text = event.title
            binding.tvEventDescription.text = event.description.ifBlank { "No description" }
            binding.tvEventLocation.text = event.location.ifBlank { "No location" }
            event.dateTime?.let { timestamp ->
                val dateFormat = SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a",
                    Locale.getDefault()
                )
                binding.tvEventDateTime.text = dateFormat.format(timestamp.toDate())
            }
            binding.btnEdit.setOnClickListener {
                onEditClick(event)
            }
            binding.btnDelete.setOnClickListener {
                onDeleteClick(event)
            }
        }
    }
}


