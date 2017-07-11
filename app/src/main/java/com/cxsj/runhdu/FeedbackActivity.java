package com.cxsj.runhdu;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.HttpUtil;
import com.dd.CircularProgressButton;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用户反馈Activity
 */
public class FeedbackActivity extends BaseActivity {

    private LinearLayout rootLayout;
    private EditText feedbackText;
    private EditText contactText;
    private CircularProgressButton feedbackButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setToolbar(R.id.feedback_toolbar, true);
        feedbackText = (EditText) findViewById(R.id.feedback_text);
        contactText = (EditText) findViewById(R.id.feedback_contact);
        feedbackButton = (CircularProgressButton) findViewById(R.id.feedback_button);
        rootLayout = (LinearLayout) findViewById(R.id.feedback_root_layout);

        feedbackButton.setIndeterminateProgressMode(true);
        feedbackButton.setOnClickListener(v -> {
            String feedbackStr = feedbackText.getText().toString();
            String contactStr = contactText.getText().toString();
            if (TextUtils.isEmpty(feedbackStr)) {
                Toast.makeText(this, "请输入反馈信息！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(contactStr)) contactStr = "未填写";

            feedbackButton.setProgress(50);
            //提交反馈
            HttpUtil.load(URLs.FEEDBACK_URL)
                    .addParam("feedbackInformation", feedbackStr)
                    .addParam("contactWay", contactStr)
                    .post(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(() -> {
                                feedbackButton.setProgress(-1);
                                Snackbar.make(rootLayout, "网络连接失败！", Snackbar.LENGTH_LONG)
                                        .setAction("重试", v -> feedbackButton.callOnClick()).show();
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            runOnUiThread(() -> {
                                feedbackButton.setProgress(100);
                                if (result.equals("true")) {
                                    feedbackButton.setClickable(false);
                                    Toast.makeText(FeedbackActivity.this, "已收到您的反馈。", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    feedbackButton.setProgress(-1);
                                }
                            });
                        }
                    });
        });
    }
}
