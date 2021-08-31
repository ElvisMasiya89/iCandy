package com.intellicco.icandy.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.intellicco.icandy.R
import com.intellicco.icandy.databinding.FragmentViewPagerBinding
import com.intellicco.icandy.onboarding.screens.FirstScreen
import com.intellicco.icandy.onboarding.screens.SecondScreen
import com.intellicco.icandy.onboarding.screens.ThirdScreen


class ViewPagerFragment : Fragment() {

    private lateinit var binding:FragmentViewPagerBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_view_pager, container, false)


        val fragmentList = arrayListOf(
            FirstScreen(),
            SecondScreen(),
            ThirdScreen()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter


        return binding.root
    }


    override fun onStart() {
        super.onStart()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }


}