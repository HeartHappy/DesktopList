##### 一、创建数据结构BindDataModel类 ，继承IBindDataModel接口，实现URL与应用名称的绑定
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
##### 三、创建自己的适配器，继承自IDesktopListAdapter接口，实现数据在视图中的显示
###### 例：
```
@Parcelize class DesktopListAdapterImpl : IDesktopListAdapter {

    /**
     * 适配器的布局
     * @return Int
     */
    override fun onAdapterResId(): Int {
        return R.layout.item_app_list
    }

    override fun onBindMyViewHolder(
        context: Context?,
        holder: DesktopListAdapter.ViewHolder,
        position: Int,
        list: List<IBindDataModel>,
        appStyle: AppStyle
    ) {
        holder.tvText.text = list[position].getAppName()
        Glide.with(it).load(list[position].getAppUrl()).into(holder.ivAppIcon)
    }

    /**
     * 注意：该返回值同你的布局高度填写，用于计算每个页面最多显示数量时使用
     * @return Int  R.dimen.dp_~
     */
    override fun onItemViewHeight(): Int {
        return R.dimen.dp_110
    }
}
```

##### 四、DesktopListView的使用

###### 4.1、布局中的使用
```
<com.hearthappy.desktoplist.desktopview.DesktopListView
        android:id="@+id/dlv"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        app:layout_constraintTop_toBottomOf="@+id/toolbar"
        app:layout_constraintVertical_weight="1" />
```
###### 4.2、Activity中实现DesktopListView的初始化，
参数分别是：1、每行显示列数  2、实现IDesktopDataModel接口的数据集合  3、实现IDesktopListAdapter接口的适配器
```
 dlv.init(3, DesktopDataModel(), DesktopListAdapterImpl())
```


##### 五、接口方法简介
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

    /**
     * @return Int 返回数据集合的数量
     */
    fun dataSize(): Int

}
```

```
interface IDesktopListAdapter : Parcelable {

    /**
     *
     * @return Int 返回ItemView布局id
     */
    fun onAdapterResId(): Int

    /**
     * 绑定ViewHolder的回调
     * @param context Context?
     * @param holder ViewHolder
     * @param position Int 下标
     * @param list List<IBindDataModel> 数据集合
     * @param appStyle AppStyle 图标显示的样式
     */
    fun onBindMyViewHolder(
        context: Context?,
        holder: DesktopListAdapter.ViewHolder,
        position: Int,
        list: List<IBindDataModel>,
        appStyle: AppStyle
    )

    /**
     *
     * @return Int 返回布局的高度
     */
    fun onItemViewHeight(): Int
}

```


##### 六、注意事项（第一、三步需要实现Parcelable接口，kotlin可在类上方通过@Parcelize注解自实现）