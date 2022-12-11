/* Copyright 2019 The Brave Authors. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

#include "chrome/common/url_constants.h"

#include "build/branding_buildflags.h"
#include "chrome/common/webui_url_constants.h"

namespace chrome {

const char kAccessibilityLabelsLearnMoreURL[] =
    "https://www.wooeen.com/";

const char kAutomaticSettingsResetLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360017903152-How-do-I-reset-Brave-settings-to-default-";

const char kAdvancedProtectionDownloadLearnMoreURL[] =
    "https://www.wooeen.com";

const char kBluetoothAdapterOffHelpURL[] =
    "https://www.wooeen.com/";

const char kCastCloudServicesHelpURL[] =
    "https://www.wooeen.com/";

const char kCastNoDestinationFoundURL[] =
    "https://www.wooeen.com/";

const char kChooserHidOverviewUrl[] =
    "https://github.com/brave/brave-browser/wiki/Web-API-Permissions";

const char kChooserSerialOverviewUrl[] =
    "https://github.com/brave/brave-browser/wiki/Web-API-Permissions";

const char kChooserUsbOverviewURL[] =
    "https://github.com/brave/brave-browser/wiki/Web-API-Permissions";

const char kChromeBetaForumURL[] =
    "https://community.brave.com/c/beta-builds";

const char kChromeFixUpdateProblems[] =
    "https://www.wooeen.com/";

const char kChromeHelpViaKeyboardURL[] =
    "https://www.wooeen.com/";

const char kChromeHelpViaMenuURL[] =
    "https://www.wooeen.com/";

const char kChromeHelpViaWebUIURL[] =
    "https://www.wooeen.com/";

const char kChromeNativeScheme[] = "chrome-native";

const char kChromeSearchLocalNtpHost[] = "local-ntp";

const char kChromeSearchMostVisitedHost[] = "most-visited";
const char kChromeSearchMostVisitedUrl[] = "chrome-search://most-visited/";

const char kChromeUIUntrustedNewTabPageBackgroundUrl[] =
    "chrome-untrusted://background.jpg";
const char kChromeUIUntrustedNewTabPageBackgroundFilename[] = "background.jpg";

const char kChromeSearchRemoteNtpHost[] = "remote-ntp";

const char kChromeSearchScheme[] = "chrome-search";

const char kChromeUIUntrustedNewTabPageUrl[] =
    "chrome-untrusted://new-tab-page/";

const char kChromiumProjectURL[] = "https://github.com/brave/brave-browser/";

const char kCloudPrintCertificateErrorLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360017880792-How-do-I-print-from-Brave-";

const char kContentSettingsExceptionsLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360018205431-How-do-I-change-site-permissions-";

const char kCookiesSettingsHelpCenterURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360018205431-How-do-I-change-site-permissions-";

const char kCrashReasonURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360018192251-How-do-I-fix-page-crashes-and-other-page-loading-errors-";

const char kCrashReasonFeedbackDisplayedURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360018192251-How-do-I-fix-page-crashes-and-other-page-loading-errors-";

const char kDoNotTrackLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360017905612-How-do-I-turn-Do-Not-Track-on-or-off-";

const char kDownloadInterruptedLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360018192491-How-do-I-fix-file-download-errors-";

const char kDownloadScanningLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360018192491-How-do-I-fix-file-download-errors-";

const char kExtensionControlledSettingLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360018185651-How-do-I-stop-extensions-from-changing-my-settings-";

const char kExtensionInvalidRequestURL[] = "chrome-extension://invalid/";

const char kFlashDeprecationLearnMoreURL[] =
    "https://blog.chromium.org/2017/07/so-long-and-thanks-for-all-flash.html";

const char kGoogleAccountActivityControlsURL[] =
    "https://www.wooeen.com/";

const char kGoogleAccountURL[] = "https://www.wooeen.com/";

const char kGoogleAccountChooserURL[] = "https://www.wooeen.com/";

const char kGooglePasswordManagerURL[] = "https://www.wooeen.com";

const char kLearnMoreReportingURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360017905872-How-do-I-enable-or-disable-automatic-crash-reporting-";

const char kManagedUiLearnMoreUrl[] = "https://www.wooeen.com/";

const char kMixedContentDownloadBlockingLearnMoreUrl[] =
    "https://www.wooeen.com/";

const char kMyActivityUrlInClearBrowsingData[] =
    "https://www.wooeen.com/";

const char kOmniboxLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360017479752-How-do-I-set-my-default-search-engine-";

const char kPageInfoHelpCenterURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360018185871-How-do-I-check-if-a-site-s-connection-is-secure-";

const char kPasswordCheckLearnMoreURL[] = "https://www.wooeen.com/";

const char kPasswordGenerationLearnMoreURL[] = "https://www.wooeen.com/";

const char kPasswordManagerLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360018185951-How-do-I-use-the-built-in-password-manager-";

const char kPaymentMethodsURL[] = "https://www.wooeen.com";

const char kPaymentMethodsLearnMoreURL[] =
    "https://www.wooeen.com";

const char kPrivacyLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360017989132-How-do-I-change-my-Privacy-Settings-";

const char kRemoveNonCWSExtensionURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360017914832-Why-am-I-seeing-the-message-extensions-disabled-by-Brave-";

const char kResetProfileSettingsLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360017903152-How-do-I-reset-Brave-settings-to-default-";

const char kSafeBrowsingHelpCenterURL[] =
    "https://www.wooeen.com/";

const char kSafetyTipHelpCenterURL[] =
    "https://www.wooeen.com/";

const char kSearchHistoryUrlInClearBrowsingData[] =
    "https://www.wooeen.com/";

const char kSeeMoreSecurityTipsURL[] =
    "https://www.wooeen.com/";

const char kSettingsSearchHelpURL[] =
    "https://www.wooeen.com/";

const char kSyncAndGoogleServicesLearnMoreURL[] =
    "https://www.wooeen.com/";

const char kSyncEncryptionHelpURL[] =
    "https://www.wooeen.com/";

const char kSyncErrorsHelpURL[] =
    "https://www.wooeen.com/";

const char kSyncGoogleDashboardURL[] =
    "https://www.wooeen.com/";

const char kSyncLearnMoreURL[] =
    "https://www.wooeen.com/";

#if !defined(OS_ANDROID)
const char kSyncTrustedVaultOptInURL[] = "https://www.wooeen.com/";
#endif

const char kUpgradeHelpCenterBaseURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360025390311-How-do-I-download-and-install-Brave-";

const char kWhoIsMyAdministratorHelpURL[] =
    "https://www.wooeen.com/";

const char kCwsEnhancedSafeBrowsingLearnMoreURL[] =
    "https://www.wooeen.com/";

#if defined(OS_ANDROID)
const char kEnhancedPlaybackNotificationLearnMoreURL[] =
// Keep in sync with chrome/android/java/strings/android_chrome_strings.grd
    "https://community.brave.com";
#endif

#if defined(OS_MAC)
const char kChromeEnterpriseSignInLearnMoreURL[] =
    "https://www.wooeen.com/";

const char kMac10_10_ObsoleteURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360025390311-How-do-I-download-and-install-Brave-";
#endif

#if defined(OS_WIN)
const char kChromeCleanerLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360017884152-How-do-I-remove-unwanted-ads-pop-ups-malware-";

const char kWindowsXPVistaDeprecationURL[] =
    "https://www.wooeen.com/";
#endif

#if BUILDFLAG(ENABLE_ONE_CLICK_SIGNIN)
const char kChromeSyncLearnMoreURL[] =
    "https://www.wooeen.com/";
#endif  // BUILDFLAG(ENABLE_ONE_CLICK_SIGNIN)

#if BUILDFLAG(ENABLE_PLUGINS)
const char kOutdatedPluginLearnMoreURL[] =
    "https://www.wooeen.com/hc/en-us/articles/"
    "360018163151-How-do-I-manage-Flash-audio-video-";
#endif

}  // namespace chrome
