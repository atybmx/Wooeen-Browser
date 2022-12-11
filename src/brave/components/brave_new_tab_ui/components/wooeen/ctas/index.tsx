/* This Source Code Form is subject to the terms of the Mozilla Public
 * License. v. 2.0. If a copy of the MPL was not distributed with this file.
 * You can obtain one at http://mozilla.org/MPL/2.0/. */
import * as React from 'react'

import {
  StyledCashbackCtaDialogContainer,
  StyledCashbackCtaDialog,
  StyledCashbackCtaContainer,
  StyledCashbackCtaItem,
  StyledCashbackCtaItemInfo,
  StyledCashbackCtaItemTitle,
  StyledCashbackCtaItemText,
  StyledCashbackCtaItemImage,
  StyledCashbackCtaAnchor
} from './style'
import { WoeIcCopy } from '../styles/icons'
import { toClipboard } from '../utils'

// Utils
import { getLocale } from '../../../../common/locale'

export interface CashbackCtaProps {
  testId?: string
  children?: React.ReactNode
}

/**
 * Styled container block around stat items used in new tab page
 * @prop {React.ReactNode} children - the child elements
 */
export class CashbackCtaContainer extends React.PureComponent<CashbackCtaProps, {}> {
  render () {
    const { children } = this.props
    return (
      <StyledCashbackCtaContainer>{children}</StyledCashbackCtaContainer>
    )
  }
}

export interface CashbackCtaItemProps {
  title: string
  text: string
  className?: string
  destinationUrl?: string
  onClickItem?: () => void
  children?: React.ReactNode
}

/**
 * Individual stat block used in new tab page
 * @prop {string} link - the link go to
 * @prop {string} text - descriptive text that goes along the stat
 * @prop {string} image - image what the cta is showing
 */
export class CashbackCtaItem extends React.PureComponent<CashbackCtaItemProps, {}> {
  render () {
    const { title, text, className, destinationUrl, onClickItem, children } = this.props

    return (
      <StyledCashbackCtaAnchor href={destinationUrl} onClick={onClickItem}>
        <StyledCashbackCtaItem className={`CashbackCtaContainer ${className}`}>
          <StyledCashbackCtaItemImage>{children}</StyledCashbackCtaItemImage>
          <StyledCashbackCtaItemInfo>
            <StyledCashbackCtaItemTitle>{title}</StyledCashbackCtaItemTitle>
            <StyledCashbackCtaItemText>{text}</StyledCashbackCtaItemText>
          </StyledCashbackCtaItemInfo>
        </StyledCashbackCtaItem>
      </StyledCashbackCtaAnchor>
    )
  }
}

interface DialogProps {
  woeUser: Wooeen.User | null
}

export class CtaShareDialog extends React.PureComponent<DialogProps, {}> {

  ctaShareDialogRef: React.RefObject<any>

  constructor(props: DialogProps) {
    super(props)
    this.ctaShareDialogRef = React.createRef()
  }

  onClose = () => {
    let woeModal = document.getElementById("WoeCtaShareModal");
    if(woeModal)
      woeModal.style.display = "none";
  }

  componentDidMount () {
    document.addEventListener('mousedown', this.handleClickOutside)
    document.addEventListener('keydown', this.onKeyPressSettings)
  }

  componentWillUnmount () {
    document.removeEventListener('mousedown', this.handleClickOutside)
    document.removeEventListener('keydown', this.onKeyPressSettings)
  }

  onKeyPressSettings = (event: KeyboardEvent) => {
    if (event.key === 'Escape') {
      this.onClose()
    }
  }

  handleClickOutside = (event: Event) => {
    if (
      this.ctaShareDialogRef &&
      this.ctaShareDialogRef.current &&
      !this.ctaShareDialogRef.current.contains(event.target)
    ) {
      this.onClose()
    }
  }

  onCopy = () => {
    let woeShareLink = document.getElementById("WoeCtaShareModalLink");
    if(woeShareLink)
      toClipboard(woeShareLink.innerText || woeShareLink.textContent)

    let woeActionTextHide = document.getElementById("woeCtaShareModalCopied");
    if(woeActionTextHide){
      woeActionTextHide.style.display = "block";

      setTimeout(function() {
        if(woeActionTextHide)
          woeActionTextHide.style.display = "none";
      }, 2000);
    }
  }

  render(){
    const {
      woeUser
    } = this.props

    return (
      <StyledCashbackCtaDialogContainer id="WoeCtaShareModal">
        <StyledCashbackCtaDialog ref={this.ctaShareDialogRef}>
          <div className="WoeModalContentHeader">
            <span onClick={this.onClose} className="WoeModalClose">&times;</span>
            <h2>{getLocale('woeCashbackCtaShare')}</h2>
          </div>
          <div className="WoeModalContentBody">
            <p className="big" id="WoeCtaShareModalLink">https://app.wooeen.com/u/rec/{woeUser && woeUser.id ? woeUser.id : ''}</p>
          </div>
          <div className="WoeModalContentFooter">
            <span className="WoeActionsTextHide" id="woeCtaShareModalCopied">{getLocale('woeCashbackCtaShareCopied')}</span>
            <span onClick={this.onCopy} className="WoeActionsIc" title={getLocale('woeCashbackCtaShareCopy')}><WoeIcCopy/></span>
          </div>
        </StyledCashbackCtaDialog>
      </StyledCashbackCtaDialogContainer>
    )
  }
}
