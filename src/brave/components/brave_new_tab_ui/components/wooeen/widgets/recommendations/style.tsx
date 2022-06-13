// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.
import * as React from 'react'
import styled, { css } from 'styled-components'
import palette from 'brave-ui/theme/colors'
import Button, { Props as ButtonProps } from 'brave-ui/components/buttonsIndicators/button'
import TOSAndPP, { Props as TOSProps } from '../../../../../brave_rewards/resources/ui/components/TOSAndPP'

interface StyleProps {
  isLast?: boolean
  isActionPrompt?: boolean
  isInTab?: boolean
}

const Base = styled('div')`
  overflow-x: hidden;
  color: white;
  font-family: Montserrat, sans-serif;
  width: 353px;
`

export const WidgetWrapper = styled(Base)`
  position: relative;
  /* Show a 1x1 grid with all items overlapping.
      This makes sure that our layered notifications increase the height of the
      whole widget. Absolute positioning would not accomplish that. */
  display: grid;
  background: #ffffff;
  border-radius: 25px;

  @media (prefers-color-scheme: dark) {
    background: #495154;
  }
`

export const WidgetLayer = styled(Base)`
  grid-row: 1 / 2;
  grid-column: 1 / 2;
`

export const WidgetBody = styled('div')<{}>`
  padding:4px;
  border-radius: 20px 20px 0 0;
  background-image: linear-gradient(0deg, #573d99 0%, #3d3fe5 100%);
  margin: 0 5px;
  padding: 30px;
`

export const Footer = styled('div')<{}>`
  max-width: 100%;
  margin: 0 5px;
`

export const BatIcon = styled('div')<{}>`
  width: 20px;
  height: 20px;
  margin-right: 20px;
`

export const RewardsTitle = styled('div')<StyleProps>`
  display: flex;
  justify-content: flex-start;
  align-items: center;
  font-size: 14px;
  font-weight: 600;
  font-family: Montserrat, sans-serif;
  text-transform: uppercase;
  color: #3e40e6;
  padding: 20px 30px;

  @media (prefers-color-scheme: dark) {
    color:#eef1f3;
  }
`

export const ServiceLink = styled('a')<{}>`
  color: ${p => p.theme.color.brandBrave};
  font-weight: 600;
  text-decoration: none;
`

export const LearnMoreText = styled('div')<{}>`
  font-size: 12px;
  line-height: 18px;
  font-weight: 500;
  font-family: Montserrat, sans-serif;
`

export const Title = styled('span')<{ isGrant?: boolean}>`
  font-size: ${p => p.isGrant ? 16 : 14}px;
  display: block;
  font-family: ${p => p.theme.fontFamily.heading};
  line-height: 1.5;
  font-weight: 500;
`

export const SubTitle = styled('span')<{}>`
  font-size: 14px;
  display: block;
  margin-top: 15px;
  max-width: 250px;
  line-height: 1.4;
`

export const SubTitleLink = styled('a')<{}>`
  color: ${p => p.theme.color.brandBrave};
  text-decoration: none;
  cursor: pointer;
  &:hover {
    text-decoration: underline;
  }
`

export const TurnOnButton = styled('button')<{}>`
  --rewards-widget-button-extra-space: 2px;
  margin: 0 auto;
  border: solid 1px ${palette.grey400};
  border-radius: 20px;
  background: transparent;
  padding: calc(8px + var(--rewards-widget-button-extra-space)) 20px;
  color: ${palette.neutral000};
  font-weight: 700;
  font-size: 16px;
  cursor: pointer;
  word-break: break-word;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  :focus {
    outline: none;
    box-shadow: 0 0 0 1px ${p => p.theme.color.brandBrave};
  }
`

const ActionButton = styled(TurnOnButton)`
  --rewards-widget-button-extra-space: 0px;
  margin: 0 auto;
  border: none;
  background: ${palette.blurple500};
  color: #fff;
  font-weight: 700;
  :hover {
    background: ${palette.blurple600};
  }
`

export const BottomButton = styled('button')`
  width: 100%;
  height:57px;
  text-align: center;
  text-transform: uppercase;
  font-size: 14px;
  color: #ffffff;
  background: #6452b2;
  border: none;
  border-top: 1px solid #ebebeb;
  font-weight: 500;
  border-radius: 0 0 25px 25px;
  cursor: pointer;
  :hover {
    background: #3e40e6;
  }
`

const StyledButtonIcon = styled('svg')`
  margin-right: 6px;
`

interface CoinsButtonProps {
  onClick?: () => void
  className?: string
}

