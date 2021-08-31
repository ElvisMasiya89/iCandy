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
import com.intellicco.icandy.databinding.FragmentSecondScreenBinding


class SecondScreen : Fragment() {

  private lateinit var binding:FragmentSecondScreenBinding




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_second_screen, container, false)

        val imageList = arrayListOf<SlideModel>()
        imageList.add(SlideModel(R.drawable.almeida, title = "Create Candy for your Eyes with our Cartoon filter"))
        imageList.add(SlideModel(R.drawable.almeida_edited ,title = "Create Candy for your Eyes with our Cartoon filter"))
        binding.imageSlider2.setImageList(imageList, ScaleTypes.CENTER_CROP)


        val  viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager)

        binding.next2.setOnClickListener{
            viewPager?.currentItem = 2
        }

        return binding.root
    }


}