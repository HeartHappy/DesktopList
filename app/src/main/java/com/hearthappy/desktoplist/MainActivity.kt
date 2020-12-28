package com.hearthappy.desktoplist

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hearthappy.desktoplist.desktopview.DesktopListAdapter
import com.hearthappy.desktoplist.desktopview.IDesktopList
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var style = 0
        btnAddPage.setOnClickListener {
            ++style
            if (style > 2) {
                style = 0
            }
            when (style) {
                1 -> {
                    dlv.appStyle(true)
                }
                2 -> {
                    dlv.appStyle(true, 8)
                }
                else -> {
                    dlv.appStyle()
                }
            }
        }


        //初始化数据集
        val dataSources = initDataSources()
        dlv.init(dataSources.size, 3, 15, object : IDesktopList {
            override fun dataSources(): MutableList<DataModel> {
                return dataSources
            }

            override fun adapterResId(): Int {
                return R.layout.item_app_list
            }

            override fun onBindViewHolder(
                holder: DesktopListAdapter.ViewHolder,
                position: Int,
                listData: MutableList<DataModel>
            ) {

            }

            override fun viewMoveBounds(leftBorder: Boolean, rightBorder: Boolean, moveView: View) {

            }
        })
    }

    private fun insetSources(): DataModel {
        return DataModel(
            "https://alifei01.cfp.cn/creative/vcg/veer/1600water/veer-375427800.jpg",
            "插入的"
        )
    }


    private fun initDataSources(): MutableList<DataModel> {
        val mutableListOf = mutableListOf<DataModel>()
        mutableListOf.add(
            DataModel(
                "http://e.hiphotos.baidu.com/image/pic/item/a1ec08fa513d2697e542494057fbb2fb4316d81e.jpg",
                "111"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg",
                "222"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg",
                "333"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg",
                "444"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/IJhforasCNd46FK.jpg",
                "555"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/wsUAxSIMtXfVh5W.jpg",
                "666"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/eHfs1vYJDtMzyNP.jpg",
                "777"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/1gktxsnzqJLSaVm.jpg",
                "888"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/fS4r1aKhVvbz5JF.jpg",
                "999"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/AQRoOnbycmTgwWF.jpg",
                "mnnmc"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/Ekba7zI95TywMNK.jpg",
                "jfka"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/CSi1tkGJYonBMxV.jpg",
                "fvcc"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/E7goR89IqH4wxiK.jpg",
                "cvv"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/Up1IvFCJPhmwHed.jpg",
                "tata"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg",
                "ytss"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg",
                "afa"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg",
                "fdaf"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/xMSbJNDX3QshWc4.jpg",
                "bvxb"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://c.hiphotos.baidu.com/image/pic/item/30adcbef76094b36de8a2fe5a1cc7cd98d109d99.jpg",
                "czvcz"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg",
                "vzvzx"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg",
                "vccv"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://e.hiphotos.baidu.com/image/pic/item/4e4a20a4462309f7e41f5cfe760e0cf3d6cad6ee.jpg",
                "kjk"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg",
                "nmn"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://e.hiphotos.baidu.com/image/pic/item/4e4a20a4462309f7e41f5cfe760e0cf3d6cad6ee.jpg",
                "opo"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://b.hiphotos.baidu.com/image/pic/item/9d82d158ccbf6c81b94575cfb93eb13533fa40a2.jpg",
                "hjj"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://e.hiphotos.baidu.com/image/pic/item/4bed2e738bd4b31c1badd5a685d6277f9e2ff81e.jpg",
                "kjh"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://g.hiphotos.baidu.com/image/pic/item/0d338744ebf81a4c87a3add4d52a6059252da61e.jpg",
                "gff"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://a.hiphotos.baidu.com/image/pic/item/f2deb48f8c5494ee5080c8142ff5e0fe99257e19.jpg",
                "rrt"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://f.hiphotos.baidu.com/image/pic/item/4034970a304e251f503521f5a586c9177e3e53f9.jpg",
                "werq"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://b.hiphotos.baidu.com/image/pic/item/279759ee3d6d55fbb3586c0168224f4a20a4dd7e.jpg",
                "fdaf"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg",
                "ggf"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg",
                "fda"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg",
                "gfd"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg",
                "vbx"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg",
                "bvn"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg",
                "zdvc"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg",
                "vbx"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg",
                "bvn"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg",
                "zdvc"
            )
        )

        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/Up1IvFCJPhmwHed.jpg",
                "tata"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg",
                "ytss"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg",
                "afa"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg",
                "fdaf"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/xMSbJNDX3QshWc4.jpg",
                "bvxb"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://c.hiphotos.baidu.com/image/pic/item/30adcbef76094b36de8a2fe5a1cc7cd98d109d99.jpg",
                "czvcz"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/Up1IvFCJPhmwHed.jpg",
                "tata"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg",
                "ytss"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg",
                "afa"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg",
                "fdaf"
            )
        )
        mutableListOf.add(
            DataModel(
                "https://i.loli.net/2019/09/09/xMSbJNDX3QshWc4.jpg",
                "bvxb"
            )
        )
        mutableListOf.add(
            DataModel(
                "http://c.hiphotos.baidu.com/image/pic/item/30adcbef76094b36de8a2fe5a1cc7cd98d109d99.jpg",
                "czvcz"
            )
        )
        return mutableListOf
    }
}