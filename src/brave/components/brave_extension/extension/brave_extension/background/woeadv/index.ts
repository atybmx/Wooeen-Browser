import * as Feed from './feed'

const ALARM_KEY_FEED_UPDATE = 'woe-adv-update-feed'
const ALARM_KEY_FEED_MEMORY = 'woe-adv-memory-feed'

// Setup alarms for refresh
function ensureUpdateFrequency () {
  chrome.alarms.get(ALARM_KEY_FEED_UPDATE, (alarm) => {
    if (!alarm) {
      chrome.alarms.create(ALARM_KEY_FEED_UPDATE, {
        delayInMinutes: 0,
        periodInMinutes: 30
      })
    }
  })
  chrome.alarms.get(ALARM_KEY_FEED_MEMORY, (alarm) => {
    if (!alarm) {
      chrome.alarms.create(ALARM_KEY_FEED_MEMORY, {
        delayInMinutes: 0,
        periodInMinutes: 1
      })
    }
  })
  chrome.alarms.onAlarm.addListener(async (alarm) => {
    switch (alarm.name) {
      case ALARM_KEY_FEED_UPDATE:
        await Feed.update()
        break
      case ALARM_KEY_FEED_MEMORY:
        await Feed.getLocalData()
        break
    }
  })
}

function stopUpdateFrequency () {
  chrome.alarms.clear(ALARM_KEY_FEED_UPDATE, () => {
    if (chrome.runtime.lastError) {
      console.error('alarms.clear failed: ' + chrome.runtime.lastError.message)
    }
  })
  chrome.alarms.clear(ALARM_KEY_FEED_MEMORY, () => {
    if (chrome.runtime.lastError) {
      console.error('alarms.clear failed: ' + chrome.runtime.lastError.message)
    }
  })
}

async function conditionallyStartupOrShutdown () {
    stopUpdateFrequency()
    ensureUpdateFrequency()
}

conditionallyStartupOrShutdown()
.catch((err) => {
  console.error('Could not startup Brave News', err)
})
