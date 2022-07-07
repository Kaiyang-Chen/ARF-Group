package cn.edu.sjtu.arf.kotlin.product

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 *
 * @Author:         zhozicho
 * @Date:     2022/7/6 13:42
 * @Desc:
 */
@Parcelize
data class Product (
    var name: String? = null,
    var description: String? = null,
    var price: Float? = null,
    var picture: String? = null,
): Parcelable