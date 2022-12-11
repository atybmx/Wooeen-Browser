// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import * as React from 'react'

// Components
import Stats from './stats'
import TopSitesGrid from './gridSites'
import Ctas from './ctas'
import FooterInfo from '../../components/default/footer/footer'
import SiteRemovalNotification from './notification'
import {
  BalanceWidget as Balance,
  CashbackWidget as Cashback,
  RecommendationsWidget as Recommendations,
  EditTopSite
} from '../../components/default'
import * as Page from '../../components/default/page'
import { addNewTopSite, editTopSite } from '../../api/topSites'

// Helpers
import VisibilityTimer from '../../helpers/visibilityTimer'

// Wooeen comps
import "../../components/wooeen/styles/style.css"
import { LogoLink, WooeenImage } from '../../components/wooeen/logo'
import WooeenNewsHint from '../../components/wooeen/news/hint'
import WooeenNews from '../../components/wooeen/news'
import { CtaShareDialog } from '../../components/wooeen/ctas'

import Realbox from './realbox'

// Types
import { getLocale } from '../../../common/locale'
import { NewTabActions } from '../../constants/new_tab_types'

// NTP features
import Settings, { TabType as SettingsTabType } from './settings'
import { MAX_GRID_SIZE } from '../../constants/new_tab_ui'

interface Props {
  newTabData: NewTab.State
  gridSitesData: NewTab.GridSitesState
  actions: NewTabActions
  saveShowNews: (value: boolean) => void
  saveShowCtas: (value: boolean) => void
  saveShowRealbox: (value: boolean) => void
  saveShowBalance: (value: boolean) => void
  saveShowCashback: (value: boolean) => void
  saveShowRecommendation: (value: boolean) => void
  saveSetAllStackWidgets: (value: boolean) => void
}

interface State {
  showSettingsMenu: boolean
  showEditTopSite: boolean
  targetTopSiteForEditing?: NewTab.Site
  backgroundHasLoaded: boolean
  activeSettingsTab: SettingsTabType | null
}

class NewTabPage extends React.Component<Props, State> {
  state: State = {
    showSettingsMenu: false,
    showEditTopSite: false,
    backgroundHasLoaded: false,
    activeSettingsTab: null
  }
  imageSource?: string = undefined
  timerIdForBrandedWallpaperNotification?: number = undefined
  onVisiblityTimerExpired = () => {
  }
  visibilityTimer = new VisibilityTimer(this.onVisiblityTimerExpired, 4000)

  componentDidMount () {
    //default settings WOOEEN

    // if a notification is open at component mounting time, close it
    this.props.actions.showTilesRemovedNotice(false)

    this.checkShouldOpenSettings()
  }

  componentDidUpdate (prevProps: Props) {

  }

  checkShouldOpenSettings () {
    const params = window.location.search
    const urlParams = new URLSearchParams(params)
    const openSettings = urlParams.get('openSettings')

    if (openSettings) {
      this.setState({ showSettingsMenu: true })
      // Remove settings param so menu doesn't persist on reload
      window.history.pushState(null, '', '/')
    }
  }

  toggleShowNews = () => {
    this.props.saveShowNews(
      !this.props.newTabData.showNews
    )
  }

  toggleShowCtas = () => {
    this.props.saveShowCtas(
      !this.props.newTabData.showCtas
    )
  }

  toggleShowRealbox = () => {
    this.props.saveShowRealbox(
      !this.props.newTabData.showRealbox
    )
  }

  toggleShowTopSites = () => {
    const { showTopSites, customLinksEnabled } = this.props.newTabData
    this.props.actions.setMostVisitedSettings(!showTopSites, customLinksEnabled)
  }

  toggleCustomLinksEnabled = () => {
    const { showTopSites, customLinksEnabled } = this.props.newTabData
    this.props.actions.setMostVisitedSettings(showTopSites, !customLinksEnabled)
  }

  setMostVisitedSettings = (showTopSites: boolean, customLinksEnabled: boolean) => {
    this.props.actions.setMostVisitedSettings(showTopSites, customLinksEnabled)
  }

  toggleShowBalance = () => {
    this.props.saveShowBalance(!this.props.newTabData.showBalance)
  }

  toggleShowCashback = () => {
    this.props.saveShowCashback(!this.props.newTabData.showCashback)
  }

  toggleShowRecommendation = () => {
    this.props.saveShowRecommendation(!this.props.newTabData.showRecommendation)
  }

  setBalanceInfo = (info: Record<string, Record<string, string>>) => {
    this.props.actions.onAssetsBalanceInfo(info)
  }

  setAssetDepositInfo = (symbol: string, address: string, url: string) => {
    this.props.actions.onAssetDepositInfo(symbol, address, url)
  }

  dismissNotification = (id: string) => {
    this.props.actions.dismissNotification(id)
  }

