// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import * as React from 'react'
import * as WooeenNewsElement from './default'
import * as Card from './cardSizes'

import { getApiUrl } from '../urls'

import { getLocale } from '../../../../common/locale'

interface Props {
  woeOffers: Array<Wooeen.Offer> | undefined
}

interface State {
  error: string,
  isLoaded: boolean,
  items: Array<Wooeen.Offer>
}

type OfferProps = {
  item: Wooeen.Offer
}

function OfferCard (props: OfferProps) {
  const { item } = props
  return (
    <Card.Card25>
      <a href={item.url}>
        <Card.BoxMedia boxBgColor={item.advertiserColor ? item.advertiserColor : ''}>
          <Card.Image
            src={item.media}
          />
        </Card.BoxMedia>
        <Card.BoxInfoVertical>
          <Card.SubTitle>
            {item.title}
          </Card.SubTitle>
          <Card.Price>
            {item.price ? 'R$ '+item.price.toFixed(2) : ''}
          </Card.Price>
        </Card.BoxInfoVertical>
      </a>
    </Card.Card25>
  )
}

class WooeenOffers extends React.PureComponent<Props, State> {
  constructor(props: Props) {
    super(props)
    this.state = {
      error: "",
      isLoaded: false,
      items: []
    };
  }

  componentDidMount() {
    const {
      woeOffers
    } = this.props

    if(woeOffers && woeOffers.length){
      //get by feed
      this.setState({
        isLoaded: true,
        items: woeOffers
      });
    }else{
      //get by api
      new Promise(
        (resolve, reject) => {
          resolve(
            getApiUrl(
              "offer/get",
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
                            price: o.price
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
              <OfferCard
                key={`card-id-${item.id}`}
                item={item}
              />
            )
          })}
          <WooeenNewsElement.CtaLinkPanel>
            <WooeenNewsElement.CtaLink href="https://app.wooeen.com/c/offers">{getLocale('woeShowMore')}</WooeenNewsElement.CtaLink>
          </WooeenNewsElement.CtaLinkPanel>
        </Card.BoxCards>
      );
    }
  }
}

export default WooeenOffers;
