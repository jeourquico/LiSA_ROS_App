package com.example.lisaapp.sub

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SSHViewModel : ViewModel() {

    var ipAddressInit = "192.168.1.100"
    var usernameInit = "lisa"
    var passwordInit = "lisa1234"

    private val _ipAddress = MutableLiveData<String>()
    val ipAddress: LiveData<String> get() = _ipAddress

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> get() = _password

    init {
        // Initial values should be set using postValue if there is a possibility of running in a background thread.
        _ipAddress.postValue(ipAddressInit)
        _username.postValue(usernameInit)
        _password.postValue(passwordInit)
    }

    // Function to update credentials
    fun updateCredentials(ip: String, user: String, pass: String) {
        // Ensure this runs on the main thread
        viewModelScope.launch(Dispatchers.Main) {
            _ipAddress.value = ip
            _username.value = user
            _password.value = pass
        }
    }
}
