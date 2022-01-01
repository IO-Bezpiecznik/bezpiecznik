package com.example.bezpiecznik.ui.exportImportData

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bezpiecznik.data.AppDatabase
import com.example.bezpiecznik.types.ExportSchema
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStream
import java.io.OutputStream
import java.util.*


class ExportImportViewModel : ViewModel() {
    private val database = AppDatabase.getConnection()

    private val _showLoad = MutableLiveData<Boolean>().apply {
        value = false
    }
    val showLoad: LiveData<Boolean> = _showLoad

    private val _toast = MutableLiveData<String?>().apply {
        value = null
    }
    val toast: LiveData<String?> = _toast

    fun stopLoad() {
        _showLoad.postValue(false)
    }

    fun startLoad() {
        _showLoad.postValue(true)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveBackup(stream: OutputStream) {
        CoroutineScope(IO).launch {
            val codes = database.codeDao().getAll()
            val grids = database.gridDao().getAll()
            val data = Json.encodeToString(
                ExportSchema.serializer(),
                ExportSchema(codes = codes, grids = grids)
            )

            val encoder = Base64.getEncoder()
            stream.write(encoder.encode(data.toByteArray()))
            stream.close()

            stopLoad()
            _toast.postValue("Pomyślnie wykonano kopię")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadBackup(stream: InputStream) {
        CoroutineScope(IO).launch {
            val decoder = Base64.getDecoder()
            val data = stream.bufferedReader().use(BufferedReader::readText)
            val exportSchema = Json.decodeFromString(
                ExportSchema.serializer(),
                decoder.decode(data).decodeToString()
            )
            database.codeDao().insertMany(*exportSchema.codes.toTypedArray())
            database.gridDao().insertMany(*exportSchema.grids.toTypedArray())

            stream.close()
            stopLoad()
            _toast.postValue("Pomyślnie wczytano kopię")
        }
    }
}