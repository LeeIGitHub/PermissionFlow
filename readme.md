# PermissionFlow（快照版本）
##### 用于简化Android中需要权限时的繁琐操作👍



###### 使用须知

1. 根据最新的[谷歌权限流程](https://developer.android.com/guide/topics/permissions/overview?hl=zh-cn#workflow)进行的开发，暂时**不支持“不再询问”的回调**，这里请注意
2. 目前只支持 androidx.activity.ComponentActivity和其子类，如果你用的是AppCompatActivity和FragmentActivity也可以继续往下看
3. 有任何问题都可提，我都会尽快解决或反馈

###### 如何使用

* 第一步

```
implementation 'io.github.leeigithub:permissionflow:0.0.1'
```

* 第二部

```
class PermissionTestActivity : AppCompatActivity() {
	//必须在Activity.onStart()之前初始化，每个activity/fragment有一个就行了
	val permissionFlow = PermissionFlow(this)
}
```

* 第三步

```
    permissionFlow
		.withPermission(Manifest.permission.CAMERA)
		.showRationale { callback ->
			//最好给出需要权限的理由，也可以什么都不做
			callback.continueRequest(true)//如果false，则不会向请求权限
		}
		.denied {
			//用户拒绝了权限，这里再次提醒，目前暂不支持“不再询问”的回调
		}
		.execute {
			//打开相机
			startCamera()
		}
```

```
//多权限
	permissionFlow
                .withPermissionByUiFeedbackCreator(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA
                    )
                )
		...
		.denied { permissionList->
			//被拒绝的权限集合
		}
```
