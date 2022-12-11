export interface Tabs {
  [key: number]: Tab
}

export interface Tab {
  id: number
  tracking: boolean
  count: number
  domain?: string | null
  platformId?: string
  advertiserId?: string
  checkoutEndpoint?: string
  checkoutData?:string
  productEndpoint?: string
  productData?:string
  queryEndpoint?: string
  queryData?:string
}

export type State = {
  tabs: Tabs
}
