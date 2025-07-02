import Foundation

public class WooeenApi {
    static var endpoint = "https://api.wooeen.com/api"
    static var appId = "bb63712ac7ac4bc8a6a746b0ace643a0"
    static var appToken = "Gpk8PYyA9oZQ07XHFbsudr"
    
    public static func printJSON(data: Data?) -> String{
        return String(data: data!, encoding: .utf8)!
    }
}

public enum WooeenApiError: Error {
    case url
    case authentication
    case empty
    case requiredParams
}
