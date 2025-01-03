package com.example.meet_ill

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.meet_ill.model.ProfileEditViewModel
import com.example.meet_ill.model.ProfileViewModel
import com.example.meet_ill.model.ViewModelFactory
import com.example.meet_ill.repos.UserRepository
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException


class ProfileEditActivity : AppCompatActivity() {
    private lateinit var imgProfile: ImageView
    private lateinit var etUsername: EditText
    private lateinit var etRealName: EditText
    private lateinit var btnSave: Button


    private lateinit var btnAddSpinner: Button
    private lateinit var btnRemoveSpinner: Button
    private lateinit var spinnersContainer: LinearLayout
    private val spinnerList = mutableListOf<Spinner>()

    private lateinit var viewModel: ProfileEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_edit)

        imgProfile = findViewById(R.id.imgProfile)
        etUsername = findViewById(R.id.etUsername)
        etRealName = findViewById(R.id.etRealName)
        btnSave = findViewById(R.id.btnSave)
        spinnersContainer = findViewById(R.id.spinnersContainer)
        btnAddSpinner = findViewById(R.id.btnAddSpinner)
        btnRemoveSpinner = findViewById(R.id.btnRemoveSpinner)



        val userRepository = UserRepository()
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(userRepository)
        )[ProfileEditViewModel::class.java]

        observeViewModel()
        rellenaHints()

        //Botones para añadir y quitar patologias
        btnAddSpinner.setOnClickListener {
            if (spinnerList.size < 5) {
                val newSpinner = createSpinner()
                spinnerList.add(newSpinner)
                spinnersContainer.addView(newSpinner)
            }
            cargaListeners()
        }
        btnRemoveSpinner.setOnClickListener {
            if (spinnerList.isNotEmpty()) {
                val lastSpinner = spinnerList.removeAt(spinnerList.size - 1)
                spinnersContainer.removeView(lastSpinner)
                viewModel.borrarPatologia()
            }
            cargaListeners()
        }
        imgProfile.setOnClickListener {
            seleccionarImagen()
        }
        val btnBack = findViewById<Button>(R.id.backButton)
        btnBack.setOnClickListener {
            finish() // Finaliza esta actividad y regresa a la anterior.
        }

        btnSave.setOnClickListener {
            viewModel.setUsername(etUsername.text.toString())
            viewModel.setRealName(etRealName.text.toString())
            viewModel.updateProfile(viewModel.userId)
        }



        viewModel.loadUserData(viewModel.userId)
    }

    private fun cargaListeners() {
        spinnerList.forEach { spinner ->
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                private var isFirstSelection = true
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    // Ignorar la primera selección inicial
                    if (isFirstSelection) {
                        isFirstSelection = false
                        return
                    }
                    val patologias = spinnerList.map { it.selectedItem.toString() }
                    viewModel.setSelectedPathologies(patologias)
                }
                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }
    }

    private fun observeViewModel() {
        viewModel.username.observe(this) { username -> etUsername.setText(username.toString()) }
        viewModel.realName.observe(this) { realname -> etRealName.setText(realname.toString()) }
        viewModel.profileImageBase64.observe(this) { base64 ->
            if (base64 != null) {
                imgProfile.setImageBitmap(convertBase64ToBitmap(base64))
            } else {
                imgProfile.setImageResource(R.drawable.default_profile_image)
            }
        }
        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
        viewModel.isProfileUpdated.observe(this) { isUpdated ->
            if (isUpdated) {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun createSpinner(): Spinner {
        val patologias = listOf(
            "","Diabetes", "VIH", "Hipertensión", "Asma", "Artritis", "Epilepsia", "Alzheimer", "Parkinson",
            "Esclerosis múltiple", "Cáncer", "Enfermedad cardíaca", "EPOC", "Insuficiencia renal", "Fibromialgia",
            "Lupus", "Anemia", "Migraña", "Obesidad", "Hipotiroidismo", "Alergias")
        val spinner = Spinner(this)

        val adapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, patologias) {
                override fun getView(
                    position: Int,
                    convertView: android.view.View?,
                    parent: android.view.ViewGroup
                ): android.view.View {
                    val view = super.getView(position, convertView, parent)
                    val textView = view as TextView
                    textView.textSize = 19f
                    textView.setPadding(20, 20, 20, 20)
                    return view
                }

                override fun getDropDownView(
                    position: Int,
                    convertView: android.view.View?,
                    parent: android.view.ViewGroup
                ): android.view.View {
                    val view = super.getDropDownView(position, convertView, parent)
                    val textView = view as TextView
                    textView.textSize = 25f
                    return view
                }
            }
        spinner.adapter = adapter
        return spinner
    }

    private fun rellenaHints() {
        viewModel.selectedPathologies.observe(this) { pathologies ->
            spinnersContainer.removeAllViews()
            spinnerList.clear()

            pathologies.forEach {
                val spinner = createSpinner()
                spinnerList.add(spinner)
                spinnersContainer.addView(spinner)
            }

            for (i in pathologies.indices) {
                if (i < spinnerList.size) {
                    val spinner = spinnerList[i]
                    val adapter = spinner.adapter as ArrayAdapter<String>
                    val position = adapter.getPosition(pathologies[i])
                    spinner.setSelection(position)
                }
            }
            cargaListeners()
        }
    }


    private fun seleccionarImagen() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100) // Llamamos al selector de imágenes
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri = data.data!!
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                val imagenPerfilBase64 = convertirBitmapABase64(bitmap)
                viewModel.setProfileImageBase64(imagenPerfilBase64)
                imgProfile.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun convertirBitmapABase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
    private fun convertBase64ToBitmap(base64: String): Bitmap {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }


}