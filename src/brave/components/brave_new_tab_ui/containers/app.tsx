// Copyright (c) 2019 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this file,
// you can obtain one at http://mozilla.org/MPL/2.0/.

import * as React from 'react'
import { Dispatch } from 'redux'
import { connect } from 'react-redux'

// Components
import NewPrivateTabPage from './privateTab'
import NewTabPage from './newTab'

// Utils
import * as PreferencesAPI from '../api/preferences'
import { getActionsForDispatch } from '../api/getActions'

// Types
import { NewTabActions } from '../constants/new_tab_types'
import { ApplicationState } from '../reducers'

interface Props {
  actions: NewTabActions
  newTabData: NewTab.State
  gridSitesData: NewTab.GridSitesState
}


function DefaultPage (props: Props) {
  const { newTabData, gridSitesData, actions } = props

  // don't render if user prefers an empty page
  if (props.newTabData.showEmptyPage && !props.newTabData.isIncognito) {
    return <div />
  }

  return props.newTabData.isIncognito
    ? <NewPrivateTabPage newTabData={newTabData} actions={actions} />
    : (
      <NewTabPage
        newTabData={newTabData}
        gridSitesData={gridSitesData}
        actions={actions}
        saveShowNews={PreferencesAPI.saveShowNews}
        saveShowCtas={PreferencesAPI.saveShowCtas}
        saveShowRealbox={PreferencesAPI.saveShowRealbox}
        saveShowBalance={PreferencesAPI.saveShowBalance}
        saveShowCashback={PreferencesAPI.saveShowCashback}
        saveShowRecommendation={PreferencesAPI.saveShowRecommendation}
        saveSetAllStackWidgets={PreferencesAPI.saveSetAllStackWidgets}
      />
    )
}

const mapStateToProps = (state: ApplicationState): Partial<Props> => ({
  newTabData: state.newTabData,
  gridSitesData: state.gridSitesData
})

const mapDispatchToProps = (dispatch: Dispatch): Partial<Props> => {
  return {
    actions: getActionsForDispatch(dispatch)
  }
}

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(DefaultPage)
