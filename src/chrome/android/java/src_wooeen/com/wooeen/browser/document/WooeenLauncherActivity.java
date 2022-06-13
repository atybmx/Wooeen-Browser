// Copyright 2021 The Wooeen Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.
package com.wooeen.browser.document;

import org.chromium.base.TraceEvent;
import org.chromium.chrome.browser.document.ChromeLauncherActivity;

import android.os.Bundle;

/**
 * Extends ChromeLauncherActivity
 */
public class WooeenLauncherActivity extends ChromeLauncherActivity {



  @Override
  public void onCreate(Bundle savedInstanceState) {
        // Third-party code adds disk access to Activity.onCreate. http://crbug.com/619824
        TraceEvent.begin("WooeenLauncherActivity.onCreate");
        super.onCreate(savedInstanceState);
  }
}
