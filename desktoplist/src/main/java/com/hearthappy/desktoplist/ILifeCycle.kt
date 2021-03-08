package com.hearthappy.desktoplist

import android.os.Parcelable

/**
 * Created Date 2021/2/26.
 * @author ChenRui
 * ClassDescription:Fragment生命周期接口
 */
interface ILifeCycle : Parcelable {
    fun onCreate(position: Int)

    fun onCreateView(position: Int)

    fun onViewCreated(position: Int)

    fun onResume(position: Int)

    fun onDestroyView(position: Int)

    fun onDestroy(position: Int)

    fun onUserVisibleHint(visibleToUser: Boolean, position: Int)


}