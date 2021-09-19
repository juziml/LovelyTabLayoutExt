package com.grock.kcp

/**
 *@Desc:
 *-
 *-
 *create by zhusw on 2020/11/20 11:07
 */
class PermissionCallback(
        val eachResult: (pre: Permission) -> Unit,
        val allGranted: (granteds: List<Permission>) -> Unit = {},
        /**
         * granteds :同意的授权
         * rejects :被拒绝的授权
         */
        val leastOneReject: (granteds: List<Permission>, rejects: List<Permission>) -> Unit = { _: List<Permission>, _: List<Permission> -> },
        val onCancel: () -> Unit,
        val onError: (e: Throwable?) -> Unit,
        val onComplete:()->Unit={}
) : KCJob {
    var canceled: Boolean = false
        private set

    override fun cancel() {
        if (!canceled) {
            canceled = true
            onCancel()
        }
    }
}

interface KCJob {
    fun cancel()
}