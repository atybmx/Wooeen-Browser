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
import { WoeIcMoney } from '../../styles/icons'

import { getAppUrl } from '../../urls'

export interface CashbackProps {
  amountPending: number,
  amountRegistered: number,
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

class Cashback extends React.PureComponent<CashbackProps, {}> {

  renderValidationItem = () => {
    const {
      woeUser
    } = this.props

    const amount = woeUser && woeUser.id && woeUser.wallet ? woeUser.wallet.amountPending + woeUser.wallet.amountRegistered : 0
    const currency = woeUser && woeUser.id && woeUser.country && woeUser.country.currency ? woeUser.country.currency.symbol : ''

    return (
      <AmountItem isActionPrompt={true} isLast={false}>

        <AmountValue data-test-id={`widget-amount-total-validation`}>
          <ConvertedAmount>{currency}</ConvertedAmount>
          <Amount>{woeUser && woeUser.id && woeUser.wallet ? amount.toFixed(0) : ''}</Amount>
          <AmountCents>,{woeUser && woeUser.id && woeUser.wallet ? amount.toFixed(2).split('.')[1] : ''}</AmountCents>
        </AmountValue>

        <AmountDescription>
          <AmountDescriptionSubTitle>{renderText('woeWidgetCashbackDesc1')}</AmountDescriptionSubTitle>
        </AmountDescription>

      </AmountItem>
    )
  }

  renderApprovedItem = () => {
    const {
      woeUser
    } = this.props

    const amount = woeUser && woeUser.id && woeUser.wallet ? woeUser.wallet.amountApproved : 0
    const currency = woeUser && woeUser.id && woeUser.country && woeUser.country.currency ? woeUser.country.currency.symbol : ''

    return (
      <AmountItem isActionPrompt={false} isLast={true}>

        <AmountValue data-test-id={`widget-amount-total-validation`}>
          <ConvertedAmount>{currency}</ConvertedAmount>
          <Amount>{woeUser && woeUser.id && woeUser.wallet ? amount.toFixed(0) : ''}</Amount>
          <AmountCents>,{woeUser && woeUser.id && woeUser.wallet ? amount.toFixed(2).split('.')[1] : ''}</AmountCents>
        </AmountValue>

        <AmountDescription>
          <AmountDescriptionSubTitle>{renderText('woeWidgetCashbackDesc2')}</AmountDescriptionSubTitle>
        </AmountDescription>

      </AmountItem>
    )
  }

  renderTitle = () => {
    const { showContent } = this.props

    return (
      <RewardsTitle isInTab={!showContent}>
        <BatIcon>
          <WoeIcMoney />
        </BatIcon>
        {getLocale('woeWidgetCashbackTitle')}
      </RewardsTitle>
    )
  }

  renderTitleTab = () => {
    const { onShowContent, stackPosition, showBalance, showCashback, showRecommendation } = this.props

    return (
      <StyledTitleTab onClick={onShowContent} stackPosition={stackPosition} first={!showBalance && showCashback} last={showCashback && !showRecommendation}>
        {this.renderTitle()}
      </StyledTitleTab>
    )
  }

  report = () => {
    window.open(`${getAppUrl()}/u/conversions`,"_self")
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
                {this.renderValidationItem()}
              </div>
              <div>
                {this.renderApprovedItem()}
              </div>
              </WidgetBody>
              <Footer>
                <BottomButton onClick={this.report.bind(null)}>{getLocale('woeWidgetCashbackBtn')}</BottomButton>
              </Footer>
            </WidgetLayer>
          </WidgetWrapper>
        </ThemeProvider>
      }
      </ThemeConsumer>
    )
  }
}

export const CashbackWidget = createWidget(Cashback)
