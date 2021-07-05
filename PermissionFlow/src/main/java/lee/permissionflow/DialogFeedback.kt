package lee.permissionflow

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog

/**
 * 对话框式的权限回调
 *
 * 权限回调的逻辑是可以自定义的，这里实现一种dialog的
 *
 */
class DialogFeedback(activity: Activity) : PermissionsUiFeedback,
    DialogInterface.OnClickListener {

    override var executable: Executable? = null
    override var showPermissionRationaleCallback: ((cb: RationaleCallback) -> Unit)? = null
    override var permissionDenied: ((deniedPermissionList: MutableList<String>) -> Unit)? = null
    private var rationaleCallback: RationaleCallback? = null

    /**
     * 解释的文字 们
     */
    private var rationaleTitle: CharSequence? = null
    private var rationaleTitleRes: Int? = null
    private var rationalePositiveButtonText: CharSequence? = null
    private var rationalePositiveButtonTextRes: Int? = null
    private var rationaleNegativeButtonText: CharSequence? = null
    private var rationaleNegativeButtonTextRes: Int? = null

    /**
     * 被拒绝后的文字 们
     */
    private var deniedTitle: CharSequence? = null
    private var deniedTitleRes: Int? = null
    private var deniedPositiveButtonText: CharSequence? = null
    private var deniedPositiveButtonTextRes: Int? = null
    private var deniedNegativeButtonText: CharSequence? = null
    private var deniedNegativeButtonTextRes: Int? = null

    /**
     * 解释dialog
     */
    private val rationaleDialog: Dialog by lazy {
        AlertDialog
            .Builder(activity)
            .apply {
                rationaleTitle?.let { setMessage(it) }
                rationaleTitleRes?.let { setMessage(it) }
            }
            .apply {
                rationalePositiveButtonText?.let {
                    setPositiveButton(it, this@DialogFeedback)
                }
                rationalePositiveButtonTextRes?.let {
                    setPositiveButton(it, this@DialogFeedback)
                }
            }
            .apply {
                rationaleNegativeButtonText?.let {
                    setNegativeButton(it, this@DialogFeedback)
                }
                rationaleNegativeButtonTextRes?.let {
                    setNegativeButton(it, this@DialogFeedback)
                }
            }
            .create()
    }

    /**
     * 被拒绝的dialog
     */
    private val deniedDialog: Dialog by lazy {
        AlertDialog
            .Builder(activity)
            .apply {
                deniedTitle?.let { setMessage(it) }
                deniedTitleRes?.let { setMessage(it) }
            }
            .apply {
                deniedPositiveButtonText?.let {
                    setPositiveButton(it, this@DialogFeedback)
                }
                deniedPositiveButtonTextRes?.let {
                    setPositiveButton(it, this@DialogFeedback)
                }
            }
            .apply {
                deniedNegativeButtonText?.let {
                    setNegativeButton(it, this@DialogFeedback)
                }
                deniedNegativeButtonTextRes?.let {
                    setNegativeButton(it, this@DialogFeedback)
                }
            }
            .create()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (dialog) {
            rationaleDialog -> {
                rationaleCallback?.continueRequest(which == DialogInterface.BUTTON_POSITIVE)
            }
            deniedDialog -> {

            }
        }
    }

    /**
     * 设置解释权限理由
     */
    fun setRationaleDialogText(
        reason: CharSequence? = null,
        reasonRes: Int? = null,
        positiveButtonText: CharSequence? = null,
        positiveButtonTextRes: Int? = null,
        negativeButtonText: CharSequence? = null,
        negativeButtonTextRes: Int? = null
    ): DialogFeedback {
        this.rationaleTitle = reason
        this.rationaleTitleRes = reasonRes
        this.rationalePositiveButtonText = positiveButtonText
        this.rationalePositiveButtonTextRes = positiveButtonTextRes
        this.rationaleNegativeButtonText = negativeButtonText
        this.rationaleNegativeButtonTextRes = negativeButtonTextRes

        showRationale {}

        return this
    }

    /**
     * 设置被拒绝弹框上的文字
     */
    fun setDeniedDialogText(
        title: CharSequence? = null,
        titleRes: Int? = null,
        positiveButtonText: CharSequence? = null,
        positiveButtonTextRes: Int? = null,
        negativeButtonText: CharSequence? = null,
        negativeButtonTextRes: Int? = null
    ): Executable {
        this.deniedTitle = title
        this.deniedTitleRes = titleRes
        this.deniedPositiveButtonText = positiveButtonText
        this.deniedPositiveButtonTextRes = positiveButtonTextRes
        this.deniedNegativeButtonText = negativeButtonText
        this.deniedNegativeButtonTextRes = negativeButtonTextRes
        return denied { }
    }

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
}
