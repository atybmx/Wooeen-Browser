export async function getApiUrl (path: string, params: URLSearchParams) {
  let searchParams = new URLSearchParams({
    app_id: "bb63712ac7ac4bc8a6a746b0ace643a0",
    app_token: "Gpk8PYyA9oZQ07XHFbsudr"
  });

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
  return 'app.wooeen.com'
}


export function getAppUrl(){
  return 'https://app.wooeen.com'
}
