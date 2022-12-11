/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

import * as React from 'react'
import { CashbackCtaContainer, CashbackCtaItem } from '../../components/wooeen/ctas'
import { createWidget } from '../../components/default'

import { WoeIcMoney } from '../../components/wooeen/styles/icons'
import { WoeIcTag } from '../../components/wooeen/styles/icons'
import { WoeIcShare } from '../../components/wooeen/styles/icons'
import { WoeIcGames } from '../../components/wooeen/styles/icons'

// Utils
import { getLocale } from '../../../common/locale'

interface Props {
  woeUser: Wooeen.User | null
}

class Ctas extends React.Component<Props, {}> {

  onShareRec = () => {
    let woeModal = document.getElementById("WoeCtaShareModal");
    if(woeModal)
      woeModal.style.display = "block";
  }

  render () {
    const {
      woeUser
    } = this.props

    return (
      <CashbackCtaContainer>
        <CashbackCtaItem destinationUrl={woeUser && woeUser.id ? "https://app.wooeen.com/u/dashboard" : "https://app.wooeen.com/u/login"} title={getLocale('woeNtpCashbackTitle')} text={getLocale('woeNtpCashbackDesc')}><WoeIcMoney/></CashbackCtaItem>
        <CashbackCtaItem destinationUrl={woeUser && woeUser.id ? "https://app.wooeen.com/u/challenges" : "https://app.wooeen.com/u/login"} title={getLocale('woeNtpChallengesTitle')} text={getLocale('woeNtpChallengesDesc')} className="CashbackCtaContainerHiglight"><WoeIcGames/></CashbackCtaItem>
        <CashbackCtaItem destinationUrl="https://app.wooeen.com/c/advertisers" title={getLocale('woeNtpAdvertisersTitle')} text={getLocale('woeNtpAdvertisersDesc')}><WoeIcTag/></CashbackCtaItem>
        {woeUser && woeUser.id &&
        <CashbackCtaItem destinationUrl="#" onClickItem={this.onShareRec} title={getLocale('woeNtpShareTitle')} text={getLocale('woeNtpShareDesc')}><WoeIcShare/></CashbackCtaItem>
        }
      </CashbackCtaContainer>
    )
  }
}

export default createWidget(Ctas)
