package com.chaos.chaoscompass.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

public class TutorialAnimation {
    private static final String TAG = "Compass:TutorialAnimation";
    private final int ANIMATION_CMD = 1;
    private final String ATTRIBUTE_NAME_DRAWABLE = "drawable";
    private final String ATTRIBUTE_NAME_DURATION = "duration";
    private final String NAME_SPACE = "http://schemas.android.com/apk/res/android";
    /* access modifiers changed from: private */
    public List<AnimationItem> mAnimation = new LinkedList();
    /* access modifiers changed from: private */
    public ImageView mAnimationView;
    private Context mContext;
    /* access modifiers changed from: private */
    public Handler mHandler = new Handler() {
        public void handleMessage(Message message) {
            if (TutorialAnimation.this.mIsAnimating) {
                TutorialAnimation tutorialAnimation = TutorialAnimation.this;
                tutorialAnimation.mIndex = (tutorialAnimation.mIndex + 1) % TutorialAnimation.this.mAnimation.size();
                AnimationItem animationItem = (AnimationItem) TutorialAnimation.this.mAnimation.get(TutorialAnimation.this.mIndex);
                TutorialAnimation.this.mAnimationView.setImageResource(animationItem.mResId);
                TutorialAnimation.this.mHandler.sendEmptyMessageDelayed(1, (long) animationItem.mDuration);
            }
        }
    };
    /* access modifiers changed from: private */
    public int mIndex;
    /* access modifiers changed from: private */
    public boolean mIsAnimating;

    class AnimationItem {
        int mDuration;
        int mResId;

        AnimationItem(int i, int i2) {
            this.mResId = i;
            this.mDuration = i2;
        }
    }

    public TutorialAnimation(Context context) {
        this.mContext = context;
    }

    public void startAnimation(ImageView imageView, int i) {
        this.mAnimationView = imageView;
        this.mIsAnimating = true;
        this.mAnimation.clear();
        this.mIndex = 0;
        loadAnimationFrame(i);
        this.mHandler.sendEmptyMessage(1);
    }

    private void loadAnimationFrame(int i) {
        String str = "http://schemas.android.com/apk/res/android";
        String str2 = TAG;
        XmlResourceParser xml = this.mContext.getResources().getXml(i);
        while (true) {
            try {
                int next = xml.next();
                if (next == 1) {
                    return;
                }
                if (next == 2) {
                    if (xml.getName().equals("item")) {
                        int attributeIntValue = xml.getAttributeIntValue(str, "duration", 100);
                        this.mAnimation.add(new AnimationItem(xml.getAttributeResourceValue(str, "drawable", -1), attributeIntValue));
                    }
                }
            } catch (XmlPullParserException e) {
                Log.e(str2, "loadAnimationFrame XmlPullParserException", e);
                return;
            } catch (IOException e2) {
                Log.e(str2, "loadAnimationFrame IOException", e2);
                return;
            }
        }
    }

    public void stopAnimation() {
        this.mIsAnimating = false;
        this.mAnimation.clear();
        this.mHandler.removeMessages(1);
    }
}
