##### 一、后台返回的数据结构BindDataModel类 ，继承IBindDataModel接口，实现URL与应用名称的绑定
###### 例：
 ```
@Parcelize class BindDataModel(private var url: String?, private var title: String?) : IBindDataModel {

    override fun getAppUrl(): String {
        return url.toString()
    }

    override fun getAppName(): String {
        return title.toString()
    }
}  
 ```
##### 二、创建BindDataModel的集合，继承自IDesktopDataModel<BindDataModel>并指定泛型，实现数据源与桌面控件的绑定
###### 例：
```
class DesktopDataModel: IDesktopDataModel<BindDataModel> {
    private val mutableListOf = ArrayList<BindDataModel>()
    private fun initDataSources(): MutableList<BindDataModel> {
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg", "222"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg", "333"))
        return mutableListOf.toMutableList()
    }

    override fun dataSources(): List<BindDataModel> {
        return initDataSources()
    }

    override fun dataSize(): Int {
       return mutableListOf.size
    }
}

```


##### 三、DesktopListView的使用

###### 3.1、布局中的使用
```
<com.hearthappy.desktoplist.desktopview.DesktopListView
        android:id="@+id/dlv"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
###### 3.2、Activity中实现DesktopListView的初始化，
参数分别是：  1、实现IDesktopDataModel接口的数据集合 2、每行显示列数
```
 dlv.init(DesktopDataModel(),3)
```


#####  四、接口方法简介
```
interface IBindDataModel : Parcelable {

    /**
     * @return String 返回图标的URL
     */
    fun getAppUrl(): String

    /**
     *
     * @return String 返回应用的名称
     */
    fun getAppName(): String
}
```

```
interface IDesktopDataModel<out DB : IBindDataModel> {

    /**
     * @return List<DB> 返回数据的集合
     */
    fun dataSources(): List<DB>

}
```



##### 五、注意事项（第一、三步需要实现Parcelable接口，kotlin可在类上方通过@Parcelize注解自实现）


##### 六、QA
Q、是否支持横竖屏
A、支持。需要在清单文件中。在当前Activity中增加 android:configChanges="orientation|keyboardHidden|screenSize"，然后在Activity中监听onConfigurationChanged回调，设置不同屏幕方向状态下的初始化