import { getApiUrl } from '../woeutils/urls'

let memoryTodayData: Wooeen.FeedTracking | undefined
let readLock: Promise<void> | null
let isKnownRemoteUpdateAvailable = false

const STORAGE_KEY = 'woetrk'
const STORAGE_SCHEMA_VERSION = 1
const STORAGE_KEY_LAST_REMOTE_CHECK = 'woetrkLastRemoteUpdateCheck'

type FeedInStorage = {
  storageSchemaVersion: number,
  feed: Wooeen.FeedTracking
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

async function setStorageData (feed: Wooeen.FeedTracking) {
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
    console.error('WOETRK: Asked to update feed but already waiting for another update!')
    return
  }

  // Only run this once at a time, otherwise wait for the update
  readLock = new Promise(async function (resolve, reject) {
    try {
        await fetch(
          await getApiUrl(
              "tracking/get",
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
                    platformId: o.platformId,
                    advertiserId: o.advertiserId,
                    deeplink: o.deeplink,
                    params: o.params,
                    domain: o.domain
                  })
              }
              memoryTodayData = {items: items}
            }
            isKnownRemoteUpdateAvailable = false
          },
          (error) => {
            console.error('WOETRK: Could not get feed contents');
          }
        )

        resolve()
        if (memoryTodayData && memoryTodayData.items && memoryTodayData.items.length > 0) {
          await setStorageData(memoryTodayData)
        }
    } catch (e) {
      console.error('WOETRK: Could not process feed contents')
      reject(e)
    } finally {
      readLock = null
    }
  })
}

export async function getTracking (domain: string) {
  await getLocalDataLock

  if(!domain) return null;

  if (memoryTodayData && memoryTodayData.items && memoryTodayData.items.length > 0){
    let trks = memoryTodayData.items.filter(a => a && a.domain && domain.endsWith(a.domain))
    if(trks && trks.length > 0)
      return trks[Math.floor(Math.random()*trks.length)]
  }

  return null
}

export function tracked(domain: string) : Wooeen.Tracking | null {
  if(!domain) return null;

  if (memoryTodayData && memoryTodayData.items && memoryTodayData.items.length > 0){
    let trks = memoryTodayData.items.filter(a => a && a.domain && ("."+domain).endsWith("."+a.domain))
    if(trks && trks.length > 0)
      return trks[Math.floor(Math.random()*trks.length)]
  }

  return null
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
