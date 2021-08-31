package com.intellicco.icandy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.intellicco.icandy.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {

    private lateinit var binding:FragmentSplashBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_splash, container, false)


        if (onBoardingFinished()){
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToGalleryFragment())
        }else{
            findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToViewPagerFragment())
        }
        return binding.root

    }

    private fun onBoardingFinished():Boolean{
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)

        return sharedPref.getBoolean("Finished",false)

    }

}