  closeSettings = () => {
    this.setState({
      showSettingsMenu: false,
      activeSettingsTab: null
    })
  }

  showEditTopSite = (targetTopSiteForEditing?: NewTab.Site) => {
    this.setState({
      showEditTopSite: true,
      targetTopSiteForEditing
    })
  }

  closeEditTopSite = () => {
    this.setState({
      showEditTopSite: false
    })
  }

  saveNewTopSite = (title: string, url: string, newUrl: string) => {
    if (url) {
      editTopSite(title, url, newUrl === url ? '' : newUrl)
    } else {
      addNewTopSite(title, newUrl)
    }
    this.closeEditTopSite()
  }

  openSettings = (activeTab?: SettingsTabType) => {
    this.props.actions.customizeClicked()
    this.setState({
      showSettingsMenu: !this.state.showSettingsMenu,
      activeSettingsTab: activeTab || null
    })
  }

  openSettingsEditCards = () => {
    this.openSettings(SettingsTabType.Cards)
  }

  setForegroundStackWidget = (widget: NewTab.StackWidget) => {
    console.log(this.props.actions);
    console.log(widget);
    this.props.actions.setForegroundStackWidget(widget)
  }

  setInitialAmount = (amount: string) => {
    this.props.actions.setInitialAmount(amount)
  }

  setInitialFiat = (fiat: string) => {
    this.props.actions.setInitialFiat(fiat)
  }

  setInitialAsset = (asset: string) => {
    this.props.actions.setInitialAsset(asset)
  }

  setUserTLDAutoSet = () => {
    this.props.actions.setUserTLDAutoSet()
  }

  setAssetDepositQRCodeSrc = (asset: string, src: string) => {
    this.props.actions.onDepositQRForAsset(asset, src)
  }

  setConvertableAssets = (asset: string, assets: string[]) => {
    this.props.actions.onConvertableAssets(asset, assets)
  }

  dismissAuthInvalid = () => {
    this.props.actions.setAuthInvalid(false)
  }

  getCryptoContent () {
    if (this.props.newTabData.hideAllWidgets) {
      return null
    }
    const {
      widgetStackOrder,
      showBalance,
      showCashback,
      showRecommendation,
    } = this.props.newTabData
    const lookup = {
      'balance': {
        display: showBalance,
        render: this.renderBalanceWidget.bind(this)
      },
      'cashback': {
        display: showCashback,
        render: this.renderCashbackWidget.bind(this)
      },
      'recommendation': {
        display: showRecommendation,
        render: this.renderRecommendationsWidget.bind(this)
      }
    }

    let widgetList = widgetStackOrder.filter((widget: NewTab.StackWidget) => {
      if (!lookup.hasOwnProperty(widget)) {
        return false
      }

      return lookup[widget].display
    })

    let widgetListOrder = ['balance','cashback','recommendation'].filter((widget: NewTab.StackWidget) => {
      return widgetList.includes(widget)
    })

    return (
      <>
        {widgetListOrder.map((widget: NewTab.StackWidget, i: number) => {
          // const isForeground = i === widgetList.length - 1
          // const isForeground = i == 0
          const isForeground = widgetList.indexOf(widget) === widgetList.length - 1

          return (
            <div key={`widget-${widget}`}>
              {lookup[widget].render(isForeground, i)}
            </div>
          )
        })}
      </>
    )
  }

  allWidgetsHidden = () => {
    const {
      showBalance,
      showCashback,
      showRecommendation,
      hideAllWidgets
    } = this.props.newTabData
    return hideAllWidgets || [
      showBalance,
      showCashback,
      showRecommendation
    ].every((widget: boolean) => !widget)
  }

  renderCryptoContent () {
    const { newTabData } = this.props
    const { widgetStackOrder } = newTabData

    if (!widgetStackOrder.length) {
      return null
    }

    return (
      <Page.GridItemWidgetStack>
        {this.getCryptoContent()}
      </Page.GridItemWidgetStack>
    )
  }

  renderBalanceWidget (showContent: boolean, position: number) {
    const { newTabData } = this.props
    const {
      showBalance: balanceWidgetOn,
      textDirection
    } = newTabData

    const shouldShowBalanceWidget = balanceWidgetOn

    if (!shouldShowBalanceWidget) {
      return null
    }

    return (
      <Balance
        balance={0}
        showContent={showContent}
        stackPosition={position}
        hideWidget={this.toggleShowBalance}
        onShowContent={this.setForegroundStackWidget.bind(this, 'balance')}
        textDirection={textDirection}
        menuPosition={'left'}
        paddingType={'none'}
        preventFocus={false}
        woeUser={newTabData.woeUser}
        showBalance={newTabData.showBalance}
        showCashback={newTabData.showCashback}
        showRecommendation={newTabData.showRecommendation}
      />
    )
  }

