package com.hjkarpet.ekomas

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.hjkarpet.ekomas.databinding.ActivityMainBinding
import com.hjkarpet.ekomas.presentation.beranda.BerandaFragment
import com.hjkarpet.ekomas.presentation.eksplor.EksplorFragment // <-- Import baru
import com.hjkarpet.ekomas.presentation.profil.ProfilFragment
import com.hjkarpet.ekomas.presentation.analitik.AnalitikFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            replaceFragment(BerandaFragment())
            binding.bottomNavigation.selectedItemId = R.id.nav_beranda
        }

        setupBottomNavigation()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_beranda -> {
                    replaceFragment(BerandaFragment())
                    true
                }
                R.id.nav_eksplor -> { // <-- Tambahkan case ini
                    replaceFragment(EksplorFragment())
                    true
                }
                R.id.nav_profil -> {
                    replaceFragment(ProfilFragment())
                    true
                }
                R.id.nav_analitik -> {
                    replaceFragment(AnalitikFragment())
                    true
                }
                else -> false
            }
        }
    }
}