package com.poc.textcapture

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.graphics.ImageDecoder
import android.os.Bundle
import android.os.Build
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.poc.textcapture.databinding.FragmentFirstBinding
import android.content.Context

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    // Declare variables for the UI elements
//    private lateinit var imageView: ImageView

    // Uri to store the location of the image file
    private var imageUri: Uri? = null

    // A modern way to handle activity results, replacing the old onActivityResult
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            // The image was successfully captured and saved to the URI
            // Set the captured image to the ImageView for preview
            binding.imageView.setImageURI(imageUri)

        } else {
            // The user canceled the capture
//            Toast.makeText(requireContext(), "Image capture canceled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.buttonCapture.setOnClickListener {
            checkCameraPermission()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted, now we can launch the camera
            dispatchTakePictureIntent()
        } else {
            // Permission denied, show a toast to the user
//            Toast.makeText(requireContext(), "Camera permission is required to capture images.", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // Permission is already granted, proceed with launching the camera
                dispatchTakePictureIntent()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Show an explanation to the user why the permission is needed
                // before requesting it again
//                Toast.makeText(requireContext(), "The app needs camera access to take pictures.", Toast.LENGTH_LONG).show()
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            else -> {
                // Request the permission for the first time
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Function to create a file URI and launch the camera intent
    private fun dispatchTakePictureIntent() {
        // Create a content URI for the new image file
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "new_image_${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }
        imageUri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (imageUri != null) {
            // Launch the camera with the created URI
            takePictureLauncher.launch(imageUri)
        } else {
//            Toast.makeText(requireContext(), "Could not create image file URI.", Toast.LENGTH_SHORT).show()
        }
    }

//    fun readTextFromImage(context: Context, imageUri: Uri): String? {
//        // Instantiate the TextRecognizer.
//        val textRecognizer = TextRecognizer.Builder(context).build()
//        var resultText: String? = null
//
//        // Check if the recognizer is operational before proceeding.
//        if (!textRecognizer.isOperational) {
////            Log.e("OCR", "TextRecognizer is not operational.")
//            return null
//        }
//
//        // Get the Bitmap from the URI
//        val bitmap: Bitmap? = try {
//            // Use ImageDecoder for modern Android versions (Android P and above)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
//                ImageDecoder.decodeBitmap(source)
//            } else {
//                // Use MediaStore for older Android versions
//                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
//            }
//        } catch (e: Exception) {
////            Log.e("OCR", "Error getting bitmap from URI", e)
//            null
//        }
//
//        // If the bitmap is successfully created, proceed with detection.
//        bitmap?.let {
//            // Create a Frame object from the Bitmap.
//            val frame = Frame.Builder().setBitmap(it).build()
//            // Detect text in the frame.
//            val textBlocks = textRecognizer.detect(frame)
//
//            // Use a StringBuilder to efficiently build the final text string.
//            val stringBuilder = StringBuilder()
//            for (i in 0 until textBlocks.size()) {
//                val textBlock = textBlocks.valueAt(i)
//                stringBuilder.append(textBlock.value)
//                stringBuilder.append("\n") // Add a newline after each text block
//            }
//
//            // Clean up the text recognizer after use.
//            textRecognizer.release()
//
//            resultText = stringBuilder.toString().trim()
//            if (resultText.isEmpty()) {
//                resultText = null
//            }
//        }
//
//        return resultText
//    }
}