package com.krlsedu.cths.mediacontrol;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

/**
 * Created by CarlosEduardo on 13/10/2017.
 */

public class MeusFabs extends FloatingActionButton {
    public MeusFabs(Context context) {
        super(context);
    }

    public MeusFabs(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeusFabs(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        // do what you want
        return true;
    }
}
