import Foundation
import SQLite3

public class WooeenDAO{
    
    static let oficialVersion:Int32 = 1
    static let dbPath: String = "wooeen.sqlite"
    public static let busyTimeout:Int32 = 500
    
    public init(){
    }
    
    public static func initDB(){
        let version = version()
        print("WOE current version: \(version), oficialVersion: \(oficialVersion)")
        if(version < oficialVersion){
            print("WOE upgrade database")
            upgrade(oldVersion: version, newVersion: oficialVersion)
            print("WOE upgrade database done")
        }
    }
    
    public static func getFileURL() -> String{
        let fileURL = try! FileManager.default.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: false)
            .appendingPathComponent(dbPath)
        return fileURL.path
    }
    
    static func version() -> Int32 {
        var version:Int32 = 0
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, 500)
            
            let queryStatementString = "PRAGMA user_version;"
            var queryStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, queryStatementString, -1, &queryStatement, nil) == SQLITE_OK {
                while sqlite3_step(queryStatement) == SQLITE_ROW {
                    version = sqlite3_column_int(queryStatement, 0)
                }
            } else {
                print("WOE BD statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(queryStatement)
            sqlite3_close(db)
        }
        
        return version
    }
    
    static func upgrade(oldVersion: Int32, newVersion: Int32){
        var result: Bool = false
        if(oldVersion <= 0){
            result = createTableAdvertiser()
            if(result){
                result = createTableTracking()
            }
        }
        
        if(result){
            setVersion()
        }
    }
    
    static func setVersion() {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, 500)
            
            let createTableString = "PRAGMA user_version = \(oficialVersion);"
            var createTableStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, createTableString, -1, &createTableStatement, nil) == SQLITE_OK{
                if sqlite3_step(createTableStatement) == SQLITE_DONE{
                    print("WOE version setted")
                }else{
                    print("WOE version could not be setted.")
                }
            }else{
                print("WOE BD statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(createTableStatement)
            sqlite3_close(db)
        }
    }
    
    static func createTableAdvertiser() -> Bool {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, 500)
            
            var result: Bool = false
            let createTableString = """
                CREATE TABLE IF NOT EXISTS advertiser(
                id INTEGER PRIMARY KEY,
                type INTEGER,
                name TEXT NOT NULL,
                color TEXT,
                url TEXT,
                domain TEXT,
                logo TEXT,
                checkout_endpoint TEXT,
                checkout_data TEXT,
                product_endpoint TEXT,
                product_data TEXT,
                query_endpoint TEXT,
                query_data TEXT,
                omnibox_title TEXT,
                omnibox_description TEXT);
                CREATE INDEX IF NOT EXISTS idx_advertiser_name ON advertiser(name);
                CREATE INDEX IF NOT EXISTS idx_advertiser_domain ON advertiser(domain);
                CREATE INDEX IF NOT EXISTS idx_advertiser_checkout ON advertiser(checkout_endpoint);
                """
            var createTableStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, createTableString, -1, &createTableStatement, nil) == SQLITE_OK{
                if sqlite3_step(createTableStatement) == SQLITE_DONE{
                    print("WOE advertiser table created.")
                    result = true
                } else {
                    print("WOE advertiser table could not be created.")
                }
            } else {
                print("WOE advertiser CREATE TABLE statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(createTableStatement)
            sqlite3_close(db)
            
            return result
        }
        
        return false
    }
    
    static func createTableTracking() -> Bool {
        var db: OpaquePointer? = nil
        if sqlite3_open(WooeenDAO.getFileURL(), &db) == SQLITE_OK{
            sqlite3_busy_timeout(db, 500)
            
            var result: Bool = false
            let createTableString = """
                CREATE TABLE IF NOT EXISTS tracking(
                id INTEGER PRIMARY KEY,
                id_platform INTEGER,
                id_advertiser INTEGER,
                advertiser_type INTEGER,
                priority INTEGER,
                deeplink TEXT,
                params TEXT,
                domain TEXT NOT NULL,
                payout DOUBLE,
                commission_type TEXT,
                commission_avg_1 DOUBLE,
                commission_min_1 DOUBLE,
                commission_max_1 DOUBLE,
                commission_avg_2 DOUBLE,
                commission_min_2 DOUBLE,
                commission_max_2 DOUBLE,
                approval_days INTEGER);
                CREATE INDEX IF NOT EXISTS idx_tracking_domain ON tracking(domain);
                """
            var createTableStatement: OpaquePointer? = nil
            if sqlite3_prepare_v2(db, createTableString, -1, &createTableStatement, nil) == SQLITE_OK{
                if sqlite3_step(createTableStatement) == SQLITE_DONE{
                    print("WOE tracking table created.")
                    result = true
                } else {
                    print("WOE tracking table could not be created.")
                }
            } else {
                print("WOE tracking CREATE TABLE statement could not be prepared. \(String(cString: sqlite3_errmsg(db)))")
            }
            sqlite3_finalize(createTableStatement)
            sqlite3_close(db)
            return result
        }
        
        return false
    }
    
}
