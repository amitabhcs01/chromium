// Copyright 2019 The Chromium Authors
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package org.chromium.android_webview;

import android.net.Uri;

import androidx.annotation.NonNull;

import org.jni_zero.CalledByNative;
import org.jni_zero.JNINamespace;
import org.jni_zero.JniType;

import org.chromium.android_webview.common.Lifetime;
import org.chromium.content_public.browser.MessagePayload;
import org.chromium.content_public.browser.MessagePort;

/**
 * Holds the {@link WebMessageListener} instance so that C++ could interact with the {@link
 * WebMessageListener}.
 */
@Lifetime.Temporary
@JNINamespace("android_webview")
public class WebMessageListenerHolder {
    private final WebMessageListener mListener;
    
    /**
     * Constructor to initialize the WebMessageListener.
     * 
     * @param listener The WebMessageListener instance to hold.
     */

   public WebMessageListenerHolder(@NonNull WebMessageListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("WebMessageListener cannot be null");
        }
        mListener = listener;
    }

    @CalledByNative
    public void onPostMessage(
            MessagePayload payload,
            @JniType("std::string") String topLevelOrigin,
            @JniType("std::string") String sourceOrigin,
            boolean isMainFrame,
            MessagePort[] ports,
            JsReplyProxy replyProxy) {
         // Post the message to the current looper/thread
        AwThreadUtils.postToCurrentLooper(
                () -> {
     // Ensure mListener is not null before invoking methods on it

                   if (mListener != null) {
                        mListener.onPostMessage(
                                payload,
                                Uri.parse(topLevelOrigin),
                                Uri.parse(sourceOrigin),
                                isMainFrame,
                                replyProxy,
                                ports);
                    } else {
                        // Log an error if mListener is null (this should not normally happen)
                        // Add logging mechanism as needed
                    }
                });
    }

    /**
     * Returns the WebMessageListener instance held by this class.
     * 
     * @return The WebMessageListener instance.
     */
    public WebMessageListener getListener() {
        return mListener;
    }
}
