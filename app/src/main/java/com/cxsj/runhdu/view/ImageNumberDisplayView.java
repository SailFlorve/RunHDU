package com.cxsj.runhdu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxsj.runhdu.R;

/**
 * Created by Sail on 2017/4/15 0015.
 * 显示数字及其表示图片。
 */

public class ImageNumberDisplayView extends LinearLayout {
    private ImageView imageView;
    private TextView numberTextView;
    private TextView unitTextView;
    private TextView descriptionTextView;

    private String number;
    private int image_res;
    private String unit;
    private String des;

    public ImageNumberDisplayView(Context context) {
        super(context);
        initView();
    }

    public ImageNumberDisplayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public ImageNumberDisplayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ImageNumberDisplayView);
        number = a.getString(R.styleable.ImageNumberDisplayView_number);
        image_res = a.getResourceId(R.styleable.ImageNumberDisplayView_imageSrc, R.drawable.ic_steps);
        unit = a.getString(R.styleable.ImageNumberDisplayView_unit);
        des = a.getString(R.styleable.ImageNumberDisplayView_description);
        setNumber(number);
        setImage(image_res);
        setUnit(unit);
        descriptionTextView.setText(des);
        a.recycle();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.image_number_display, this);
        numberTextView = (TextView) view.findViewById(R.id.ind_number_text);
        imageView = (ImageView) view.findViewById(R.id.ind_number_indicate_image);
        unitTextView = (TextView) view.findViewById(R.id.ind_unit_text);
        descriptionTextView = (TextView) findViewById(R.id.description_text);
    }

    public void setNumber(String n) {
        numberTextView.setText(n);
    }

    public void setImage(int image) {
        imageView.setImageResource(image);
    }

    public void setUnit(String unit) {
        unitTextView.setText(unit);
    }

    public String getNumber() {
        return numberTextView.getText().toString();
    }
}
