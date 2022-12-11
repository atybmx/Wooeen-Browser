import { getApiUrlPath } from '../woeutils/urls'

let memoryTodayData: Wooeen.Version | undefined
let readLock: Promise<void> | null
let isKnownRemoteUpdateAvailable = false

const STORAGE_KEY = 'woever'
const STORAGE_SCHEMA_VERSION = 1
const STORAGE_KEY_LAST_REMOTE_CHECK = 'woeverLastRemoteUpdateCheck'

type FeedInStorage = {
  storageSchemaVersion: number,
  feed: Wooeen.Version
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

async function setStorageData (feed: Wooeen.Version) {
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
    console.error('WOEVER: Asked to update feed but already waiting for another update!')
    return
  }
  // Only run this once at a time, otherwise wait for the update
  readLock = new Promise(async function (resolve, reject) {
    try {
        await fetch(
          await getApiUrlPath(
              "version/script")
        )
        .then(res => res.json())
        .then(
          (result) => {
            setLastUpdateCheckTime(Date.now())

            if(result.result && result.callback){
              let o = result.callback;
              memoryTodayData = {
                checkout: o.checkout,
                product: o.product,
                query: o.query
              }
            }
            isKnownRemoteUpdateAvailable = false
          },
          (error) => {
            console.error('WOEVER: Could not get feed contents');
          }
        )

        resolve()
        if (memoryTodayData) {
          await setStorageData(memoryTodayData)
        }
    } catch (e) {
      console.error('WOEVER: Could not process feed contents')
      reject(e)
    } finally {
      readLock = null
    }
  })
}

export function getVersion() : Wooeen.Version | undefined {
  return memoryTodayData;
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
  if (memoryTodayData && !isKnownRemoteUpdateAvailable) {
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
