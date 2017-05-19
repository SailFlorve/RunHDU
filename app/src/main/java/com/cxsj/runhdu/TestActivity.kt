package com.cxsj.runhdu

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.cxsj.runhdu.utils.MD5Util
import com.cxsj.runhdu.utils.ZipUtil
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        setToolbar(R.id.toolbar_test, true)
        compress_info_text.text = "压缩前：${lab_text.length()}"

        GZip_button.setOnClickListener { _ ->
            val str = lab_text.text.toString()
            val zipStr = ZipUtil.compress(str)
            lab_text.setText(zipStr)
            compress_info_text.text = "压缩后：${zipStr.length}"
            GZip_button.isClickable = false
            zip_button.isClickable = false
        }

        zip_button.setOnClickListener { _ ->
            val str = lab_text.text.toString()
            val zipStr = ZipUtil.compress(str)
            lab_text.setText(zipStr)
            compress_info_text.text = "压缩后：${zipStr.length}"
            GZip_button.isClickable = false
            zip_button.isClickable = false
        }

        md5_button.setOnClickListener { _ ->
            lab_text.setText(MD5Util.encode(lab_text.text.toString()))
            md5_button.isClickable = false
        }

        lab_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                compress_info_text.text = "压缩前：${s.toString().length}"
            }
        })
    }
}
