package com.example.eventmanagement.events.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.core.content.ContextCompat
import com.example.eventmanagement.R
import com.example.eventmanagement.auth.data.repository.AuthRepository
import com.example.eventmanagement.auth.ui.activity.Authentication
import com.example.eventmanagement.databinding.ActivityDashboardBinding
import com.example.eventmanagement.events.data.models.Events
import com.example.eventmanagement.events.ui.adapters.EventListAdapter
import com.example.eventmanagement.events.viewmodel.EventState
import com.example.eventmanagement.events.viewmodel.EventViewModel
import com.example.eventmanagement.utils.ThemeManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class Dashboard : AppCompatActivity() {
    lateinit var binding : ActivityDashboardBinding
    private lateinit var eventAdapter: EventListAdapter

    private val viewModel: EventViewModel by viewModels()

    private val authRepository = AuthRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestNotificationPermission()

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

        binding.rvEvents.apply {

            layoutManager = LinearLayoutManager(this@Dashboard)

            adapter = eventAdapter

            //setHasFixedSize(false)
        }
    }

    private fun setupListeners() {

        binding.fabAddEvent.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    EventActivity::class.java
                )
            )
        }

        binding.btnTheme.setOnClickListener {
            showThemeSelectionDialog()
        }

        binding.btnLogout.setOnClickListener {

            showLogoutDialog()
        }

        binding.tvViewAll.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ViewAllEvents::class.java
                )
            )
        }

    }
    private fun observeEvents() {

        viewModel.events.observe(this) { events ->

            eventAdapter.submitList(events)

            updateEventCounts(events)
            setupBarChart(events)
            showUpcomingEvents(events)
        }

        viewModel.eventState.observe(this) { state ->

            if (state is EventState.Error) {

                Toast.makeText(
                    this,
                    state.message,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun requestNotificationPermission() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                notificationPermissionLauncher.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                Toast.makeText(
                    this,
                    "Notifications enabled",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private fun updateEventCounts(events: List<Events>) {

        val now = System.currentTimeMillis()

        val total = events.size

        val upcoming = events.count { event ->
            event.dateTime?.toDate()?.time?.let {
                it >= now
            } == true
        }

        val past = events.count { event ->
            event.dateTime?.toDate()?.time?.let {
                it < now
            } == true
        }

        binding.tvTotalEvents.text = total.toString()
        binding.tvUpcomingEvents.text = upcoming.toString()
        binding.tvPastEvents.text = past.toString()
    }

    private fun showUpcomingEvents(events: List<Events>) {

        val now = System.currentTimeMillis()

        val upcomingEvents = events
            .filter { event ->

                event.dateTime
                    ?.toDate()
                    ?.time
                    ?.let { time ->
                        time >= now
                    } == true
            }
            .sortedBy { event ->

                event.dateTime
                    ?.toDate()
                    ?.time
                    ?: Long.MAX_VALUE
            }
            .take(4)

        eventAdapter.submitList(upcomingEvents)
    }

    private fun showLogoutDialog() {

        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("YES") { _, _ ->

                authRepository.logout()

                startActivity(
                    Intent(
                        this,
                        Authentication::class.java
                    )
                )

                finish()
            }
            .show()
    }

    private fun openEditEvent(event: Events) {

        val intent = Intent(
            this,
            EventActivity::class.java
        )

        intent.putExtra("eventId", event.id)
        intent.putExtra("title", event.title)
        intent.putExtra("description", event.description)
        intent.putExtra("location", event.location)
        intent.putExtra(
            "dateTime",
            event.dateTime?.toDate()?.time
        )

        startActivity(intent)
    }

    private fun showDeleteDialog(event: Events) {

        MaterialAlertDialogBuilder(this)
            .setTitle("Delete Event")
            .setMessage(
                "Are you sure you want to delete \"${event.title}\"?"
            )
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("YES") { _, _ ->

                viewModel.deleteEvent(event.id)
            }
            .show()
    }

    private fun setupBarChart(events: List<Events>) {

        // Count events for each month
        val monthlyEventCount = IntArray(12)

        events.forEach { event ->

            event.dateTime?.let { timestamp ->

                val calendar = Calendar.getInstance()
                calendar.time = timestamp.toDate()

                val month = calendar.get(Calendar.MONTH)

                monthlyEventCount[month]++
            }
        }


        val entries = mutableListOf<BarEntry>()

        for (month in 0..11) {

            entries.add(
                BarEntry(
                    month.toFloat(),
                    monthlyEventCount[month].toFloat()
                )
            )
        }


        val dataSet = BarDataSet(
            entries,
            "Events per Month"
        )

        dataSet.valueTextSize = 12f


        val barData = BarData(dataSet)

        barData.barWidth = 0.6f

        binding.eventsChart.data = barData

        val months = arrayOf(
            "Jan",
            "Feb",
            "Mar",
            "Apr",
            "May",
            "Jun",
            "Jul",
            "Aug",
            "Sep",
            "Oct",
            "Nov",
            "Dec"
        )

        binding.eventsChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(months)

        binding.eventsChart.xAxis.granularity = 1f
        binding.eventsChart.xAxis.position =
            XAxis.XAxisPosition.BOTTOM

        binding.eventsChart.axisLeft.axisMinimum = 0f

        binding.eventsChart.axisRight.isEnabled = false

        val chartTextColor = ContextCompat.getColor(this, R.color.text_primary)
        dataSet.valueTextColor = chartTextColor
        binding.eventsChart.xAxis.textColor = chartTextColor
        binding.eventsChart.axisLeft.textColor = chartTextColor
        binding.eventsChart.legend.textColor = chartTextColor

        binding.eventsChart.description.isEnabled = false
        binding.eventsChart.legend.isEnabled = true

        binding.eventsChart.animateY(800)

        binding.eventsChart.invalidate()
    }

    private fun showThemeSelectionDialog() {
        val options = arrayOf("System Default", "Light Mode", "Dark Mode")

        MaterialAlertDialogBuilder(this)
            .setTitle("Choose Theme")
            .setItems(options) { _, which ->
                val theme = when (which) {
                    1 -> ThemeManager.LIGHT
                    2 -> ThemeManager.DARK
                    else -> ThemeManager.SYSTEM
                }
                ThemeManager.setTheme(this, theme)
            }
            .show()
    }


}

