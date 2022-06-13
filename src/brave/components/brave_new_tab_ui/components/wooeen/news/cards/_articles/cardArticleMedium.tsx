// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import * as React from 'react'
import * as Card from '../../cardSizes'

import { generateRelativeTimeFormat } from '../../../utils'

interface Props {
  content: (ArticleType)[]
}

type ArticleProps = {
  item: ArticleType
}

interface ArticleType{
  id: string,
  link: string,
  title: string,
  image: string,
  date: string
}

function MediumArticle (props: ArticleProps) {
  const { item } = props
  return (
    <Card.Small>
      <a href={item.link}>
        <Card.ImageFrame>
          <Card.Image
            src={item.image}
          />
        </Card.ImageFrame>
        <Card.Content>
          <Card.Text>
            {item.title}
          <Card.Time>{generateRelativeTimeFormat(item.date)}</Card.Time>
          </Card.Text>
        </Card.Content>
      </a>
    </Card.Small>
  )
}

export default function CardSingleArticleMedium (props: Props) {
  const { content }: Props = props

  // no full content no render®
  if (content.length !== 2) {
    return null
  }

  return (
    <Card.ContainerForTwo>
      {
        content.map((item, index) => {
          return (
            <MediumArticle
              key={`card-id-${item.id}`}
              item={item}
            />
          )
        })
      }
    </Card.ContainerForTwo>
  )
}
