import Foundation

public class NumberUtils{
    
    public static func realToString(currency: String?, country:String?, language: String?, amount: Double) -> String{
        if let currency = currency, let country = country, var language = language,
           !currency.isEmpty, !country.isEmpty, !language.isEmpty{
            if language.count > 2{
                language = String(language.prefix(2))
            }
            
            let formatter = NumberFormatter()
            formatter.locale = Locale(identifier: "\(language.lowercased())_\(country.uppercased())@currency=\(currency.uppercased())")
            formatter.numberStyle = .currency
            
            if let formattedTipAmount = formatter.string(from: amount as NSNumber) {
                return formattedTipAmount
            }
        }
        
        
        return String(format: "\(currency ?? "")%.02f", amount)
    }
    
}
