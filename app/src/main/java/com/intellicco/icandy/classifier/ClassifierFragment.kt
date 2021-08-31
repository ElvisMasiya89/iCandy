package com.intellicco.icandy.classifier

import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.intellicco.icandy.R
import com.intellicco.icandy.databinding.FragmentClassifierBinding
import com.intellicco.icandy.ml.WhiteboxCartoonGanDr
import kotlinx.coroutines.*
import org.tensorflow.lite.support.image.TensorImage
import java.util.*


class ClassifierFragment : Fragment() {

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)
    private lateinit var binding: FragmentClassifierBinding
    private lateinit var args: ClassifierFragmentArgs
    private lateinit var outputImg: Bitmap
    private lateinit var shareItem:MenuItem
    private var imgHeight = 0
    private var imgWidth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true) // enable toolbar

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_classifier, container, false)


        //Passing the arguments
        args = arguments?.let { ClassifierFragmentArgs.fromBundle(it)}!!

        //Getting the dimensions of the image
        imgHeight = args.inputBmp.height
        imgWidth = args.inputBmp.width

        Glide.with(binding.imageviewOutput.context).load(args.inputBmp).into(binding.imageviewOutput)
        //binding.imageviewOutput.setImageBitmap((args.inputBmp))
        binding.ProgressBar.progress

        coroutineScope.launch(Dispatchers.Main) {
            val (outputBitmap) = getOutputAsync(args.inputBmp).await()

            //Resizing the processed image using its original dimensions
            val resizedBitmap = Bitmap.createScaledBitmap(outputBitmap, imgWidth, imgHeight, false)
            updateUI(resizedBitmap)
            outputImg=resizedBitmap
            shareItem.isVisible = true

        }
        return binding.root
    }


    private fun getOutputAsync(bitmap: Bitmap): Deferred<Pair<Bitmap, Long>> =
        // use async() to create a coroutine in an IO optimized Dispatcher for model inference
        coroutineScope.async(Dispatchers.IO) {

            //val options = Model.Options.Builder().setDevice(Model.Device.GPU).setNumThreads(4).build()
            val sourceImage = TensorImage.fromBitmap(bitmap)
            val startTime = SystemClock.uptimeMillis()
            val cartoonizedImage: TensorImage = inferenceWithDrModel(sourceImage)

            val inferenceTime = SystemClock.uptimeMillis() - startTime
            val cartoonizedImageBitmap = cartoonizedImage.bitmap

            return@async Pair(cartoonizedImageBitmap, inferenceTime)
        }

    private fun inferenceWithDrModel(sourceImage: TensorImage): TensorImage {
        val model = WhiteboxCartoonGanDr.newInstance(requireContext())
        val outputs = model.process(sourceImage)
        val cartoonizedImage = outputs.cartoonizedImageAsTensorImage
        model.close()
        return cartoonizedImage
    }


    private fun updateUI(outputBitmap: Bitmap) {
        binding.ProgressBar.visibility = View.GONE
        Glide.with(binding.imageviewOutput.context).load(outputBitmap).into(binding.imageviewOutput)
        //binding.imageviewOutput.setImageBitmap(outputBitmap)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        with(inflater) { inflate(R.menu.classifier_app_bar, menu) }
        shareItem = menu.findItem(R.id.nav_share)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_share->{//val bitmap:Bitmap = binding.imageviewOutput.drawable.toBitmap();
            findNavController().navigate(ClassifierFragmentDirections.actionClassifierFragmentToSharingFragment(outputImg))

            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onDestroy() {
        super.onDestroy()
        // clean up coroutine job
        parentJob.cancel()
    }

    override fun onStart() {
        super.onStart()
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Styling"
    }




}