package com.hearthappy.desktoplist.test

import com.hearthappy.desktoplist.interfaces.IDesktopDataModel

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription:数据类
 */
class DesktopDataModel : IDesktopDataModel<BindDataModel> {
    private val mutableListOf = ArrayList<BindDataModel>()


    private fun initDataSources(): MutableList<BindDataModel> {
        mutableListOf.add(BindDataModel("http://e.hiphotos.baidu.com/image/pic/item/a1ec08fa513d2697e542494057fbb2fb4316d81e.jpg", "AAA"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg", "BBB"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg", "CCC"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg", "DDD"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/IJhforasCNd46FK.jpg", "EEE"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/wsUAxSIMtXfVh5W.jpg", "FFF"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/eHfs1vYJDtMzyNP.jpg", "GGG"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/1gktxsnzqJLSaVm.jpg", "HHH"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg", "III"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/fS4r1aKhVvbz5JF.jpg", "JJJ"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/AQRoOnbycmTgwWF.jpg", "KKK"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/Ekba7zI95TywMNK.jpg", "LLL"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/CSi1tkGJYonBMxV.jpg", "MMM"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/E7goR89IqH4wxiK.jpg", "NNN"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/Up1IvFCJPhmwHed.jpg", "OOO"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg", "PPP"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg", "QQQ"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/xMSbJNDX3QshWc4.jpg", "RRR"))

        mutableListOf.add(BindDataModel("http://c.hiphotos.baidu.com/image/pic/item/30adcbef76094b36de8a2fe5a1cc7cd98d109d99.jpg", "SSS"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg", "TTT"))
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "UUU"))
        mutableListOf.add(BindDataModel("http://e.hiphotos.baidu.com/image/pic/item/4e4a20a4462309f7e41f5cfe760e0cf3d6cad6ee.jpg", "VVV"))
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "WWW"))
        mutableListOf.add(BindDataModel("http://e.hiphotos.baidu.com/image/pic/item/4e4a20a4462309f7e41f5cfe760e0cf3d6cad6ee.jpg", "XXX"))
        mutableListOf.add(BindDataModel("http://b.hiphotos.baidu.com/image/pic/item/9d82d158ccbf6c81b94575cfb93eb13533fa40a2.jpg", "YYY"))
        mutableListOf.add(BindDataModel("http://e.hiphotos.baidu.com/image/pic/item/4bed2e738bd4b31c1badd5a685d6277f9e2ff81e.jpg", "ZZZ"))
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/0d338744ebf81a4c87a3add4d52a6059252da61e.jpg", "ABS"))
        mutableListOf.add(BindDataModel("http://a.hiphotos.baidu.com/image/pic/item/f2deb48f8c5494ee5080c8142ff5e0fe99257e19.jpg", "ABC"))
        mutableListOf.add(BindDataModel("http://f.hiphotos.baidu.com/image/pic/item/4034970a304e251f503521f5a586c9177e3e53f9.jpg", "DSE"))
        mutableListOf.add(BindDataModel("http://b.hiphotos.baidu.com/image/pic/item/279759ee3d6d55fbb3586c0168224f4a20a4dd7e.jpg", "DES"))
        mutableListOf.add(BindDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg", "AES"))
        mutableListOf.add(BindDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg", "ADC"))
        mutableListOf.add(BindDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg", "NDP"))
        mutableListOf.add(BindDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg", "UDP"))
        mutableListOf.add(BindDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg", "TCP"))
        mutableListOf.add(BindDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg", "ADP"))
        mutableListOf.add(BindDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg", "GFG"))
        mutableListOf.add(BindDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg", "UFO"))
        mutableListOf.add(BindDataModel("https://alifei02.cfp.cn/creative/vcg/veer/800water/veer-134695071.jpg", "DYG"))

        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/Up1IvFCJPhmwHed.jpg", "QGH"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg", "AGC"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg", "RMB"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg", "SQL"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/xMSbJNDX3QshWc4.jpg", "ODL"))
        mutableListOf.add(BindDataModel("http://c.hiphotos.baidu.com/image/pic/item/30adcbef76094b36de8a2fe5a1cc7cd98d109d99.jpg", "NEW"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/Up1IvFCJPhmwHed.jpg", "NOT"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/zqvDRAUk2jKhZfT.jpg", "TOP"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/tQ9gwTiJMR1bq5s.jpg", "LEFT"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/u5NMgOH8jkEa6Xw.jpg", "RIG"))
        mutableListOf.add(BindDataModel("https://i.loli.net/2019/09/09/xMSbJNDX3QshWc4.jpg", "WID"))
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "INS")) //初始化后新增的
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "DNS")) //初始化后新增的第2条
        mutableListOf.add(BindDataModel("http://c.hiphotos.baidu.com/image/pic/item/30adcbef76094b36de8a2fe5a1cc7cd98d109d99.jpg", "MAC"))

        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "YDG")) //批量新增
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "YDJ"))
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "KPL"))
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "TCL"))
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "PPT"))
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "UAM"))
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "NGD"))
        mutableListOf.add(BindDataModel("http://g.hiphotos.baidu.com/image/pic/item/55e736d12f2eb938d5277fd5d0628535e5dd6f4a.jpg", "XIA"))
        return mutableListOf.toMutableList()
    }

    override fun dataSources(): List<BindDataModel> {
        return initDataSources()
    }

}