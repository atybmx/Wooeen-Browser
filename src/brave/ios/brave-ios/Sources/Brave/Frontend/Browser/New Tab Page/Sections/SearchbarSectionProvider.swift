import BraveUI
import CoreData
import Foundation
import Shared
import UIKit
import Wooeen

class SearchbarButton: SpringButton {
    var searchCount: Timer?
    var hint: UILabel
    
    private let backgroundView = UIVisualEffectView(effect: UIBlurEffect(style: .light)).then {
    $0.clipsToBounds = true
    $0.isUserInteractionEnabled = false
    $0.backgroundColor = UIColor.white
  }
    
    private let topStackView = UIStackView().then {
      $0.distribution = .equalSpacing
      $0.alignment = .center
      $0.isLayoutMarginsRelativeArrangement = true
      $0.directionalLayoutMargins = .init(.init(top: 0, leading: 0, bottom: 0, trailing: 0))
    }

  override init(frame: CGRect) {
    hint = UILabel().then {
        $0.text = Strings.Wooeen.woeSearchHint
        $0.textColor = FrontUtils.rgbaToUIColor(red: 62, green: 63, blue: 65, alpha: 1)
        $0.font = UIFontMetrics.default.scaledFont(for: FrontUtils.getPoppinsRegular(size: 13)!)
    }
      
    super.init(frame: frame)
      
    let searchButton = BraveButton(type: .system).then {
        $0.setImage(
          UIImage(sharedNamed: "woe_search")!.template,
          for: .normal
        )
        $0.tintColor = .white
        $0.backgroundColor = FrontUtils.wooeenPrimary()
        $0.clipsToBounds = true
        $0.layer.cornerRadius = 20
        
        var configuration = UIButton.Configuration.plain()
        configuration.contentInsets = NSDirectionalEdgeInsets(top: 12, leading: 20, bottom: 12, trailing: 20)
        $0.configuration = configuration
        
        $0.hitTestSlop = UIEdgeInsets(equalInset: -25)
    }

    backgroundView.layer.cornerCurve = .continuous

    addSubview(backgroundView)
    backgroundView.contentView.addSubview(topStackView)
      
    topStackView.addStackViewItems(.view(hint), .view(searchButton))
      
    backgroundView.snp.makeConstraints {
      $0.edges.equalToSuperview()
        $0.width.equalTo(640)
    }
    topStackView.snp.makeConstraints {
      $0.edges.equalToSuperview().inset(UIEdgeInsets(top: 7, left: 15, bottom: 7, right: 10))
    }
      
    initCounter()
      
      NotificationCenter.default.addObserver(
        self,
        selector: #selector(appMovedToActiveInit),
        name: UIApplication.willEnterForegroundNotification,
        object: nil
      )
      NotificationCenter.default.addObserver(
        self,
        selector: #selector(appMovedToBackgroundInit),
        name: UIApplication.didEnterBackgroundNotification,
        object: nil)
      
  }
    
    deinit{
        NotificationCenter.default.removeObserver(
          self,
          name: UIApplication.willEnterForegroundNotification,
          object: nil
        )
        NotificationCenter.default.removeObserver(
          self,
          name: UIApplication.didEnterBackgroundNotification,
          object: nil)
        
        finishCounter()
    }
    
    func initCounter(){
        
        guard let search = searchCount, search.isValid else{
            var searchHint:String? = Strings.Wooeen.woeSearchWords
            
            if let country = UserDAO.getCountry(),
            !(country.searchHint ?? "").isEmpty{
                searchHint = country.searchHint ?? ""
            }
            
            if !(searchHint ?? "").isEmpty{
                words = searchHint?.components(separatedBy: ",")
                
                if !(words?.isEmpty ?? true){
                    searchCount = Timer.scheduledTimer(withTimeInterval: searchTimeRate, repeats: true, block: {timer in
                        self.hint.text = self.getWord()
                    })
                }
            }
            
            return
        }
    }
    
    func finishCounter(){
        searchCount?.invalidate()
    }
    
    @objc func appMovedToActiveInit(){
        initCounter()
    }
    
    @objc func appMovedToBackgroundInit(){
        finishCounter()
    }
    
    let searchTimeRate = 0.2
    var words:[String]?
    var wordsIndex:Int = -1
    var word:String?
    var wordIndex:Int = -1
    
    func getWord() -> String{
        wordIndex+=1
        if(word == nil || wordIndex == 0 || wordIndex >= word?.count ?? 0) {
            if(word == nil){
                wordIndex = 0
            }else if(wordIndex >= word!.count){
                wordIndex = -2
            }

            if(wordIndex >= 0) {
                wordsIndex+=1;
                if (wordsIndex >= words?.count ?? 0){
                    wordsIndex = 0
                }
                word = words?[wordsIndex]
            }
        }
        
        return String(word?.prefix(wordIndex < 0 ? word?.count ?? 0 : wordIndex + 1) ?? "")
    }
    
    override func display(_ layer: CALayer) {
        print("")
    }

  override func layoutSubviews() {
    super.layoutSubviews()
    backgroundView.layer.cornerRadius = bounds.height / 2.0  // Pill shape
  }
}

class SearchbarSectionProvider: NSObject, NTPObservableSectionProvider {
  let action: () -> Void
  var sectionDidChange: (() -> Void)?

  private typealias SearchbarCell = NewTabCenteredCollectionViewCell<
    SearchbarButton
  >

  init(action: @escaping () -> Void) {
    self.action = action
    super.init()
  }
    
  @objc private func tappedButton() {
    action()
  }

  func collectionView(
    _ collectionView: UICollectionView,
    numberOfItemsInSection section: Int
  ) -> Int {
    return 1
  }

  func registerCells(to collectionView: UICollectionView) {
    collectionView.register(SearchbarCell.self)
  }

  func collectionView(
    _ collectionView: UICollectionView,
    cellForItemAt indexPath: IndexPath
  ) -> UICollectionViewCell {
    let cell = collectionView.dequeueReusableCell(for: indexPath) as SearchbarCell
    cell.view.addTarget(self, action: #selector(tappedButton), for: .touchUpInside)
    return cell
  }

  func collectionView(
    _ collectionView: UICollectionView,
    layout collectionViewLayout: UICollectionViewLayout,
    sizeForItemAt indexPath: IndexPath
  ) -> CGSize {
    var size = fittingSizeForCollectionView(collectionView, section: indexPath.section)
    size.height = 24
    return size
  }

  func collectionView(
    _ collectionView: UICollectionView,
    layout collectionViewLayout: UICollectionViewLayout,
    insetForSectionAt section: Int
  ) -> UIEdgeInsets {
        return UIEdgeInsets(top: 8, left: 16, bottom: 8, right: 16)
  }
    
    func collectionView(
        _ collectionView: UICollectionView,
        didEndDisplaying cell: UICollectionViewCell,
        forItemAt indexPath: IndexPath) {
    }
    
}

extension SearchbarSectionProvider: NSFetchedResultsControllerDelegate {
  func controllerDidChangeContent(_ controller: NSFetchedResultsController<NSFetchRequestResult>) {
    DispatchQueue.main.async {
      self.sectionDidChange?()
    }
  }
}
