// @ts-ignore
window.trustedTypes.createPolicy('default', {
  createHTML: (content:string) => {
    return content
  },
  createScriptURL: (url: string) => {
    const parsed = new URL(url, document.location.href)
    const isSameOrigin = parsed.origin === document.location.origin
    if (!isSameOrigin) {
      throw new Error(`Asked for a script url that has a disallowed origin of ${parsed.origin}. URL was: ${url}.`)
    }
    return url
  }
})
