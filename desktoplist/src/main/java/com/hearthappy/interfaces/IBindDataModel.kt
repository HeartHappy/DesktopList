package com.hearthappy.interfaces

import android.os.Parcelable

/**
 * Created Date 2020/12/21.
 * @author ChenRui
 * ClassDescription:自定义数据结构与接口实现数据绑定
 */
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

    /**
     * 应用唯一id。如果存在相同应用名称，则使用该id区分，如果没有重复名，可忽略
     * @return String
     */
    fun getAppId(): String
}