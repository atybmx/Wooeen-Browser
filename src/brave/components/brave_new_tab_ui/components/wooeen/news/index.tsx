// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import * as React from 'react'
import * as WooeenNewsElement from './default'

import { WoeIcPost } from '../styles/icons'
import { WoeIcOffer } from '../styles/icons'
import { WoeIcCoupon } from '../styles/icons'
import { WoeIcTask } from '../styles/icons'

import WooeenPosts from './posts'
import WooeenTasks from './tasks'
import WooeenOffers from './offers'
import WooeenCoupons from './coupons'

// Utils
import { getLocale } from '../../../../common/locale'

export interface Props {
  woePosts: Array<Wooeen.Post> | undefined,
  woeTasks: Array<Wooeen.Task> | undefined,
  woeOffers: Array<Wooeen.Offer> | undefined,
  woeCoupons: Array<Wooeen.Coupon> | undefined
}

interface State {
  newsType: string
}


export default class Balance extends React.PureComponent<Props, State> {

  constructor(props: Props) {
    super(props)
    this.state = {
      newsType: 'posts'
    };
  }

  loadPosts = () => {
    this.setState({
      newsType: 'posts'
    });
  }

  loadOffers = () => {
    this.setState({
      newsType: 'offers'
    });
  }

  loadCoupons = () => {
    this.setState({
      newsType: 'coupons'
    });
  }

  loadTasks = () => {
    this.setState({
      newsType: 'tasks'
    });
  }

  render(){
    const {
      woePosts,
      woeTasks,
      woeOffers,
      woeCoupons
    } = this.props
    const { newsType } = this.state;

    return (
    <WooeenNewsElement.Section>
      <span id="woeContentCopied">{getLocale('woeContentShareCopied')}</span>
      <WooeenNewsElement.NavMenuCont>
        <WooeenNewsElement.NavMenu>
          <WooeenNewsElement.NavMenuItem isSelected={newsType == 'posts' ? true : false} onClick={this.loadPosts}>
            <WooeenNewsElement.NavMenuItemName>{getLocale('woeNewsPosts')}</WooeenNewsElement.NavMenuItemName>
            <WooeenNewsElement.NavMenuItemIcon><WoeIcPost/></WooeenNewsElement.NavMenuItemIcon>
          </WooeenNewsElement.NavMenuItem>
          <WooeenNewsElement.NavMenuItem isSelected={newsType == 'offers' ? true : false} onClick={this.loadOffers}>
            <WooeenNewsElement.NavMenuItemName>{getLocale('woeNewsOffers')}</WooeenNewsElement.NavMenuItemName>
            <WooeenNewsElement.NavMenuItemIcon><WoeIcOffer/></WooeenNewsElement.NavMenuItemIcon>
          </WooeenNewsElement.NavMenuItem>
          <WooeenNewsElement.NavMenuItem isSelected={newsType == 'coupons' ? true : false} onClick={this.loadCoupons}>
            <WooeenNewsElement.NavMenuItemName>{getLocale('woeNewsCoupons')}</WooeenNewsElement.NavMenuItemName>
            <WooeenNewsElement.NavMenuItemIcon><WoeIcCoupon/></WooeenNewsElement.NavMenuItemIcon>
          </WooeenNewsElement.NavMenuItem>
          <WooeenNewsElement.NavMenuItem isSelected={newsType == 'tasks' ? true : false} onClick={this.loadTasks}>
            <WooeenNewsElement.NavMenuItemName>{getLocale('woeNewsTasks')}</WooeenNewsElement.NavMenuItemName>
            <WooeenNewsElement.NavMenuItemIcon><WoeIcTask/></WooeenNewsElement.NavMenuItemIcon>
          </WooeenNewsElement.NavMenuItem>
        </WooeenNewsElement.NavMenu>
      </WooeenNewsElement.NavMenuCont>
      {newsType == 'posts' &&
        <WooeenPosts woePosts={woePosts}></WooeenPosts>
      }
      {newsType == 'tasks' &&
        <WooeenTasks woeTasks={woeTasks}></WooeenTasks>
      }
      {newsType == 'offers' &&
        <WooeenOffers woeOffers={woeOffers}></WooeenOffers>
      }
      {newsType == 'coupons' &&
        <WooeenCoupons woeCoupons={woeCoupons}></WooeenCoupons>
      }
    </WooeenNewsElement.Section>
    )
  }
}
