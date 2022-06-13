import IBraveTheme from 'brave-ui/theme/theme-interface'

export default function customizeTheme (theme: IBraveTheme): IBraveTheme {
  return {
    ...theme,
    color: {
      ...theme.color,
      primaryBackground: '#3e40e6',
      secondaryBackground: '#573d99'
    }
  }
}
