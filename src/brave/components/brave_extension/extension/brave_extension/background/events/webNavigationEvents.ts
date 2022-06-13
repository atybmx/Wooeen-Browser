/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

// Actions
import actions from '../actions/webNavigationActions'
import settingsActions from '../actions/settingsActions'

import * as WoeState from '../../types/state/woeCashbackState'
import { state } from '../reducers/woeCashbackReducer'
import { isHttpOrHttps, getDomain, parseTrackingLink, getCheckoutEndpoint } from '../../helpers/urlUtils'
import { checkout } from '../woeadv/feed'
import { checkout as checkoutTask } from '../woetsk/feed'
import { tracked } from '../woetrk/feed'
import * as Auth from '../woeuse/auth'
import { getAppDomain } from '../woeutils/urls'

chrome.cookies.onChanged.addListener( function(details){
    if(details && details.cookie && details.cookie.domain == getAppDomain()){
      Auth.saveAuth(details.cookie.name, details.cookie.value, details.removed)
    }
  }
)

chrome.tabs.onActivated.addListener( function (activeInfo) {
	if(activeInfo && activeInfo.tabId){
		chrome.tabs.get(activeInfo.tabId,function(tab){
      if(tab && tab.id && tab.url && isHttpOrHttps(tab.url))
			   verifyUrl(tab.id, tab.url);
		});
	}
});

chrome.tabs.onUpdated.addListener( function (tabId, changeInfo, tab) {
	if(changeInfo &&
			changeInfo.status &&
			changeInfo.status === 'loading' &&
			tab &&
			tab.url &&
      isHttpOrHttps(tab.url)){
		verifyUrl(tab.id, tab.url);
	}
});

function verifyUrl(tabId: number | undefined, url: string){
  if(tabId){
    let checkoutEndpoint = getCheckoutEndpoint(url)
    if(checkoutEndpoint){
      let tab: WoeState.Tab = state.tabs[tabId];
      if(tab && tab.platformId){
        let adv = checkout(checkoutEndpoint)
        if(adv && adv.id && adv.checkout && adv.checkout.data){
          let user = Auth.getUser();
          if(user && user.id){
            let woecheckout = {
                    pl: tab.platformId,
                    u: user.id,
                    a: adv.id,
                    data: adv.checkout.data
                  }
            chrome.storage.local.set(
              {checkoutTemp: JSON.stringify(woecheckout)}
            );

            chrome.scripting.executeScript({
      				target: { tabId: tabId },
      				function: checkoutInject
      			});
          }
        }
      }

      //Task checkout endpoint
      let tsk = checkoutTask(checkoutEndpoint)
      if(tsk && tsk.id && tsk.checkout){
        let user = Auth.getUser();
        if(user && user.id){
          let woecheckoutTask = {
                  pl: tsk.platformId,
                  u: user.id,
                  a: tsk.advertiserId,
                  t: tsk.id
                }
          if(tsk.checkout.data)
            woecheckoutTask['data'] = tsk.checkout.data;

          chrome.storage.local.set(
            {checkoutTaskTemp: JSON.stringify(woecheckoutTask)}
          );

          chrome.scripting.executeScript({
            target: { tabId: tabId },
            function: checkoutTaskInject
          });
        }
      }
    }
  }
}

function checkoutInject(){
  chrome.storage.local.get(["checkoutTemp"], ({ checkoutTemp }) => {
    let data = JSON.parse(checkoutTemp);
    if (typeof data === 'object' &&
        !Array.isArray(data) &&
        data !== null
    ) {
      let woeCheckout = document.createElement('script');
      woeCheckout.innerHTML =
        'var woecheckout = {'+
          'pl: '+data.pl+','+
          'u: '+data.u+','+
          'a: '+data.a+','+
          'data: '+data.data+
        '}';

      let woeCheckoutJs = document.createElement('script');
      woeCheckoutJs.type = 'application/javascript';
      woeCheckoutJs.src = 'https://app.wooeen.com/checkout.js';
      if(document.body){
        document.body.appendChild(woeCheckout);
        document.body.appendChild(woeCheckoutJs);
      }

      chrome.storage.local.remove("checkoutTemp");
    }
  });

}

function checkoutTaskInject(){
  chrome.storage.local.get(["checkoutTaskTemp"], ({ checkoutTaskTemp }) => {
    let data = JSON.parse(checkoutTaskTemp);
    if (typeof data === 'object' &&
        !Array.isArray(data) &&
        data !== null
    ) {
      let woeCheckout = document.createElement('script');
      woeCheckout.innerHTML =
        'var woecheckout = {'+
          'pl: '+data.pl+','+
          'u: '+data.u+','+
          'a: '+data.a+','+
          't: '+data.t+
          (data.data ? ',data: '+data.data : '')+
        '}';

      let woeCheckoutJs = document.createElement('script');
      woeCheckoutJs.type = 'application/javascript';
      woeCheckoutJs.src = 'https://app.wooeen.com/checkout.js';
      if(document.body){
        document.body.appendChild(woeCheckout);
        document.body.appendChild(woeCheckoutJs);
      }

      chrome.storage.local.remove("checkoutTaskTemp");
    }
  });

}

chrome.webRequest.onBeforeRequest.addListener(
  function(details) {
    let tab: WoeState.Tab = state.tabs[details.tabId];
    if(!tab){
      tab = {id: details.tabId, tracking: false, domain: null, count: 0}
      state.tabs[details.tabId] = tab
    }

    if(details.url){
      let domain = getDomain(details.url);

      if(tab.tracking){
        tab.count = tab.count + 1
        if(domain == tab.domain || tab.count >= 4){
            tab.tracking = false
            tab.count = 0;
        }
      }else{
        if(tab.domain && tab.domain != domain)
          tab.domain = null

        if(!tab.domain){
          let tracking: Wooeen.Tracking | null = tracked(domain);
          if(tracking && tracking.id){
            tab.tracking = true;
            tab.count = 0;
            tab.domain = domain;
            tab.platformId = tracking.platformId;

            let user = Auth.getUser();
            let goto: string | null = parseTrackingLink(tracking.deeplink, tracking.params, details.url, user && user.id ? user.id : '' )
            if(goto)
              return {redirectUrl: goto};
          }
        }
      }

    }

    return {cancel: false};
  },
  {
    urls: ["https://*/*"],
    types: ["main_frame"]
  },
  ["blocking"]
)

chrome.webNavigation.onBeforeNavigate.addListener(function ({ tabId, url, frameId }: chrome.webNavigation.WebNavigationParentedCallbackDetails) {
  const isMainFrame: boolean = frameId === 0
  actions.onBeforeNavigate(tabId, url, isMainFrame)
})

let shouldRequestSettingsData = true
chrome.webNavigation.onCommitted.addListener(function ({ tabId, url, frameId }: chrome.webNavigation.WebNavigationTransitionCallbackDetails) {
  const isMainFrame: boolean = frameId === 0
  actions.onCommitted(tabId, url, isMainFrame)
  if (shouldRequestSettingsData) {
    // check whether or not the settings store should update based on settings changes.
    // this action is needed in the onCommitted phase for edge cases such as when after Brave is re-launched
    settingsActions.fetchAndDispatchSettings()
    // this request only needs to perform once
    shouldRequestSettingsData = false
  }
})
