package cn.edu.sjtu.arf.kotlin.carthelper

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class CartItemDisplay(var UID: String? = null,
                      var name: String? = null,
                      var price: String? = null,
                      imageUrl: String? = null) {
    var imageUrl: String? by CartItemDisplayDelegate(imageUrl)
}

class CartItemDisplayDelegate private constructor ():
        ReadWriteProperty<Any?, String?> {
    private var _value: String? = null
        // Kotlin custom setter
        set(newValue) {
            newValue ?: run {
                field = null
                return
            }
            field = if (newValue == "null" || newValue.isEmpty()) null else newValue
        }

    constructor(initialValue: String?): this() { _value = initialValue }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = _value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        _value = value
    }
}