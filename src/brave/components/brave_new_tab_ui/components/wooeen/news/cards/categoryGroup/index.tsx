// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import * as React from 'react'
import * as Card from './style'

import { generateRelativeTimeFormat } from '../../../utils'

interface Props {
  content: (ArticleType)[]
  articleToScrollTo?: BraveToday.FeedItem
}

type ListItemProps = {
  item: ArticleType
}

interface ArticleType{
  id: string,
  link: string,
  title: string,
  image: string,
  date: string
}

function ListItem (props: ListItemProps) {
  const { item } = props
  return (
    <Card.ListItem>
      <a href={item.link}>
        <Card.Content>
          <Card.Heading>{item.title}</Card.Heading>
          <Card.Time>{generateRelativeTimeFormat(item.date)}</Card.Time>
        </Card.Content>
        <Card.ListItemImageFrame>
          <Card.Image
            src={item.image}
          />
        </Card.ListItemImageFrame>
      </a>
    </Card.ListItem>
  )
}

export default function CategoryGroup (props: Props) {
  // No content no render®
  if (props.content.length < 3) {
    return null
  }
  return (
    <Card.BrandedList>
      <Card.List>
        {
          props.content.map((item, index) => {
            return <ListItem
              item={item}
              key={`card-id-${item.id}`}
            />
          })
        }
      </Card.List>
    </Card.BrandedList>
  )
}
