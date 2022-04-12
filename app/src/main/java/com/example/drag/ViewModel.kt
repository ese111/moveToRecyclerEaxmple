package com.example.drag

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModel : ViewModel() {

    private val _tasks = MutableLiveData<List<Task>>()
    val tasks : LiveData<List<Task>> = _tasks

    fun load() {
        val list = mutableListOf<Task>()
        for (i in 0..10) {
            list.add(Task("${i}", "${'a' + i}"))
        }
        Log.d("TAG", "load")
        _tasks.value = list
    }

}