package com.example.todonotion

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.todonotion.overview.auth.AuthNetworkViewModel
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.overview.auth.TokenViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationItemView

import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


//ctrl + o -> override method
//https://stackoverflow.com/questions/44777869/hide-show-bottomnavigationview-on-scroll
class MainActivity : AppCompatActivity() {

    /*
    * The lateinit keyword is something new.
    * It's a promise that your code will initialize the variable before using it. If you don't, your app will crash
    */
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var listener: NavController.OnDestinationChangedListener

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var navigationView: NavigationView

    private val tokenViewModel: TokenViewModel by viewModels {
        TokenViewModelFactory(
            (application as BaseApplication).database.tokenDao()
        )
    }

    private val authNetworkViewModel: AuthNetworkViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //navigation drawer with navigation Component
        //https://stackoverflow.com/questions/43805524/how-to-properly-combine-navigationview-and-bottomnavigationview
        //BottomNavigationView with Navigation Component
        //https://www.youtube.com/watch?v=Chso6xrJ6aU&ab_channel=Stevdza-San
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        navigationView = findViewById(R.id.nav_view)
        //appBarConfiguration = AppBarConfiguration(setOf(R.id.todoListFragment, R.id.todoSearchFragment, R.id.loginFragment))
        // Get the navigation host fragment from this Activity
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // Instantiate the navController using the NavHostFragment
        navController = navHostFragment.navController
        drawerLayout = findViewById(R.id.drawer_layout)

        //bottomNavigation
        bottomNavigationView.setupWithNavController(navController)
        //drawer navigation
        navigationView.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        // Make sure actions in the ActionBar get propagated to the NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        //bottom navigation event
        listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val targetId = destination.id
            //bottom navigation is different from destination id
           // bottomNavigationView.isVisible = targetId == R.id.todoListFragment && targetId == R.id.todoSearchFragment
            observeToken()
            observeUser()


            /*
            Toast.makeText(
                this,
                destination.id.toString() + " is selected " + destination.label,
                Toast.LENGTH_SHORT
            ).show()
             */
        }

        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(Color.parseColor("#FF018786"))
        )

        //https://stackoverflow.com/questions/49644542/how-to-remove-bottom-border-in-android-action-bar
        supportActionBar?.elevation = 0F

        observeToken()
        observeUser()

        //click logout button in bottom Navigation
        bottomNavigationView.findViewById<BottomNavigationItemView>(R.id.logout)
            .setOnClickListener {
                showLogoutDialog()
            }


        /*
        //click logout button in draw Navigation
        navigationView.findViewById<NavigationView>(R.id.logout)
            .setOnClickListener {
                showLogoutDialog()
            }
        */
    }

    //https://stackoverflow.com/questions/61023968/what-do-i-use-now-that-handler-is-deprecated
    private fun observeToken() {
        tokenViewModel.tokens.observe(this, Observer {

            lifecycleScope.launch {
                hideLoadingProgress()
                delay(3000)
                showLoadingProgress()
            }

            if (it.isNotEmpty()) {

                Toast.makeText(this, "have token!!!", Toast.LENGTH_SHORT).show()

                bottomNavigationView.findViewById<BottomNavigationView>(R.id.logout).isVisible = true
                bottomNavigationView.findViewById<BottomNavigationView>(R.id.loginFragment).isVisible = false
                //drawer navigation have to hide sign in
                navigationView.findViewById<NavigationView>(R.id.loginFragment).isVisible = false
                //navigationView.findViewById<NavigationView>(R.id.logout).isVisible = true

                /*
                Toast.makeText(
                    this,
                    it[0].accessToken + " accessToken",
                    Toast.LENGTH_SHORT
                ).show()
                */
                //getUser use token
                authNetworkViewModel.getUserAction(it[0].accessToken)
            } else {
                bottomNavigationView.findViewById<BottomNavigationView>(R.id.logout).isVisible = false
                bottomNavigationView.findViewById<BottomNavigationView>(R.id.loginFragment).isVisible = true
                //drawer navigation have to show sign in
                navigationView.findViewById<NavigationView>(R.id.loginFragment).isVisible= true
                //navigationView.findViewById<NavigationView>(R.id.logout).isVisible = false
            }
        })
    }

    //https://developer.android.com/guide/topics/resources/string-resource
    private fun observeUser() {
        authNetworkViewModel.user.observe(this, Observer {

            if (it.username.isNotEmpty()) {
                val text = String.format(getString(R.string.nav_title1), it.username)
                navigationView.findViewById<TextView>(R.id.drawer_title1).text =
                    text//"Hello, " + it.username
            } else {
                val text = String.format(getString(R.string.nav_title1), "")
                navigationView.findViewById<TextView>(R.id.drawer_title1).text =
                    text//"Hello, " + it.username
            }
        })
    }


    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.logout_cont))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                //logout action, clear token
                // setLoadingProgress()
                deleteToken()
            }
            .show()
    }


    private fun deleteToken() {
        for (item in tokenViewModel.tokens.value!!) {
            // checking if the entered string matched with any item of our recycler view.
            tokenViewModel.deleteToken(item)
        }
    }

    private fun hideLoadingProgress() {
        bottomNavigationView.isVisible = false
    }

    private fun showLoadingProgress() {
        bottomNavigationView.isVisible = true

    }

    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onPause() {
        super.onPause()
        navController.removeOnDestinationChangedListener(listener)
    }


    // method to inflate the options menu when
    // the user opens the menu for the first time

    /*
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }
    */
    // methods to control the operations that will
    // happen when user clicks on the action buttons
    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        /*
        when (item.itemId) {
            R.id.search -> Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show()
            R.id.refresh -> Toast.makeText(this, "Refresh Clicked", Toast.LENGTH_SHORT).show()
            R.id.more -> Toast.makeText(this, "More Clicked", Toast.LENGTH_SHORT).show()
            R.id.home -> Toast.makeText(this, "home Clicked", Toast.LENGTH_SHORT).show()

        }

         */
        return super.onOptionsItemSelected(item)
    }
    */

    /**
     * Enables back button support. Simply navigates one element up on the stack.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}




