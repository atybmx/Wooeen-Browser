// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import * as React from 'react'

import {
  SettingsMenu,
  SettingsTitle,
  SettingsWrapper,
  SettingsSidebar,
  SettingsFeatureBody,
  SettingsContent,
  SettingsCloseIcon,
  SettingsSidebarButton,
  SettingsSidebarActiveButtonSlider,
  SettingsSidebarButtonText,
  SettingsSidebarSVGContent
} from '../../components/default'

import { getLocale } from '../../../common/locale'

// Icons
import { CloseStrokeIcon } from 'brave-ui/components/icons'
import CtasIcon from './settings/icons/backgroundImage.svg'
import RealboxIcon from './settings/icons/search.svg'
import NewsIcon from './settings/icons/braveToday.svg'
import TopSitesIcon from './settings/icons/topSites.svg'
import CardsIcon from './settings/icons/cards.svg'

// Tabs
const CtasSettings = React.lazy(() => import('./settings/ctas'))
const RealboxSettings = React.lazy(() => import('./settings/realbox'))
const NewsSettings = React.lazy(() => import('./settings/news'))
const TopSitesSettings = React.lazy(() => import('./settings/topSites'))
const CardsSettings = React.lazy(() => import('./settings/cards'))

// Types
import { NewTabActions } from '../../constants/new_tab_types'

export interface Props {
  actions: NewTabActions
  textDirection: string
  showSettingsMenu: boolean
  onClose: () => void
  toggleShowRealbox: () => void
  toggleShowCtas: () => void
  toggleShowNews: () => void
  toggleShowTopSites: () => void
  setMostVisitedSettings: (show: boolean, customize: boolean) => void
  toggleShowBalance: () => void
  toggleShowCashback: () => void
  toggleShowRecommendation: () => void
  toggleCards: (show: boolean) => void
  showRealbox: boolean
  showCtas: boolean
  showNews: boolean
  showTopSites: boolean
  customLinksEnabled: boolean
  showBalance: boolean
  showCashback: boolean
  showRecommendation: boolean
  setActiveTab?: TabType
  cardsHidden: boolean
}

export enum TabType {
  Ctas = 'ctas',
  Realbox = 'realbox',
  News = 'news',
  TopSites = 'topSites',
  Cards = 'cards'
}

interface State {
  activeTab: TabType
}

const allTabTypes = [...Object.values(TabType)]

export default class Settings extends React.PureComponent<Props, State> {
  settingsMenuRef: React.RefObject<any>
  constructor (props: Props) {
    super(props)
    this.settingsMenuRef = React.createRef()
    this.state = {
      activeTab: this.getInitialTab()
    }
  }

  handleClickOutside = (event: Event) => {
    if (
      this.settingsMenuRef &&
      this.settingsMenuRef.current &&
      !this.settingsMenuRef.current.contains(event.target)
    ) {
      this.props.onClose()
    }
  }

  componentDidMount () {
    document.addEventListener('mousedown', this.handleClickOutside)
    document.addEventListener('keydown', this.onKeyPressSettings)
  }

  componentWillUnmount () {
    document.removeEventListener('mousedown', this.handleClickOutside)
  }

  componentDidUpdate (prevProps: Props) {
    if (prevProps.setActiveTab !== this.props.setActiveTab && this.props.setActiveTab) {
      this.setActiveTab(this.props.setActiveTab)
    }
    const isNewlyShown = (!prevProps.showSettingsMenu && this.props.showSettingsMenu)
    if (isNewlyShown) {
      this.setActiveTab(this.getInitialTab())
    }
  }

  onKeyPressSettings = (event: KeyboardEvent) => {
    if (event.key === 'Escape') {
      this.props.onClose()
    }
  }

  getInitialTab () {
    let tab = TabType.Ctas
    if (this.props.setActiveTab) {
      if (this.getActiveTabTypes().includes(this.props.setActiveTab)) {
        tab = this.props.setActiveTab
      }
    }
    return tab
  }

  toggleShowCtas = () => {
    this.props.toggleShowCtas()
  }

  toggleShowRealbox = () => {
    this.props.toggleShowRealbox()
  }

  toggleShowNews = () => {
    this.props.toggleShowNews()
  }

  setActiveTab (activeTab: TabType) {
    this.setState({ activeTab })
  }

  getActiveTabTypes (): TabType[] {
    return allTabTypes
  }

