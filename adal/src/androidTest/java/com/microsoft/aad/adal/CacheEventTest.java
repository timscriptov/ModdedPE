// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.microsoft.aad.adal;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import android.text.TextUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public final class CacheEventTest {

    @Test
    public void testProcessEvent() {
        CacheEvent event1 = new CacheEvent(EventStrings.TOKEN_CACHE_LOOKUP);
        event1.setTokenType(EventStrings.TOKEN_TYPE_FRT);
        event1.setTokenTypeFRT(true);

        CacheEvent event2 = new CacheEvent(EventStrings.TOKEN_CACHE_LOOKUP);
        event2.setTokenType(EventStrings.TOKEN_TYPE_MRRT);
        event2.setTokenTypeMRRT(true);

        CacheEvent event3 = new CacheEvent(EventStrings.TOKEN_CACHE_LOOKUP);
        event3.setTokenType(EventStrings.TOKEN_TYPE_RT);
        event3.setTokenTypeRT(true);

        final Map<String, String> dispatchMap = new HashMap();
        event1.processEvent(dispatchMap);
        event2.processEvent(dispatchMap);
        event3.processEvent(dispatchMap);

        assertTrue(dispatchMap.containsKey(EventStrings.CACHE_EVENT_COUNT));
        // There should be 3 events in the event count
        assertTrue(dispatchMap.get(EventStrings.CACHE_EVENT_COUNT).equals("3"));

        // Only the properties from the last token are to be stored
        assertTrue(TextUtils.isEmpty(dispatchMap.get(EventStrings.TOKEN_TYPE_IS_FRT)));
        assertTrue(TextUtils.isEmpty(dispatchMap.get(EventStrings.TOKEN_TYPE_IS_MRRT)));
        assertTrue(dispatchMap.get(EventStrings.TOKEN_TYPE_IS_RT).equals("true"));
    }

    @Test
    public void testDroppedEvents() {
        CacheEvent event1 = new CacheEvent(EventStrings.TOKEN_CACHE_DELETE);
        event1.setTokenType(EventStrings.TOKEN_TYPE_FRT);
        event1.setTokenTypeFRT(true);

        CacheEvent event2 = new CacheEvent(EventStrings.TOKEN_CACHE_WRITE);
        event2.setTokenType(EventStrings.TOKEN_TYPE_MRRT);
        event2.setTokenTypeMRRT(true);

        final Map<String, String> dispatchMap = new HashMap();
        event1.processEvent(dispatchMap);
        event2.processEvent(dispatchMap);

        assertTrue(dispatchMap.isEmpty());
    }

}
