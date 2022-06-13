// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import styled from 'styled-components'
import {
  Block as StandardBlock,
  Heading as StandardHeading,
  Text as StandardText,
  Time as StandardTime
} from './default'

export const Large = styled(StandardBlock.withComponent('article'))`
  padding: 0;
`

export const Medium = styled(StandardBlock.withComponent('article'))`
  padding: 0;
`

export const Small = styled(StandardBlock.withComponent('article'))`
  padding: 0;
  width: 100%;
  min-height: 310px;
`

export const DealsCard = styled(StandardBlock)`
`

export const BoxCards = styled('div')<{}>`
  width: 100%;
  max-width: 1000px;
  display: flex;
  flex-wrap: wrap;
`

export const Card50 = styled('div')<{}>`
  box-sizing: border-box;
  position: relative;
  width: 50%;
  border-bottom: 1px solid #ebebeb;
  margin-bottom: 30px;
  a {
    text-decoration: none;
  }

  @media (prefers-color-scheme: dark) {
    border-bottom: 1px solid #495154;
  }
`

export const CouponCard50 = styled(Card50)<{}>`
  border-bottom: none;

  @media (prefers-color-scheme: dark) {
    border-bottom: none;
  }
`

export const Card25 = styled('div')<{}>`
  box-sizing: border-box;
  position: relative;
  width: 25%;
  margin-bottom: 30px;
  a {
    text-decoration: none;
  }
`

export const BoxHorizontal = styled('div')<{}>`
  position: relative;
  display:flex;
  border-radius: 25px;
  filter: drop-shadow(0px 5px 10px #e7e7e7) drop-shadow(0px 2px 0px #f6f6f6);
  background-color: #ffffff;
  margin: 0 15px;

  @media (prefers-color-scheme: dark) {
    filter: drop-shadow(0px 5px 5px #333c41) drop-shadow(0px 2px 0px #0000005A);
  }
`

export const BoxHorizontalCoupon = styled(BoxHorizontal)<{}>`
  align-items: stretch;
  height: 100%;

  > a{
    display: contents;
  }
`

type BoxMediaProps = {
  boxBgColor?: string
}
export const BoxMedia = styled('figure')<BoxMediaProps>`
  border-radius: 25px;
  filter: drop-shadow(0px 5px 5px #f6f6f6) drop-shadow(0px 2px 0px #f6f6f6);
  transition: opacity 240ms ease-out;
  height: auto;
  position: relative;
  display: block;
  overflow: hidden;
  opacity: 1;
  padding-top: 50%;
  margin: 0 25px 20px;

  background: ${p => p.boxBgColor ? p.boxBgColor : 'transparent'};

  @media (prefers-color-scheme: dark) {
    filter: drop-shadow(0px 5px 5px #333c41) drop-shadow(0px 2px 0px #0000005A);
  }
`

export const BoxMediaWithAdv = styled('figure')<BoxMediaProps>`
  border-radius: 25px;
  height: auto;
  position: relative;
  display: block;
  overflow: hidden;
  padding-top: 50%;
  width:100%;
  margin: 0;

  background: ${p => p.boxBgColor ? p.boxBgColor : 'transparent'};
`

export const BoxMediaCoupon = styled('div')<BoxMediaProps>`
  width: 148px;
  background: ${p => p.boxBgColor ? p.boxBgColor : 'transparent'};
  display:flex;
  align-items:center;
  justify-content:center;
  border-radius: 25px 0 0 25px;
`

export const BoxVoucher = styled('div')<{}>`
  padding: 5px 10px;
  display:flex;
  border-radius: 22px;
  background-color: #f2f3f5;
  margin-top:10px;
  align-items: center;
`

export const Voucher = styled('span')<{}>`
  text-transform: uppercase;
  font-weight:700;
  color: #3e40e6;
  font-size: 12px;
  flex-grow: 1;
`

export const BoxInfo = styled('div')<{}>`
  padding: 10px 25px 20px;
  display:flex;
`

export const BoxInfoVertical = styled('div')<{}>`
  padding: 10px 25px 20px;
  display:flex;
  flex-direction: column;
`

export const BoxInfoVerticalCoupon = styled(BoxInfoVertical)<{}>`
  flex-grow: 1;
  width: calc(100% - 148px);
`

export const BoxCloudwatch = styled('div')<{}>`
  display:flex;
  align-items: center;
`

export const CloudwatchIcon = styled('div')<{}>`
  width: 20px;
  height: 20px;
  color: #5052e7;

  svg{
    fill:#5052e7;
  }
`

export const CloudwatchText = styled('span')<{}>`
  font-size: 12px;
  color: #5052e7;
  font-weight: 600;
`

export const CloudwatchTime = styled('span')<{}>`
  font-size: 12px;
  color: #FFFFFF;
  font-weight: 600;
  background: #5052e7;
  padding:5px;
`

export const Title = styled('p')<{}>`
  font-size: 18px;
  line-height: 23px;
  color: #3e40e6;
  font-weight: 700;
  margin: 0;

  @media (prefers-color-scheme: dark) {
    color:#ffffff;
  }
`

