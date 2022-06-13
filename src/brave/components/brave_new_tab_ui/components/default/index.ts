/* This Source Code Form is subject to the terms of the Mozilla Public
 * License,
 * v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

export * from './settings'
import { AddSiteTile, AddSiteTileImage, Tile, TileActionsContainer, TileAction, TileFavicon, TileMenu, TileMenuItem, TileTitle } from './gridSites'
import { SiteRemovalNotification, SiteRemovalText, SiteRemovalAction } from './notification'
import { BalanceWidget } from '../wooeen/widgets/balance'
import { CashbackWidget } from '../wooeen/widgets/cashback'
import { RecommendationsWidget } from '../wooeen/widgets/recommendations'
import { RewardsWidget } from './rewards'
import { BinanceWidget } from './binance'
import { BraveTalkWidget } from './braveTalk'
import { GeminiWidget } from './gemini'
import { CryptoDotComWidget } from './cryptoDotCom'
import EditCards from './editCards'
import EditTopSite from './editTopSite'
import createWidget from './widget'

export * from './page'

export {
  BalanceWidget,
  CashbackWidget,
  RecommendationsWidget,
  AddSiteTile,
  AddSiteTileImage,
  Tile,
  TileActionsContainer,
  TileAction,
  TileFavicon,
  TileMenu,
  TileMenuItem,
  TileTitle,
  SiteRemovalNotification,
  SiteRemovalText,
  SiteRemovalAction,
  RewardsWidget,
  BinanceWidget,
  BraveTalkWidget,
  EditCards,
  GeminiWidget,
  CryptoDotComWidget,
  createWidget,
  EditTopSite
}
