package com.intellicco.icandy.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.intellicco.icandy.R
import com.intellicco.icandy.databinding.FragmentCameraBinding


class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false)

        return  binding.root
    }

}