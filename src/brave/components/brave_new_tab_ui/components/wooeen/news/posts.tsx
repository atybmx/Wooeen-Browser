// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import * as React from 'react'
import * as WooeenNewsElement from './default'
import CardLarge from './cards/_articles/cardArticleLarge'
import CardSmall from './cards/_articles/cardArticleMedium'
import CategoryGroup from './cards/categoryGroup'

import { getLocale } from '../../../../common/locale'

interface Props {
  woePosts: Array<Wooeen.Post> | undefined
}

interface State {
  error: string,
  isLoaded: boolean,
  items: Array<Wooeen.Post>
}

enum CardType {
  Headline,
  HeadlinePaired,
  CategoryGroup
}

function getCard (cardType: CardType, headlines: Array<Wooeen.Post>,index: number) {
  switch (cardType) {
    case CardType.Headline:
      return <CardLarge
              content={headlines.slice(index, index + 1)}
              />
    case CardType.HeadlinePaired:
      return <CardSmall
              content={headlines.slice(index, index + 2)}
              />
    case CardType.CategoryGroup:
      return <CategoryGroup
        content={headlines.slice(index, index + 3)}
      />
    }
    console.error('Asked to create unknown card type', cardType)
    return <></>
}

class WooeenPosts extends React.PureComponent<Props, State> {
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
      woePosts
    } = this.props

    if(woePosts && woePosts.length){
      //get by feed
      this.setState({
        isLoaded: true,
        items: woePosts
      });
    }else{
      //get by api
      fetch("https://www.wooeen.com/blog/wp-json/wp/v2/posts?_fields=id,featured_media,fimg_url,title,link,date&per_page=30")
        .then(res => res.json())
        .then(
          (result) => {
            let items = [];
            for (var i = 0; i < result.length; i++){
                let o = result[i];
                items.push({
                  id: o.id,
                  link: o.link,
                  title: o.title.rendered,
                  image: o.fimg_url,
                  date: o.date
                })
            }
            this.setState({
              isLoaded: true,
              items: items
            });
          },
          // Nota: É importante lidar com os erros aqui
          // em vez de um bloco catch() para não recebermos
          // exceções de erros dos componentes.
          (error) => {
            this.setState({
              isLoaded: true,
              error
            });
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
      let gap: number = 0;
      return (
        <>
          {items.map((item,index)=>{
            if(gap == 0){
              let cardType = CardType.Headline;
              let r = Math.floor(Math.random() * 10);
              if(r >= 9 && index + 2 < items.length){
                gap = 2
                cardType = CardType.CategoryGroup
              }else if(r >= 5 && index + 1 < items.length){
                gap = 1
                cardType = CardType.HeadlinePaired
              }

              const cardInstance = getCard(cardType, items, index);
              return (
                <React.Fragment key={`cardf-${index}`}>
                  {cardInstance}
                </React.Fragment>
              )
            }else{
              gap--;

              return (
                <React.Fragment key={`cardf-${index}`}>
                </React.Fragment>)
            }
          })}
          <WooeenNewsElement.CtaLinkPanel>
            <WooeenNewsElement.CtaLink href="https://www.wooeen.com/blog">{getLocale('woeShowMore')}</WooeenNewsElement.CtaLink>
          </WooeenNewsElement.CtaLinkPanel>
        </>
      );
    }
  }
}

export default WooeenPosts;
