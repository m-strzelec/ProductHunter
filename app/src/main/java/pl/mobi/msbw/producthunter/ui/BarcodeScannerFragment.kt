package pl.mobi.msbw.producthunter.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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

    private var _binding: FragmentBarcodeScannerBinding? = null
    private val binding get() = _binding!!

    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var barcodeScanner: BarcodeScanner

    private val requiredPermissions = arrayOf(Manifest.permission.CAMERA)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        barcodeScanner = BarcodeScanning.getClient(BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build())

        binding.captureImg.setOnClickListener {
            takePhoto()
        }
        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
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
        preview.setSurfaceProvider(binding.previewView.surfaceProvider)
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
    }

    private fun takePhoto() {
        val photoCaptureCallback = object : ImageCapture.OnImageCapturedCallback() {
            @OptIn(ExperimentalGetImage::class)
            override fun onCaptureSuccess(image: ImageProxy) {
                image.image?.let {
                    val inputImage = InputImage.fromMediaImage(it, image.imageInfo.rotationDegrees)
                    analyzeBarcode(inputImage)
                }
                image.close()
            }
            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(requireContext(), getString(R.string.capture_failed), Toast.LENGTH_SHORT).show()
            }
        }
        imageCapture.takePicture(cameraExecutor, photoCaptureCallback)
    }

    private fun analyzeBarcode(image: InputImage) {
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.no_code_found), Toast.LENGTH_SHORT).show()
                } else {
                    handleBarcodes(barcodes)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), getString(R.string.scanning_failed), Toast.LENGTH_SHORT).show()
            }
    }

    private fun handleBarcodes(barcodes: List<Barcode>) {
        barcodes.forEach { barcode ->
            val rawValue = barcode.rawValue ?: return@forEach
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.find_product))
                .setMessage(getString(R.string.product_code).plus(" ").plus(rawValue))
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    val uri: Uri = if (barcode.format == Barcode.FORMAT_QR_CODE) {
                        Uri.parse(rawValue)
                    } else {
                        Uri.parse("https://www.barcodelookup.com/$rawValue")
                    }
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
                .setNegativeButton(android.R.string.cancel, null)
                .setIcon(android.R.drawable.ic_dialog_info)
                .show()
        }
    }

    private fun allPermissionsGranted() = requiredPermissions.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), getString(R.string.permissions_needed), Toast.LENGTH_SHORT).show()
            activity?.supportFragmentManager?.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        _binding = null
    }
}
