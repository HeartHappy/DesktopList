package com.hearthappy.desktoplist.test

import android.os.Parcel
import android.os.Parcelable
import com.hearthappy.desktoplist.interfaces.IBindDataModel

/**
 * Created Date 2021/1/4.
 * @author ChenRui
 * ClassDescription:
 */
class BindDataModel(var url: String?, var title: String?) : IBindDataModel {

    constructor(parcel: Parcel) : this(
        parcel.readString(), parcel.readString()
    ) {
    }

    override fun getAppUrl(): String {
        return url.toString()
    }

    override fun getAppName(): String {
        return title.toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        //        super.writeToParcel(parcel, flags)
        parcel.writeString(url)
        parcel.writeString(title)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BindDataModel> {
        override fun createFromParcel(parcel: Parcel): BindDataModel {
            return BindDataModel(parcel)
        }

        override fun newArray(size: Int): Array<BindDataModel?> {
            return arrayOfNulls(size)
        }
    }
}
