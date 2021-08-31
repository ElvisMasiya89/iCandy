package com.intellicco.icandy.gallery

import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.intellicco.icandy.R
import com.intellicco.icandy.databinding.FragmentGalleryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryFragment: Fragment() {
    private lateinit var externalStoragePhotoAdapter: SharedPhotoAdapter
    private var readPermissionGranted = false
    private var writePermissionGranted: Boolean = false
    private lateinit var permissionsLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var contentObserver: ContentObserver



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true) // enable toolbar

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_gallery, container, false)

        externalStoragePhotoAdapter = SharedPhotoAdapter ( activity?.applicationContext, SharedPhotoAdapter.OnClickListener{
            findNavController().navigate(GalleryFragmentDirections.actionGalleryFragmentToCroppingFragment(it.contentUri,null))

        })

        setupExternalStorageRecyclerView()
        initContentObserver()

        permissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions->

            readPermissionGranted= permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE]?: readPermissionGranted
            writePermissionGranted= permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]?: writePermissionGranted

            if(readPermissionGranted){
                loadPhotosFromExternalStorageIntoRecyclerView()
            }else{
                Toast.makeText(activity, "Cant read files without permission.", Toast.LENGTH_LONG).show()

            }

        }

        updateOrRequestPermission()

        val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){
            if (it==null){
                Toast.makeText(activity, "Image capture Unsuccessful", Toast.LENGTH_LONG).show()
            }else{
                findNavController().navigate(GalleryFragmentDirections.actionGalleryFragmentToCroppingFragment(null, it))
             }


        }


        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_camera -> {
                    takePhoto.launch()
                    true
                }

                R.id.nav_gallery->{selectImageFromGalleryResult.launch("image/*")
                         true
                }

                else -> false
            }
        }


        loadPhotosFromExternalStorageIntoRecyclerView()

        return binding.root
    }


    private fun initContentObserver(){
        contentObserver = object : ContentObserver(null){
            override fun onChange(selfChange: Boolean) {
                if (readPermissionGranted){
                    loadPhotosFromExternalStorageIntoRecyclerView()
                }
            }
        }
        activity?.contentResolver?.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }


    private suspend fun loadPhotosFromExternalStorage():List<SharedStoragePhoto>{

        var counter = 0
        //Media store is  database for all media
        return withContext(Dispatchers.IO){
            val collection = sdk29AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = sdk29AndUp {   arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.RELATIVE_PATH)}?:arrayOf(

                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.HEIGHT,
                MediaStore.Images.Media.WIDTH
            )




            val selection = sdk29AndUp {"${MediaStore.MediaColumns.RELATIVE_PATH} LIKE ?"}
            val selectionArgs = sdk29AndUp { arrayOf("DCIM/Camera%") }
            val sortOrder = sdk29AndUp { "${MediaStore.Images.Media.DATE_ADDED} DESC"}


            val photos = mutableListOf<SharedStoragePhoto>()

            activity?.contentResolver?.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use {cursor ->

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)



                while (cursor.moveToNext() && counter <= 100){
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val height = cursor.getInt(heightColumn)
                    val width = cursor.getInt(widthColumn)
                    val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,id)

                    photos.add(SharedStoragePhoto(id, displayName, width, height, contentUri))
                    counter++
                }
                photos.toList()
            }?: listOf()
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


    private fun setupExternalStorageRecyclerView() = binding.rvPublicPhotos.apply {
        adapter = externalStoragePhotoAdapter
        layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
    }




    private fun loadPhotosFromExternalStorageIntoRecyclerView(){
        lifecycleScope.launch {
            val photos = loadPhotosFromExternalStorage()
            val sublistPhotos:List<SharedStoragePhoto> = if(photos.size>=200) {
                photos.subList(0, 200)//THIS IS A TEMPORARY FIX CAN RESULT IN OUT OF
            }else{
                photos
            }
            externalStoragePhotoAdapter.submitList(sublistPhotos)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        with(inflater) { inflate(R.menu.top_app_bar, menu) }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
          R.id.settings_button -> findNavController().navigate(GalleryFragmentDirections.actionGalleryFragmentToSettingsFragment())

        }
        return super.onOptionsItemSelected(item)
    }

    private val selectImageFromGalleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
       uri?.let { findNavController().navigate(GalleryFragmentDirections
           .actionGalleryFragmentToCroppingFragment(uri,null)) }
    }



    override fun onStart() {
        super.onStart()
        (activity as? AppCompatActivity)?.supportActionBar?.title = "iCandy"
        (activity as? AppCompatActivity)?.supportActionBar?.show()


    }


}