const CoinsButton: React.FunctionComponent<CoinsButtonProps> = ({ onClick, className, children }) => {
  return (
    <ActionButton className={className} onClick={onClick}>
      <StyledButtonIcon xmlns='http://www.w3.org/2000/svg' width='18' height='12'>
        <path
          fill='#FFF'
          fillRule='evenodd'
          d='M12 12a6 6 0 01-1.491-.2c-.483.126-.988.2-1.51.2a5.999 5.999 0 01-1.49-.2c-.483.127-.987.2-1.509.2-3.308 0-6-2.692-6-6s2.692-6 6-6c.521 0 1.025.074 1.508.2.486-.125.984-.2 1.491-.2.522 0 1.025.074 1.508.2.486-.125.985-.2 1.492-.2C15.31 0 18 2.692 18 6s-2.692 6-6 6zM6 1.5A4.505 4.505 0 001.5 6c0 2.481 2.018 4.5 4.5 4.5s4.5-2.019 4.5-4.5S8.483 1.5 6 1.5zm4.136.163A5.976 5.976 0 0112 6a5.976 5.976 0 01-1.865 4.336C12.066 9.83 13.5 8.086 13.5 6c0-2.087-1.435-3.83-3.364-4.337zm2.999 0A5.978 5.978 0 0115 6a5.978 5.978 0 01-1.865 4.337C15.065 9.83 16.5 8.087 16.5 6c0-2.087-1.435-3.83-3.365-4.337zM6 5.25c1.262 0 2.25.823 2.25 1.875 0 .83-.62 1.511-1.5 1.764V9a.75.75 0 11-1.5 0H4.5a.75.75 0 110-1.5H6c.465 0 .75-.243.75-.375S6.465 6.75 6 6.75c-1.262 0-2.25-.824-2.25-1.876 0-.83.62-1.51 1.5-1.763V3a.75.75 0 111.5 0h.75a.75.75 0 110 1.5H6c-.465 0-.75.242-.75.374 0 .133.285.376.75.376z'
        />
      </StyledButtonIcon>
      {children}
    </ActionButton>
  )
}

export const TurnOnAdsButton = styled(Button as React.ComponentType<ButtonProps>)`
  margin: 15px 0;
  display: inline-block;
  font-size: 12px;
  font-weight: 600;
  border: none;
  padding: 8px 17px;
`

export const NotificationButton = styled(CoinsButton)`
  margin-top: 25px;
`

export const AmountItem = styled('div')<StyleProps>`
  text-align: center;
  display: flex;
  align-items: center;
  ${p => p.isActionPrompt && css`
    border-bottom: solid 1px rgba(235,235,235,.3);
    padding-bottom: 10px;
  `}

  ${p => p.isLast && css`
    padding-top: 10px;
  `}
`

export const AmountValue = styled('div')<StyleProps>`
  flex-grow: 1;
  width:50%;
  display: flex;
  justify-content: flex-start;
`

export const Amount = styled('span')<{}>`
  font-size: 47px;
  font-weight:700;
  margin-right: 2px;
  font-family: Montserrat, sans-serif;
  color: #eef1f3;
`

export const AmountCents = styled('span')<{}>`
  font-size: 19px;
  font-weight: 700;
  margin-top: 5px;
  margin-right: 5px;
  color: #eef1f3;
`

export const ConvertedAmount = styled('span')<{}>`
  font-size: 14px;
  font-weight: 500;
  margin-top: 7px;
  margin-right: 5px;
  color: #eef1f3;
`

export const AmountDescription = styled('div')<{}>`
  width:50%;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
`

export const AmountDescriptionTitle = styled('span')<{}>`
  font-size: 18px;
  color: #eef1f3;
  font-weight: 700;
  max-width: 100px;
  text-align: right;
`

export const AmountDescriptionSubTitle = styled('span')<{}>`
  font-size: 14px;
  color: #eef1f3;
  font-weight: 500;
  text-align: right;
  max-width: 100px;
`

export const AmountUSD = styled('span')`
  margin-left: 12px;
`

export const FooterPs = styled('div')<{}>`
  font-size: 11px;
  color: #eef1f3;
  font-weight: 500;
  text-align: center;
  margin-top: 10px;
`

export const NotificationsList = styled('div')`
  grid-row: 1 / 2;
  grid-column: 1 / 2;
  display: grid;
  margin-top: 10px;
`
export const NotificationAction = styled('a')<{}>`
  margin-top: 20px;
  max-width: 250px;
  display: block;
  cursor: pointer;
  font-size: 14px;
  color: ${palette.blurple300};
  font-weight: 700;
  text-decoration: none;
  &:hover {
    text-decoration: underline;
  }
`

export const CloseIcon = styled('div')<{}>`
  color: #fff;
  width: 13px;
  height: 13px;
  float: right;
  cursor: pointer;
  margin-top: 2px;
`

export const UnsupportedMessage = styled('div')<{}>`
  color: rgba(255, 255, 255, 0.70);
  font-size: 14px;
  max-width: 235px;
  margin-top: 8px;
`

export const TurnOnText = styled('div')<{}>`
  font-size: 13px;
  color: ${palette.grey300};
  margin-top: 8px;
`

export const TurnOnTitle = styled('div')<{}>`
  font-size: 15px;
  font-weight: 600;
  color: ${palette.grey300};
  margin-top: 10px;
`

export const StyledTOS = styled(TOSAndPP as React.ComponentType<TOSProps>)`
  font-size: 12px;

  a {
    color: ${p => p.theme.color.brandBrave};
  }
`

export const StyleCenter = styled('div')<{}>`
  text-align: center;
`

export const ArrivingSoon = styled.div`
  background: ${p => p.theme.palette.white};
  border-radius: 6px;
  padding: 0 10px;
  color: ${p => p.theme.palette.neutral900};
  font-size: 14px;
  line-height: 22px;
  margin-bottom: 8px;
`
