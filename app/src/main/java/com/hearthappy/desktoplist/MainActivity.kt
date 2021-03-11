package com.hearthappy.desktoplist

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.hearthappy.appstyle.AppStyle
import com.hearthappy.desktoplist.databinding.ActivityMainBinding
import com.hearthappy.desktoplist.databinding.ItemAppListBinding
import com.hearthappy.interfaces.IBindDataModel
import com.hearthappy.interfaces.ItemViewListener
import com.hearthappy.transformpage.PagerTransformer


class MainActivity : AppCompatActivity() {

    private var styleIndex = 0
    private var transformPagerIndex = 0
    private lateinit var viewBinding: ActivityMainBinding
    private val desktopDataModel = DesktopDataModel()
    private var searchStr = arrayOf("A", "B", "C", "D", "P")
    private var strIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        setContentView(R.layout.activity_main)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        viewBinding.apply {

            updateBtnText()

            setAppStyle()

            setDesktopTransform()

            searchData()

            /**
             * 参数分别是：1、绑定数据的集合  2、每行显示列数
             */
            dlv.init(desktopList = desktopDataModel.dataSources(), 4)

            setDesktopAdapterListener()
            sfl.setOnRefreshListener {
                dlv.init(desktopList = desktopDataModel.dataSources(), 4)
                sfl.isRefreshing = false
                Toast.makeText(this@MainActivity, "触发刷新了", Toast.LENGTH_SHORT).show()
            }

        }
    }

    @SuppressLint("SetTextI18n") private fun ActivityMainBinding.updateBtnText() {
        btnRefresh.text = "搜索${searchStr[strIndex]}"
    }


    /**
     * 数据的更新和恢复    注意：数据的更新不会记录存储，只是临时的UI显示，适用于搜索后的数据更新
     * @receiver ActivityMainBinding
     */
    private fun ActivityMainBinding.searchData() {
        btnRefresh.setOnClickListener {
            if (strIndex < searchStr.size) {
                val filter = desktopDataModel.dataSources().filter { it.getAppName().contains(searchStr[strIndex]) }
                filter.forEach { Log.d(TAG, "updateData: ${it.getAppName()}") }
                dlv.notifyDesktopDataChange(filter)
                strIndex++
                updateBtnText()
            } else {
                dlv.restoreDesktopData()
            }
        }
    }


    /**
     * 设置适配器视图与数据绑定监听。
     * @receiver ActivityMainBinding
     */
    private fun ActivityMainBinding.setDesktopAdapterListener() {
        dlv.setDesktopAdapterListener(object : ItemViewListener {
            override fun onClickItemView(bindDataModel: IBindDataModel) {
                val myBindDataModel = bindDataModel as BindDataModel
                Toast.makeText(this@MainActivity, "name:${myBindDataModel.getAppName()},id:${myBindDataModel.getAppId()}", Toast.LENGTH_SHORT).show()
            }

            override fun onBindView(
                position: Int,
                list: List<IBindDataModel>,
                viewBinding: ItemAppListBinding,
            ) {
                Glide.with(this@MainActivity).load(list[position].getAppUrl()).placeholder(R.mipmap.ic_launcher).error(android.R.drawable.ic_menu_report_image).into(viewBinding.appIcon)

                viewBinding.appName.text = list[position].getAppName()
                viewBinding.appName.setSubText("子文本:$position", delay = 5000)

            }
        })
    }


    /**
     * 设置滑动时的动画
     * @receiver ActivityMainBinding
     */
    private fun ActivityMainBinding.setDesktopTransform() {
        btnSwitchTransformPage.setOnClickListener {
            when (++transformPagerIndex % 3) {
                1 -> dlv.setTransformAnimation(PagerTransformer.AnimSpecies.Windmill).notifyChangeStyle()
                2 -> dlv.setTransformAnimation(PagerTransformer.AnimSpecies.FloatUp).notifyChangeStyle()
                else -> dlv.setTransformAnimation(PagerTransformer.AnimSpecies.Translate).notifyChangeStyle()
            }
            Toast.makeText(this@MainActivity, "切换成功", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * 设置应用图标显示样式
     * @receiver ActivityMainBinding
     */
    private fun ActivityMainBinding.setAppStyle() {
        btnSwitchAppStyle.setOnClickListener {
            when (++styleIndex % 3) {
                1 -> dlv.setAppStyle(AppStyle.Rounded(24)).notifyChangeStyle()
                2 -> dlv.setAppStyle(AppStyle.Circle).notifyChangeStyle()
                else -> dlv.setAppStyle(AppStyle.NotStyle).notifyChangeStyle()
            }
            Toast.makeText(this@MainActivity, "切换成功", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * 支持横竖屏方向下，显示不同列数
     * 注意：需要在清单文件中。在当前Activity中增加 android:configChanges="orientation|keyboardHidden|screenSize"
     * @param newConfig Configuration
     */
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Log.d(TAG, "onConfigurationChanged: 竖屏")
            viewBinding.dlv.init(desktopList = desktopDataModel.dataSources(), 4)
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Log.d(TAG, "onConfigurationChanged: 横屏")
            viewBinding.dlv.init(desktopList = desktopDataModel.dataSources(), 6)
        }
    }


    companion object {
        private const val TAG = "MainActivity"
    }

}
