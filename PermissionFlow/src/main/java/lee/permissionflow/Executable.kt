package lee.permissionflow

/**
 * 可执行的
 *
 * 这里已经设置好权限和用户的各种交互，下一步直接执行
 */
interface Executable {
    var executableOperation: (() -> Unit)?
    fun execute(f: () -> Unit)
}