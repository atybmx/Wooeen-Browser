import Foundation

public class UserApi{
    
    var token: UserTokenTO?
    
    public init(token: UserTokenTO?) {
        self.token = token
    }
    
    public func detail(callback: @escaping (Result<UserTO, Error>)->Void){
        guard let token = token else{
            callback(.failure(WooeenApiError.requiredParams))
            return
        }

        var urlBuilder = URLComponents(string: "\(WooeenApi.endpoint)/user/get")
        
        var parameters = [URLQueryItem]()
        
        urlBuilder?.queryItems = parameters
        
        guard let url = urlBuilder?.url else {
            callback(.failure(WooeenApiError.url))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("\(token.idToken)", forHTTPHeaderField: "uti")
        request.setValue("\(token.accessToken)", forHTTPHeaderField: "uta")
        
        let task = URLSession.shared.dataTask(with: request) { (data, response, error) in
            
            if let error = error {
                callback(.failure(error))
                return
            }
            
            guard let data = data else{ return }
            do {
                let resultData = try JSONDecoder().decode(UserResponse.self, from: data)
                if(resultData.result){
                    if let user = resultData.callback{
                        callback(.success(user))
                    }
                }else{
                    print("WOE result error \(error)")
                    callback(.failure(WooeenApiError.authentication))
                }
                
            } catch {
                print("WOE decoder erro \(error)")
                callback(.failure(error))
            }
            return
            
            
        }
        
        task.resume()
    }
    
    public func wallet(callback: @escaping (Result<UserTO, Error>)->Void){
        guard let token = token else{
            callback(.failure(WooeenApiError.requiredParams))
            return
        }

        var urlBuilder = URLComponents(string: "\(WooeenApi.endpoint)/wallet/get")
        
        var parameters = [URLQueryItem]()
        
        urlBuilder?.queryItems = parameters
        
        guard let url = urlBuilder?.url else {
            callback(.failure(WooeenApiError.url))
            return
        }
        
        var request = URLRequest(url: url)
        request.httpMethod = "GET"
        request.setValue("\(token.idToken)", forHTTPHeaderField: "uti")
        request.setValue("\(token.accessToken)", forHTTPHeaderField: "uta")
        
        let task = URLSession.shared.dataTask(with: request) { (data, response, error) in
            
            if let error = error {
                callback(.failure(error))
                return
            }
            
            guard let data = data else{ return }
            do {
                let resultData = try JSONDecoder().decode(UserResponse.self, from: data)
                if(resultData.result){
                    if let user = resultData.callback{
                        callback(.success(user))
                    }
                }else{
                    print("WOE result error \(error)")
                    callback(.failure(WooeenApiError.authentication))
                }
                
            } catch {
                print("WOE decoder erro \(error)")
                callback(.failure(error))
            }
            return
            
            
        }
        
        task.resume()
    }
}
