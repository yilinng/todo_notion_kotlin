package com.example.todonotion.ui.main

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ReportFragment.Companion.reportFragment

import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.todonotion.BaseApplication
import com.example.todonotion.R
import com.example.todonotion.data.token.Token
import com.example.todonotion.overview.auth.TokenViewModel
import com.example.todonotion.overview.auth.AuthNetworkViewModel

import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

//ctrl + o -> override method
//https://stackoverflow.com/questions/44777869/hide-show-bottomnavigationview-on-scroll
//https://stackoverflow.com/questions/10903077/calling-a-fragment-method-from-a-parent-activity
class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var navigationView: NavigationView

    private lateinit var lastAccessToken: String

    private lateinit var circleProgressIndicator: CircularProgressIndicator

    private lateinit var fragmentContainerView: FragmentContainerView

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    // Stores an instance of RegistrationComponent so that its Fragments can access it

    //https://stackoverflow.com/questions/68058302/difference-between-activityviewmodels-and-lazy-viewmodelprovider


    private val tokenViewModel: TokenViewModel by viewModels {
        viewModelFactory
    }

    private val authNetworkViewModel: AuthNetworkViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as BaseApplication).appComponent.userManager().mainComponent!!.inject(this)

        setContentView(R.layout.activity_main)
        // The Toolbar defined in the layout has the id "my_toolbar".
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //https://stackoverflow.com/questions/74545340/android-kotlin-click-event-for-back-button-in-action-bar


        //https://stackoverflow.com/questions/71917856/sethasoptionsmenuboolean-unit-is-deprecated-deprecated-in-java
        // Add menu items without overriding methods in the Activity
        /*
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                //menuInflater.inflate(R.menu.actionbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle the menu selection
                return false
            }
        })

        */
        //navigation drawer with navigation Component
        //https://stackoverflow.com/questions/43805524/how-to-properly-combine-navigationview-and-bottomnavigationview
        //BottomNavigationView with Navigation Component
        //https://www.youtube.com/watch?v=Chso6xrJ6aU&ab_channel=Stevdza-San
        //bottomNavigationView = findViewById(R.id.bottom_navigation)
        navigationView = findViewById(R.id.nav_view)
        //main_content.xml
        circleProgressIndicator = findViewById(R.id.circle_progress_indicator)
        fragmentContainerView = findViewById(R.id.nav_host_fragment)

        lastAccessToken = ""
        // Get the navigation host fragment from this Activity
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        // Instantiate the navController using the NavHostFragment
        navController = navHostFragment.navController
        drawerLayout = findViewById(R.id.drawer_layout)

        //bottomNavigation
        //bottomNavigationView.setupWithNavController(navController)
        //drawer navigation
        navigationView.setupWithNavController(navController)

        //https://developer.android.com/guide/navigation/integrations/ui
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.todoListFragment,
                R.id.todoSearchFragment,
                R.id.postListFragment,
                R.id.loginFragment
            ), drawerLayout
        )

        // appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)
        // Make sure actions in the ActionBar get propagated to the NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        //bottom navigation event
        /*
        listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            val targetId = destination.id
            //bottom navigation is different from destination id

            if (targetId == R.id.todoListFragment) {
                // hideLoadingProgress()
            }
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
        */


        /*
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(Color.parseColor("#FF018786"))
        )

        //https://stackoverflow.com/questions/49644542/how-to-remove-bottom-border-in-android-action-bar
        supportActionBar?.elevation = 0F
        */
        observeInvalidToken()
        observeToken()
        observeUser()

        //click logout button in bottom Navigation
        /*
        bottomNavigationView.findViewById<BottomNavigationItemView>(R.id.logout)
            .setOnClickListener {
                showLogoutDialog()
            }
        */
        //https://stackoverflow.com/questions/9294603/how-do-i-get-the-currently-displayed-fragment
        if ("TodoListFragment" in supportFragmentManager.fragments.last()
                ?.getChildFragmentManager()?.fragments?.get(0).toString()
        ) {
            Log.d(
                "currentFragment ",
                supportFragmentManager.fragments.last()
                    ?.getChildFragmentManager()?.fragments?.get(0).toString()
            )
            navigationView.setCheckedItem(R.id.todoListFragment)
        }

        Log.d(
            "currentFragment_after ",
            supportFragmentManager.fragments.last()?.getChildFragmentManager()?.fragments.toString()
        )
        //click logout button in draw Navigation
        //https://stackoverflow.com/questions/31954993/hide-a-navigation-drawer-menu-item-android
        //default is true
        navigationView.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            if (it.isVisible) {

                showLogoutDialog()
                false
            } else {
                false
            }
        }

        //https://github.com/google-developer-training/basic-android-kotlin-compose-training-workmanager/blob/intermediate/app/src/main/java/com/example/bluromatic/workers/WorkerUtils.kt
        //https://developer.android.com/develop/ui/views/notifications/build-notification
        //https://stackoverflow.com/questions/58526610/what-channelid-should-i-pass-to-the-constructor-of-notificationcompat-builder
        /*
        makeStatusNotification(
            applicationContext.resources.getString(R.string.app_name),
            applicationContext
        )
         */


    }

    //https://stackoverflow.com/questions/61023968/what-do-i-use-now-that-handler-is-deprecated


    @OptIn(DelicateCoroutinesApi::class)
    private fun observeToken() {
        tokenViewModel.tokens.observe(this) {
            if (it.isNotEmpty()) {
                // Toast.makeText(this, "$it have token!!!", Toast.LENGTH_SHORT).show()
                /*
                bottomNavigationView.findViewById<BottomNavigationItemView>(R.id.logout).isVisible =
                    true
                bottomNavigationView.findViewById<BottomNavigationItemView>(R.id.loginFragment).isVisible =
                    false
                 */
                //drawer navigation have to hide sign in
                //https://stackoverflow.com/questions/31954993/hide-a-navigation-drawer-menu-item-android
                navigationView.menu.findItem(R.id.loginFragment).isVisible = false
                navigationView.menu.findItem(R.id.logout).isVisible = true

                //store token in authNetworkViewModel when valid token
                if (authNetworkViewModel.user.value == null && authNetworkViewModel.token.value == null) {
                    Log.d("authNetworkViewModel", "user and token is null")
                    Log.d("authNetworkViewModel", it.toString())

                    lastAccessToken = it[it.size - 1].accessToken

                    GlobalScope.launch {
                        waitLogin(it)
                    }

                }

            } else {
                // Toast.makeText(this, "no exist token!!!", Toast.LENGTH_SHORT).show()
                //when app is reset have to logout if user is login
                /*
                bottomNavigationView.findViewById<BottomNavigationItemView>(R.id.logout).isVisible =
                    false
                bottomNavigationView.findViewById<BottomNavigationItemView>(R.id.loginFragment).isVisible =
                    true
                 */
                //drawer navigation have to show sign in
                navigationView.menu.findItem(R.id.loginFragment).isVisible = true
                navigationView.menu.findItem(R.id.logout).isVisible = false
                authNetworkViewModel.setToken(null)
                tokenViewModel.initUser()
            }
        }
    }


    private fun observeInvalidToken() {
        authNetworkViewModel.error.observe(this) {
            if ("Invalid Token" in it.toString()) {
                authNetworkViewModel.updateToken()
            }

        }

        authNetworkViewModel.token.observe(this) {
            Log.d("observeInvalidToken", "work $lastAccessToken")

            if (lastAccessToken != "" && it != null && it.accessToken == lastAccessToken) {
                Log.d(
                    "observeInvalidToken",
                    "lastAccessToken != \"\" && it!!.accessToken == lastAccessToken"
                )
            }

            if (lastAccessToken != "" && it != null && it.accessToken != lastAccessToken) {
                Log.d(
                    "observeInvalidToken",
                    "lastAccessToken != \"\" && it!!.accessToken != lastAccessToken"
                )
                authNetworkViewModel.getUserAction(it.accessToken)
                tokenViewModel.updateToken(it)
            }
        }

    }


    //https://developer.android.com/guide/topics/resources/string-resource
    private fun observeUser() {
        authNetworkViewModel.user.observe(this) {
            if (it != null) {
                //add new user to tokenViewModel
                tokenViewModel.setUser(it)
                Toast.makeText(this, "$it have user!!!", Toast.LENGTH_SHORT).show()
                val text = String.format(getString(R.string.nav_title1), it.name)
                navigationView.findViewById<TextView>(R.id.drawer_title1).visibility = VISIBLE
                navigationView.findViewById<TextView>(R.id.drawer_title1).text = text
                //hide default title
                navigationView.findViewById<TextView>(R.id.drawer_title_default).visibility = GONE
            } else {
                //show default title
                navigationView.findViewById<TextView>(R.id.drawer_title_default).visibility =
                    VISIBLE
                //hide title1
                navigationView.findViewById<TextView>(R.id.drawer_title1).visibility = GONE
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showLogoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.logout_title))
            .setMessage(getString(R.string.logout_cont))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.cancel)) { _, _ ->
            }
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                GlobalScope.launch {
                    withContext(Dispatchers.Main) {
                        showLoadingProgress()
                        delay(3000)
                        waitLogout()
                    }
                }

            }
            .show()
    }

    //https://developer.android.com/kotlin/coroutines/coroutines-adv

    private suspend fun waitLogout() = coroutineScope {
        val deferredList = listOf(     // fetch three docs at the same time
            async { authNetworkViewModel.logoutAction() },  // async returns a result for the first doc
            async { deleteToken() },
            async { authNetworkViewModel.setToken(null) },
            async { hideLoadingProgress() }
        )
        deferredList.awaitAll()
    }

    private suspend fun waitLogin(it: List<Token>) = coroutineScope {
        val deferredList = listOf(
            // fetch three docs at the same time
            async { authNetworkViewModel.getUserAction(it[it.size - 1].accessToken) },  // async returns a result for the first doc
            async {
                authNetworkViewModel.setToken(
                    Token(
                        id = it[it.size - 1].id,
                        accessToken = it[it.size - 1].accessToken,
                        refreshToken = it[it.size - 1].refreshToken,
                        userId = it[it.size - 1].userId
                    )
                )
            }
        )
        deferredList.awaitAll()
    }


    private fun deleteToken() {
        for (item in tokenViewModel.tokens.value!!) {
            // checking if the entered string matched with any item of our recycler view.
            tokenViewModel.deleteToken(item)
        }
    }


    private fun hideLoadingProgress() {
        circleProgressIndicator.isVisible = false
        fragmentContainerView.isVisible = true
        navigationView.isVisible = true

    }

    private fun showLoadingProgress() {
        circleProgressIndicator.isVisible = true
        fragmentContainerView.isVisible = false
        navigationView.isVisible = false
    }
    /*

     override fun onResume() {
         super.onResume()
         //   navController.addOnDestinationChangedListener(listener)
     }

     override fun onPause() {
         super.onPause()
         //  navController.removeOnDestinationChangedListener(listener)
     }
     */

    /**
     * Enables back button support. Simply navigates one element up on the stack.
     */
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    /*
    //https://www.geeksforgeeks.org/actionbar-in-android-with-example/
    // method to inflate the options menu when
    // the user opens the menu for the first time
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // methods to control the operations that will
    // happen when user clicks on the action buttons
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> Toast.makeText(this, "Search Clicked", Toast.LENGTH_SHORT).show()
            R.id.refresh -> Toast.makeText(this, "Refresh Clicked", Toast.LENGTH_SHORT).show()
            R.id.add -> Toast.makeText(this, "add Todo", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }
     */


}




// Keys for navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
//const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3



