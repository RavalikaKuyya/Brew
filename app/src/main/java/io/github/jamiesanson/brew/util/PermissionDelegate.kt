package io.github.jamiesanson.brew.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import org.jetbrains.anko.alert
import org.jetbrains.anko.cancelButton

/**
 * Class for helping with permission checking. Does so using coroutines and Dexter for permission requesting
 */
class PermissionDelegate {

    suspend fun checkPermissions(activity: Activity, justification: String = "", permission: String, vararg permissions: String): Boolean {
        // Check permissions first
        if (arePermissionsGranted(activity, permission, *permissions)) {
            return true
        }

        // Show justification if provided
        var justificationAccepted = true
        if (justification.isNotEmpty()) {
            justificationAccepted = showJustificationDialog(activity, justification)
        }

        if (!justificationAccepted) {
            return false
        }

        // Check permissions
        return requestPermissions(activity, permission, *permissions)
    }

    private suspend fun showJustificationDialog(activity: Activity, justification: String) = suspendCancellableCoroutine<Boolean> { cont ->
        val dialog = activity.alert(justification) {
            positiveButton("Continue") { cont.resume(true) }
            cancelButton { cont.resume(false) }
        }.show()

        cont.invokeOnCompletion { if(cont.isCancelled) dialog.dismiss() }
    }

    private fun arePermissionsGranted(context: Context, vararg permissions: String): Boolean {
        var granted = true

        for (permission in permissions) {
            granted = granted && ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }

        return granted
    }

    private suspend fun requestPermissions(activity: Activity, vararg permissions: String) = suspendCancellableCoroutine<Boolean> { cont ->
        Dexter.withActivity(activity)
                .withPermissions(*permissions)
                .withListener(object: MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        cont.resume(report?.areAllPermissionsGranted() ?: false)
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }
                })
                .check()
    }
}