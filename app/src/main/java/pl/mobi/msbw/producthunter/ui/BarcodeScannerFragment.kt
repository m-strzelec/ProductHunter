package pl.mobi.msbw.producthunter.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import pl.mobi.msbw.producthunter.R
import pl.mobi.msbw.producthunter.databinding.FragmentBarcodeScannerBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeScannerFragment : Fragment() {

    private lateinit var binding: FragmentBarcodeScannerBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture

    private val requestPermissionsCode = 10
    private val requiredPermissions = arrayOf(Manifest.permission.CAMERA)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentBarcodeScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        binding.captureImg.setOnClickListener {
            takePhoto()
        }
        if (!allPermissionsGranted()) {
            requestPermissions(requiredPermissions, requestPermissionsCode)
        } else {
            startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        imageCapture = ImageCapture.Builder().build()
        val camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
    }

    private fun takePhoto() {
        imageCapture.takePicture(cameraExecutor, object : ImageCapture.OnImageCapturedCallback() {
            @OptIn(ExperimentalGetImage::class)
            override fun onCaptureSuccess(image: ImageProxy) {
                val mediaImage: Image? = image.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(mediaImage, image.imageInfo.rotationDegrees)
                    analyzeBarcode(inputImage)
                }
                image.close()
            }
            override fun onError(exception: ImageCaptureException) {
                val a = getString(R.string.capture_failed)
                Toast.makeText(requireContext(), a, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun analyzeBarcode(image: InputImage) {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()
        val scanner: BarcodeScanner = BarcodeScanning.getClient(options)
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val rawValue = barcode.rawValue ?: ""
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(getString(R.string.find_product))
                        .setMessage(getString(R.string.product_code) + rawValue)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            val uri: Uri = if (barcode.format == Barcode.FORMAT_QR_CODE) {
                                Uri.parse(rawValue)
                            } else {
                                Uri.parse("https://www.barcodelookup.com/$rawValue")
                            }
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            startActivity(intent)
                        }
                        .setNegativeButton(android.R.string.cancel, null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show()
                }
            }
            .addOnFailureListener {
                val a = getString(R.string.scanning_failed)
                Toast.makeText(requireContext(), a, Toast.LENGTH_SHORT).show()
            }
    }

    private fun allPermissionsGranted() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == requestPermissionsCode && grantResults.isNotEmpty() && allPermissionsGranted()) {
            startCamera()
        } else {
            val a = getString(R.string.permissions_needed)
            Toast.makeText(requireContext(), a, Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}
