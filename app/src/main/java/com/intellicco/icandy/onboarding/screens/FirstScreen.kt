package com.intellicco.icandy.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.intellicco.icandy.R
import com.intellicco.icandy.databinding.FragmentFirstScreenBinding

class FirstScreen : Fragment() {

    private lateinit var binding:FragmentFirstScreenBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_first_screen, container, false)


        val imageList = arrayListOf<SlideModel>()
       imageList.add(SlideModel(R.drawable.elvis))
        imageList.add(SlideModel(R.drawable.elvis_toon))
        binding.imageSlider.setImageList(imageList,ScaleTypes.CENTER_CROP)

        val  viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager)

        binding.next.setOnClickListener{
            viewPager?.currentItem = 1
        }

        return binding.root
    }

}