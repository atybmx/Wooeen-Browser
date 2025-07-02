import Foundation
import UIKit

public class FrontUtils{
    
    public static func rgbaToUIColor(red: CGFloat, green:CGFloat, blue:CGFloat, alpha:CGFloat?) -> UIColor{
        return UIColor(red: red/255.0, green: CGFloat(green)/255.0, blue: blue/255.0, alpha: alpha ?? 1)
    }
    
    public static func wooeenPrimary() -> UIColor{
        return rgbaToUIColor(red: 62, green: 64, blue: 230, alpha: 1)
    }
    
    public static func getPoppinsRegular(size: CGFloat) -> UIFont?{
        return UIFont(name: "Poppins-Regular", size: size)
    }
    
    public static func getPoppinsMedium(size: CGFloat) -> UIFont?{
        return UIFont(name: "Poppins-Medium", size: size)
    }
    
    public static func getPoppinsSemiBold(size: CGFloat) -> UIFont?{
        return UIFont(name: "Poppins-SemiBold", size: size)
    }
    
}
