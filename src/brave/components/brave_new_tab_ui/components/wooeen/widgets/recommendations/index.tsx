/* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0. If a copy of the MPL was not distributed with this file,
* You can obtain one at http://mozilla.org/MPL/2.0/. */

import * as React from 'react'
import createWidget from '../../../default/widget/index'
import { getLocale } from '../../../../../common/locale'

import { ThemeProvider, ThemeConsumer } from 'styled-components'
import customizeTheme from './theme'
import {
  WidgetWrapper,
  WidgetLayer,
  BatIcon,
  RewardsTitle,
  AmountItem,
  AmountDescription,
  AmountDescriptionSubTitle,
  Amount,
  AmountValue,
  AmountCents,
  ConvertedAmount,
  WidgetBody,
  Footer,
  BottomButton
} from './style'
import { StyledTitleTab } from '../../../default/widgetTitleTab'
import { WoeIcShare } from '../../styles/icons'

import { getAppUrl } from '../../urls'

export interface RecommendationsProps {
  totalRegistered: number,
  amountRegistered: number,
  amountConfirmed: number,
  showContent: boolean,
  stackPosition: number,
  onShowContent: () => void,
  woeUser: Wooeen.User | null,
  showBalance: boolean,
  showCashback: boolean,
  showRecommendation: boolean
}

function splitMessage (key: string) {
  return getLocale(key).split(/\$\d+/g)
}

function renderText (text: string) {
  const [
    before,
    during,
    after
  ] = splitMessage(text)

  return <>{before}<strong>{during}</strong>{after}</>
}

class Recommendations extends React.PureComponent<RecommendationsProps, {}> {
  renderTotalRegisteredItem = () => {
    const {
      woeUser
    } = this.props

    const total = woeUser && woeUser.id && woeUser.wallet ? woeUser.wallet.recommendationsRegistered + woeUser.wallet.recommendationsConverted + woeUser.wallet.recommendationsConfirmed : 0

    return (
      <AmountItem isActionPrompt={true} isLast={false}>

        <AmountValue data-test-id={`widget-amount-total-confirmed`}>
          <Amount>{woeUser && woeUser.id && woeUser.wallet ? total : ''}</Amount>
        </AmountValue>

        <AmountDescription>
          <AmountDescriptionSubTitle style={{maxWidth:'130px'}}>{renderText('woeWidgetRecommendationsDesc1')}</AmountDescriptionSubTitle>
        </AmountDescription>

      </AmountItem>
    )
  }

  renderRegisteredItem = () => {
    const {
      woeUser
    } = this.props

    const amount = woeUser && woeUser.id && woeUser.wallet ?
                      woeUser.wallet.recommendationsRegisteredAmount ?
                          woeUser.wallet.recommendationsRegisteredAmount :
                          (woeUser.wallet.recommendationsRegistered + woeUser.wallet.recommendationsConverted) * 3 :
                      0
    const currency = woeUser && woeUser.id && woeUser.country && woeUser.country.currency ? woeUser.country.currency.symbol : ''

    return (
      <AmountItem isActionPrompt={true} isLast={true}>

        <AmountValue data-test-id={`widget-amount-total-confirmed`}>
          <ConvertedAmount>{currency}</ConvertedAmount>
          <Amount>{woeUser && woeUser.id && woeUser.wallet ? amount.toFixed(0) : ''}</Amount>
          <AmountCents>,{woeUser && woeUser.id && woeUser.wallet ? amount.toFixed(2).split('.')[1] : ''}</AmountCents>
        </AmountValue>

        <AmountDescription>
          <AmountDescriptionSubTitle>{renderText('woeWidgetRecommendationsDesc2')}</AmountDescriptionSubTitle>
        </AmountDescription>

      </AmountItem>
    )
  }

  renderConfirmedItem = () => {
    const {
      woeUser
    } = this.props

    const amount = woeUser && woeUser.id && woeUser.wallet ? woeUser.wallet.recommendationsConfirmedAmount : 0
    const currency = woeUser && woeUser.id && woeUser.country && woeUser.country.currency ? woeUser.country.currency.symbol : ''

    return (
      <AmountItem isActionPrompt={false} isLast={true}>

        <AmountValue data-test-id={`widget-amount-total-confirmed`}>
          <ConvertedAmount>{currency}</ConvertedAmount>
          <Amount>{woeUser && woeUser.id && woeUser.wallet ? amount.toFixed(0) : ''}</Amount>
          <AmountCents>,{woeUser && woeUser.id && woeUser.wallet ? amount.toFixed(2).split('.')[1] : ''}</AmountCents>
        </AmountValue>

        <AmountDescription>
          <AmountDescriptionSubTitle>{renderText('woeWidgetRecommendationsDesc3')}</AmountDescriptionSubTitle>
        </AmountDescription>

      </AmountItem>
    )
  }

  renderTitle = () => {
    const { showContent } = this.props

    return (
      <RewardsTitle isInTab={!showContent}>
        <BatIcon>
          <WoeIcShare />
        </BatIcon>
        {getLocale('woeWidgetRecommendationsTitle')}
      </RewardsTitle>
    )
  }

  renderTitleTab = () => {
    const { onShowContent, stackPosition, showBalance, showCashback, showRecommendation } = this.props

    return (
      <StyledTitleTab onClick={onShowContent} stackPosition={stackPosition}  first={!showBalance && !showCashback && showRecommendation} last={showRecommendation}>
        {this.renderTitle()}
      </StyledTitleTab>
    )
  }

  report = () => {
    window.open(`${getAppUrl()}/u/recommendations`,"_self")
  }

  render () {
    const {
      showContent
    } = this.props

    if (!showContent) {
      return this.renderTitleTab()
    }

    return (
      <ThemeConsumer>
      {theme =>
        <ThemeProvider theme={customizeTheme(theme)}>
          <WidgetWrapper>
            <WidgetLayer>
              {this.renderTitle()}
              <WidgetBody>
              <div>
                {this.renderTotalRegisteredItem()}
              </div>
              <div>
                {this.renderRegisteredItem()}
              </div>
              <div>
                {this.renderConfirmedItem()}
              </div>
              </WidgetBody>
              <Footer>
                <BottomButton onClick={this.report.bind(null)}>{getLocale('woeWidgetRecommendationsBtn')}</BottomButton>
              </Footer>
            </WidgetLayer>
          </WidgetWrapper>
        </ThemeProvider>
      }
      </ThemeConsumer>
    )
  }
}

export const RecommendationsWidget = createWidget(Recommendations)
