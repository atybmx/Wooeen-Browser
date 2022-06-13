/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

import { isHttpOrHttps, getDomain } from '../../helpers/urlUtils'
import { getAdvertiser } from '../woeadv/feed'

// TODO: use `import` for these assets once the webpack build
// for brave extension allows dynamic file serving like other brave-core
// components
const shieldsOnIcon18Url = 'assets/img/shields-on.png'
const shieldsOnIcon36Url = 'assets/img/shields-on@2x.png'
const shieldsOnIcon54Url = 'assets/img/shields-on@3x.png'
const shieldsOffIcon18Url = 'assets/img/shields-off.png'
const shieldsOffIcon36Url = 'assets/img/shields-off@2x.png'
const shieldsOffIcon54Url = 'assets/img/shields-off@3x.png'

export const shieldsOnIcon = {
  18: shieldsOnIcon18Url,
  36: shieldsOnIcon36Url,
  54: shieldsOnIcon54Url
}
export const shieldsOffIcon = {
  18: shieldsOffIcon18Url,
  36: shieldsOffIcon36Url,
  54: shieldsOffIcon54Url
}

/**
 * Initializes the browser action UI
 */
export function init () {
  // Setup badge color
  chrome.browserAction.setBadgeBackgroundColor({
    color: '#3b3e4f'
  }, () => {
    if (chrome.runtime.lastError) {
      console.warn('browserAction.setBadgeBackgroundColor failed: ' + chrome.runtime.lastError.message)
    }
  })
  // Initial / default icon / WOE default off icon
  chrome.browserAction.setIcon({
    path: shieldsOffIcon
  })
  // By default, icon is disabled,
  // so that we do not enable the icon in a new tab and then disable it
  // when the context is not http(s).
  chrome.browserAction.disable(undefined, () => {
    if (chrome.runtime.lastError) {
      console.error('browserAction.disable failed: ' + chrome.runtime.lastError.message)
    }
  })
}

/**
 * Sets the badge text
 * @param {string} text - The text to put on the badge
 */
// export const setBadgeText = (tabId: number, text: string) => {
  // if (chrome.browserAction) {
  //   chrome.browserAction.setBadgeText({
  //     tabId,
  //     text: String(text)
  //   }, () => {
  //     if (chrome.runtime.lastError) {
  //       console.error('browserAction.setBadgeText failed: ' + chrome.runtime.lastError.message)
  //     }
  //   })
  // }
// }

/**
 * Updates the shields icon based on shields state
 */
export const setIcon = (url: string, tabId: number, shieldsOn: boolean) => {

  const actionIsDisabled = !isHttpOrHttps(url)
  if (chrome.browserAction) {
    new Promise((resolve)=>{
      resolve(hasCashback(url))
    }).then(function(cashbackOn){
      chrome.browserAction.setIcon({
        path: cashbackOn ? shieldsOnIcon : shieldsOffIcon,
        tabId
      })
    })

    if (actionIsDisabled) {
      chrome.browserAction.disable(tabId, () => {
        if (chrome.runtime.lastError) {
          console.error('browserAction.disable failed: ' + chrome.runtime.lastError.message)
        }
      })
    } else {
      chrome.browserAction.enable(tabId, () => {
        if (chrome.runtime.lastError) {
          console.error('browserAction.enable failed: ' + chrome.runtime.lastError.message)
        }
      })
    }
  }
}

/**
* Check if domain has cashback
*/
async function hasCashback (url: string){
  let domain = getDomain(url);
  if(domain){
    return getAdvertiser(domain);
  }

  return null
}
