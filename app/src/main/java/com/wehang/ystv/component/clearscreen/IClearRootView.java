package com.wehang.ystv.component.clearscreen;

import android.view.View;

/**
 * Created by Yellow5A5 on 16/10/24.
 */

public interface IClearRootView {

    void setClearSide(ClearScreenConstants.Orientation orientation);

    void setIPositionCallBack(IPositionCallBack l);

    void setIClearEvent(IClearEvent l);

    void addView(View child, int index);
}
