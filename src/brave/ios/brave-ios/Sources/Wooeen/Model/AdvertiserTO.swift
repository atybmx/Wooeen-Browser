import Foundation

public struct AdvertiserTO: Decodable {
    public var id: Int
    public var type: Int?
    public var name: String?
    public var color: String?
    public var logo: String?
    public var url: String?
    public var domain: String?
    public var omniboxTitle: String?
    public var omniboxDescription: String?
    public var checkout: CheckoutTO?
    public var product: CheckoutTO?
    public var query: CheckoutTO?
    
    public init(id:Int){
        self.id = id
    }
}

public struct CheckoutTO: Decodable {
    public var endpoint: String?
    public var data: String?
}

public struct AdvertiserResponse: Decodable{
    public var result: Bool
    public var callback: [AdvertiserTO]?
    public var message: String?
}

public struct AdvertiserTOP{
    public var id: Int?
    public var domain: String?
    public var name: String?
    public var qtdPerPage: Int?
    public var page: Int?
    
    public init(){
        
    }
}
