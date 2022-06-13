// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import * as React from 'react'
import * as WooeenNewsElement from './default'
import * as Card from './cardSizes'

import { getApiUrl } from '../urls'
import { toClipboard } from '../utils'

import { WoeIcCopy } from '../styles/icons'
import { WoeIcArrowRightLong } from '../styles/icons'
import { WoeIcStopwatch } from '../styles/icons'

import { getLocale } from '../../../../common/locale'

interface Props {
  woeCoupons: Array<Wooeen.Coupon> | undefined
}

interface State {
  error: string,
  isLoaded: boolean,
  items: Array<Wooeen.Coupon>
}

type CouponProps = {
  item: Wooeen.Coupon
}

type CloudwatchProps = {
  dateExpiration: string
}
type CloudwatchState = {
  started: boolean
  stopwatch?: string
}

export class Stopwatch extends React.PureComponent<CloudwatchProps, CloudwatchState> {

  constructor(props: CloudwatchProps) {
    super(props)
    this.state = {
      started: false
    };
  }

  componentDidMount() {
      let countDownDate = new Date(this.props.dateExpiration).getTime();
      let now = new Date().getTime();
      let distance = countDownDate - now;
      let days = Math.floor(distance / (1000 * 60 * 60 * 24));
      const that = this;

      if(days <= 0){
        setInterval(function() {
          let now = new Date().getTime();
          let distance = countDownDate - now;

          if(distance > 0){
        	  let hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        	  let minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        	  let seconds = Math.floor((distance % (1000 * 60)) / 1000);

        	  let stopwatch = (hours > 0 ? hours + getLocale('woeHoursSymbol')+": " : "") +
                              (minutes > 0 ? minutes + getLocale('woeMinutesSymbol') + ": " : "") +
                              seconds + getLocale('woeSecondsSymbol');
            that.setState({
              started: true,
              stopwatch: stopwatch
            });
          }
      	}, 1000);
      }
  }

  render(){
      const { started, stopwatch } = this.state

      if(started)
        return (
            <Card.BoxCloudwatch>
                <Card.CloudwatchIcon><WoeIcStopwatch/></Card.CloudwatchIcon>
                <Card.CloudwatchText>{getLocale("woeExpireIn")}</Card.CloudwatchText>
                <Card.CloudwatchTime>{stopwatch}</Card.CloudwatchTime>
            </Card.BoxCloudwatch>)
      else return (<></>)
    }
}

export class CouponCard extends React.PureComponent<CouponProps, {}> {
  onRules = (item:string | undefined) => {
    let woeCouponRulesModalText = document.getElementById("WoeCouponRulesModalText");
    if(item && woeCouponRulesModalText)
      woeCouponRulesModalText.innerText = item;
    let woeModal = document.getElementById("WoeCouponRulesModal");
    if(woeModal)
      woeModal.style.display = "block";
  }

  onCopy = (voucher:string | undefined) => {
    toClipboard(voucher);

    let woeContentCopied = document.getElementById("woeContentCopied");
    if(woeContentCopied){
      woeContentCopied.style.display = "block";
      setTimeout(function(){
        let woeContentCopied = document.getElementById("woeContentCopied");
        if(woeContentCopied)
          woeContentCopied.style.display = "none";
      }, 3000)
    }
  }

  onLink = (url:string | undefined) => {
    window.open(url,"_self")
  }

  render(){
    const { item } = this.props

    return (
      <Card.CouponCard50>
          <Card.BoxHorizontalCoupon>
            <a href={item.url}>
              <Card.BoxMediaCoupon boxBgColor={item.advertiserColor ? item.advertiserColor : ''}>
                  <Card.BoxMediaWithAdv>
                    <Card.Image
                      src={item.media}
                    />
                  </Card.BoxMediaWithAdv>
              </Card.BoxMediaCoupon>
            </a>
            <Card.BoxInfoVerticalCoupon>
              {item.description &&
                <Card.ButtonRules onClick={() => {this.onRules(item.description)}}>{getLocale("woeSeeRules")}</Card.ButtonRules>
              }
              <Card.CouponSubTitle>
                {item.title}
              </Card.CouponSubTitle>
              {item.voucher &&
                <Card.BoxVoucher>
                  <Card.Voucher>{item.voucher}</Card.Voucher>
                  <Card.ButtonVoucher onClick={() => {this.onCopy(item.voucher)}}><WoeIcCopy/></Card.ButtonVoucher>
                </Card.BoxVoucher>
              }
              {!item.voucher && item.url &&
              <Card.BoxVoucher>
                <Card.Voucher>{getLocale('woeActiveCoupon')}</Card.Voucher>
                <Card.ButtonVoucher onClick={() => {this.onLink(item.url)}}><WoeIcArrowRightLong/></Card.ButtonVoucher>
              </Card.BoxVoucher>
              }
              {item.dateExpiration &&
                <Stopwatch dateExpiration={item.dateExpiration}/>
              }
            </Card.BoxInfoVerticalCoupon>
          </Card.BoxHorizontalCoupon>
      </Card.CouponCard50>
    )
  }
}

