package com.intellicco.icandy.onboarding.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.intellicco.icandy.R
import com.intellicco.icandy.databinding.FragmentThirdScreenBinding


class ThirdScreen : Fragment() {

    private lateinit var binding: FragmentThirdScreenBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_third_screen, container, false)

        val imageList = arrayListOf<SlideModel>()
        imageList.add(SlideModel(R.drawable.mel, title = "Share with Friends"))
        imageList.add(SlideModel(R.drawable.mel_edited ,title = "Share with Friends"))
        binding.imageSlider3.setImageList(imageList, ScaleTypes.CENTER_CROP)

        binding.finish.setOnClickListener{
            findNavController().navigate(R.id.action_viewPagerFragment_to_galleryFragment)
            onBoardingFinished()
        }

        binding.privacy.setOnClickListener {
            gotoURL("https://sites.google.com/view/intellicco/home")
        }

        binding.terms.setOnClickListener{
            gotoURL("https://sites.google.com/view/intellicco/terms_and_conditions")
        }


        return binding.root
    }


    private fun gotoURL(s: String) {
        val uri: Uri = Uri.parse(s)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    private fun onBoardingFinished(){
        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("Finished", true)
        editor.apply()
    }


}