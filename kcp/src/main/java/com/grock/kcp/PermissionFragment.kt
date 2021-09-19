package com.grock.kcp

import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import java.lang.Exception
import java.util.*

/**
 *@Desc:
 *-
 *-
 *create by zhusw on 2020/11/19 17:48
 */
internal class PermissionFragment : Fragment() {
    companion object {
        val TAG = PermissionFragment::class.java.name
    }

    private lateinit var hostActivity: FragmentActivity
    private val random: Random = Random()
    private val requestMap = SparseArray<PermissionCallback>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true //activity 异常重建时 ，fragment不重建
        hostActivity = this.activity!!
    }
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionRequestListener = requestMap.get(requestCode)

        try {
            requestMap.remove(requestCode)
            permissionRequestListener?.let {
                handlerPermissionResult(permissions, grantResults, permissionRequestListener)
            }
        }catch (e:Exception){
            permissionRequestListener?.let {
                it.onError(e)
            }
        }
    }

    /**
     * 将结果进行分类传递
     */
    private fun handlerPermissionResult(
            permissions: Array<out String>,
            grantResults: IntArray, permissionCallback: PermissionCallback
    ) {
        if (!permissionCallback.canceled) {
            val rejectList = arrayListOf<Permission>()
            val grantedList = arrayListOf<Permission>()
            var allGrant = true
            for (index in grantResults.indices) {
                val pName = permissions[index]
                val grant = grantResults[index]
                val permission = Permission(pName)
                val accept = grant == PackageManager.PERMISSION_GRANTED
                when (grant) {
                    PackageManager.PERMISSION_GRANTED -> {
                        permission.accept = accept
                    }
                    else -> {
                        permission.accept = false
                        permission.shouldShowRequestPermissionRationale = ActivityCompat.shouldShowRequestPermissionRationale(hostActivity, pName)
                    }
                }
                permissionCallback.eachResult(permission)
                if (permission.accept) {
                    grantedList.add(permission)
                } else {
                    rejectList.add(permission)
                    allGrant = false
                }
            }
            if (allGrant) {
                permissionCallback.allGranted(grantedList)
            } else {
                permissionCallback.leastOneReject(grantedList,rejectList)
            }
        }
        permissionCallback.onComplete
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun easyRequestPermissions(
            name: Array<out String>,
            permissionRequestCallBack: PermissionCallback
    ) {
        if(KCPermission.IS_BIG_THAN_SDK_M){
            val requestCode = makeRequestCode()
            requestMap.put(requestCode, permissionRequestCallBack)
            requestPermissions(name, requestCode)
        }
    }

    private fun makeRequestCode(): Int {
        var newCode: Int
        var tryCount = 0
        do {
            newCode = random.nextInt(60000)//最大 65535
            tryCount++
        } while (requestMap.indexOfKey(newCode) >= 0 && tryCount < 10)
        return newCode
    }

}

class Permission(
        var name: String,
        var accept: Boolean,
        //是否可弹出权限申请窗口
        var shouldShowRequestPermissionRationale: Boolean = false
) {
    constructor(name:String) : this(name, false, false)

    override fun toString(): String {
        return "name=$name,accept=$accept,shouldShowWindow=$shouldShowRequestPermissionRationale"
    }
}

