package lee.permissionflow

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

/**
 * 请求权限流处理
 * 一个简化需要权限繁琐操作的处理工具流
 *
 * 注意：初始化必须在Activity.Start状态之前
 *
 */
class PermissionFlow(val activity: ComponentActivity) {
    private var isMultiplePermission = false

    private var needRequestPermissionArray: Array<String>? = null

    private var resultLauncher: ActivityResultLauncher<Array<String>>? = null

    private var activityResultCallback: ActivityResultCallback<Map<String, Boolean>>? = null

    private var permissionUiFeedback: PermissionsUiFeedback? = null

    init {
        resultLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
            activityResultCallback?.onActivityResult(it)
        }
    }

    /**
     * 设置权限，返回一个PermissionsUiFeedbackImpl()
     */
    fun withPermission(permission: String): PermissionsUiFeedbackImpl {
        needRequestPermissionArray = arrayOf(permission)
        return PermissionsUiFeedbackImpl()
    }

    fun withPermission(mp: Array<String>): PermissionsUiFeedbackImpl {
        needRequestPermissionArray = mp
        if (mp.size > 1) isMultiplePermission = true
        return PermissionsUiFeedbackImpl()
    }

    /**
     * 设置权限，不同于 @see withPermission(String)，返回了一个UiFeedbackCreator()
     * 当你需要自定义PermissionsUiFeedback时，调用此方法
     */
    fun withPermissionByUiFeedbackCreator(permission: String): UiFeedbackCreator {
        needRequestPermissionArray = arrayOf(permission)
        return UiFeedbackCreator()
    }

    fun withPermissionByUiFeedbackCreator(mp: Array<String>): UiFeedbackCreator {
        needRequestPermissionArray = mp
        if (mp.size > 1) isMultiplePermission = true
        return UiFeedbackCreator()
    }

    /**
     * 权限Ui反馈的创造者
     *
     * 注意：这里默认初始化了ExecutableImpl()，暂不支持自定义Executable，目前觉得没有必要
     */
    inner class UiFeedbackCreator {
        fun <T : PermissionsUiFeedback> permissionUiFeedback(pufb: T): T {
            permissionUiFeedback = pufb.apply {
                executable = ExecutableImpl()
            }
            return pufb
        }
/*
        fun permissionUiFeedback(): PermissionsUiFeedbackImpl {
            permissionUiFeedback = PermissionsUiFeedbackImpl().apply {
                executable = ExecutableImpl()
            }
            return permissionUiFeedback as PermissionsUiFeedbackImpl
        }

 */
    }

    /**
     * 权限反馈的基本实现
     */
    inner class PermissionsUiFeedbackImpl : PermissionsUiFeedback {
        override var showPermissionRationaleCallback: ((cb: RationaleCallback) -> Unit)? = null
        override var permissionDenied: ((deniedPermissionList: MutableList<String>) -> Unit)? = null
        override var executable: Executable? = ExecutableImpl()

        override fun showRationale(f: (cb: RationaleCallback) -> Unit): PermissionsUiFeedback {
            showPermissionRationaleCallback = f
            return this
        }

        override fun denied(f: (deniedPermissionList: List<String>) -> Unit): Executable {
            permissionDenied = f
            return executable!!
        }
    }

    /**
     * 执行的具体实现
     *
     */
    protected open inner class ExecutableImpl() : Executable {

        override var executableOperation: (() -> Unit)? = null

        override fun execute(f: () -> Unit) {
            executableOperation = f

            activityResultCallback = ActivityResultCallback { resultMap ->
                var isGranted = true
                val deniedPermissionList = mutableListOf<String>()
                for (r in resultMap) {
                    if (!r.value) {
                        isGranted = false
                        deniedPermissionList.add(r.key)
                    }
                }
                //用户点了同意
                if (isGranted) {
                    executeNeedPermissionOperation()
                } else {
                    /**
                     * 这里是个重点，用户点了拒绝，拒绝不在询问，系统都返回拒绝
                     * 甚至在oneplus7 氧系统， Android11上，都没有以后都不要再询问，第二次点击拒绝，
                     * 就相当于以前点击的不在询问，以后都请求不了
                     */
                    executePermissionDenied(deniedPermissionList)
                }
            }

            executePermissionFlow()
        }

        private fun executePermissionFlow() {
            /**
             *  如果版本低于android6.0（这里不知道这么做对不对）
             *  或者
             *  验证是否有权限
             */

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkPermission()
            ) {
                //有，直接执行
                executeNeedPermissionOperation()
            } else {
                //是否需要展示理由
                val showRationale = shouldShowRequest(activity)
                if (showRationale) {
                    showPermissionRationale()
                } else {
                    requestPermission()
                }
            }
        }

        //检查权限
        private fun checkPermission(): Boolean {
            fun check(p: String): Boolean {
                return ContextCompat.checkSelfPermission(
                    activity,
                    p
                ) == PackageManager.PERMISSION_GRANTED
            }

            var pass = true
            for (p in needRequestPermissionArray!!) {
                if (!check(p)) {
                    pass = false
                }
            }
            return pass
        }

        //执行需要权限的操作，如调用相机
        private fun executeNeedPermissionOperation() {
            executableOperation?.let { it() }
        }

        //展示请求权限的理由
        private fun showPermissionRationale() {
            if (permissionUiFeedback == null && permissionUiFeedback!!.showPermissionRationaleCallback == null) {
                //如果没写，则直接执行，不过不推荐
                requestPermission()
            } else {
                //展示理由处，用户可以拒绝，返回false的话，则不请求
                val cb = object : RationaleCallback {
                    override fun continueRequest(c: Boolean) {
                        if (c) {
                            requestPermission()
                        }
                    }
                }
                permissionUiFeedback!!.showPermissionRationaleCallback!!(cb)
            }
        }

        //请求权限
        private fun requestPermission() {
            resultLauncher?.launch(needRequestPermissionArray)
        }

        //执行用户拒绝的操作
        private fun executePermissionDenied(deniedPermissionList: MutableList<String>) {
            permissionUiFeedback?.permissionDenied?.let { it(deniedPermissionList) }
        }

        //是否需要显示理由
        @RequiresApi(Build.VERSION_CODES.M)
        private fun shouldShowRequest(activity: ComponentActivity): Boolean {
            var show = false
            for (p in needRequestPermissionArray!!) {
                if (activity.shouldShowRequestPermissionRationale(p)) show = true
            }
            return show
        }
    }

    fun unregister() {
        resultLauncher?.unregister()
    }
}