package lee.permissionflow

/**
 * 权限Ui反馈接口
 * 用来给开发者收到用户权限操作的反馈回调
 *
 * @see showPermissionRationaleCallback 需要展示权限理由/说明的时候，会回调这里，
 * 里面返回的 RationaleCallback 用来继续执行请求
 *
 * @see permissionDenied 权限被拒绝会回调这里
 * 里面返回的 MutableList<String> 是所被拒绝的具体权限
 */
interface PermissionsUiFeedback {
    var showPermissionRationaleCallback: ((cb: RationaleCallback) -> Unit)?
    var permissionDenied: ((deniedPermissionList: MutableList<String>) -> Unit)?

    var executable: Executable?

    fun showRationale(f: (cb: RationaleCallback) -> Unit): PermissionsUiFeedback
    fun denied(f: (deniedPermissionList: List<String>) -> Unit): Executable
}

interface RationaleCallback {
    fun continueRequest(c: Boolean)
}