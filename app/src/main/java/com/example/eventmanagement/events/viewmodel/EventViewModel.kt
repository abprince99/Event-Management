package com.example.eventmanagement.events.viewmodel

import android.R.id.message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventmanagement.events.data.models.Events
import com.example.eventmanagement.events.data.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel : ViewModel() {
    private val repository = EventRepository()

    private val _eventState = MutableLiveData<EventState>()
    val eventState: LiveData<EventState> = _eventState

    private val _events = MutableLiveData<List<Events>>()
    val events: LiveData<List<Events>> = _events

    //CREATE
    fun createEvent(event: Events) {

        viewModelScope.launch {

            _eventState.value = EventState.Loading

            val result = repository.createEvent(event)

            result.onSuccess {

                _eventState.value =
                    EventState.Success

            }.onFailure { exception ->

                _eventState.value =
                    EventState.Error(
                        getErrorMessage(exception)
                    )
            }
        }
    }

    fun startListeningToEvents() {

        repository.listenToEvents(

            onSuccess = { eventList ->

                val sortedEvents = eventList.sortedByDescending {
                    it.dateTime
                }

                _events.postValue(sortedEvents)
            },

            onError = { exception ->

                _eventState.postValue(
                    EventState.Error(
                        getErrorMessage(exception)
                    )
                )
            }
        )
    }

    //UPDATE
    fun updateEvent(event: Events) {

        viewModelScope.launch {

            _eventState.value = EventState.Loading

            val result = repository.updateEvent(event)

            result.onSuccess {

                _eventState.value = EventState.Success

            }.onFailure { exception ->

                _eventState.value = EventState.Error(
                    getErrorMessage(exception)
                )
            }
        }
    }

    // DELETE
    fun deleteEvent(eventId: String) {

        viewModelScope.launch {

            _eventState.value = EventState.Loading

            val result = repository.deleteEvent(eventId)

            result.onSuccess {

                _eventState.value = EventState.Success

            }.onFailure { exception ->

                _eventState.value = EventState.Error(
                    getErrorMessage(exception)
                )
            }
        }
    }

    private fun getErrorMessage(exception: Throwable): String {

        val message = exception.message ?: ""

        return if (message.contains("network", ignoreCase = true)) {
            "Please check your internet connection"
        } else {
            message.ifBlank {
                "Unable to create event"
            }
        }
    }
}

sealed class EventState {

    data object Loading : EventState()

    data object Success : EventState()

    data class Error(
        val message: String
    ) : EventState()
}