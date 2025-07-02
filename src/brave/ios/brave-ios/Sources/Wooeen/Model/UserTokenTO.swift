import Foundation

public struct UserTokenTO {
    public var idToken: String
    public var accessToken: String
    
    public init(idToken:String, accessToken:String){
        self.idToken = idToken
        self.accessToken = accessToken
    }
}
