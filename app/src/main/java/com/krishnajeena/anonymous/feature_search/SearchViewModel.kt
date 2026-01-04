package com.krishnajeena.anonymous.feature_search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnajeena.anonymous.data.FirestoreUserRepository
import com.krishnajeena.anonymous.domain.user.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepository: FirestoreUserRepository
) : ViewModel() {

    var query by mutableStateOf("")
        private set

    var results by mutableStateOf<List<User>>(emptyList())
        private set

    fun onQueryChange(newQuery: String) {
        query = newQuery

        viewModelScope.launch {
            results = userRepository.searchUsersByTag(newQuery.trim())
        }
    }
}
