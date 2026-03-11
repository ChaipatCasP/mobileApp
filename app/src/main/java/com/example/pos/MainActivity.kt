package com.example.pos

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.example.pos.databinding.ActivityMainBinding
import com.example.pos.service.TokenManager
import com.example.pos.service.auth.AuthService

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // เริ่มต้น TokenManager ก่อนใช้งาน API
        TokenManager.init(applicationContext)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        // Only nav_home shows the toolbar + open drawer
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Auto-skip login if token already saved
        if (TokenManager.isLoggedIn()) {
            navController.navigate(
                R.id.action_loginFragment_to_nav_home,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build()
            )
        }

        // Handle drawer item selection — intercept logout
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    drawerLayout.closeDrawer(Gravity.START)
                    showLogoutConfirmDialog()
                    true
                }
                else -> {
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawer(Gravity.START)
                    true
                }
            }
        }

        // Hide toolbar and lock drawer on all screens except nav_home
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val showToolbar = destination.id == R.id.nav_home
            binding.appBarMain.toolbar.visibility =
                if (showToolbar) View.VISIBLE else View.GONE
            drawerLayout.setDrawerLockMode(
                if (showToolbar) DrawerLayout.LOCK_MODE_UNLOCKED
                else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            )
        }
    }

    private fun showLogoutConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout_confirm_title))
            .setMessage(getString(R.string.logout_confirm_message))
            .setPositiveButton(getString(R.string.confirm)) { _, _ -> performLogout() }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun performLogout() {
        AuthService().logout()
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        navController.navigate(
            R.id.loginFragment,
            null,
            androidx.navigation.NavOptions.Builder()
                .setPopUpTo(R.id.mobile_navigation, true)
                .build()
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}