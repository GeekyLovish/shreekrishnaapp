package com.raman.kumar.shrikrishan.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.Calendar


object ImageSelectionHelper {
    const val image_file="image/*"

    var permissions1 = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    var c_permissions1 = arrayOf(
        Manifest.permission.CAMERA
    )
    var permissions = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES
    )

    var c_permissions = arrayOf(
        Manifest.permission.CAMERA
    )
    val GALLERY = 1
    val CAMERA: Int = 2

    var currentSelection=""
    lateinit var context: Activity
    lateinit var listener: CommonListeners
    var filePath: Uri? = null
    var byteArray: ByteArray? = null
    lateinit var tempFile: File
    lateinit var bitmap: Bitmap
    fun init(context: Activity, Choice: String, listener: CommonListeners) {
        this.context = context
        this.listener = listener
        this.currentSelection = Choice
        if (checkPermissions(Choice)) {
            ImageSelection(Choice)
        }
    }

    private fun ImageSelection(Choice: String) {
        if (Choice == "Gallery") {
            val galleryIntent = Intent()
            galleryIntent.type = image_file
            galleryIntent.action = Intent.ACTION_PICK
            context?.startActivityForResult(
                Intent.createChooser(
                    galleryIntent,
                    "Select Image"
                ), GALLERY
            )
        } else {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.TITLE, "New Picture")
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
            val contentResolver= context.contentResolver
            contentResolver
            filePath = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath)
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivityForResult(intent, CAMERA)
            }
        }
    }

    @Throws(IOException::class)
    private fun getBitmapFromUri(uri: Uri): Bitmap {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            context?.getContentResolver()?.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor?.fileDescriptor
        val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor?.close()
        return image
    }

    fun Uri.getOrientation():Int{
        var orientation =0
        try {
            context.contentResolver.openInputStream(this).use { inputStream ->
                val exif = ExifInterface(inputStream!!)
                orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_TRANSPOSE
                    )
            }
        } catch (e: IOException) {
            orientation=0
        }
        return orientation
    }

    /*fun Uri.getOrientation():Int{

        var exif: ExifInterface? = null
        try {
            exif = ExifInterface(this.path?:"")
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return exif!!.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
    }*/

    fun Uri.rotateBitmap(): Bitmap {
        val bitmap=getBitmapFromUri(this!!)
        val orientation= this.getOrientation()
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_NORMAL -> return bitmap
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                matrix.setRotate(180f)
                matrix.postScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }

            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }
        return try {
            val bmRotated = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
            )
            bitmap.recycle()
            bmRotated
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            bitmap
        }
    }

    fun Uri.getTempFile():Uri{
        val bitmap = getBitmapFromUri(this)
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes)
        //you can create a new file name "test.jpg" in sdcard folder.
        var pdfFolder: File
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            pdfFolder = File(
                Environment.getExternalStorageDirectory().toString() + "/"
                        + "QCharge"
            )
            if (pdfFolder.exists()) {
                pdfFolder = File(
                    (Environment.getExternalStorageDirectory().toString() + "/"
                            + "QCharge")
                )
            } else {
                pdfFolder.mkdirs()
                pdfFolder = File(
                    (Environment.getExternalStorageDirectory().toString() + "/"
                            + "QCharge")
                )
            }
            // creates folder with a pathname including the android storage directory
            // check this warning, may be important for diff API levels
            if (!pdfFolder.exists()) {
                pdfFolder.mkdirs()
                Log.i("CreateFolder", "Folder successfully created")
            }
        } else {
            pdfFolder = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "QCharge"
            )
            if (!pdfFolder.isFile) {
                if (!(pdfFolder.isDirectory)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        try {
                            Files.createDirectory(Paths.get(pdfFolder.absolutePath))
                        } catch (e: IOException) {
                            e.printStackTrace()
                            //Toast.makeText(getApplicationContext(), R.string.unable_to_download, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        pdfFolder.mkdir()
                    }
                }
            }
        }
        val f =
            File(pdfFolder.toString() + "/" + File.separator + Calendar.getInstance().timeInMillis + ".jpg")
        try {
            f.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.fromFile(f)
    }

    @SuppressLint("StaticFieldLeak")
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if ((requestCode == GALLERY && resultCode == Activity.RESULT_OK) && data != null) {
            val imageurl = data.data
            imageurl?.let {
                try {
                    filePath = it
                    val inputStream = context.contentResolver.openInputStream(it)
                    byteArray = inputStream?.readBytes()
                    inputStream?.close()
                    bitmap = filePath!!.rotateBitmap()
                    val bytes = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes)
                    listener.onGalleryClick(bitmap, byteArray!!)
                } catch (e: Exception) {
                }
            }
        } else if ((requestCode == CAMERA && resultCode == Activity.RESULT_OK)) {
            try {
                val inputStream = context.contentResolver.openInputStream(filePath!!)
                byteArray = inputStream?.readBytes()
                inputStream?.close()
                bitmap = filePath!!.rotateBitmap()
                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, bytes)
                listener.onCameraClick(bitmap, byteArray!!)
            } catch (e: Exception) {
            }
        }
    }

    private fun checkPermissions(Choice:String): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (Choice == "Gallery") {
                for (p: String in permissions) {
                    result = ContextCompat.checkSelfPermission(context, p)
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(p)
                    }
                }
            }else{
                for (p: String in c_permissions) {
                    result = ContextCompat.checkSelfPermission(context, p)
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(p)
                    }
                }
            }

        } else {
            if (Choice == "Gallery") {
                for (p: String in permissions1) {
                    result = ContextCompat.checkSelfPermission(context, p)
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(p)
                    }
                }
            }else{
                for (p: String in c_permissions1) {
                    result = ContextCompat.checkSelfPermission(context, p)
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        listPermissionsNeeded.add(p)
                    }
                }
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                context,
                listPermissionsNeeded.toTypedArray<String>(),
                100
            )
            return false
        }
        return true
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 100) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ImageSelection(currentSelection)
            }
            return
        }
    }
}