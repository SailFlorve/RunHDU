package com.cxsj.runhdu.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cxsj.runhdu.R;

/**
 * Created by Sail on 2017/3/25 0025.
 * Show a number and its description.
 */

public class NumberView extends LinearLayout {
    private TextView numberTextView;
    private TextView desTextView;
    private TextView unitTextView;

    private String numberText;
    private String desText;
    private String unitText;

    private int numberColor;
    private int desColor;
    private int unitColor;

    private float numberSize;

    public NumberView(Context context) {
        super(context);
        initView();
    }

    public NumberView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public NumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberView);
        numberText = a.getString(R.styleable.NumberView_numberText);
        desText = a.getString(R.styleable.NumberView_numberDesText);
        unitText = a.getString(R.styleable.NumberView_numberUnitText);
        numberColor = a.getColor(R.styleable.NumberView_numberTextColor,
                getResources().getColor(R.color.TextPrimary));
        desColor = a.getColor(R.styleable.NumberView_numberDesTextColor,
                getResources().getColor(R.color.TextSecondary));
        unitColor = a.getColor(R.styleable.NumberView_numberUnitTextColor,
                getResources().getColor(R.color.TextSecondary));

        setText(numberText);
        setDesText(desText);
        setUnitText(unitText);

        setNumberColor(numberColor);
        setDesColor(desColor);
        setUnitColor(unitColor);
        a.recycle();
    }

    private void initView() {
        View view = View.inflate(getContext(), R.layout.number_view, this);
        numberTextView = (TextView) view.findViewById(R.id.number);
        desTextView = (TextView) view.findViewById(R.id.number_des);
        unitTextView = (TextView) view.findViewById(R.id.number_unit);
    }

    public void setText(String numberText) {
        numberTextView.setText(numberText);
    }

    public void setDesText(String desText) {
        desTextView.setText(desText);
    }

    public void setUnitText(String unitText) {
        unitTextView.setText(unitText);
    }

    public void setNumberColor(int numberColor) {
        numberTextView.setTextColor(numberColor);
    }

    public void setDesColor(int desColor) {
        desTextView.setTextColor(desColor);
    }

    public void setUnitColor(int unitColor) {
        unitTextView.setTextColor(unitColor);
    }

    public void setNumberSize(float size) {
        numberTextView.setTextSize(size);
    }

    public String getText() {
        return numberTextView.getText().toString();
    }
}