  renderCashbackWidget (showContent: boolean, position: number) {
    const { newTabData } = this.props
    const {
      showCashback: cashbackWidgetOn,
      textDirection
    } = newTabData

    const shouldShowCashbackWidget = cashbackWidgetOn

    if (!shouldShowCashbackWidget) {
      return null
    }

    return (
      <Cashback
        amountPending={0}
        amountRegistered={0}
        showContent={showContent}
        stackPosition={position}
        hideWidget={this.toggleShowCashback}
        onShowContent={this.setForegroundStackWidget.bind(this, 'cashback')}
        textDirection={textDirection}
        menuPosition={'left'}
        paddingType={'none'}
        preventFocus={false}
        woeUser={newTabData.woeUser}
        showBalance={newTabData.showBalance}
        showCashback={newTabData.showCashback}
        showRecommendation={newTabData.showRecommendation}
      />
    )
  }

  renderRecommendationsWidget (showContent: boolean, position: number) {
    const { newTabData } = this.props
    const {
      showRecommendation: recommendationWidgetOn,
      textDirection
    } = newTabData

    const shouldShowRecommendationWidget = recommendationWidgetOn

    if (!shouldShowRecommendationWidget) {
      return null
    }

    return (
      <Recommendations
        totalRegistered={0}
        amountRegistered={0}
        amountConfirmed={0}
        showContent={showContent}
        stackPosition={position}
        hideWidget={this.toggleShowRecommendation}
        onShowContent={this.setForegroundStackWidget.bind(this, 'recommendation')}
        textDirection={textDirection}
        menuPosition={'left'}
        paddingType={'none'}
        preventFocus={false}
        woeUser={newTabData.woeUser}
        showBalance={newTabData.showBalance}
        showCashback={newTabData.showCashback}
        showRecommendation={newTabData.showRecommendation}
      />
    )
  }

