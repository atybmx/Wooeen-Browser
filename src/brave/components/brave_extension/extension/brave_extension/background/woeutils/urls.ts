import * as FeedCr from '../woecr/feed'
import * as FeedUser from '../woeuse/feed'

export async function getApiUrl (path: string, params: URLSearchParams) {
  //append auth params
  let searchParams = new URLSearchParams({
    app_id: "bb63712ac7ac4bc8a6a746b0ace643a0",
    app_token: "Gpk8PYyA9oZQ07XHFbsudr"
  });

  //append cr param
  let country = 'BR'
  let crUser = await FeedUser.getOrFetchData(true)
  if(crUser && crUser.items && crUser.items.country && crUser.items.country.id)
    country = crUser.items.country.id
  else{
      let cr = await FeedCr.getOrFetchData(true)
      if(cr && cr.id)
        country = cr.id
  }
  searchParams.append('cr', country);

  for(let p of params) {
    searchParams.append(p[0], p[1]);
  }

  return `https://api.wooeen.com/api/${path}?${searchParams.toString()}`
}

export async function getBlogUrl (path: string, params: URLSearchParams) {
  let searchParams = new URLSearchParams({
  });

  for(let p of params) {
    searchParams.append(p[0], p[1]);
  }

  return `https://www.wooeen.com/blog/${path}?${searchParams.toString()}`
}

export function getAppDomain(){
  return '.wooeen.com'
}

export function getOAuthUrl(){
  return 'https://app.wooeen.com/u/login/sync'
}

export function getOAuthSuccessUrl(){
  return 'https://app.wooeen.com/u/login/sync/success'
}
