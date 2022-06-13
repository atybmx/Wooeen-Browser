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
  AmountDescriptionTitle,
  AmountDescriptionSubTitle,
  Amount,
  AmountValue,
  AmountCents,
  ConvertedAmount,
  WidgetBody,
  Footer,
  FooterPs
} from './style'
import {
  ActionButton
} from '../../../../widgets/shared/styles'
import { StyledTitleTab } from '../../../default/widgetTitleTab'
import { WoeIcWallet } from '../../styles/icons'
import { getAppUrl } from '../../urls'
import { getMinWithdraw } from '../../utils'

export interface BalanceProps {
  balance: number,
  showContent: boolean,
  stackPosition: number,
  onShowContent: () => void,
  woeUser: Wooeen.User | null,
  showBalance: boolean,
  showCashback: boolean,
  showRecommendation: boolean
}

class Balance extends React.PureComponent<BalanceProps, {}> {
  renderAmountItem = () => {
    const {
      woeUser
    } = this.props

    const amount:number = woeUser && woeUser.id && woeUser.wallet && woeUser.wallet.balance ? woeUser.wallet.balance : 0
    const currency = woeUser && woeUser.id && woeUser.country && woeUser.country.currency ? woeUser.country.currency.symbol : ''

    return (
      <AmountItem isActionPrompt={false} isLast={false}>

        <AmountDescription>
          <AmountDescriptionTitle>{getLocale('woeWidgetBalanceDesc')}</AmountDescriptionTitle>
          <AmountDescriptionSubTitle>{getLocale('woeWidgetBalanceSubtitle')}</AmountDescriptionSubTitle>
        </AmountDescription>

        <AmountValue data-test-id={`widget-amount-total-ads`}>
          <ConvertedAmount>{currency}</ConvertedAmount>
          <Amount>{woeUser && woeUser.id && woeUser.wallet ? amount.toFixed(0) : '0'}</Amount>
          <AmountCents>,{woeUser && woeUser.id && woeUser.wallet ? amount.toFixed(2).split('.')[1] : '00'}</AmountCents>
        </AmountValue>

      </AmountItem>
    )
  }

  renderTitle = () => {
    const { showContent } = this.props

    return (
      <RewardsTitle isInTab={!showContent}>
        <BatIcon>
          <WoeIcWallet />
        </BatIcon>
        {getLocale('woeWidgetBalanceTitle')}
      </RewardsTitle>
    )
  }

  renderTitleTab = () => {
    const { onShowContent, stackPosition, showBalance, showCashback, showRecommendation } = this.props

    return (
      <StyledTitleTab onClick={onShowContent} stackPosition={stackPosition} first={showBalance} last={showBalance && !showCashback && !showRecommendation}>
        {this.renderTitle()}
      </StyledTitleTab>
    )
  }

  withdraw = () => {
    window.open(`${getAppUrl}/u/withdraw/new`,"_self")
  }

  render () {
    const {
      showContent,
      woeUser
    } = this.props

    if (!showContent) {
      return this.renderTitleTab()
    }

    let hasWithdraw = woeUser && woeUser.id && woeUser.wallet && woeUser.wallet.balance && woeUser.wallet.balance >= getMinWithdraw()
    const currency = woeUser && woeUser.id && woeUser.country && woeUser.country.currency ? woeUser.country.currency.symbol : ''

    return (
      <ThemeConsumer>
      {theme =>
        <ThemeProvider theme={customizeTheme(theme)}>
          <WidgetWrapper>
            <WidgetLayer>
              {this.renderTitle()}
              <WidgetBody>
                {this.renderAmountItem()}
                <Footer>
                  <ActionButton
                      isSelected={true}
                      disabled={hasWithdraw == false}
                      onClick={this.withdraw.bind(null)}>{hasWithdraw ? getLocale('woeWidgetBalanceBtn') : getLocale('woeWidgetBalanceBtnDisabled',{'AMOUNT': currency+' '+getMinWithdraw().toFixed(0)})}</ActionButton>
                </Footer>
                <FooterPs>{getLocale('woeWidgetBalancePs')}</FooterPs>
              </WidgetBody>
            </WidgetLayer>
          </WidgetWrapper>
        </ThemeProvider>
      }
      </ThemeConsumer>
    )
  }
}

export const BalanceWidget = createWidget(Balance)
