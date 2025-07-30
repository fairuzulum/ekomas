package com.hjkarpet.ekomas.presentation.profil

import ProfilViewModel
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.hjkarpet.ekomas.R
import com.hjkarpet.ekomas.core.utils.SecurityPreferences
import com.hjkarpet.ekomas.databinding.FragmentProfilBinding
import com.hjkarpet.ekomas.presentation.auth.LoginActivity
import kotlinx.coroutines.launch

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfilViewModel by viewModels()
    private var newImageUri: Uri? = null

    // Launcher untuk memilih gambar dari galeri
    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            newImageUri = it
            binding.ivProfile.load(it) {
                crossfade(true)
                placeholder(R.drawable.ic_person)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadProfile()
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        // Listener untuk mengubah foto
        binding.ivProfile.setOnClickListener { imagePickerLauncher.launch("image/*") }
        binding.tvChangePhoto.setOnClickListener { imagePickerLauncher.launch("image/*") }

        // Listener untuk tombol update
        binding.btnUpdate.setOnClickListener {
            val updatedData = mapOf(
                "namaMasjid" to binding.etNamaMasjid.text.toString().trim(),
                "telepon" to binding.etTelepon.text.toString().trim(),
                "provinsi" to binding.etProvinsi.text.toString().trim(),
                "kabupatenKota" to binding.etKabupatenKota.text.toString().trim(),
                "alamatLengkap" to binding.etAlamat.text.toString().trim(),
                "namaDkm" to binding.etDkm.text.toString().trim()
            )
            viewModel.updateProfile(updatedData, newImageUri)
            newImageUri = null // Reset URI setelah dikirim ke ViewModel
        }

        // Listener untuk tombol logout
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileState.collect { state ->
                // Atur visibilitas progress bar
                binding.progressBar.visibility = if (state is ProfileState.Loading) View.VISIBLE else View.GONE

                when(state) {
                    is ProfileState.Success -> {
                        val masjid = state.masjid
                        // Isi semua field dengan data dari Firestore
                        binding.etNamaMasjid.setText(masjid.namaMasjid)
                        binding.etEmail.setText(masjid.email)
                        binding.etTelepon.setText(masjid.telepon)
                        binding.etProvinsi.setText(masjid.provinsi)
                        binding.etKabupatenKota.setText(masjid.kabupatenKota)
                        binding.etAlamat.setText(masjid.alamatLengkap)
                        binding.etDkm.setText(masjid.namaDkm)

                        // Muat gambar profil menggunakan Coil
                        binding.ivProfile.load(masjid.fotoProfilUrl) {
                            crossfade(true)
                            placeholder(R.drawable.ic_person)
                            error(R.drawable.ic_person)
                        }
                    }
                    is ProfileState.UpdateSuccess -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    is ProfileState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> { /* State lain seperti Idle atau Loading */ }
                }
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Apakah Anda yakin ingin keluar dari akun ini?")
            .setPositiveButton("Ya") { _, _ ->
                // Logout dari Firebase
                FirebaseAuth.getInstance().signOut()
                // Hapus PIN yang tersimpan di lokal
                SecurityPreferences(requireContext()).clearPin()

                // Arahkan ke halaman Login dan hapus semua history activity
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Mencegah memory leak pada Fragment
    }
}