  render () {
    const { newTabData, gridSitesData, actions } = this.props
    const { showSettingsMenu, showEditTopSite, targetTopSiteForEditing } = this.state

    if (!newTabData) {
      return null
    }

    const hasImage = this.imageSource !== undefined
    const cryptoContent = this.renderCryptoContent()
    const showAddNewSiteMenuItem = newTabData.customLinksNum < MAX_GRID_SIZE

    let showTopSites = newTabData.showTopSites
    // In favorites mode, add site tile is visible by default if there is no
    // item. In frecency, top sites widget is hidden with empty tiles.
    if (showTopSites && !newTabData.customLinksEnabled) {
      showTopSites = this.props.gridSitesData.gridSites.length !== 0
    }

    return (
      <Page.App
        dataIsReady={newTabData.initialDataLoaded}
        hasImage={hasImage}
        imageSrc={this.imageSource}
        imageHasLoaded={this.state.backgroundHasLoaded}
      >
        <Page.Page
            hasImage={hasImage}
            imageSrc={this.imageSource}
            imageHasLoaded={this.state.backgroundHasLoaded}
            showCtas={newTabData.showCtas}
            showBalance={newTabData.showBalance}
            showCashback={newTabData.showCashback}
            showRecommendation={newTabData.showRecommendation}
            showTopSites={showTopSites}
        >
        <Page.LeftPanel>
          <Page.LeftPanelC1>
            <LogoLink href={'https://www.wooeen.com'}>
              <WooeenImage />
            </LogoLink>
            {newTabData.showCtas &&
              <Page.GridItemCtas>
                <Ctas
                  paddingType={'none'}
                  widgetTitle={getLocale('woeCtasWidgetTitle')}
                  textDirection={newTabData.textDirection}
                  hideWidget={this.toggleShowCtas}
                  menuPosition={'right'}
                  woeUser={this.props.newTabData.woeUser}>
                </Ctas>
                <CtaShareDialog
                  woeUser={this.props.newTabData.woeUser}></CtaShareDialog>
              </Page.GridItemCtas>
            }
          </Page.LeftPanelC1>
          <Page.LeftPanelC2>
            {newTabData.showRealbox &&
              <Page.GridItemRealbox>
                <Realbox
                  woeUser={newTabData.woeUser}
                  woeCountry={newTabData.woeCountry}
                  paddingType={'right'}
                  widgetTitle={getLocale('woeRealboxWidgetTitle')}
                  textDirection={newTabData.textDirection}
                  hideWidget={this.toggleShowRealbox}
                  menuPosition={'right'}></Realbox>
              </Page.GridItemRealbox>
            }
            {newTabData.showStats && 1 > 2 &&
            <Page.GridItemStats>
              <Stats
                paddingType={'right'}
                widgetTitle={getLocale('statsTitle')}
                textDirection={newTabData.textDirection}
                hideWidget={this.toggleShowCtas}
                menuPosition={'right'}
              />
            </Page.GridItemStats>
            }
            {
              showTopSites
                ? (
                <Page.GridItemTopSites>
                  <TopSitesGrid
                    actions={actions}
                    paddingType={'none'}
                    customLinksEnabled={newTabData.customLinksEnabled}
                    onShowEditTopSite={this.showEditTopSite}
                    widgetTitle={getLocale('topSitesTitle')}
                    gridSites={gridSitesData.gridSites}
                    menuPosition={'right'}
                    hideWidget={this.toggleShowTopSites}
                    onAddSite={showAddNewSiteMenuItem ? this.showEditTopSite : undefined}
                    onToggleCustomLinksEnabled={this.toggleCustomLinksEnabled}
                    textDirection={newTabData.textDirection}
                  />
                </Page.GridItemTopSites>
                ) : null
            }
          </Page.LeftPanelC2>
        </Page.LeftPanel>
          {
            gridSitesData.shouldShowSiteRemovedNotification
            ? (
            <Page.GridItemNotification>
              <SiteRemovalNotification actions={actions} showRestoreAll={!newTabData.customLinksEnabled} />
            </Page.GridItemNotification>
            ) : null
          }
            {cryptoContent}
          <Page.Footer>
            <Page.FooterContent>
            <FooterInfo
              textDirection={newTabData.textDirection}
              onClickSettings={this.openSettings}
            />
            </Page.FooterContent>
          </Page.Footer>
          {newTabData.showNews &&
          <Page.GridItemNavigationWooeenNews>
            <WooeenNewsHint />
          </Page.GridItemNavigationWooeenNews>
          }
        </Page.Page>
        {newTabData.showNews &&
        <WooeenNews
          woeLoadPosts={newTabData.woeUser && newTabData.woeUser.id && newTabData.woeUser.country && newTabData.woeUser.country.id ? newTabData.woeUser.country.loadPosts : newTabData.woeCountry && newTabData.woeCountry.id ? newTabData.woeCountry.loadPosts : false}
          woePosts={newTabData.woePosts ? newTabData.woePosts.items : undefined}
          woeLoadTasks={newTabData.woeUser && newTabData.woeUser.id && newTabData.woeUser.country && newTabData.woeUser.country.id ? newTabData.woeUser.country.loadTasks : newTabData.woeCountry && newTabData.woeCountry.id ? newTabData.woeCountry.loadTasks : false}
          woeTasks={newTabData.woeTasks ? newTabData.woeTasks.items : undefined}
          woeLoadOffers={newTabData.woeUser && newTabData.woeUser.id && newTabData.woeUser.country && newTabData.woeUser.country.id ? newTabData.woeUser.country.loadOffers : newTabData.woeCountry && newTabData.woeCountry.id ? newTabData.woeCountry.loadOffers : false}
          woeOffers={newTabData.woeOffers ? newTabData.woeOffers.items : undefined}
          woeLoadCoupons={newTabData.woeUser && newTabData.woeUser.id && newTabData.woeUser.country && newTabData.woeUser.country.id ? newTabData.woeUser.country.loadCoupons : newTabData.woeCountry && newTabData.woeCountry.id ? newTabData.woeCountry.loadCoupons : false}
          woeCoupons={newTabData.woeCoupons ? newTabData.woeCoupons.items : undefined}/>
        }
        <Settings
          actions={actions}
          textDirection={newTabData.textDirection}
          showSettingsMenu={showSettingsMenu}
          onClose={this.closeSettings}
          setActiveTab={this.state.activeSettingsTab || undefined}
          toggleShowNews={this.toggleShowNews}
          toggleShowCtas={this.toggleShowCtas}
          toggleShowRealbox={this.toggleShowRealbox}
          toggleShowTopSites={this.toggleShowTopSites}
          setMostVisitedSettings={this.setMostVisitedSettings}
          showTopSites={newTabData.showTopSites}
          customLinksEnabled={newTabData.customLinksEnabled}
          showNews={newTabData.showNews}
          showCtas={newTabData.showCtas}
          showRealbox={newTabData.showRealbox}
          showBalance={newTabData.showBalance}
          showCashback={newTabData.showCashback}
          showRecommendation={newTabData.showRecommendation}
          toggleShowBalance={this.toggleShowBalance}
          toggleShowCashback={this.toggleShowCashback}
          toggleShowRecommendation={this.toggleShowRecommendation}
          cardsHidden={this.allWidgetsHidden()}
          toggleCards={this.props.saveSetAllStackWidgets}
        />
        {
          showEditTopSite ?
            <EditTopSite
              targetTopSiteForEditing={targetTopSiteForEditing}
              textDirection={newTabData.textDirection}
              onClose={this.closeEditTopSite}
              onSave={this.saveNewTopSite}
            /> : null
        }
      </Page.App>
    )
  }
}

export default NewTabPage
