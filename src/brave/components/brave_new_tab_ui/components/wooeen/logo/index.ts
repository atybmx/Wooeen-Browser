/* This Source Code Form is subject to the terms of the Mozilla Public
 * License. v. 2.0. If a copy of the MPL was not distributed with this file.
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

import styled from 'styled-components'

import LogoWooeenImage from './wooeen_logo_dark.png'
import LogoWooeenImageDark from './wooeen_logo_dark.png'

const isDarkTheme = (p: any) => {
  return p.theme.name === 'Brave Dark'
}

export const LogoLink = styled('a')<{}>`
  text-decoration: none;
  grid-column: 1;
  align-self: center;
`

const BaseImage = styled('img')<{}>`
  box-sizing: border-box;
  display: block;
  padding: 50px;
`

export const WooeenImage = styled(BaseImage).attrs({})`
  width: 200px;
  content: url(${p => isDarkTheme(p) ? LogoWooeenImageDark : LogoWooeenImage})
`
