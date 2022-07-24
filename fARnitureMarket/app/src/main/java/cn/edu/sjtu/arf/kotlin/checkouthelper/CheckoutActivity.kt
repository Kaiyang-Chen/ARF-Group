package cn.edu.sjtu.arf.kotlin.checkouthelper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import cn.edu.sjtu.arf.R
import cn.edu.sjtu.arf.kotlin.carthelper.CartItemDisplayStore
import cn.edu.sjtu.arf.kotlin.carthelper.CartItemDisplayStore.totalPrice
import cn.edu.sjtu.arf.kotlin.carthelper.CartItemUIDStore
import cn.edu.sjtu.arf.kotlin.carthelper.CartItemUIDStore.cartitemUIDs

class CheckoutActivity : AppCompatActivity() {
    lateinit var address: EditText
    lateinit var cardNumber: EditText
    lateinit var month: EditText
    lateinit var year: EditText
    lateinit var cvc: EditText
    lateinit var checkoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_back)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initViews()

        listener()
    }

    private fun initViews() {
        address = findViewById<EditText>(R.id.pay_address)
        cardNumber = findViewById<EditText>(R.id.pay_card)
        month = findViewById<EditText>(R.id.pay_month)
        year = findViewById<EditText>(R.id.pay_year)
        cvc = findViewById<EditText>(R.id.pay_cvc)
        checkoutButton = findViewById<Button>(R.id.pay)
        checkoutButton.text = "Confirm and Pay ¥ " + totalPrice.toString().format("%.1f")
    }

    private fun listener() {
        checkoutButton.setOnClickListener(View.OnClickListener {
            if (address.text.toString().isEmpty()) {
                Toast.makeText(this, "Empty Address", Toast.LENGTH_SHORT).show()
            }
            else if (cardNumber.text.toString().length != 16) {
                Toast.makeText(this, "Wrong Card Number", Toast.LENGTH_SHORT).show()
            }
            else if (!arrayOf("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12").contains(month.text.toString())) {
                 Toast.makeText(this, "Wrong Month", Toast.LENGTH_SHORT).show()
            }
            else if (year.text.toString().length != 2) {
                Toast.makeText(this, "Wrong Year", Toast.LENGTH_SHORT).show()
            }
            else if (cvc.text.toString().length != 3) {
                Toast.makeText(this, "Wrong CVC/CVV2", Toast.LENGTH_SHORT).show()
            }
            else {
                //cartitemUIDs
                //
                Toast.makeText(this, "Paied Successfully!", Toast.LENGTH_SHORT).show()
                checkoutButton.text = "No Furniture To Check Out"
                CartItemDisplayStore.cartitemdisplays.clear()
                CartItemUIDStore.getCartItemUIDs()
                finish()
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }

        return super.onOptionsItemSelected(item)
    }
}