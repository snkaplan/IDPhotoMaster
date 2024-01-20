package com.idphoto.idphotomaster.core.data.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.idphoto.idphotomaster.core.common.Constants.KEY_TEMP_FILE_START_PREFIX
import java.io.File


class DeleteTempFilesWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {
            val fileNamePrefix: String? = inputData.getString(KEY_TEMP_FILE_START_PREFIX)
            if (fileNamePrefix != null) {
                deleteTempFiles(context.cacheDir, fileNamePrefix)
            }
        } catch (e: Exception) {
            Log.e("TAG", "doWork:", e)
        }
        return Result.success()
    }

    private fun deleteTempFiles(file: File, fileNamePrefix: String) {
        if (file.isDirectory) {
            val files: Array<out File>? = file.listFiles()
            if (files != null) {
                for (f in files) {
                    if (f.isDirectory) {
                        deleteTempFiles(f, fileNamePrefix)
                    } else {
                        if (f.name.startsWith(fileNamePrefix)) {
                            f.delete()
                        }
                    }
                }
            }
        }
    }
}