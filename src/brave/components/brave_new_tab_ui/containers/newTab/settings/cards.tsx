// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import * as React from 'react'

import {
  FeaturedSettingsWidget,
  StyledBannerImage,
  StyledSettingsInfo,
  StyledSettingsTitle,
  StyledSettingsCopy,
  StyledWidgetToggle,
  SettingsWidget,
  StyledAddButtonIcon,
  StyledHideButtonIcon,
  StyledWidgetSettings,
  StyledButtonLabel,
  ToggleCardsWrapper,
  ToggleCardsTitle,
  ToggleCardsCopy,
  ToggleCardsSwitch,
  ToggleCardsText
} from '../../../components/default'
import balanceBanner from './assets/balance.png'
import cashbackBanner from './assets/cashback.png'
import recommendationBanner from './assets/recommendation.png'
import HideIcon from './assets/hide-icon'
import { Toggle } from '../../../components/toggle'
import { PlusIcon } from 'brave-ui/components/icons'

import { getLocale } from '../../../../common/locale'

interface Props {
  toggleShowBalance: () => void
  showBalance: boolean
  toggleShowCashback: () => void
  showCashback: boolean
  toggleShowRecommendation: () => void
  showRecommendation: boolean
  toggleCards: (show: boolean) => void
  cardsHidden: boolean
}

class CardsSettings extends React.PureComponent<Props, {}> {

  renderToggleButton = (on: boolean, toggleFunc: any, float: boolean = true) => {
    const ButtonContainer = on ? StyledHideButtonIcon : StyledAddButtonIcon
    const ButtonIcon = on ? HideIcon : PlusIcon

    return (
      <StyledWidgetToggle
        isAdd={!on}
        float={float}
        onClick={toggleFunc}
      >
        <ButtonContainer>
          <ButtonIcon />
        </ButtonContainer>
        <StyledButtonLabel>
          {
            on
            ? getLocale('hideWidget')
            : getLocale('addWidget')
          }
        </StyledButtonLabel>
      </StyledWidgetToggle>
    )
  }

  render () {
    const {
      toggleShowBalance,
      showBalance,
      toggleShowCashback,
      showCashback,
      toggleShowRecommendation,
      showRecommendation,
      toggleCards,
      cardsHidden
    } = this.props
    return (
      <StyledWidgetSettings>
        <SettingsWidget>
          <StyledBannerImage src={balanceBanner} />
          <StyledSettingsInfo>
            <StyledSettingsTitle>
              {getLocale('woeWidgetBalanceTitle')}
            </StyledSettingsTitle>
            <StyledSettingsCopy>
              {getLocale('woeWidgetBalancePref')}
            </StyledSettingsCopy>
          </StyledSettingsInfo>
          {this.renderToggleButton(showBalance, toggleShowBalance, false)}
        </SettingsWidget>
        <SettingsWidget>
          <StyledBannerImage src={cashbackBanner} />
          <StyledSettingsInfo>
            <StyledSettingsTitle>
              {getLocale('woeWidgetCashbackTitle')}
            </StyledSettingsTitle>
            <StyledSettingsCopy>
              {getLocale('woeWidgetCashbackPref')}
            </StyledSettingsCopy>
          </StyledSettingsInfo>
          {this.renderToggleButton(showCashback, toggleShowCashback, false)}
        </SettingsWidget>
        <SettingsWidget>
          <StyledBannerImage src={recommendationBanner} />
          <StyledSettingsInfo>
            <StyledSettingsTitle>
              {getLocale('woeWidgetRecommendationsTitle')}
            </StyledSettingsTitle>
            <StyledSettingsCopy>
              {getLocale('woeWidgetRecommendationPref')}
            </StyledSettingsCopy>
          </StyledSettingsInfo>
          {this.renderToggleButton(showRecommendation, toggleShowRecommendation, false)}
        </SettingsWidget>
        <FeaturedSettingsWidget>
          <ToggleCardsWrapper>
            <ToggleCardsText>
              <ToggleCardsTitle>
                {getLocale('cardsToggleTitle')}
              </ToggleCardsTitle>
              <ToggleCardsCopy>
                {getLocale('cardsToggleDesc')}
              </ToggleCardsCopy>
            </ToggleCardsText>
            <ToggleCardsSwitch>
              <Toggle
                size={'large'}
                onChange={toggleCards.bind(this, cardsHidden)}
                checked={!cardsHidden}
              />
            </ToggleCardsSwitch>
          </ToggleCardsWrapper>
        </FeaturedSettingsWidget>
      </StyledWidgetSettings>
    )
  }
}

export default CardsSettings
