/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

// Actions
import actions from '../actions/webNavigationActions'
import settingsActions from '../actions/settingsActions'

import * as WoeState from '../../types/state/woeCashbackState'
import { state } from '../reducers/woeCashbackReducer'
import { isHttpOrHttps, getDomain, parseTrackingLink, getCheckoutEndpoint } from '../../helpers/urlUtils'
import { getAdvertiserById, getAdvertiserByDomain } from '../woeadv/feed'
import { getVersion } from '../woever/feed'
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
        let advertiserId = tab.advertiserId;
        if(advertiserId){
          let user = Auth.getUser();
          if(user && user.id){
            let version = getVersion();

            //CHECKOUT
            if(tab.checkoutEndpoint && tab.checkoutData){
              let woecheckout = {
                      version: version && version.checkout ? version.checkout : new Date().getTime(),
                      pl: tab.platformId,
                      u: user.id,
                      a: advertiserId,
                      endpoint: ''+tab.checkoutEndpoint,
                      data: tab.checkoutData
                    }
              chrome.storage.local.set(
                {checkoutTemp: JSON.stringify(woecheckout)}
              );

              chrome.scripting.executeScript({
        				target: { tabId: tabId },
        				function: checkoutInject
        			});
            }

            //PRODUCT
            if(tab.productEndpoint && tab.productData){
              let woeproduct = {
                      version: version && version.product ? version.product : new Date().getTime(),
                      pl: tab.platformId,
                      u: user.id,
                      a: advertiserId,
                      endpoint: ''+tab.productEndpoint,
                      data: tab.productData
                    }
              chrome.storage.local.set(
                {productTemp: JSON.stringify(woeproduct)}
              );

              chrome.scripting.executeScript({
        				target: { tabId: tabId },
        				function: productInject
        			});
            }

            //QUERY
            if(tab.queryEndpoint && tab.queryData){
              let woequery = {
                      version: version && version.query ? version.query : new Date().getTime(),
                      pl: tab.platformId,
                      u: user.id,
                      a: advertiserId,
                      endpoint: ''+tab.queryEndpoint,
                      data: tab.queryData
                    }
              chrome.storage.local.set(
                {queryTemp: JSON.stringify(woequery)}
              );

              chrome.scripting.executeScript({
        				target: { tabId: tabId },
        				function: queryInject
        			});
            }
          }
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
        'if(typeof woecheckoutDone === "undefined"){var woecheckoutDone = "";}'+
        'var woecheckout = {'+
          'pl: '+data.pl+','+
          'u: '+data.u+','+
          'a: '+data.a+','+
          'endpoint: "'+data.endpoint+'",'+
          'data: '+data.data+
        '}';

      let woeCheckoutJs = document.createElement('script');
      woeCheckoutJs.type = 'application/javascript';
      woeCheckoutJs.src = 'https://app.wooeen.com/checkout.js?v='+data.version;
      if(document.body){
        document.body.appendChild(woeCheckout);
        document.body.appendChild(woeCheckoutJs);
      }

      chrome.storage.local.remove("checkoutTemp");
    }
  });
}

function productInject(){
  chrome.storage.local.get(["productTemp"], ({ productTemp }) => {
    let data = JSON.parse(productTemp);
    if (typeof data === 'object' &&
        !Array.isArray(data) &&
        data !== null
    ) {
      let woeProduct = document.createElement('script');
      woeProduct.innerHTML =
        'if(typeof woeproductDone === "undefined"){var woeproductDone = "";}'+
        'var woeproduct = {'+
          'pl: '+data.pl+','+
          'u: '+data.u+','+
          'a: '+data.a+','+
          'endpoint: "'+data.endpoint+'",'+
          'data: '+data.data+
        '}';

      let woeProductJs = document.createElement('script');
      woeProductJs.type = 'application/javascript';
      woeProductJs.src = 'https://app.wooeen.com/product.js?v='+data.version;
      if(document.body){
        document.body.appendChild(woeProduct);
        document.body.appendChild(woeProductJs);
      }

      chrome.storage.local.remove("productTemp");
    }
  });
}

function queryInject(){
  chrome.storage.local.get(["queryTemp"], ({ queryTemp }) => {
    let data = JSON.parse(queryTemp);
    if (typeof data === 'object' &&
        !Array.isArray(data) &&
        data !== null
    ) {
      let woeQuery = document.createElement('script');
      woeQuery.innerHTML =
        'if(typeof woequeryDone === "undefined"){var woequeryDone = "";}'+
        'var woequery = {'+
          'pl: '+data.pl+','+
          'u: '+data.u+','+
          'a: '+data.a+','+
          'endpoint: "'+data.endpoint+'",'+
          'data: '+data.data+
        '}';

      let woeQueryJs = document.createElement('script');
      woeQueryJs.type = 'application/javascript';
      woeQueryJs.src = 'https://app.wooeen.com/query.js?v='+data.version;
      if(document.body){
        document.body.appendChild(woeQuery);
        document.body.appendChild(woeQueryJs);
      }

      chrome.storage.local.remove("queryTemp");
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
            tab.checkoutEndpoint = undefined;
            tab.checkoutData = undefined;
            tab.productEndpoint = undefined;
            tab.productData = undefined;
            tab.queryEndpoint = undefined;
            tab.queryData = undefined;

            let advertiser = tracking.advertiserId ? getAdvertiserById(tracking.advertiserId) :  getAdvertiserByDomain(domain);
            if(advertiser){
              tab.advertiserId = advertiser.id;
              tab.checkoutEndpoint = advertiser.checkout ? advertiser.checkout.endpoint : undefined;
              tab.checkoutData = advertiser.checkout ? advertiser.checkout.data : undefined;
              tab.productEndpoint = advertiser.product ? advertiser.product.endpoint : undefined;
              tab.productData = advertiser.product ? advertiser.product.data : undefined;
              tab.queryEndpoint = advertiser.query ? advertiser.query.endpoint : undefined;
              tab.queryData = advertiser.query ? advertiser.query.data : undefined;
            }

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
