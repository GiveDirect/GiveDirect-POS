package io.givedirect.givedirectpos.view.common;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import io.givedirect.givedirectpos.R;

public class CenteredTitleToolbar extends Toolbar {

    private TextView centeredTitleTextView;

    public CenteredTitleToolbar(Context context) {
        super(context);
    }

    public CenteredTitleToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CenteredTitleToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTitle(@StringRes int resId) {
        String s = getResources().getString(resId);
        setTitle(s);
    }

    @Override
    public void setTitle(CharSequence title) {
        getCenteredTitleTextView().setText(title);
    }

    @Override
    public CharSequence getTitle() {
        return getCenteredTitleTextView().getText().toString();
    }

    public void setTypeface(Typeface font) {
        getCenteredTitleTextView().setTypeface(font);
    }

    private TextView getCenteredTitleTextView() {
        if (centeredTitleTextView == null) {
            centeredTitleTextView = new TextView(getContext());
            centeredTitleTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.pacifico_regular));
            centeredTitleTextView.setSingleLine();
            centeredTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
            centeredTitleTextView.setGravity(Gravity.CENTER);
            centeredTitleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorSecondaryText));
            centeredTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

            Toolbar.LayoutParams lp = new Toolbar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            centeredTitleTextView.setLayoutParams(lp);

            addView(centeredTitleTextView);
        }
        return centeredTitleTextView;
    }
}
