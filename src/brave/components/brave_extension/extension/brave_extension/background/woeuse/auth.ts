import * as Wallet from './feed'

var memoryUser:Wooeen.User | undefined;

export const LOGGED_USER_ID = '_ui'
export const LOGGED_USER_NAME = '_un'
export const LOGGED_USER_TOKEN_ID = '_uti'
export const LOGGED_USER_TOKEN_ACCESS = '_uta'

function getFromStorage() {
  return new Promise<Wooeen.User | undefined>(resolve => {
    chrome.storage.local.get(
      ['_ui',
      '_un',
      '_uti',
      '_uta'], (data) => {
        if(data && Object.keys(data).length !== 0 && data.constructor === Object && data._ui){
          resolve({
            id: data._ui,
            name: data._un,
            tokenId: data._uti,
            tokenAccess: data._uta
          })
        } else {
          resolve()
        }
    })
  })
}

async function getStorageData () {
  const data = await getFromStorage()
  if (!data || !data.id) {
    return
  }
  memoryUser = data
}

const getLocalDataLock = getStorageData()

export async function getLocalData () {
  await getLocalDataLock
  return memoryUser
}

export function getUser(){
  return memoryUser
}

export function saveAuth(name:string, value:string,removed:boolean){
  switch (name) {
    case LOGGED_USER_ID:
      if(removed){
        chrome.storage.local.remove('_ui')
        memoryUser = undefined;
      }else{
        chrome.storage.local.set({'_ui': value})
        if(!memoryUser)
          memoryUser = {}
        memoryUser.id = value
      }
      break;
    case LOGGED_USER_NAME:
      if(removed)
        chrome.storage.local.remove('_un')
      else{
        chrome.storage.local.set({'_un': value})
        if(!memoryUser)
          memoryUser = {}
        memoryUser.name = value
      }
      break;
    case LOGGED_USER_TOKEN_ID:
      if(removed)
        chrome.storage.local.remove('_uti')
      else{
        chrome.storage.local.set({'_uti': value})
        if(!memoryUser)
          memoryUser = {}
        memoryUser.tokenId = value
      }
      break;
    case LOGGED_USER_TOKEN_ACCESS:
      if(removed)
        chrome.storage.local.remove('_uta')
      else{
        chrome.storage.local.set({'_uta': value})
        if(!memoryUser)
          memoryUser = {}
        memoryUser.tokenAccess = value
      }
      break;

    default:
      break;
  }

  //update the wallet if logout or complete login
  if(!memoryUser || (memoryUser && memoryUser.id && memoryUser.tokenId && memoryUser.tokenAccess)){
    Wallet.update(true)
  }
}
