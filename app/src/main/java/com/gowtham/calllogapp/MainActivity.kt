package com.gowtham.calllogapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gowtham.calllogapp.databinding.ActivityMainBinding
import com.gowtham.calllogapp.fragments.phonebook.FPhoneBookViewModel
import com.gowtham.calllogapp.utils.ContactScreenState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private val shareViewModel by viewModels<FPhoneBookViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        setDataInView()
    }

    private fun setDataInView() {
        navController = Navigation.findNavController(binding.navHostFragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            onDestinationChanged()
        }
        binding.bottomNav.setOnNavigationItemSelectedListener(onBottomNavigationListener)
    }

    private fun onDestinationChanged() {
        if(navController.isValidDestination(R.id.FCalls))
            binding.bottomNav.selectedItemId = R.id.nav_calls
        else
            binding.bottomNav.selectedItemId = R.id.nav_contacts
    }

    private val onBottomNavigationListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                if(item.itemId==R.id.nav_calls){
                    val navOptions =NavOptions.Builder().setPopUpTo(R.id.nav_host_fragment, true).build()
                    if (isNotSameDestination(R.id.FCalls)) {
                        onBackPressed()
                    }
                }else if (isNotSameDestination(R.id.FPhoneBook)) {
                      shareViewModel.setState(ContactScreenState.IdleState)
                        Navigation.findNavController(this, R.id.nav_host_fragment)
                                .navigate(R.id.FPhoneBook)
                    }
                true
            }

    private fun isNotSameDestination(destination: Int): Boolean {
        return destination != navController.currentDestination!!.id
    }


    override fun onBackPressed() {
        if(navController.isValidDestination(R.id.FCalls))
            finish()
        else {
            super.onBackPressed()
        }
    }
}