import Foundation

public struct WalletTO: Decodable {
     public var conversionsPending:Int?
     public var conversionsRegistered:Int?
     public var conversionsApproved:Int?
     public var conversionsRejected:Int?
     public var conversionsWithdrawn:Int?
     public var amountPending:Double?
     public var amountRegistered:Double?
     public var amountApproved:Double?
     public var amountRejected:Double?
     public var amountWithdrawn:Double?
     public var affConversionsPending:Int?
     public var affConversionsRegistered:Int?
     public var affConversionsApproved:Int?
     public var affConversionsRejected:Int?
     public var affConversionsWithdrawn:Int?
     public var affAmountPending:Double?
     public var affAmountRegistered:Double?
     public var affAmountApproved:Double?
     public var affAmountRejected:Double?
     public var affAmountWithdrawn:Double?
     public var recommendationsRegistered:Int?
     public var recommendationsConverted:Int?
     public var recommendationsConfirmed:Int?
     public var recommendationsRegisteredAmount:Double?
     public var recommendationsConvertedAmount:Double?
     public var recommendationsConfirmedAmount:Double?
     public var balance:Double?
}
