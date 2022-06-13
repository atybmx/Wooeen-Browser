// Copyright (c) 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import * as React from 'react'

import {
  SettingsRow,
  SettingsText
} from '../../../components/default'
import { Toggle } from '../../../components/toggle'

import { getLocale } from '../../../../common/locale'

interface Props {
  toggleShowRealbox: () => void
  showRealbox: boolean
}

class RealboxSettings extends React.PureComponent<Props, {}> {
  render () {
    const {
      toggleShowRealbox,
      showRealbox
    } = this.props

    return (
      <div>
        <SettingsRow>
          <SettingsText>{getLocale('woeShowRealbox')}</SettingsText>
          <Toggle
            onChange={toggleShowRealbox}
            checked={showRealbox}
            size='large'
          />
        </SettingsRow>
      </div>
    )
  }
}

export default RealboxSettings
