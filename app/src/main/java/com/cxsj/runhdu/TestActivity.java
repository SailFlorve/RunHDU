package com.cxsj.runhdu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.cxsj.runhdu.utils.ZipUtil;

public class TestActivity extends AppCompatActivity {

    private Button button;
    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        button = (Button) findViewById(R.id.lab_button);
        text = (EditText) findViewById(R.id.lab_text);
        button.setOnClickListener(v -> {
            if (button.getText().equals("压缩")) {
                button.setText("解压");
                String str = text.getText().toString();
                String zipStr = ZipUtil.compress(text.getText().toString());
                text.setText(String.format(zipStr));
            } else {
                button.setText("压缩");
                text.setText(ZipUtil.decompress(text.getText().toString()));
            }
        });
    }
}
