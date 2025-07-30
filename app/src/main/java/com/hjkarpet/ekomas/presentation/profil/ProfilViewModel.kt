// presentation/profil/ProfilViewModel.kt
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.hjkarpet.ekomas.domain.model.Masjid
import com.hjkarpet.ekomas.domain.repository.AuthRepository
import com.hjkarpet.ekomas.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


sealed class ProfileState {
    object Loading : ProfileState()
    data class Success(val masjid: Masjid) : ProfileState()
    data class Error(val message: String) : ProfileState()
    data class UpdateSuccess(val message: String) : ProfileState()
}

class ProfilViewModel : ViewModel() {
    private val repository: AuthRepository = AuthRepositoryImpl()
    private val auth = FirebaseAuth.getInstance()

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    fun loadProfile() {
        val user = auth.currentUser
        if (user == null) {
            _profileState.value = ProfileState.Error("User tidak ditemukan.")
            return
        }
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            val result = repository.getMasjidProfile(user.uid)
            result.onSuccess { masjid ->
                if (masjid != null) {
                    _profileState.value = ProfileState.Success(masjid)
                } else {
                    _profileState.value = ProfileState.Error("Profil tidak ditemukan.")
                }
            }.onFailure {
                _profileState.value = ProfileState.Error(it.message ?: "Gagal memuat profil.")
            }
        }
    }

    fun updateProfile(updatedData: Map<String, Any>, newImageUri: Uri?) {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            var finalData = updatedData
            // Jika ada gambar baru, unggah dulu
            if (newImageUri != null) {
                repository.uploadProfileImage(newImageUri).onSuccess { imageUrl ->
                    finalData = updatedData + mapOf("fotoProfilUrl" to imageUrl)
                }.onFailure {
                    _profileState.value = ProfileState.Error(it.message ?: "Gagal unggah foto.")
                    return@launch
                }
            }

            // Update data ke Firestore
            repository.updateMasjidProfile(user.uid, finalData).onSuccess {
                _profileState.value = ProfileState.UpdateSuccess("Profil berhasil diperbarui.")
                loadProfile() // Muat ulang data profil yang baru
            }.onFailure {
                _profileState.value = ProfileState.Error(it.message ?: "Gagal perbarui profil.")
            }
        }
    }
}