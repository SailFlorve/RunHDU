package com.cxsj.runhdu

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
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
        }

        base64_button.setOnClickListener { _ ->
            val str = lab_text.text.toString()
            val base64Str = Base64.encode(str.toByteArray(), Base64.DEFAULT)
            lab_text.setText(String(base64Str))
            compress_info_text.text = "压缩后：${base64Str.size}"
        }

        GZip_decode_button.setOnClickListener { _ ->
            lab_text.setText(ZipUtil.decompress(lab_text.text.toString()))
        }

        base64_decode.setOnClickListener { _ ->
            lab_text.setText(String(Base64.decode(lab_text.text.toString(), Base64.DEFAULT)))
        }
        md5_button.setOnClickListener { _ ->
            lab_text.setText(MD5Util.encode(lab_text.text.toString()))
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
