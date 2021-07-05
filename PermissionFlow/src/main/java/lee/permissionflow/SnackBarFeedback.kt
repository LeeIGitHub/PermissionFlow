package lee.permissionflow

import android.app.Activity
import android.view.View

/*
class SnackBarFeedback(activity: Activity) : PermissionsUiFeedback {
    override var showPermissionRationaleCallback: ((cb: RationaleCallback) -> Unit)? = null
    override var permissionDenied: ((deniedPermissionList: MutableList<String>) -> Unit)? = null
    override var executable: Executable? = null

    private var rationaleCallback: RationaleCallback? = null

    override fun showRationale(f: (cb: RationaleCallback) -> Unit): PermissionsUiFeedback {
        showPermissionRationaleCallback = {
            rationaleCallback = it
            rationaleDialog.show()
        }
        return this
    }

    override fun denied(f: (deniedPermissionList: List<String>) -> Unit): Executable {
        permissionDenied = {
            deniedDialog.show()
        }
        return executable!!
    }

    fun View.showSnackBar(
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(this, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(this)
            }.show()
        }
    }

}


 */