class WooeenCoupons extends React.PureComponent<Props, State> {
  ctaShareDialogRef: React.RefObject<any>

  constructor(props: Props) {
    super(props)
    this.state = {
      error: "",
      isLoaded: false,
      items: []
    };
    this.ctaShareDialogRef = React.createRef()
  }

  onClose = () => {
    let woeModal = document.getElementById("WoeCouponRulesModal");
    if(woeModal)
      woeModal.style.display = "none";
  }

  componentDidMount() {
    const {
      woeCoupons
    } = this.props

    document.addEventListener('mousedown', this.handleClickOutside)
    document.addEventListener('keydown', this.onKeyPressSettings)

    if(woeCoupons && woeCoupons.length){
      //get by feed
      this.setState({
        isLoaded: true,
        items: woeCoupons
      });
    }else{
      //get by api
      new Promise(
        (resolve, reject) => {
          resolve(
            getApiUrl(
              "coupon/get",
              new URLSearchParams({
                  st:'1',
                  pg: '0',
                  qpp: '150'
                }))
              )
          }
      ).then((urlApi: string) => {
              fetch(urlApi)
                .then(res => res.json())
                .then(
                  (result) => {
                    let items = [];
                    if(result.result && result.callback && result.callback.length > 0){
                      for (var i = 0; i < result.callback.length; i++){
                          let o = result.callback[i];
                          items.push({
                            id: o.id,
                            advertiserId: o.advertiserId,
                            advertiserName: o.advertiserName,
                            advertiserColor: o.advertiserColor,
                            title: o.title,
                            description: o.description,
                            url: o.url,
                            media: o.media,
                            voucher: o.voucher,
                            discountType: o.discountType,
                            discount: o.discount,
                            dateExpiration: o.dateExpiration,
                            timezoneExpiration: o.timezoneExpiration,
                          })
                      }
                    }
                    this.setState({
                      isLoaded: true,
                      items: items
                    });
                  },
                  (error) => {
                    this.setState({
                      isLoaded: true,
                      error
                    });
                  }
                )
            }
        )

      }
  }

  componentWillUnmount () {
    document.removeEventListener('mousedown', this.handleClickOutside)
    document.removeEventListener('keydown', this.onKeyPressSettings)
  }

  onKeyPressSettings = (event: KeyboardEvent) => {
    if (event.key === 'Escape') {
      this.onClose()
    }
  }

  handleClickOutside = (event: Event) => {
    if (
      this.ctaShareDialogRef &&
      this.ctaShareDialogRef.current &&
      !this.ctaShareDialogRef.current.contains(event.target)
    ) {
      this.onClose()
    }
  }

  render() {
    const { error, isLoaded, items } = this.state;

    if (error) {
      return (<WooeenNewsElement.ArticlesGroup>
                <WooeenNewsElement.Text>
                  <WooeenNewsElement.Heading></WooeenNewsElement.Heading>
                </WooeenNewsElement.Text>
              </WooeenNewsElement.ArticlesGroup>);
    } else if (!isLoaded) {
      return (<WooeenNewsElement.ArticlesGroup>
                <WooeenNewsElement.Text>
                  <div className={`loader`}></div>
                </WooeenNewsElement.Text>
              </WooeenNewsElement.ArticlesGroup>);
    } else {
      return (
        <Card.BoxCards>
          {items.map((item,index)=>{
            return (
              <CouponCard
                key={`card-id-${item.id}`}
                item={item}
              />
            )
          })}
          <WooeenNewsElement.CtaLinkPanel>
            <WooeenNewsElement.CtaLink href="https://app.wooeen.com/c/coupons">{getLocale('woeShowMore')}</WooeenNewsElement.CtaLink>
          </WooeenNewsElement.CtaLinkPanel>
          <Card.StyledCouponRulesDialogContainer id="WoeCouponRulesModal">
            <Card.StyledCouponRulesDialog>
              <div className="WoeModalContentHeader">
                <span onClick={this.onClose} className="WoeModalClose">&times;</span>
                <h2>{getLocale('woeSeeRules')}</h2>
              </div>
              <div className="WoeModalContentBody">
                <p className="big" id="WoeCouponRulesModalText"></p>
              </div>
            </Card.StyledCouponRulesDialog>
          </Card.StyledCouponRulesDialogContainer>
        </Card.BoxCards>
      );
    }
  }
}

export default WooeenCoupons;
