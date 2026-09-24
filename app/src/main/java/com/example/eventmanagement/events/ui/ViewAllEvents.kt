package com.example.eventmanagement.events.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.eventmanagement.R
import com.example.eventmanagement.databinding.ActivityViewAllEventsBinding
import com.example.eventmanagement.events.data.models.Events
import com.example.eventmanagement.events.ui.adapters.EventListAdapter
import com.example.eventmanagement.events.viewmodel.EventState
import com.example.eventmanagement.events.viewmodel.EventViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ViewAllEvents : AppCompatActivity() {
    private lateinit var binding: ActivityViewAllEventsBinding
    private lateinit var eventAdapter: EventListAdapter
    private val viewModel: EventViewModel by viewModels()
    private var allEvents: List<Events> = emptyList()
    private var selectedFilter = "ALL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeEvents()

        viewModel.startListeningToEvents()
    }

    private fun setupRecyclerView() {
        eventAdapter = EventListAdapter(
            onEditClick = { event ->
                openEditEvent(event)
            },
            onDeleteClick = { event ->
                showDeleteDialog(event)
            }
        )

        binding.rvAllEvents.apply {
            layoutManager = LinearLayoutManager(this@ViewAllEvents)
            adapter = eventAdapter
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFilters()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.filterGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                selectedFilter = when (checkedId) {
                    R.id.btnUpcoming -> "UPCOMING"
                    R.id.btnPast -> "PAST"
                    else -> "ALL"
                }
                applyFilters()
            }
        }
    }

    private fun observeEvents() {
        viewModel.events.observe(this) { events ->
            allEvents = events
            applyFilters()
        }

        viewModel.eventState.observe(this) { state ->
            if (state is EventState.Error) {
                Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun applyFilters() {
        val query = binding.etSearch.text.toString().trim().lowercase()
        val now = System.currentTimeMillis()

        val filteredList = allEvents.filter { event ->
            val matchesQuery = event.title.lowercase().contains(query) ||
                    event.description.lowercase().contains(query) ||
                    event.location.lowercase().contains(query)

            val eventTime = event.dateTime?.toDate()?.time ?: 0L
            val matchesFilter = when (selectedFilter) {
                "UPCOMING" -> eventTime >= now
                "PAST" -> eventTime < now
                else -> true
            }

            matchesQuery && matchesFilter
        }

        eventAdapter.submitList(filteredList)
    }

    private fun openEditEvent(event: Events) {
        val intent = Intent(this, EventActivity::class.java).apply {
            putExtra("eventId", event.id)
            putExtra("title", event.title)
            putExtra("description", event.description)
            putExtra("location", event.location)
            putExtra("dateTime", event.dateTime?.toDate()?.time)
        }
        startActivity(intent)
    }

    private fun showDeleteDialog(event: Events) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Event")
            .setMessage("Are you sure you want to delete \"${event.title}\"?")
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("YES") { _, _ ->
                viewModel.deleteEvent(event.id)
            }
            .show()
    }
}
