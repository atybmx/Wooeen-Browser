import { getApiUrl } from '../woeutils/urls'

let memoryTodayData: Wooeen.FeedOffer | undefined
let readLock: Promise<void> | null
let isKnownRemoteUpdateAvailable = false

const STORAGE_KEY = 'woeoff'
const STORAGE_SCHEMA_VERSION = 1
const STORAGE_KEY_LAST_REMOTE_CHECK = 'woeoffLastRemoteUpdateCheck'

type FeedInStorage = {
  storageSchemaVersion: number,
  feed: Wooeen.FeedOffer
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

async function setStorageData (feed: Wooeen.FeedOffer) {
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
    console.error('WOEOFF: Asked to update feed but already waiting for another update!')
    return
  }
  // Only run this once at a time, otherwise wait for the update
  readLock = new Promise(async function (resolve, reject) {
    try {
        await fetch(
          await getApiUrl(
              "offer/get",
              new URLSearchParams({
                  st:'1',
                  pg: '0',
                  qpp: '150'
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
                    advertiserId: o.advertiserId,
                    advertiserName: o.advertiserName,
                    advertiserColor: o.advertiserColor,
                    title: o.title,
                    description: o.description,
                    url: o.url,
                    media: o.media,
                    price: o.price
                  })
              }
              memoryTodayData = {items: items}
            }
            isKnownRemoteUpdateAvailable = false
          },
          (error) => {
            console.error('WOEOFF: Could not get feed contents');
          }
        )

        resolve()
        if (memoryTodayData && memoryTodayData.items && memoryTodayData.items.length > 0) {
          await setStorageData(memoryTodayData)
        }
    } catch (e) {
      console.error('WOEOFF: Could not process feed contents')
      reject(e)
    } finally {
      readLock = null
    }
  })
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
