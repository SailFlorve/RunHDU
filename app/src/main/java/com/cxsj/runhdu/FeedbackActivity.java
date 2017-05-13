package com.cxsj.runhdu;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.cxsj.runhdu.constant.URLs;
import com.cxsj.runhdu.utils.HttpUtil;
import com.dd.CircularProgressButton;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FeedbackActivity extends BaseActivity {

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
        feedbackButton.setIndeterminateProgressMode(true);

        feedbackButton.setOnClickListener(v -> {
            String feedbackStr = feedbackText.getText().toString();
            String contactStr = contactText.getText().toString();
            if (TextUtils.isEmpty(feedbackStr)) {
                Toast.makeText(this, "请输入反馈信息！", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(contactStr)) contactStr = "0";
            feedbackButton.setProgress(50);
            HttpUtil.load(URLs.FEEDBACK_URL)
                    .addParam("feedbackInformation", feedbackStr)
                    .addParam("contactWay", contactStr)
                    .post(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            runOnUiThread(() -> {
                                feedbackButton.setProgress(0);
                                feedbackButton.setIdleText("无网络，点击重试");
                            });

                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String result = response.body().string();
                            runOnUiThread(() -> {
                                if (result.equals("true")) {
                                    feedbackButton.setClickable(false);
                                    feedbackButton.setProgress(100);
                                } else {
                                    feedbackButton.setProgress(0);
                                    feedbackButton.setIdleText("失败，点击重试");
                                }
                            });
                        }
                    });
        });
    }
}
