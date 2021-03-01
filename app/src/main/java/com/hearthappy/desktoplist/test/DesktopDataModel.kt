package com.hearthappy.desktoplist.test

import com.hearthappy.desktoplist.interfaces.IDesktopDataModel

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription:数据类
 */
class DesktopDataModel : IDesktopDataModel<BindDataModel> {
    private val mutableListOf = ArrayList<BindDataModel>()

    private fun generateTitle(): String {
        return ('A'..'z').map { it }.shuffled().subList(0, 4).joinToString("")
    }

    private  fun generateDataModel(url: String) {
        mutableListOf.add(BindDataModel(url,generateTitle()))
    }


    private fun initDataSources(): MutableList<BindDataModel> {
        generateDataModel("http://e.hiphotos.baidu.com/image/pic/item/a1ec08fa513d2697e542494057fbb2fb4316d81e.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/IJhforasCNd46FK.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/wsUAxSIMtXfVh5W.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/eHfs1vYJDtMzyNP.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/1gktxsnzqJLSaVm.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/fS4r1aKhVvbz5JF.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/AQRoOnbycmTgwWF.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/Ekba7zI95TywMNK.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/CSi1tkGJYonBMxV.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/E7goR89IqH4wxiK.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/Up1IvFCJPhmwHed.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/xMSbJNDX3QshWc4.jpg")

        generateDataModel("http://c.hiphotos.baidu.com/image/pic/item/30adcbef76094b36de8a2fe5a1cc7cd98d109d99.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg")
        generateDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg")
        generateDataModel("http://e.hiphotos.baidu.com/image/pic/item/4e4a20a4462309f7e41f5cfe760e0cf3d6cad6ee.jpg")
        generateDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg")
        generateDataModel("http://e.hiphotos.baidu.com/image/pic/item/4e4a20a4462309f7e41f5cfe760e0cf3d6cad6ee.jpg")
        generateDataModel("http://b.hiphotos.baidu.com/image/pic/item/9d82d158ccbf6c81b94575cfb93eb13533fa40a2.jpg")
        generateDataModel("http://e.hiphotos.baidu.com/image/pic/item/4bed2e738bd4b31c1badd5a685d6277f9e2ff81e.jpg")
        generateDataModel("http://g.hiphotos.baidu.com/image/pic/item/0d338744ebf81a4c87a3add4d52a6059252da61e.jpg")
        generateDataModel("http://a.hiphotos.baidu.com/image/pic/item/f2deb48f8c5494ee5080c8142ff5e0fe99257e19.jpg")
        generateDataModel("http://f.hiphotos.baidu.com/image/pic/item/4034970a304e251f503521f5a586c9177e3e53f9.jpg")
        generateDataModel("http://b.hiphotos.baidu.com/image/pic/item/279759ee3d6d55fbb3586c0168224f4a20a4dd7e.jpg")
        generateDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg")
        generateDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg")
        generateDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg")
        generateDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg")
        generateDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg")
        generateDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg")
        generateDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg")
        generateDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg")
        generateDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg")

        generateDataModel("https://i.loli.net/2019/09/09/Up1IvFCJPhmwHed.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/xMSbJNDX3QshWc4.jpg")
        generateDataModel("http://c.hiphotos.baidu.com/image/pic/item/30adcbef76094b36de8a2fe5a1cc7cd98d109d99.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/Up1IvFCJPhmwHed.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg")
        generateDataModel("https://i.loli.net/2019/09/09/xMSbJNDX3QshWc4.jpg")
        generateDataModel("http://c.hiphotos.baidu.com/image/pic/item/30adcbef76094b36de8a2fe5a1cc7cd98d109d99.jpg")
        return mutableListOf.toMutableList()
    }

    override fun dataSources(): List<BindDataModel> {
        return initDataSources()
    }

    override fun dataSize(): Int {
        return mutableListOf.size
    }


}