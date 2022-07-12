import * as React from 'react'

import {
  RealboxContainer,
  RealboxInput,
  RealboxButton,
  Realboxwoesearch,
  Realboxwoesearchbrand
} from './style'

import { WoeIcSearch } from '../styles/icons'
import { myLg } from '../utils'

// Utils
import { getLocale } from '../../../../common/locale'

interface RealboxProps {
  woeUser: Wooeen.User | null
  woeCountry: Wooeen.Country | undefined
}

export default class Realbox extends React.PureComponent<RealboxProps, {}> {
  constructor(props: RealboxProps) {
    super(props)
  }

  componentDidMount () {
    const {
      woeUser,
      woeCountry
    } = this.props

    window["realbox"](woeUser && woeUser && woeUser.country && woeUser.country.id ? woeUser.country.id : woeCountry && woeCountry.id ? woeCountry.id : 'BR', myLg());
  }

  render(){
    const {
      woeUser,
      woeCountry
    } = this.props

    return (
      <form method="GET" action="https://search.wooeen.com/s/search">
        <RealboxContainer id="realbox">
          <input type="hidden" name="cr" value={woeUser && woeUser && woeUser.country && woeUser.country.id ? woeUser.country.id : woeCountry && woeCountry.id ? woeCountry.id : 'BR'}/>
          <input type="hidden" name="lg" value={myLg()}/>
          <RealboxInput name="q" type="text" autoComplete="off" id="realbox-input" placeholder={getLocale('woeRealboxInputPlaceholder')}/>
          <RealboxButton type="submit"><WoeIcSearch /></RealboxButton>
        </RealboxContainer>
        <Realboxwoesearch><Realboxwoesearchbrand>Wooeen Search Engine</Realboxwoesearchbrand></Realboxwoesearch>
      </form>
    )
  }
}
