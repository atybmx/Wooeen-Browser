/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

export const isHttpOrHttps = (url?: string) => {
  if (!url) {
    return false
  }
  return /^https?:/i.test(url)
}

/**
 * Get the URL origin via Web API
 * @param {string} url - The URL to get the origin from
 */
export const getOrigin = (url: string) => {
  // for URLs such as blob:// and data:// that doesn't have a
  // valid origin, return the full url.
  if (!isHttpOrHttps(url)) {
    return url
  }
  return new window.URL(url).origin
}

/**
 * Get the URL hostname via Web API
 * @param {string} url - The URL to get the origin from
 */
export const getHostname = (url: string) => {
  // for URLs such as blob:// and data:// that doesn't have a
  // valid origin, return the full url.
  if (!isHttpOrHttps(url)) {
    return url
  }
  return new window.URL(url).hostname
}

/**
 * Strip http/https protocol
 * @param {string} url - The URL to strip the protocol from
 */
export const stripProtocolFromUrl = (url: string) => url.replace(/(^\w+:|^)\/\//, '')

/**
 * Get the URL domain via Web API
 * @param {string} url - The URL to get the origin from
 */
export const getDomain = (url: string) => {
  // for URLs such as blob:// and data:// that doesn't have a
  // valid origin, return the full url.
  if (!isHttpOrHttps(url)) {
    return url
  }
  let domain = (new URL(url));
  return domain.hostname.replace('www.','');
}

export const replaceAll = (str:string, find: string, replace:string) => {
	  return str.replace(new RegExp(find, 'g'), replace);
}

export const encodeUTF8 = (s:string) => {
  return encodeURIComponent(s);
}

export const updateQueryStringParameter = (uri: string, key: string, value: string) => {
	  var re = new RegExp("([?&])" + key + "=.*?(&|$)", "i");
	  var separator = uri.indexOf('?') !== -1 ? "&" : "?";
	  if (uri.match(re)) {
	    return uri.replace(re, '$1' + key + "=" + value + '$2');
	  }
	  else {
	    return uri + separator + key + "=" + value;
	  }
	}

export const parseTrackingLink = (deeplink: string | undefined, params: string | undefined, link: string | undefined, userId: string | undefined) : string | null => {
    if(!link)
        return null;

    //parse the params and add into final link
    if(params) {
        if (params.includes("{user_id}"))
            params = replaceAll(params, "{user_id}", userId ? "" + userId : "");

        //add params into link
        let ps = params.split('&');
      	if(ps && ps.length){
      		for(let i = 0; i < ps.length; i++){
      			let p = ps[i];
      			if(p && p.includes("=")){
      				link = updateQueryStringParameter(link, p.split('=')[0], p.split('=')[1]);
      			}
      		}
      	}
    }

    //parse the deeplink
    if(deeplink) {
        if (deeplink.includes("{link}"))
            deeplink = replaceAll(deeplink, "{link}", link ? link : "");
        if (deeplink.includes("{link_encode_utf8}"))
            deeplink = replaceAll(deeplink,"{link_encode_utf8}", link ? encodeUTF8(link) : "");
        if (deeplink.includes("{user_id}"))
            deeplink = replaceAll(deeplink, "{user_id}", userId ? "" + userId : "");

        return deeplink;
    }

    return link;
}

export const getCheckoutEndpoint = (checkoutEndpoint: string) : string | null => {
    if(!checkoutEndpoint)
      return null

    checkoutEndpoint = checkoutEndpoint.replace(/^https?:\/\//, '');
    if(checkoutEndpoint.includes("?"))
      checkoutEndpoint = checkoutEndpoint.substring(0, checkoutEndpoint.indexOf("?"));
    if(checkoutEndpoint.includes("/")) {
			let checkoutPath = checkoutEndpoint.substring(checkoutEndpoint.lastIndexOf("/"));
			if(checkoutPath && checkoutPath.includes(".")) {
				checkoutEndpoint =
		    		checkoutEndpoint.substring(0, checkoutEndpoint.lastIndexOf("/")) +
		    		checkoutPath.substring(0, checkoutPath.indexOf("."));
			}
		}
    return checkoutEndpoint
}
