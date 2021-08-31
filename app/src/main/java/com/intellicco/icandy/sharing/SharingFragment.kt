package com.intellicco.icandy.sharing

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.intellicco.icandy.R
import com.intellicco.icandy.databinding.FragmentSharingBinding
import com.intellicco.icandy.gallery.sdk29AndUp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class SharingFragment : Fragment() {


    private var readPermissionGranted = false
    private var writePermissionGranted: Boolean = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var  binding: FragmentSharingBinding



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_sharing, container, false)

        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions->
            readPermissionGranted= permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE]?: readPermissionGranted
            writePermissionGranted= permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]?: writePermissionGranted

        }

        updateOrRequestPermission()

        val args = arguments?.let { SharingFragmentArgs.fromBundle(it)}

        Glide.with(binding.sharingImg.context).load(args?.outPutImg).into(binding.sharingImg)

        binding.btnSave.setOnClickListener {
            lifecycleScope.launch {

                val isSavedSuccessfully = when {
                    writePermissionGranted -> savePhotoToExternalStorage("iCandy" +
                        UUID.randomUUID().toString(), args?.outPutImg!!
                    )
                    else -> false
                }

                if (isSavedSuccessfully) {
                    sdk29AndUp {Toast.makeText(activity, "Photo saved to Pictures/iCandy", Toast.LENGTH_LONG).show() }?:
                    Toast.makeText(activity, "Photo saved to Pictures", Toast.LENGTH_LONG).show()


                } else {
                    Toast.makeText(activity, "Fail to save photo", Toast.LENGTH_SHORT).show()
                }
            }

        }

        //Sharing Image with other Apps
        binding.Other.setOnClickListener{
            lifecycleScope.launch {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    putExtra(Intent.EXTRA_STREAM, saveImage(args?.outPutImg))
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                shareIntent.type = "image/png"
                startActivity(Intent.createChooser(shareIntent, "send to"))
            }
        }

        return binding.root
    }


    private suspend fun savePhotoToExternalStorage(displayName: String, bmp : Bitmap):Boolean{

        return withContext(Dispatchers.IO){
            val imageCollection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val contentValues = sdk29AndUp {   ContentValues().apply{
                put(MediaStore.Images.Media.DISPLAY_NAME,"$displayName.jpg")
                put(MediaStore.Images.Media.MIME_TYPE,"image/jpg")
                put(MediaStore.Images.Media.WIDTH,bmp.width)
                put(MediaStore.Images.Media.HEIGHT,bmp.height)
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/iCandy/")
            }
            }?:ContentValues().apply{
                put(MediaStore.Images.Media.DISPLAY_NAME,"$displayName.jpg")
                put(MediaStore.Images.Media.MIME_TYPE,"image/jpg")
                put(MediaStore.Images.Media.WIDTH,bmp.width)
                put(MediaStore.Images.Media.HEIGHT,bmp.height)

            }

            try{
                val activity = activity
                (activity?.contentResolver)?.insert(imageCollection,contentValues)?.also { uri ->
                    activity.contentResolver?.openOutputStream(uri).use { outputStream ->
                        if (!bmp.compress(Bitmap.CompressFormat.JPEG, 100,outputStream)) {
                            throw IOException("Couldn't save bitmap.")

                        }
                    }

                   // path = uri.path.toString()
                }?: throw IOException("Couldn't create MediaStore entry")


                true

            }catch (e: IOException){
                e.printStackTrace()
                false
            }

        }
    }


    private fun updateOrRequestPermission(){
        val hasReadPermission = ContextCompat.checkSelfPermission(
            activity?.applicationContext!!,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED

        val hasWritePermission = ContextCompat.checkSelfPermission(
            activity?.applicationContext!!,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED

        val minSdk29 = Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q

        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()

        if(!writePermissionGranted){
            permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(!readPermissionGranted){
            permissionsToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if(permissionsToRequest.isNotEmpty()){
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())

        }
    }



    //method for saving shared image into stream
    private suspend fun  saveImage(image: Bitmap?): Uri? {
        return withContext(Dispatchers.IO){
        val imagesFolder = File(activity?.cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "shared_image.png")
            val stream = FileOutputStream(file)
            image?.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = activity?.let { FileProvider.getUriForFile(it, "com.domain.fileprovider", file) }
        } catch (e: IOException) {
            Log.d(TAG, "IOException while trying to write file for sharing: " + e.message)
        }
            uri}
      }



    override fun onStart() {
        super.onStart()
        (activity as? AppCompatActivity)?.supportActionBar?.title = "Share"
    }


    companion object {
        private const val TAG = "CartoonFragment"
    }

}