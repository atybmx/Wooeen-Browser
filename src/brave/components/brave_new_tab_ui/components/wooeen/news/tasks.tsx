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
  woeTasks: Array<Wooeen.Task> | undefined
}

interface State {
  error: string,
  isLoaded: boolean,
  items: Array<Wooeen.Task>
}

type TaskProps = {
  item: Wooeen.Task
}

function TaskCard (props: TaskProps) {
  const { item } = props
  return (
    <Card.Card50>
      <a href={item.url}>
        <Card.BoxMedia>
          <Card.Image
            src={item.media}
          />
        </Card.BoxMedia>
        <Card.BoxInfoVertical>
          <Card.Title>
            {item.title}
          </Card.Title>
          <Card.SubTitle>
            {item.description}
          </Card.SubTitle>
        </Card.BoxInfoVertical>
      </a>
    </Card.Card50>
  )
}

class WooeenTasks extends React.PureComponent<Props, State> {
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
      woeTasks
    } = this.props

    if(woeTasks && woeTasks.length){
      //get by feed
      this.setState({
        isLoaded: true,
        items: woeTasks
      });
    }else{
      //get by api
      new Promise(
        (resolve, reject) => {
          resolve(
            getApiUrl(
              "task/get",
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
                            platformId: o.platformId,
                            title: o.title,
                            description: o.description,
                            url: o.url,
                            payout: o.payout,
                            media: o.media,
                            dateExpiration: o.dateExpiration,
                            timezoneExpiration: o.timezoneExpiration,
                            checkout: {
                              endpoint: o.checkout ? o.checkout.endpoint : undefined,
                              data: o.checkout ? o.checkout.data : undefined
                            }
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
              <TaskCard
                key={`card-id-${item.id}`}
                item={item}
              />
            )
          })}
          <WooeenNewsElement.CtaLinkPanel>
            <WooeenNewsElement.CtaLink href="https://app.wooeen.com/c/tasks">{getLocale('woeShowMore')}</WooeenNewsElement.CtaLink>
          </WooeenNewsElement.CtaLinkPanel>
        </Card.BoxCards>
      );
    }
  }
}

export default WooeenTasks;
