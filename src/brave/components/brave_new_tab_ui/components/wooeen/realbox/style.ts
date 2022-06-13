/* This Source Code Form is subject to the terms of the Mozilla Public
 * License. v. 2.0. If a copy of the MPL was not distributed with this file.
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

import styled from 'styled-components'

export const RealboxContainer = styled('div')<{}>`
  display: flex;
  flex-wrap: wrap;
  align-items: start;
  margin: 0;
  height: 65px;
  position: relative;
  width: 624px;
  max-width: 100%;
`

export const RealboxInput = styled('input')<{}>`
  background-color: var(--default-bg-color);
  border: none;
  border-radius: 33px;
  color: #3e3f41;
  font-family: inherit;
  font-size: inherit;
  height: 100%;
  outline: none;
  padding-inline-end:  44px;
  padding-inline-start: 52px;
  position: relative;
  width: 100%;
  font-size: 17px;
  font-weight: 400;
  background-color: #f2f3f5;

  &::placeholder{
    color: #3e3f41;
    font-size: 17px;
    font-weight: 400;
  }

  @media (prefers-color-scheme: dark) {
    background-color:#333c41;
    color #ffffff;

    &::placeholder{
      color: #ffffff;
    }
  }
`

export const RealboxButton = styled('button')<{}>`
  border: none;
  border-radius: 30px;
  width: 48px;
  height: 48px;
  cursor: pointer;
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  right: 10px;
  box-sizing: border-box;
  display: block;
  z-index: 99999999;
  background: #3e40e6;
  padding: 13px;

  svg{
    fill: #ffffff;
  }

  :hover{
    background: #22236c;
  }
`
