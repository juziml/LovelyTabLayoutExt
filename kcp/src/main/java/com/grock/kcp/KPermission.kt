package com.grock.kcp

import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 *@Desc:
 * 这是一个纯原生的权限请求
 * 借助透明Fragment实现，任意页面发起权限请求
 * 关于部分手机在主动关闭权限的情况下，走权限申请直接返回true ，适配范围过大，需自行处理
 *create by zhusw on 2020/11/20 09:54
 */
val TAG = KCPermission::class.java.simpleName

class KCPermission {
    constructor(builder: Builder) {
        this.builder = builder
    }

    private lateinit var builder: Builder

    private val permissionFragment: PermissionFragment by lazy {
        getPermissionFragment(builder.fragManager)
    }

    /*关于targetApi,是为在使用低版本编译时lint可以检查出不可用的方法,所以方法内部还是需要做版本判断的*/
    companion object {
        val IS_BIG_THAN_SDK_M = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

        @TargetApi(Build.VERSION_CODES.M)
        fun prepare(): Builder {
            return Builder()
        }

        @TargetApi(Build.VERSION_CODES.M)
        fun checkAll(context: Context, vararg perNames: String): ArrayList<String> {
            val needRequestList = arrayListOf<String>()
             if(IS_BIG_THAN_SDK_M){
                perNames.forEach {
                    val granted = ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
                    if (!granted) {
                        needRequestList.add(it)
                    }
                }
            }
            return needRequestList
        }

        @TargetApi(Build.VERSION_CODES.M)
        fun isGranted(context: Context, perName: String): Boolean {
            return if(IS_BIG_THAN_SDK_M){
                 ContextCompat.checkSelfPermission(context, perName) == PackageManager.PERMISSION_GRANTED
            }else{
                true
            }
        }

        @TargetApi(Build.VERSION_CODES.M)
        fun isRevoked(activity: FragmentActivity, permission: String): Boolean {
            return if(IS_BIG_THAN_SDK_M){
                 activity.packageManager.isPermissionRevokedByPolicy(permission, activity.packageName)
            }else{
                false
            }
        }

    }

    fun request(
            eachResult: (pre: Permission) -> Unit = {},
            allGranted: (rejects: List<Permission>) -> Unit = {},
            /**
             * granteds :同意的授权
             * rejects :被拒绝的授权
             */
            leastOneReject: (granteds: List<Permission>, rejects: List<Permission>) -> Unit = { _: List<Permission>, _: List<Permission> -> },
            onCancel: () -> Unit = {},
            onError: (e: Throwable?) -> Unit = {},
            onComplete: () -> Unit = {}
    ): KCJob {
        val permissionCallback = PermissionCallback(
                eachResult,
                allGranted,
                leastOneReject,
                onCancel,
                onError,
                onComplete
        )
        permissionFragment.easyRequestPermissions(builder.permissionNames, permissionCallback)
        return permissionCallback
    }


    class Builder {
        lateinit var fragManager: FragmentManager
            private set
        lateinit var permissionNames: Array<out String>
            private set
        fun with(fragmentActivity: FragmentActivity): Builder {
            fragManager = fragmentActivity.supportFragmentManager
            return this
        }

        fun with(fragment: Fragment): Builder? {
            fragment.activity?.let {
                fragManager = it.supportFragmentManager
                return with@ this
            }
            Log.e(TAG, "invoke with(fragment) but it has no activity attached")
            return null
        }

        fun target(vararg pNames: String): Builder {
            permissionNames = pNames
            return this
        }

        /**
         * 暴露空返回，避免外部错误
         */
        fun build(): KCPermission? {
            if (null != fragManager && !permissionNames.isNullOrEmpty()) {
                return KCPermission(this@Builder)
            }
            Log.e(TAG, "miss fragManager or permissionNames is empty,fragManager=$fragManager permissionNames=$permissionNames")
            return null
        }
    }

    private fun getPermissionFragment(fm: FragmentManager): PermissionFragment {
        var oldPf = fm.findFragmentByTag(TAG)
        if (null == oldPf) {
            oldPf = PermissionFragment()
            fm.beginTransaction()
                    .add(oldPf, PermissionFragment.TAG)
                    .commitNowAllowingStateLoss()
        }
        return oldPf as PermissionFragment
    }
}



