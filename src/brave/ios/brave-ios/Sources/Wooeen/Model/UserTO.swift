import Foundation

public struct UserTO: Decodable {
    public var id: Int
    public var name: String?
    public var photoProfile: MediaTO?
    public var category: CategoryTO?
    public var email: String?
    public var emailMd5: String?
    public var pass: String?
    public var document: String?
    public var phone: String?
    public var timezone: String?
    public var country: CountryTO?
    public var language: String?
    public var recTerms: Bool?
    public var recTermsIp: String?
    public var recTermsSocial: Bool?
    public var recTermsSocialIp: String?
    public var wallet: WalletTO?
    public var company: CompanyTO?
    
    public init(id: Int){
        self.id = id
    }
}

public struct CompanyTO: Decodable {
    public var id: Int
    public var name: String?
    public var photoProfile: MediaTO?
    public var category: CategoryTO?
    public var email: String?
    public var emailMd5: String?
    public var pass: String?
    public var document: String?
    public var phone: String?
    public var timezone: String?
    public var country: CountryTO?
    public var language: String?
    public var recTerms: Bool?
    public var recTermsIp: String?
    public var recTermsSocial: Bool?
    public var recTermsSocialIp: String?
    public var wallet: WalletTO?
}

public struct UserResponse: Decodable{
    public var result: Bool
    public var callback: UserTO?
    public var message: String?
}
