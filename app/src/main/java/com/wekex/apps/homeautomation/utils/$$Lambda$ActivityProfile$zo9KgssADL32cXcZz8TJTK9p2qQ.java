package com.wekex.apps.homeautomation.utils;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;

/* renamed from: com.wekex.apps.homeautomation.activity.-$$Lambda$ActivityProfile$zo9KgssADL32cXcZz8TJTK9p2qQ reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ActivityProfile$zo9KgssADL32cXcZz8TJTK9p2qQ implements ErrorListener {
    public static final /* synthetic */ $$Lambda$ActivityProfile$zo9KgssADL32cXcZz8TJTK9p2qQ INSTANCE = new $$Lambda$ActivityProfile$zo9KgssADL32cXcZz8TJTK9p2qQ();

    private /* synthetic */ $$Lambda$ActivityProfile$zo9KgssADL32cXcZz8TJTK9p2qQ() {
    }

    public final void onErrorResponse(VolleyError volleyError) {
        ActivityProfile.lambda$postData$1(volleyError);
    }
}
