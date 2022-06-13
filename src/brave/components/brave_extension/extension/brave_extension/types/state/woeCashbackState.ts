export interface Tabs {
  [key: number]: Tab
}

export interface Tab {
  id: number
  tracking: boolean
  count: number 
  domain?: string | null
  platformId?: string
}

export type State = {
  tabs: Tabs
}
