import { getApiUrl } from '../woeutils/urls'

let memoryTodayData: Wooeen.FeedAdvertiser | undefined
let readLock: Promise<void> | null
let isKnownRemoteUpdateAvailable = false

const STORAGE_KEY = 'woeadv'
const STORAGE_SCHEMA_VERSION = 1
const STORAGE_KEY_LAST_REMOTE_CHECK = 'woeadvLastRemoteUpdateCheck'

type FeedInStorage = {
  storageSchemaVersion: number,
  feed: Wooeen.FeedAdvertiser
}

function getFromStorage<T> (key: string) {
  return new Promise<T | null>(resolve => {
    chrome.storage.local.get(key, (data) => {
      if (Object.keys(data).includes(key)) {
        resolve(data[key] as T)
      } else {
        resolve(null)
      }
    })
  })
}

async function setStorageData (feed: Wooeen.FeedAdvertiser) {
  chrome.storage.local.set({
    [STORAGE_KEY]: {
      storageSchemaVersion: STORAGE_SCHEMA_VERSION,
      feed
    }
  })
}

function setLastUpdateCheckTime (time: number) {
  chrome.storage.local.set({
    [STORAGE_KEY_LAST_REMOTE_CHECK]: time
  })
}

export async function getLastUpdateCheckTime () {
  return getFromStorage<number>(STORAGE_KEY_LAST_REMOTE_CHECK)
}

async function getStorageData () {
  const data = await getFromStorage<FeedInStorage>(STORAGE_KEY)
  if (!data || !data.feed) {
    return
  }
  if (data.storageSchemaVersion !== STORAGE_SCHEMA_VERSION) {
    return
  }
  memoryTodayData = data.feed
}

function clearStorageData () {
  return new Promise(resolve => {
    chrome.storage.local.remove(STORAGE_KEY, resolve)
  })
}

// Immediately read from local storage and ensure we don't try
// to fetch whilst we're waiting.
const getLocalDataLock = getStorageData()

function performUpdateFeed () {
  // Sanity check
  if (readLock) {
    console.error('WOEADV: Asked to update feed but already waiting for another update!')
    return
  }
  // Only run this once at a time, otherwise wait for the update
  readLock = new Promise(async function (resolve, reject) {
    try {
        await fetch(
          await getApiUrl(
              "advertiser/get",
              new URLSearchParams({
                  st:'1'
                }))
        )
        .then(res => res.json())
        .then(
          (result) => {
            setLastUpdateCheckTime(Date.now())

            if(result.result && result.callback && result.callback.length > 0){
              let items = [];
              for (var i = 0; i < result.callback.length; i++){
                  let o = result.callback[i];
                  items.push({
                    id: o.id,
                    name: o.name,
                    color: o.color,
                    logo: o.logo,
                    url: o.url,
                    domain: o.domain,
                    omniboxTitle: o.omniboxTitle,
                    omniboxDescription: o.omniboxDescription,
                    checkout: {
                      endpoint: o.checkout ? o.checkout.endpoint : undefined,
                      data: o.checkout ? o.checkout.data : undefined
                    },
                    product: {
                      endpoint: o.product ? o.product.endpoint : undefined,
                      data: o.product ? o.product.data : undefined
                    },
                    query: {
                      endpoint: o.query ? o.query.endpoint : undefined,
                      data: o.query ? o.query.data : undefined
                    }
                  })
              }
              memoryTodayData = {items: items}
            }
            isKnownRemoteUpdateAvailable = false
          },
          (error) => {
            console.error('WOEADV: Could not get feed contents');
          }
        )

        resolve()
        if (memoryTodayData && memoryTodayData.items && memoryTodayData.items.length > 0) {
          await setStorageData(memoryTodayData)
        }
    } catch (e) {
      console.error('WOEADV: Could not process feed contents')
      reject(e)
    } finally {
      readLock = null
    }
  })
}

export async function getAdvertiser (domain: string) {
  await getLocalDataLock

  if(!domain) return false;

  if (memoryTodayData && memoryTodayData.items && memoryTodayData.items.length > 0){
    return memoryTodayData.items.find(a => a && a.domain && ("."+domain).endsWith("."+a.domain))
  }

  return null
}

export function checkout(endpoint: string) : Wooeen.Advertiser | undefined {
  if(!endpoint) return;

  if (memoryTodayData && memoryTodayData.items && memoryTodayData.items.length > 0){
    return memoryTodayData.items.find(a => a && a.checkout && a.checkout.endpoint && ("."+endpoint).endsWith("."+a.checkout.endpoint))
  }

  return
}

export function getAdvertiserById(id: string | undefined) : Wooeen.Advertiser | undefined {
  if(!id) return;

  if (memoryTodayData && memoryTodayData.items && memoryTodayData.items.length > 0){
    return memoryTodayData.items.find(a => a && a.id && a.id == id)
  }

  return
}

export function getAdvertiserByDomain(domain: string | undefined) : Wooeen.Advertiser | undefined {
  if(!domain) return;

  if (memoryTodayData && memoryTodayData.items && memoryTodayData.items.length > 0){
    return memoryTodayData.items.find(a => a && a.domain && ("."+domain).endsWith("."+a.domain))
  }

  return
}

export async function getLocalData () {
  if (readLock) {
    await readLock
  }
  await getLocalDataLock
  return memoryTodayData
}

export async function getOrFetchData (waitForInProgressUpdate: boolean = false) {
  await getLocalDataLock
  if (waitForInProgressUpdate && readLock) {
    await readLock
  }
  if (memoryTodayData && memoryTodayData.items && memoryTodayData.items.length > 0 && !isKnownRemoteUpdateAvailable) {
    return memoryTodayData
  }
  return update()
}

export async function update (force: boolean = false) {
  // Fetch but only once at a time, and wait.
  if (!readLock) {
    performUpdateFeed()
  } else if (force) {
    // If there was already an update in-progress, and we want
    // to make sure we use the latest data, we'll have to perform
    // another update to be sure.
    await readLock
    performUpdateFeed()
  }
  await readLock
  return memoryTodayData
}

export async function clearCache () {
  await getLocalDataLock
  if (readLock) {
    await readLock
  }
  await clearStorageData()
  memoryTodayData = undefined
}
