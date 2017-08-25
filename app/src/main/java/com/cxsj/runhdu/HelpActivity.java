package com.cxsj.runhdu;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cxsj.runhdu.adapters.HelpAdapter;
import com.cxsj.runhdu.bean.HelpItem;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.MeasureSpec.makeMeasureSpec;


/**
 * 帮助
 */
public class HelpActivity extends BaseActivity {
    private List<HelpItem> helpList = new ArrayList<>();
    private final int duration = 200;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setToolbar(R.id.help_toolbar, true);
        initHelpItem();
        HelpAdapter adapter = new HelpAdapter(HelpActivity.this, R.layout.help_item, helpList);
        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            TextView infoText = (TextView) view.findViewById(R.id.help_info);
            ImageView imagemore = (ImageView) view.findViewById(R.id.help_more);
            int spaceWidth = makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY);
            int spaceHeight = makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            infoText.measure(spaceWidth, spaceHeight);
            int height = infoText.getMeasuredHeight();
            if (infoText.getVisibility() == View.GONE) {
                show(infoText, height);
                more(imagemore);
            } else {
                dismiss(infoText, height);
                less(imagemore);
            }
        });
    }

    private void initHelpItem() {
        String[] titleArray = getResources().getStringArray(R.array.help_title);
        String[] answerArray = getResources().getStringArray(R.array.help_answer);
        for (int i = 0; i < titleArray.length; i++) {
            helpList.add(new HelpItem(titleArray[i], answerArray[i]));
        }
    }

    public void show(final View v, int height) {
        v.setVisibility(View.VISIBLE);
        ValueAnimator animator = ValueAnimator.ofInt(0, height);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            v.getLayoutParams().height = (int) (Integer) animation.getAnimatedValue();
            v.setLayoutParams(v.getLayoutParams());
        });
        animator.start();
    }

    public void dismiss(final View v, int height) {

        ValueAnimator animator = ValueAnimator.ofInt(height, 0);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            int value = (Integer) animation.getAnimatedValue();
            if (value == 0) {
                v.setVisibility(View.GONE);
            }
            v.getLayoutParams().height = value;
            v.setLayoutParams(v.getLayoutParams());
        });
        animator.start();
    }

    public void more(final View v) {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 90);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            float value = (Float) animation.getAnimatedValue();
            v.setRotation(value);
        });
        animator.start();
    }

    public void less(final View v) {
        ValueAnimator animator = ValueAnimator.ofFloat(90, 0);
        animator.setDuration(duration);
        animator.addUpdateListener(animation -> {
            float value = (Float) animation.getAnimatedValue();
            v.setRotation(value);
        });
        animator.start();
    }

}
