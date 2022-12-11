import * as Auth from './auth'
import { getApiUrlPath } from '../woeutils/urls'

let memoryTodayData: Wooeen.FeedUser | undefined
let readLock: Promise<void> | null
let isKnownRemoteUpdateAvailable = false

const STORAGE_KEY = 'woeuse'
const STORAGE_SCHEMA_VERSION = 1
const STORAGE_KEY_LAST_REMOTE_CHECK = 'woeuseLastRemoteUpdateCheck'

type FeedInStorage = {
  storageSchemaVersion: number,
  feed: Wooeen.FeedUser
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

async function setStorageData (feed: Wooeen.FeedUser) {
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
    console.error('WOEUSE: Asked to update feed but already waiting for another update!')
    return
  }

  // Only run this once at a time, otherwise wait for the update
  readLock = new Promise(async function (resolve, reject) {
    try {
        let user = await Auth.getLocalData();
        if(user && user.id && user.tokenId && user.tokenAccess){
          await fetch(
            await getApiUrlPath("wallet/get"),
              {
                method: 'GET',
                headers: new Headers({
                  'uti': user.tokenId,
                  'uta': user.tokenAccess
                })
              }
          )
          .then(res => res.json())
          .then(
            (result) => {
              setLastUpdateCheckTime(Date.now())

              if(result.result && result.callback && result.callback.id){
                let o = result.callback;

                memoryTodayData = {
                  loaded: true,
                  items:{
                    id: o.id,
                    name: o.name,
                    timezone: o.timezone,
                    language: o.language,
                    country:{
                      id: o.country.id,
                      language: o.country.language,
                      currency: {
                        id: o.country.currency.id,
                        symbol: o.country.currency.symbol
                      },
                      loadPosts: o.country.loadPosts,
                      loadOffers: o.country.loadOffers,
                      loadCoupons: o.country.loadCoupons,
                      loadTasks: o.country.loadTasks
                    },
                    wallet:{
                      conversionsPending: o.wallet.conversionsPending,
                    	conversionsRegistered: o.wallet.conversionsRegistered,
                    	conversionsApproved: o.wallet.conversionsApproved,
                    	conversionsRejected: o.wallet.conversionsRejected,
                    	conversionsWithdrawn: o.wallet.conversionsWithdrawn,
                    	amountPending: o.wallet.amountPending,
                    	amountRegistered: o.wallet.amountRegistered,
                    	amountApproved: o.wallet.amountApproved,
                    	amountRejected: o.wallet.amountRejected,
                    	amountWithdrawn: o.wallet.amountWithdrawn,
                    	recommendationsRegistered: o.wallet.recommendationsRegistered,
                    	recommendationsConverted: o.wallet.recommendationsConverted,
                    	recommendationsConfirmed: o.wallet.recommendationsConfirmed,
                      recommendationsRegisteredAmount: o.wallet.recommendationsRegisteredAmount,
                    	recommendationsConvertedAmount: o.wallet.recommendationsConvertedAmount,
                    	recommendationsConfirmedAmount: o.wallet.recommendationsConfirmedAmount,
                    	balance: o.wallet.balance
                    }
                  }
                }
              }
              isKnownRemoteUpdateAvailable = false
            },
            (error) => {
              console.error('WOEUSE: Could not get feed contents');
            }
          )
        }else{
          memoryTodayData = {
            loaded: true
          }
        }

        resolve()
        if (memoryTodayData && memoryTodayData.loaded) {
          await setStorageData(memoryTodayData)
        }

    } catch (e) {
      console.error('WOEUSE: Could not process feed contents')
      reject(e)
    } finally {
      readLock = null
    }
  })
}

export async function getUser () {
  await getOrFetchData

  if(memoryTodayData && memoryTodayData.loaded && memoryTodayData.items && memoryTodayData.items.id)
    return memoryTodayData.items

  return undefined
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
  if (memoryTodayData && memoryTodayData.loaded && !isKnownRemoteUpdateAvailable) {
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
