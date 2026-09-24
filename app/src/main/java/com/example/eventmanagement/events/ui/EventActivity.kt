package com.example.eventmanagement.events.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.eventmanagement.R
import com.example.eventmanagement.databinding.ActivityEventBinding
import com.example.eventmanagement.events.data.models.Events
import com.example.eventmanagement.events.viewmodel.EventState
import com.example.eventmanagement.events.viewmodel.EventViewModel
import com.google.firebase.Timestamp
import java.util.Locale

class EventActivity : AppCompatActivity() {
    lateinit var binding: ActivityEventBinding
    private val selectedDateTime = Calendar.getInstance()
    private val viewModel: EventViewModel by viewModels()
    private var isEditMode = false
    private var eventId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        eventId = intent.getStringExtra("eventId") ?: ""

        isEditMode = eventId.isNotBlank()

        if (isEditMode) {
            loadEventForEdit()
        }

        setupToolbar()
        setupDatePicker()
        setupTimePicker()
        setupCreateEventButton()
        observeEventState()

    }

    private fun setupToolbar() {
        binding.ibBack.setOnClickListener {
            finish()
        }


    }

    private fun setupDatePicker() {

        binding.etDate.setOnClickListener {

            val today = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->

                    selectedDateTime.set(
                        Calendar.YEAR,
                        year
                    )

                    selectedDateTime.set(
                        Calendar.MONTH,
                        month
                    )

                    selectedDateTime.set(
                        Calendar.DAY_OF_MONTH,
                        dayOfMonth
                    )

                    val dateFormat = SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.getDefault()
                    )

                    binding.etDate.setText(
                        dateFormat.format(selectedDateTime.time)
                    )

                    binding.etTime.setText("")
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.datePicker.minDate = today.timeInMillis

            datePicker.show()
        }
    }

    private fun setupTimePicker() {

        binding.etTime.setOnClickListener {

            if (binding.etDate.text.isNullOrBlank()) {
                binding.dateLayout.error = "Please select a date first"
                return@setOnClickListener
            }

            binding.dateLayout.error = null

            val now = Calendar.getInstance()

            val timePicker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->

                    selectedDateTime.set(
                        Calendar.HOUR_OF_DAY,
                        hourOfDay
                    )

                    selectedDateTime.set(
                        Calendar.MINUTE,
                        minute
                    )

                    selectedDateTime.set(
                        Calendar.SECOND,
                        0
                    )

                    selectedDateTime.set(
                        Calendar.MILLISECOND,
                        0
                    )

                    if (selectedDateTime.before(now)) {

                        binding.timeLayout.error =
                            "Event date and time cannot be in the past"

                        binding.etTime.setText("")

                    } else {

                        binding.timeLayout.error = null

                        val timeFormat = SimpleDateFormat(
                            "hh:mm a",
                            Locale.getDefault()
                        )

                        binding.etTime.setText(
                            timeFormat.format(selectedDateTime.time)
                        )
                    }
                },
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
            )

            timePicker.show()
        }
    }

    private fun setupCreateEventButton() {

        binding.btnCreateEvent.setOnClickListener {

            if (!validateForm()) {
                return@setOnClickListener
            }

            if (isEditMode) {
                updateEvent()
            } else {
                createEvent()
            }
        }
    }

    private fun validateForm(): Boolean {

        val title = binding.etTitle.text
            .toString()
            .trim()

        val date = binding.etDate.text
            .toString()
            .trim()

        val time = binding.etTime.text
            .toString()
            .trim()

        if (title.isBlank()) {

            binding.titleLayout.error =
                "Event title is required"

            binding.etTitle.requestFocus()

            return false
        }

        binding.titleLayout.error = null

        if (date.isBlank()) {

            binding.dateLayout.error =
                "Please select an event date"

            return false
        }

        binding.dateLayout.error = null

        if (time.isBlank()) {

            binding.timeLayout.error =
                "Please select an event time"

            return false
        }

        binding.timeLayout.error = null

        if (selectedDateTime.before(Calendar.getInstance())) {

            binding.timeLayout.error =
                "Event date and time cannot be in the past"

            return false
        }

        return true
    }

    private fun createEvent() {

        val title = binding.etTitle.text
            .toString()
            .trim()

        val description = binding.etDescription.text
            .toString()
            .trim()

        val location = binding.etLocation.text
            .toString()
            .trim()

        /*Toast.makeText(
            this,
            "Validation successful",
            Toast.LENGTH_SHORT
        ).show()*/

        val event = Events(
            title = title,
            description = description,
            dateTime = Timestamp(
                selectedDateTime.time
            ),
            location = location
        )

        viewModel.createEvent(event)
    }

    private fun observeEventState() {

        viewModel.eventState.observe(this) { state ->
            when (state) {
                EventState.Loading -> {
                    binding.btnCreateEvent.isEnabled = false
                    binding.btnCreateEvent.text = if (isEditMode) {
                        "UPDATING..."
                    } else {
                        "CREATING..."
                    }
                }

                EventState.Success -> {
                    binding.btnCreateEvent.isEnabled = true
                    binding.btnCreateEvent.text = if (isEditMode) {
                        "UPDATE EVENT"
                    } else {
                        "CREATE EVENT"
                    }
                    Toast.makeText (this,
                        if (isEditMode) {
                            "Event updated successfully"
                        } else {
                            "Event created successfully"
                               },
                        Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }

                is EventState.Error -> {
                    binding.btnCreateEvent.isEnabled = true
                    binding.btnCreateEvent.text = if (isEditMode) {
                        "UPDATE EVENT"
                    } else {
                        "CREATE EVENT"
                    }
                    Toast.makeText (this, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loadEventForEdit() {
        binding.tvTitle.text = "Update Event"
        binding.tvHeader.text = "Update Existing Event"
        binding.tvSubText.visibility = View.GONE

        binding.etTitle.setText(
            intent.getStringExtra("title") ?: ""
        )

        binding.etDescription.setText(
            intent.getStringExtra("description") ?: ""
        )

        binding.etLocation.setText(
            intent.getStringExtra("location") ?: ""
        )

        val dateTimeMillis = intent.getLongExtra(
            "dateTime",
            0L
        )

        if (dateTimeMillis > 0) {

            selectedDateTime.timeInMillis = dateTimeMillis

            val dateFormat = SimpleDateFormat(
                "dd MMM yyyy",
                Locale.getDefault()
            )

            val timeFormat = SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
            )

            binding.etDate.setText(
                dateFormat.format(selectedDateTime.time)
            )

            binding.etTime.setText(
                timeFormat.format(selectedDateTime.time)
            )
        }

        binding.btnCreateEvent.text = "UPDATE EVENT"
    }

    private fun updateEvent() {

        val event = Events(
            id = eventId,
            title = binding.etTitle.text.toString().trim(),
            description = binding.etDescription.text.toString().trim(),
            dateTime = Timestamp(selectedDateTime.time),
            location = binding.etLocation.text.toString().trim()
        )

        viewModel.updateEvent(event)
    }

}