  getTabIcon (tab: TabType, isActiveTab: boolean) {
    let srcUrl
    switch (tab) {
      case TabType.Ctas:
        srcUrl = CtasIcon
        break
      case TabType.Realbox:
        srcUrl = RealboxIcon
        break
      case TabType.News:
        srcUrl = NewsIcon
        break
      case TabType.TopSites:
        srcUrl = TopSitesIcon
        break
      case TabType.Cards:
        srcUrl = CardsIcon
        break
      default:
        srcUrl = CardsIcon
        break
    }
    return <SettingsSidebarSVGContent isActive={isActiveTab} src={srcUrl} />
  }

  getTabTitleKey = (tab: TabType) => {
    switch (tab) {
      case TabType.Ctas:
        return 'woeCtasTitle'
      case TabType.Realbox:
        return 'woeRealboxTitle'
      case TabType.News:
        return 'woeNewsTitle'
      case TabType.TopSites:
        return 'topSitesTitle'
      case TabType.Cards:
        return 'cards'
      default:
        return ''
    }
  }

  render () {
    const {
      textDirection,
      showSettingsMenu,
      toggleShowTopSites,
      setMostVisitedSettings,
      toggleShowBalance,
      toggleShowCashback,
      toggleShowRecommendation,
      showRealbox,
      showCtas,
      showNews,
      showTopSites,
      customLinksEnabled,
      showBalance,
      showCashback,
      showRecommendation,
      toggleCards,
      cardsHidden
    } = this.props
    const { activeTab } = this.state

    if (!showSettingsMenu) {
      return null
    }

    const tabTypes = this.getActiveTabTypes()
    return (
      <SettingsWrapper textDirection={textDirection}>
        <SettingsMenu
          ref={this.settingsMenuRef}
          textDirection={textDirection}
          title={getLocale('dashboardSettingsTitle')}
        >
          <SettingsTitle id='settingsTitle'>
            <h1>{getLocale('dashboardSettingsTitle')}</h1>
            <SettingsCloseIcon onClick={this.props.onClose}>
              <CloseStrokeIcon />
            </SettingsCloseIcon>
          </SettingsTitle>
          <SettingsContent id='settingsBody'>
            <SettingsSidebar id='sidebar'>
              <SettingsSidebarActiveButtonSlider
                translateTo={tabTypes.indexOf(activeTab)}
              />
              {
                tabTypes.map((tabType, index) => {
                  const titleKey = this.getTabTitleKey(tabType)
                  const isActive = (activeTab === tabType)
                  return (
                    <SettingsSidebarButton
                      tabIndex={0}
                      key={`sidebar-button-${index}`}
                      activeTab={isActive}
                      onClick={this.setActiveTab.bind(this, tabType)}
                    >
                      {this.getTabIcon(tabType, isActive)}
                      <SettingsSidebarButtonText
                        isActive={isActive}
                        data-text={getLocale(titleKey)}
                      >
                        {getLocale(titleKey)}
                      </SettingsSidebarButtonText>
                    </SettingsSidebarButton>
                  )
                })
              }
            </SettingsSidebar>
            <SettingsFeatureBody id='content'>
              {/* Empty loading fallback is ok here since we are loading from local disk. */}
              <React.Suspense fallback={(<div/>)}>
              {
                activeTab === TabType.Ctas
                  ? (
                  <CtasSettings
                    toggleShowCtas={this.toggleShowCtas}
                    showCtas={showCtas}
                  />
                ) : null
              }
              {
                activeTab === TabType.Realbox
                  ? (
                  <RealboxSettings
                    toggleShowRealbox={this.toggleShowRealbox}
                    showRealbox={showRealbox}
                  />
                ) : null
              }
              {
                activeTab === TabType.News
                  ? (
                  <NewsSettings
                    toggleShowNews={this.toggleShowNews}
                    showNews={showNews}
                  />
                ) : null
              }
              {
                activeTab === TabType.TopSites
                  ? (
                    <TopSitesSettings
                      toggleShowTopSites={toggleShowTopSites}
                      showTopSites={showTopSites}
                      customLinksEnabled={customLinksEnabled}
                      setMostVisitedSettings={setMostVisitedSettings}
                    />
                  ) : null
              }
              {
                activeTab === TabType.Cards
                  ? (
                    <CardsSettings
                      toggleCards={toggleCards}
                      cardsHidden={cardsHidden}
                      toggleShowBalance={toggleShowBalance}
                      showBalance={showBalance}
                      toggleShowCashback={toggleShowCashback}
                      showCashback={showCashback}
                      toggleShowRecommendation={toggleShowRecommendation}
                      showRecommendation={showRecommendation}
                    />
                  ) : null
              }
              </React.Suspense>
            </SettingsFeatureBody>
          </SettingsContent>
        </SettingsMenu>
      </SettingsWrapper>
    )
  }
}
