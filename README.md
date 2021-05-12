##### 效果图
<div align=center><img src="https://github.com/HeartHappy/DesktopList/blob/master/DesktopView.gif" width="500"/></div>


##### 1、项目gradle
```
repositories {
        maven { url 'https://jitpack.io' }
}
```

##### 2、app gradle
```
dependencies {
    implementation 'com.github.hearthappy:DesktopList:1.0.0'
}
```

##### 3、布局中使用DesktopView

```
<com.hearthappy.desktoplist.DesktopListView
            android:id="@+id/desktopView"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
```

##### 4、你的数据结构实现IBindDataModel接口，并序列化。注意：appId必须唯一可为空，如果appName有重名，请用appId区分），参考如下：
```
@kotlinx.parcelize.Parcelize
class BindDataModel(private var url: String, private var title: String, private var appId: String = "") : IBindDataModel {

    override fun getAppUrl(): String {
        return url
    }

    override fun getAppName(): String {
        return title
    }

    override fun getAppId(): String {
        return appId
    }
}
```

##### 5、创建或由后台返回的IBindDataModel数据集合

```
class DesktopDataModel {
    private val dataSources = ArrayList<BindDataModel>().apply {
        add(BindDataModel("http://e.hiphotos.baidu.com/image/pic/item/a1ec08fa513d2697e542494057fbb2fb4316d81e.jpg", "AAA", "key1"))
        add(BindDataModel("http://e.hiphotos.baidu.com/image/pic/item/a1ec08fa513d2697e542494057fbb2fb4316d81e.jpg", "AAA", "key2"))
        add(BindDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg", "BBB"))
        add(BindDataModel("https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg", "CCC"))
        add(BindDataModel("https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg", "DDD"))
        ...
        }


    fun dataSources(): List<BindDataModel> {
        return dataSources
    }
```

##### 6、初始化DesktopView数据

```
/**
 * 参数分别是：1、绑定数据的集合  2、每行显示列数
 */
val desktopDataModel = DesktopDataModel()
desktopView.init(desktopList = desktopDataModel.dataSources(), 4)
```

##### 7、设置DesktopView的事件监听

```
desktopView.setDesktopAdapterListener(object : ItemViewListener {

            //点击事件
            override fun onClickItemView(bindDataModel: IBindDataModel) {
                val myBindDataModel = bindDataModel as BindDataModel
                Toast.makeText(
                    this@MainActivity,
                    "name:${myBindDataModel.getAppName()},id:${myBindDataModel.getAppId()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            
            /**
             *
             * @param position Int 每页的索引
             * @param list List<IBindDataModel> 每页的数据
             * @param viewBinding ItemAppListBinding 你的视图View
             * @param showAppId Boolean 是否显示AppId(true:显示appId，false:显示appName) 默认为false
             */
            @SuppressLint("SetTextI18n") override fun onBindView(
                position: Int,
                list: List<IBindDataModel>,
                viewBinding: ItemAppListBinding,
                showAppId: Boolean,
            ) { //显示Icon
                Glide.with(this@MainActivity).load(list[position].getAppUrl())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(android.R.drawable.ic_menu_report_image).into(viewBinding.appIcon)

                //显示id，或应用名称
                if (showAppId) {
                    val appId = list[position].getAppId()
                    if (appId.isBlank()) {
                        viewBinding.appName.text = list[position].getAppName()
                    } else {
                        viewBinding.appName.text = list[position].getAppId()
                    }
                } else {
                    viewBinding.appName.text = list[position].getAppName()
                }
            }
        })
```


##### 8、扩展
###### 8.1、设置桌面侧滑动画，分别提供了：Windmill、FloatUp、Translate
```
    desktopView.setTransformAnimation(PagerTransformer.AnimSpecies.Windmill)
                    .notifyChangeStyle()
```
###### 8.2、设置图标显示风格，圆形：AppStyle.Circle，没有样式：AppStyle.NotStyle，圆角矩形:AppStyle.Rounded(24)如下
```
    //圆角矩形
    desktopView.setAppStyle(AppStyle.Rounded(24)).notifyChangeStyle()
    
```
###### 8.3、设置显示应用Id，默认false：显示应用名

```
    desktopView.isShowAppId = true
```


###### 8.4、如果你的应用需要支持横竖屏，并且在不通屏幕下显示不同列数。否则请强制竖屏或横屏。参考如下：
```
/**
     * 支持横竖屏方向下，显示不同列数
     * 注意：需要在清单文件中。在当前Activity中增加 android:configChanges="orientation|keyboardHidden|screenSize"
     * @param newConfig Configuration
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "onConfigurationChanged: 竖屏")
            desktopView.init(desktopList = desktopDataModel.dataSources(), 4)
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "onConfigurationChanged: 横屏")
            desktopView.init(desktopList = desktopDataModel.dataSources(), 6)
        }
    }
```
###### 8.5、如果你需要使用搜索并更新列表，请参考如下

```
//例如搜索包含A的数据
val filter = desktopDataModel.dataSources().filter {
                    it.getAppName().contains("A")
                }
//通过notifyDesktopDataChange（过滤后的数据）更新            
desktopView.notifyDesktopDataChange(filter)

//恢复原数据
desktopView.restoreDesktopData()
```

##### 注意事项：如果将DesktopView作为子控件，父控件也需要手势事件，例如下拉刷新会存在事件冲突问题。请参考Demo中提供的DesktopRefreshLayout解决















