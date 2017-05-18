package com.cxsj.runhdu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cxsj.runhdu.utils.ZipUtil;

public class TestActivity extends BaseActivity {

    private Button GzipButton;
    private Button zipButton;
    private EditText text;
    private TextView compressInfoText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setToolbar(R.id.toolbar_test, true);
        GzipButton = (Button) findViewById(R.id.GZip_compress_button);
        zipButton = (Button) findViewById(R.id.zip_compress_button);
        text = (EditText) findViewById(R.id.lab_text);
        compressInfoText = (TextView) findViewById(R.id.compress_info_text);
        compressInfoText.setText(String.format("压缩前：%d", text.length()));
        GzipButton.setOnClickListener(v -> {
            String str = text.getText().toString();
            String zipStr = ZipUtil.compress(str);
            text.setText(zipStr);
            compressInfoText.setText(String.format("压缩后%d", zipStr.length()));
            GzipButton.setClickable(false);
            zipButton.setClickable(false);
        });
        zipButton.setOnClickListener(v -> {
            String str = text.getText().toString();
            String zipStr = ZipUtil.compressForZip(str);
            text.setText(zipStr);
            compressInfoText.setText(String.format("压缩后%d", zipStr.length()));
            GzipButton.setClickable(false);
            zipButton.setClickable(false);
        });
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                compressInfoText.setText(String.format("压缩前%d", s.toString().length()));
            }
        });
    }
}
