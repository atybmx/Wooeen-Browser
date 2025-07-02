import Foundation
import Security

public class UserDAO{
    
    public static func getCr() -> String?{
        let preferences = UserDefaults.standard
        
        let countryId = preferences.string(forKey: "userCountry")
        if !(countryId ?? "").isEmpty {
            return countryId
        }
        
        let locale = Locale.current
        var countryCode:String? = nil
        if #available(iOS 16, *) {
            if let value = locale.region?.identifier {
                countryCode = value
            }
        } else {
            if let value = locale.regionCode {
                countryCode = value
            }
        }
        return countryCode
    }
    
    public static func getCountry() -> CountryTO?{
        let preferences = UserDefaults.standard
        
        let countryId = preferences.string(forKey: "countryId")
        if (countryId ?? "").isEmpty {
            return nil
        }
        
        var country = CountryTO(id: countryId ?? "")
        
        country.language = preferences.string(forKey: "countryLanguage")
        country.currency = CurrencyTO(id: preferences.string(forKey: "countryCurrency")!)
        country.searchHint = preferences.string(forKey: "countrySearchHint")
        
        return country
    }
    
    public static func saveCountry(country: CountryTOA){
        let preferences = UserDefaults.standard

        preferences.set(country.id, forKey: "countryId")
        preferences.set(country.language, forKey: "countryLanguage")
        preferences.set(country.currency, forKey: "countryCurrency")
        preferences.set(country.searchHint, forKey: "countrySearchHint")
        
        preferences.synchronize()
    }
    
    public static func getUser() -> UserTO?{
        let preferences = UserDefaults.standard
        
        let userId = preferences.integer(forKey: "userId")
        if userId <= 0 {
            return nil
        }
        
        var user = UserTO(id: userId)
        
        user.name = preferences.string(forKey: "userName")
        user.email = preferences.string(forKey: "userEmail")
        user.photoProfile = MediaTO(url: preferences.string(forKey: "userPhoto"))
        user.country = CountryTO(id: preferences.string(forKey: "userCountry")!)
        user.language = preferences.string(forKey: "userLanguage")
        user.timezone = preferences.string(forKey: "userTimezone")
        user.recTerms = preferences.bool(forKey: "userRecTerms")
        user.wallet = WalletTO(balance: preferences.double(forKey: "userWalletBalance"))
        
        return user
    }
    
    public static func removeUser(){
        let preferences = UserDefaults.standard
        
        preferences.removeObject(forKey: "userId")
        preferences.removeObject(forKey: "userName")
        preferences.removeObject(forKey: "userEmail")
        preferences.removeObject(forKey: "userPhoto")
        preferences.removeObject(forKey: "userCountry")
        preferences.removeObject(forKey: "userLanguage")
        preferences.removeObject(forKey: "userTimezone")
        preferences.removeObject(forKey: "userRecTerms")
        preferences.removeObject(forKey: "userWalletBalance")
        
        preferences.synchronize()
    }
    
    public static func saveUser(user: UserTO){
        let preferences = UserDefaults.standard

        preferences.set(user.id, forKey: "userId")
        preferences.set(user.name, forKey: "userName")
        preferences.set(user.email, forKey: "userEmail")
        preferences.set(user.photoProfile?.url, forKey: "userPhoto")
        preferences.set(user.country?.id, forKey: "userCountry")
        preferences.set(user.language, forKey: "userLanguage")
        preferences.set(user.timezone, forKey: "userTimezone")
        preferences.set(user.recTerms, forKey: "userRecTerms")
        preferences.set(user.wallet?.balance, forKey: "userWalletBalance")
        
        preferences.synchronize()
    }
    
    public static func getVersion() -> VersionTO?{
        let preferences = UserDefaults.standard
        
        var version = VersionTO()
        
        version.checkout = preferences.integer(forKey: "versionCheckout")
        version.product = preferences.integer(forKey: "versionProduct")
        version.query = preferences.integer(forKey: "versionQuery")
        version.pub = preferences.integer(forKey: "versionPub")
        version.ios = preferences.string(forKey: "versionIos")
        
        return version
    }
    
    public static func saveVersion(version: VersionTO){
        let preferences = UserDefaults.standard

        preferences.set(version.checkout, forKey: "versionCheckout")
        preferences.set(version.product, forKey: "versionProduct")
        preferences.set(version.query, forKey: "versionQuery")
        preferences.set(version.pub, forKey: "versionPub")
        preferences.set(version.ios, forKey: "versionIos")
        
        preferences.synchronize()
    }
    
    public static func saveUserToken(token: UserTokenTO){
        guard let idToken = keychainGet(key: "com.wooeen.keys.user.id"),
           let accessToken = keychainGet(key: "com.wooeen.keys.user.access") else{
                keychainInsert(key: "com.wooeen.keys.user.id", value: token.idToken)
                keychainInsert(key: "com.wooeen.keys.user.access", value: token.accessToken)
                return
           }
        
        keychainUpdate(key: "com.wooeen.keys.user.id", value: token.idToken)
        keychainUpdate(key: "com.wooeen.keys.user.access", value: token.accessToken)
    }
    
    public static func deleteUserToken(){
        if let idToken = keychainGet(key: "com.wooeen.keys.user.id"),
           let accessToken = keychainGet(key: "com.wooeen.keys.user.access"){
                keychainDelete(key: "com.wooeen.keys.user.id")
                keychainDelete(key: "com.wooeen.keys.user.access")
                return
           }
    }
    
    public static func getUserToken() -> UserTokenTO?{
        guard let idToken = keychainGet(key: "com.wooeen.keys.user.id"),
           let accessToken = keychainGet(key: "com.wooeen.keys.user.access") else{
                return nil
           }
        
        return UserTokenTO(idToken: idToken, accessToken: accessToken)
    }
    
    static func keychainInsert(key: String, value: String){
        let data: [String: Any] = [kSecClass as String: kSecClassGenericPassword,
                                        kSecAttrAccount as String: key,
                                        kSecValueData as String: value.data(using: .utf8) as Any]
        
        if SecItemAdd(data as CFDictionary, nil) == noErr{
            print("WOE User token inserted successfully in the keychain")
        }else{
            print("WOE Something went wrong trying to save the user token in the keychain")
        }
    }
    
    static func keychainUpdate(key: String, value: String){
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
        ]
        
        let attributes: [String: Any] = [kSecValueData as String: value.data(using: .utf8) as Any]
        
        if SecItemUpdate(query as CFDictionary, attributes as CFDictionary) == noErr{
            print("WOE User token updated successfully in the keychain")
        }else{
            print("WOE Something went wrong trying to save the user token in the keychain")
        }
    }
    
    static func keychainDelete(key: String){
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: key,
        ]
        
        if SecItemDelete(query as CFDictionary) == noErr{
            print("WOE User token saved successfully in the keychain")
        }else{
            print("WOE Something went wrong trying to save the user token in the keychain")
        }
    }
    
    static func keychainGet(key: String) -> String?{
        let query: [String: Any] = [kSecClass as String: kSecClassGenericPassword,
                                           kSecAttrAccount as String: key,
                                           kSecMatchLimit as String: kSecMatchLimitOne,
                                           kSecReturnAttributes as String: true,
                                           kSecReturnData as String: true,]
        
        var item: CFTypeRef?
        if SecItemCopyMatching(query as CFDictionary, &item) == noErr {
            if let existingItem = item as? [String: Any],
               let data = existingItem[kSecValueData as String] as? Data{
                return String(data: data, encoding: .utf8)!
            }
        } else {
            print("WOE Something went wrong trying to find the user token in the keychain")
        }
        
        return nil
    }
}
