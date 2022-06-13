/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

import styled from 'styled-components'

interface StyleProps {
  stackPosition: number
  first?: boolean
  last?: boolean
}

export const StyledTitleTab = styled('div')<StyleProps>`
  color: #3e40e6;
  cursor: pointer;
  border-radius: ${p => p.first ? '25px' : '0 0 25px 25px'};
  filter: drop-shadow(0px 5px 5px #f6f6f6) drop-shadow(0px 2px 0px #f6f6f6);
  border-bottom: 1px solid #f6f6f6;
  background: #ffffff;
  width: 353px;
  margin-bottom: ${p => p.last ? '0' : '10px'};

  @media (prefers-color-scheme: dark) {
    filter: drop-shadow(0px 5px 5px #333c41) drop-shadow(0px 2px 0px #0000005A);
    background: #495154;
    border-bottom: 1px solid #495154;
  }
`
