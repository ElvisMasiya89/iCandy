package com.intellicco.icandy.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.intellicco.icandy.R
import com.intellicco.icandy.databinding.FragmentSettingsBinding


class SettingsFragment : Fragment() {

    private lateinit var binding:FragmentSettingsBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_settings, container, false)



        binding.privacyPolicy.setOnClickListener {

           gotoURL("https://sites.google.com/view/intellicco/home")
        }
        binding.termsAndConditions.setOnClickListener{

            gotoURL("https://sites.google.com/view/intellicco/terms_and_conditions")

        }

        return binding.root
    }

    private fun gotoURL(s: String) {

       val uri:Uri = Uri.parse(s)
        startActivity(Intent(Intent.ACTION_VIEW, uri))

    }

    override fun onStart() {
        super.onStart()
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Settings"



    }

}