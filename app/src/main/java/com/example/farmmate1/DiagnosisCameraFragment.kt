package com.example.farmmate1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.content.Intent
import android.provider.MediaStore
import android.app.Activity
import java.io.File
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.example.farmmate1.databinding.FragmentDiagnosisCameraBinding
import java.io.FileOutputStream
import java.io.IOException

class DiagnosisCameraFragment : Fragment() {

    private val REQUEST_IMAGE_CAPTURE = 101
    private val CAMERA_PERMISSION_CODE = 102
    private val REQUEST_IMAGE_FROM_GALLERY = 103
    private val REQUEST_PERMISSION_CODE = 104

    private var _binding: FragmentDiagnosisCameraBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiagnosisCameraBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedCrop = arguments?.getString("selectedCrop")
        binding.diagnosisCameraTvPlant.text = "진단 할 식물: $selectedCrop"

//        if (!checkPermission()) {
//            requestPermission()
//        }
//
//        binding.diagnosisCameraBtnOpencam.setOnClickListener{
//            if(checkPermission()){
//                dispatchTakePictureIntent()
//            }
//            else{
//                requestPermission()
//            }
//        }

        binding.diagnosisCameraBtnOpencam.setOnClickListener{
            checkCameraPermission()
        }

        binding.diagnosisCameraBtnGallery.setOnClickListener {
            getAlbum()
        }

        // 초기에 진단하기 버튼 비활성화
        binding.diagnosisCameraBtnNext.visibility = View.INVISIBLE
        binding.diagnosisCameraIvImage.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            // 이미지 변경시 버튼 활성화
            binding.diagnosisCameraBtnNext.visibility = View.VISIBLE
        }

        //startCamera()
    }

//    private fun requestPermission() {
//        requestPermissions(
//            arrayOf(
//                Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.CAMERA
//            ),
//            REQUEST_PERMISSION_CODE
//        )
//    }

    private fun checkPermission(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val storagePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return cameraPermission && storagePermission
    }

    private fun checkCameraPermission() {
        // 카메라 권한이 부여되었는지 확인
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 없다면 권한 요청
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            // 권한이 이미 허용되었다면 사진 촬영 진행
            dispatchTakePictureIntent()
        }
    }

    // 권한이 없다면 권한 요청 > 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용되면 사진 촬영 진행
                dispatchTakePictureIntent()
            } else {
                // 권한이 거부되었을 때 처리
                Toast.makeText(requireContext(), "권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    // 사진 찍기 후 결과 이미지 처리
                    saveCapturedImage(data)
                }
                REQUEST_IMAGE_FROM_GALLERY -> {
                    // 갤러리에서 이미지 선택 후 처리
                    saveGalleryImage(data)
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
//            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
//                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
//            }
//        }
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
    }

    fun getAlbum() {
        if (checkPermission()) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_FROM_GALLERY)
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    private fun saveCapturedImage(data: Intent?) {
        if (data != null && data.extras != null) {
            val imageBitmap = data.extras!!.get("data") as Bitmap

            // 이미지를 파일로 저장
            val imagePath = saveImageToFile(imageBitmap)

            // 이미지를 ImageView에 표시
            binding.diagnosisCameraIvImage.setImageBitmap(imageBitmap)
        }
    }

    private fun saveGalleryImage(data: Intent?) {
        val imageUri = data?.data
        val imageBitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)

        // 이미지를 파일로 저장
        val imagePath = saveImageToFile(imageBitmap)

        // 이미지를 ImageView에 표시
        binding.diagnosisCameraIvImage.setImageBitmap(imageBitmap)
    }

    private fun saveImageToFile(bitmap: Bitmap): String {
        val filesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(filesDir, "captured_image.jpg")

        try {
            FileOutputStream(imageFile).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return imageFile.absolutePath
    }



}