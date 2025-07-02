import Foundation

public class WooeenSync{
    
    public init() {}
    
    public static func syncBD(){
        syncUser()
        syncCountry()
        syncVersion()
        if let cr = UserDAO.getCr(){
            syncAdvertiser(country: cr, page: 0, lastSynced: 0)
            syncTracking(country: cr, page: 0, lastSynced: 0)
        }
    }
    
    public static func syncUser(){
        if let token = UserDAO.getUserToken(){
            let apiDAO = UserApi(token: token)
            apiDAO.wallet(callback: {result in
                switch result {
                case .success(let user):
                    if(user.id > 0){
                        UserDAO.saveUser(user: user)
                    }
                case .failure(let error):
                    print(error.localizedDescription)
                }
            })
        }
    }
    
    public static func syncCountry(){
        if let cr = UserDAO.getCr(){
            let apiDAO = CountryApi()
            apiDAO.detail(id: cr, callback: {result in
                switch result {
                case .success(let country):
                    if country.id != "" {
                        UserDAO.saveCountry(country: country)
                    }
                case .failure(let error):
                    print(error.localizedDescription)
                }
            })
        }
    }
    
    public static func syncVersion(){
        let apiDAO = VersionApi()
        apiDAO.search(callback: {result in
            switch result {
                case .success(let version):
                    if let checkout = version.checkout{
                        if checkout > 0{
                            UserDAO.saveVersion(version: version)
                        }
                    }
                case .failure(let error):
                    print(error.localizedDescription)
            }
        })
    }
    
    public static func syncAdvertiser(country: String, page: Int, lastSynced: Int){
        print("WOE Getting advertisers \(page)")
        
        let qtdPerPage: Int = 40
        var page = page
        var lastSynced = lastSynced
        
        let apiDAO = AdvertiserApi(country: country)
        apiDAO.search(pg: page, qtdPerPage: qtdPerPage, callback: {result in
            switch result {
                case .success(let advertisers):
                    let advertiserDAO = AdvertiserDAO()
                    if(!advertisers.isEmpty){
                        //ORDER 0..n
                        let advertisersSorted = advertisers.sorted(by: { $0.id < $1.id })
                        
                        //SEND TO DAO delete before first
                        if(page == 0){
                            let first = advertisersSorted[0].id
                            if(first > 1){
                                advertiserDAO.deleteTo(id: first)
                            }
                        }
                        
                        //sync removeds
                        var last = advertisersSorted[0].id
                        advertisersSorted.forEach {advertiser in
                            //SEND TO DAO insert
                            advertiserDAO.insert(advertiser: advertiser)
                            
                            if(advertiser.id > last){
                                //SEND TO DAO delete empty range
                                advertiserDAO.deleteByRange(from: last, to: advertiser.id - 1)
                            }
                            last = advertiser.id + 1
                        }
                    
                        //set the last synced
                        lastSynced = last - 1
                    
                        //get more
                        if(advertisers.count == qtdPerPage){
                            page += 1
                            syncAdvertiser(country: country, page: page, lastSynced: lastSynced)
                        }else{
                            //SEND TO DAO delete from lastSynced
                            advertiserDAO.deleteFrom(id: lastSynced)
                        }
                    }else{
                        // SEND TO DAO delete from lastSynced
                        advertiserDAO.deleteFrom(id: lastSynced)
                    }
                    
                
                case .failure(let error):
                    print(error.localizedDescription)
            }
        })
    }
    
    public static func syncTracking(country: String, page: Int, lastSynced: Int){
        print("WOE Getting trackings \(page)")
        
        let qtdPerPage: Int = 40
        var page = page
        var lastSynced = lastSynced
        
        let apiDAO = TrackingApi(country: country)
        apiDAO.search(pg: page, qtdPerPage: qtdPerPage, callback: {result in
            switch result {
                case .success(let trackings):
                    let trackingDAO = TrackingDAO()
                    if(!trackings.isEmpty){
                        //ORDER 0..n
                        let trackingsSorted = trackings.sorted(by: { $0.id < $1.id })
                        
                        //SEND TO DAO delete before first
                        if(page == 0){
                            let first = trackingsSorted[0].id
                            if(first > 1){
                                trackingDAO.deleteTo(id: first)
                            }
                        }
                        
                        //sync removeds
                        var last = trackingsSorted[0].id
                        trackingsSorted.forEach {tracking in
                            //SEND TO DAO insert
                            trackingDAO.insert(tracking: tracking)
                            
                            if(tracking.id > last){
                                //SEND TO DAO delete empty range
                                trackingDAO.deleteByRange(from: last, to: tracking.id - 1)
                            }
                            last = tracking.id + 1
                        }
                    
                        //set the last synced
                        lastSynced = last - 1
                    
                        //get more
                        if(trackings.count == qtdPerPage){
                            page += 1
                            syncTracking(country: country, page: page, lastSynced: lastSynced)
                        }else{
                            //SEND TO DAO delete from lastSynced
                            trackingDAO.deleteFrom(id: lastSynced)
                        }
                    }else{
                        // SEND TO DAO delete from lastSynced
                        trackingDAO.deleteFrom(id: lastSynced)
                    }
                    
                
                case .failure(let error):
                    print(error.localizedDescription)
            }
        })
    }
}