export const SubTitle = styled('p')<{}>`
  font-size: 15px;
  line-height: 23px;
  color: #5f5f5f;
  font-weight: 500;
  margin: 0;

  @media (prefers-color-scheme: dark) {
    color:#eef1f3;
  }
`
export const CouponSubTitle = styled(SubTitle)<{}>`
  color: #5f5f5f;

  @media (prefers-color-scheme: dark) {
    color:#5f5f5f;
  }
`

export const Price = styled('span')<{}>`
  font-size: 16px;
  line-height: 20px;
  color: #5052e7;
  font-weight: 700;
  margin: 5px 0 0;

  @media (prefers-color-scheme: dark) {
    color:#ffffff;
  }
`

export const Button = styled('button')<{}>`
  background: transparent;
  border: none;
  border-radius: 30px;
  width: 50px;
  height: 50px;
  color: #5f5f5f;
  cursor: pointer;
  padding: 15px;

  svg{
    fill: #5f5f5f;
  }

  :hover{
    background: #dcdcdc;
  }
`

export const ButtonVoucher = styled(Button)<{}>`
  background: #3e40e6;
  color: #ffffff;
  width: 40px;
  height: 40px;
  padding: 12px;

  svg{
    fill: #ffffff;
  }

  :hover{
    fill: #5f5f5f;
    background: #dcdcdc;
    border-radius: 30px;

    svg{
      fill: #5f5f5f;
    }
  }
`

export const ButtonRules = styled('button')<{}>`
  cursor: pointer;
  padding: 5px;
  border-radius:10px;
  background: transparent;
  color: #3e40e6;
  border: 1px solid #3e40e6;
  width: fit-content;
  margin-bottom:10px;

  svg{
    fill: #3e40e6;
  }

  :hover{
    fill: #ffffff;
    background: #3e40e6;

    svg{
      fill: #ffffff;
    }
  }
`

export const Content = styled('div')<{}>`
  box-sizing: border-box;
  padding: 25px 35px;
  ${Small} & {
    padding: 20px 28px;
  }
`

export const ImageFrame = styled('figure')`
  opacity: 1;
  margin: 0;
  width: 100%;
  position: relative;
  padding-top: 67%;
  display: block;
  height: auto;
  overflow: hidden;
  transition: opacity 240ms ease-out;
`

export const ListImageFrame = styled(ImageFrame)`
  height: 100%;
  padding-top: 0;
`

export const Image = styled('img')`
  box-sizing: border-box;
  display: block;
  position: absolute;
  border: none;
  background: none;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
`

export const Cover = styled('img')`
  max-width: 100%;
`

export const Heading = styled(StandardHeading)`
  font-weight: 600;
  a {
    display: block;
    color: inherit;
    text-decoration: none;
  }
`

export const PublisherHeading = styled(StandardHeading)`
  font-weight: 600;
`

export const Text = styled(StandardText)`
  font-family: ${p => p.theme.fontFamily.heading};
  font-size: 18px;
  line-height: 25px;
  font-weight: 600;

  a {
    display: block;
    color: inherit;
    text-decoration: none;
  }
`

export const Time = styled(StandardTime)`
  ${Large} & {
    margin-top: 8px;
  }
`

export const Source = styled('div')`
  margin: 10px 0 0 0;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  align-items: center;
`

export const Publisher = styled('div')`
  box-sizing: border-box;
  font: 500 14px ${p => p.theme.fontFamily.heading};
  color: #fff;
`

export const PromotedLabel = styled('a')`
  border-radius: 4px;
  background: #495057;
  color: rgba(255, 255, 255, .8);
  padding: 4px 8px;
  font: 400 12px ${p => p.theme.fontFamily.heading};
  display: flex;
  flex-direction: row;
  gap: 5px;
`

export const PromotedIcon = styled('div')`
  width: 16px;
  height: 9px;
  color: inherit;
  svg {
    width: 100%;
    height: 100%;
    color: inherit;
    fill: currentColor;
  }
`

export const ContainerForTwo = styled('div')<{}>`
  width: 680px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-gap: 30px;
`

export const DealsCategory = styled('h3')`
  margin: 0;
  font: 600 18px ${p => p.theme.fontFamily.heading};
  color: white;
`

export const DealsList = styled('div')`
  margin-top: 24px;
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  grid-gap: 30px;
`

export const DealItem = styled('a')`
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: stretch;
  gap: 8px;
`

export const DealDescription = styled(Time)`
  margin: 0;
`


export const StyledCouponRulesDialogContainer = styled('div')<{}>`
  display: none;
  position: fixed;
  z-index: 1;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  overflow: auto;
  background-color: rgb(0,0,0);
  background-color: rgba(0,0,0,0.4);
`

export const StyledCouponRulesDialog = styled('div')<{}>`
  background-color: var(--default-bg-color);
  margin: 15% auto;
  padding: 20px;
  border: 1px solid #888;
  width: 500px;
  max-width:80%;
  animation-name: animatetop;
  animation-duration: 0.4s

  @keyframes animatetop {
    from {top: -300px; opacity: 0}
    to {top: 0; opacity: 1}
  }
`
