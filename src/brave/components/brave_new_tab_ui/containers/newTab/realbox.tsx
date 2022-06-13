/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/. */

import * as React from 'react'
import RealboxContainer from '../../components/wooeen/realbox'
import { createWidget } from '../../components/default'

interface Props {
  woeUser: Wooeen.User | null
  woeCountry: Wooeen.Country | undefined
}

class RealBox extends React.Component<Props, {}> {

  render () {
    const {
      woeUser,
      woeCountry
    } = this.props

    return (
      <RealboxContainer woeUser={woeUser} woeCountry={woeCountry}></RealboxContainer>
    )
  }
}

export default createWidget(RealBox)
