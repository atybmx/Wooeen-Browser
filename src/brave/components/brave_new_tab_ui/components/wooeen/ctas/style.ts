/* This Source Code Form is subject to the terms of the Mozilla Public
 * License. v. 2.0. If a copy of the MPL was not distributed with this file.
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

import styled from 'styled-components'

export const StyledCashbackCtaDialogContainer = styled('div')<{}>`
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

export const StyledCashbackCtaDialog = styled('div')<{}>`
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

export const StyledCashbackCtaContainer = styled('div')<{}>`
  display: flex;
  flex-wrap: wrap;
  align-items: start;
  margin: 0;
`

export const StyledCashbackCtaItem = styled('div')<{}>`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: space-between;
  padding: 15px;
  width: 220px;
  height: 110px;
  border-top: 1px solid #ffffff30;
  display: flex;
  flex-direction: row;
  align-items: center;

  &:not(last-child){
    margin-right: 0px;
  }

  @media (prefers-color-scheme: dark) {
    filter: none;
  }
`

export const StyledCashbackCtaItemText = styled('span')<{}>`
  color: inherit;
  font-family: ${p => p.theme.fontFamily.heading};
  font-size: 13px;
  color: #ffffff;
  font-weight: 700;
  text-transform: uppercase;
  max-width: 115px;
  line-height: 20px;
`

export const StyledCashbackCtaAnchor = styled('a')`
  cursor: pointer;
  text-decoration: none;
`

export const StyledCashbackCtaItemImage = styled('div')<{}>`
  box-sizing: border-box;
  display: block;
  width: 52px;
  height: 52px;
  border-radius: 18px;
  filter: drop-shadow(0px 5px 8px rgba(62,64,230,0.15)) drop-shadow(0px 2px 0px #f6f6f6);
  background-color: #ffffff;
  padding: 10px;

  svg{
    fill: #3e40e6;
  }
`
