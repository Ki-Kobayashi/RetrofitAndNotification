package com.example.retrofitdemo.pages.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitdemo.domain.repository.AlbumDataRepository
import com.example.retrofitdemo.domain.repository.AlbumUIState
import com.example.retrofitdemo.domain.repository.ApiDogRepository
import com.example.retrofitdemo.model.Albums
import com.example.retrofitdemo.model.AlbumsItem
import com.example.retrofitdemo.model.user.UserRequestDto
import com.example.retrofitdemo.model.user.UserResponseDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

/**
 * Created by K.Kobayashi on 2023/07/20.
 */
@HiltViewModel
class TopViewModel @Inject constructor(
    private val albumRepository: AlbumDataRepository,
    private val userRepository: ApiDogRepository

) : ViewModel() {
    private var _albums = MutableStateFlow<AlbumUIState>(AlbumUIState.Success(null))
    val albums: StateFlow<AlbumUIState> = _albums

    private var _albumState = MutableStateFlow<AlbumsItem?>(null)
    val albumState: StateFlow<AlbumsItem?> = _albumState

    private var _userInfo = MutableStateFlow<UserResponseDto?>(null)
    val userInfo: StateFlow<UserResponseDto?> = _userInfo

    fun getAlbums() {
        viewModelScope.launch {
            val data = albumRepository.getAlbumsData()
            Timber.d("aaaaaa : $data")
            _albums.value = AlbumUIState.Success(data)
        }
    }

    fun getAlbumListByUserId(userId: Int) {
        viewModelScope.launch {
            val response = albumRepository.findAlbumListByUserId(userId = userId)
            _albums.value = AlbumUIState.Success(response)
        }
    }

    fun getAlbumById(albumId: Int) {
        viewModelScope.launch {
            val album = albumRepository.getAlbumById(albumId)
            _albumState.value = album
        }
    }

    fun getUserInfo(userId: Int) {
        val req = UserRequestDto(userId = userId)
        viewModelScope.launch {
            val user = userRepository.fetchUser(req)
            Timber.d("${user}")
            _userInfo.value = user
        }
    }
}

