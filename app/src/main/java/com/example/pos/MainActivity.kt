package com.example.pos

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
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
import com.google.android.material.snackbar.Snackbar
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

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Handle drawer item selection — intercept logout
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_logout -> {
                    drawerLayout.closeDrawer(Gravity.LEFT)
                    showLogoutConfirmDialog()
                    true
                }
                else -> {
                    // ให้ Navigation Component จัดการ fragment อื่นตามปกติ
                    val handled = navView.menu.findItem(menuItem.itemId) != null
                    navController.navigate(menuItem.itemId)
                    drawerLayout.closeDrawer(Gravity.LEFT)
                    handled
                }
            }
        }

        // Hide toolbar, FAB, and lock drawer on the login/order screen
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.nav_order || destination.id == R.id.nav_inventory) {
                binding.appBarMain.toolbar.visibility = View.GONE
                binding.appBarMain.fab.visibility = View.GONE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                binding.appBarMain.toolbar.visibility = View.VISIBLE
                binding.appBarMain.fab.visibility = View.VISIBLE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }
    }

    private fun showLogoutConfirmDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logout_confirm_title))
            .setMessage(getString(R.string.logout_confirm_message))
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                performLogout()
            }
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