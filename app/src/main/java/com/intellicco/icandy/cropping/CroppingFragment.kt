package com.intellicco.icandy.cropping
import android.graphics.Bitmap
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.intellicco.icandy.R
import com.intellicco.icandy.databinding.FragmentCroppingBinding
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.IOException


class CroppingFragment : Fragment() {

   private lateinit var binding:FragmentCroppingBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true) // enable toolbar
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cropping, container, false)
        val args = arguments?.let { CroppingFragmentArgs.fromBundle(it) }

        binding.cropImageView.guidelines = CropImageView.Guidelines.ON
        binding.cropImageView.cropShape = CropImageView.CropShape.RECTANGLE
        binding.cropImageView.scaleType = CropImageView.ScaleType.FIT_CENTER
        binding.cropImageView.isAutoZoomEnabled = true
        //binding.cropImageView.isShowProgressBar = true
        binding.cropImageView.rotateImage(90)

        try {
            if (args?.imageUri != null) {

                binding.cropImageView.setImageUriAsync(args.imageUri)

            } else {
                binding.cropImageView.setImageBitmap(args?.imageBmp)

            }

        }catch (e:IOException){
            //print(e.stackTrace)
            Toast.makeText(activity, "Failed to Load Image", Toast.LENGTH_LONG).show()
        }



        return binding.root
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        with(inflater) { inflate(R.menu.cropping_app_bar, menu) }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
             R.id.nav_rotate->binding.cropImageView.rotateImage(90)

             R.id.nav_flip->binding.cropImageView.flipImageHorizontally()

             R.id.nav_crop ->{val bitmap: Bitmap = binding.cropImageView.croppedImage
             findNavController().navigate(CroppingFragmentDirections.actionCroppingFragmentToClassifierFragment(bitmap))}

        }
        return super.onOptionsItemSelected(item)

    }

    override fun onStart() {
        super.onStart()
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Crop"
//        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
//
//        val  toolbar =  (activity as? AppCompatActivity)?.findViewById<Toolbar>(R.id.topAppBar)
//        toolbar?.setNavigationOnClickListener {
//           requireActivity().onBackPressed()
//        }




    }

}


