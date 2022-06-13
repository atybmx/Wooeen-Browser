import { formatDistance } from 'date-fns'
import { enUS, enGB, pt, ptBR } from 'date-fns/locale'
import { getLocale } from '../../../common/locale'

const locales = { enUS, enGB, pt, ptBR };

export function myLocale() {
  let l = getClientLocale() || 'en';
  return l.includes("-") ? l.split("-")[0]+l.split("-")[1].toUpperCase() : l;
}

export function myLg() {
  let l = getClientLocale() || 'en';
  return l.includes("-") ? l.split("-")[0]+"_"+l.split("-")[1].toUpperCase() : l;
}

function getClientLocale() {
  if (typeof Intl !== 'undefined') {
    try {
      return Intl.NumberFormat().resolvedOptions().locale;
    } catch (err) {
      return getClientLocaleByWindow();
    }
  }

  return getClientLocaleByWindow();
}

function getClientLocaleByWindow(){
  if (window.navigator.languages) {
      return window.navigator.languages[0];
  } else {
      return window.navigator.language;
  }
}

export function generateRelativeTimeFormat (publishTime: string) {
  if (!publishTime) {
    return
  }

  const locale = locales[myLocale()];

  return formatDistance(
    new Date(publishTime),
    new Date(),
    {locale: locale}
  ) + ' ' + getLocale('woeAgo')
}


export function toClipboard(text: string | null | undefined){
  if(!text)
    return;

  if (!navigator.clipboard) {
    let textArea = document.createElement("textarea");
    textArea.value = text;
    textArea.style.top = "0";
    textArea.style.left = "0";
    textArea.style.position = "fixed";
    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try{
      document.execCommand('copy');
    }catch(err){
      console.error('Fallback: Oops, unable to copy: ', err);
    }

    document.body.removeChild(textArea);
  }else{
    navigator.clipboard.writeText(text)
        .then(() => {
        })
        .catch(err => {
          console.log('Something went wrong', err);
        })
  }
}

export const appendScript = (scriptToAppend: string) => {
    const script = document.createElement("script");
    script.src = scriptToAppend;
    script.async = true;
    document.body.appendChild(script);
}

export function getMinWithdraw(){
  return 10
}
