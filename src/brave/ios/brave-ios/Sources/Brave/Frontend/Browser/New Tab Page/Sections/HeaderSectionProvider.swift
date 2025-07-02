import BraveShields
import BraveStrings
import BraveUI
import Foundation
import Preferences
import Shared
import UIKit
import Wooeen

class HeaderSectionProvider: NSObject, NTPSectionProvider {
    
  /// Set of actions that can occur from the Brave News section
  enum Action {
      /// The user interacted with the right actions
      case headerRightAction(HeaderRightAction)
  }
    
  var actionHandler: (Action) -> Void
  
  init(
    actionHandler: @escaping (Action) -> Void
  ) {
     self.actionHandler = actionHandler
  }

  @objc private func tappedButton(_ gestureRecognizer: UIGestureRecognizer) {
      guard let cell = gestureRecognizer.view as? WooeenHeaderView else {
        return
      }

      cell.isHighlighted = true
  }

  func collectionView(
    _ collectionView: UICollectionView,
    numberOfItemsInSection section: Int
  ) -> Int {
    return 1
  }

  func registerCells(to collectionView: UICollectionView) {
    collectionView.register(NewTabCenteredCollectionViewCell<WooeenHeaderView>.self)
  }

  func collectionView(
    _ collectionView: UICollectionView,
    cellForItemAt indexPath: IndexPath
  ) -> UICollectionViewCell {
      let cell =
        collectionView.dequeueReusableCell(for: indexPath)
        as NewTabCenteredCollectionViewCell<WooeenHeaderView>
      cell.view.headerRightActionHandler = { [weak self] action in
        self?.actionHandler(.headerRightAction(action))
      }

      return cell
  }

  func collectionView(
    _ collectionView: UICollectionView,
    layout collectionViewLayout: UICollectionViewLayout,
    sizeForItemAt indexPath: IndexPath
  ) -> CGSize {
    var size = fittingSizeForCollectionView(collectionView, section: indexPath.section)
    size.height = 110
    return size
  }

  func collectionView(
    _ collectionView: UICollectionView,
    layout collectionViewLayout: UICollectionViewLayout,
    insetForSectionAt section: Int
  ) -> UIEdgeInsets {
      var sz = 366
      if UIDevice.current.userInterfaceIdiom == .pad {
          sz = 390
      }
      var mb = (Int(collectionView.bounds.height) - sz) / 2
        
      if mb < 0 {
          mb = 20
      }
      return UIEdgeInsets(top: 8, left: 16, bottom:  CGFloat(mb), right: 16)//margins
  }
}

class WooeenHeaderView: SpringButton {
    
  public var headerRightActionHandler: ((HeaderRightAction) -> Void)?{
      didSet{
            rightView.headerRightActionHandler = headerRightActionHandler
      }
  }

  private lazy var rightView: HeaderRightView = {
    let view = HeaderRightView(frame: CGRect.zero)
    view.headerRightActionHandler = headerRightActionHandler
    return view
  }()

  private let bodyStackView = UIStackView().then {
    $0.distribution = .fillEqually
    $0.spacing = 8
  }
    
  private let contentStackView = UIStackView().then {
    $0.axis = .vertical
    $0.spacing = 8
    $0.isLayoutMarginsRelativeArrangement = true
    $0.directionalLayoutMargins = .init(.init(top: 16, leading: 16, bottom: 16, trailing: 16))
  }

  private let backgroundView = UIView()

  override init(frame: CGRect) {
    super.init(frame: .zero)

    bodyStackView.addStackViewItems(
      .view(rightView)
    )
    contentStackView.addStackViewItems(.view(bodyStackView))
    addSubview(contentStackView)
      
    backgroundView.backgroundColor = .white
    backgroundView.layer.cornerRadius = 20
    backgroundView.layer.cornerCurve = .continuous
    backgroundView.isUserInteractionEnabled = false
    insertSubview(backgroundView, at: 0)
    backgroundView.snp.makeConstraints {
      $0.edges.equalToSuperview()
    }
      
    update()

    contentStackView.snp.makeConstraints {
      $0.edges.equalToSuperview()
      $0.width.equalTo(640)
    }
  }

  required init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  deinit {
    NotificationCenter.default.removeObserver(self)
  }
    
  @objc private func update() {
      if let user = UserDAO.getUser(){
          let country = UserDAO.getCountry()
          rightView.balanceValue = NumberUtils.realToString(
            currency: country?.currency?.id,
            country: country?.id,
            language: country?.language,
            amount: user.wallet?.balance ?? 0)
          rightView.setLogged(logged: true)
      }else{
          rightView.balanceValue = ""
          rightView.setLogged(logged: false)
      }
  }

}

public enum HeaderRightAction {
  case balanceButtonTapped
  case cashbackActiveButtonTapped
}

private class HeaderRightView: UIView {
    
  public var headerRightActionHandler: ((HeaderRightAction) -> Void)?

  var balanceValue: String = "" {
    didSet {
      balanceLabel.text = "\(balanceValue)"
    }
  }
    
    public func setLogged(logged: Bool){
        if logged {
            balanceLabel.isHidden = false
            balanceBtn.isHidden = false
            cashbackActive.isHidden = true
        }else{
            balanceLabel.isHidden = true
            balanceBtn.isHidden = true
            cashbackActive.isHidden = false
        }
    }

  private var balanceBtn = UIButton(type: .system).then {
    $0.setTitle(Strings.Wooeen.woeHeaderBalance.uppercased(), for: .normal)
    $0.titleLabel?.font = UIFontMetrics.default.scaledFont(for: FrontUtils.getPoppinsSemiBold(size: 10)!)
    $0.titleLabel?.textColor = FrontUtils.wooeenPrimary()
    $0.backgroundColor = FrontUtils.rgbaToUIColor(red: 242, green: 243, blue: 245, alpha: 1)
    $0.clipsToBounds = true
    $0.layer.cornerRadius = 10
    $0.contentEdgeInsets = UIEdgeInsets(top: 5, left: 5, bottom: 5, right: 5)
  }

  fileprivate var balanceLabel: UILabel = {
    let label = UILabel()
    label.textColor = .white
    label.textAlignment = .right
    label.numberOfLines = 0
    label.font = UIFontMetrics.default.scaledFont(for: FrontUtils.getPoppinsSemiBold(size: 25)!)
    label.textColor = FrontUtils.wooeenPrimary()
    return label
  }()
    
    public let cashbackActive = ActionButton().then {
      $0.setTitle(Strings.Wooeen.woeCashackActive.uppercased(), for: .normal)
      $0.layer.borderWidth = 0
      $0.titleLabel?.font = UIFontMetrics.default.scaledFont(for: FrontUtils.getPoppinsSemiBold(size: 12)!)
      $0.titleLabel?.textColor = .white
      $0.contentEdgeInsets = UIEdgeInsets(top: 5, left: 15, bottom: 5, right: 15)
      $0.backgroundColor = FrontUtils.rgbaToUIColor(red: 255, green: 32, blue: 0, alpha: 1)
    }

  override init(frame: CGRect) {
    super.init(frame: frame)

    let stackView = UIStackView()
    stackView.axis = .vertical
    stackView.alignment = .trailing

    stackView.addStackViewItems(.view(balanceLabel), .view(balanceBtn), .view(cashbackActive))

    addSubview(stackView)

    stackView.snp.makeConstraints {
      $0.edges.equalToSuperview()
    }
      
    balanceBtn.addTarget(self, action: #selector(tappedBalanceButton), for: .touchUpInside)
    cashbackActive.addTarget(self, action: #selector(tappedCashbackActiveButton), for: .touchUpInside)
  }

  required init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
    
    @objc private func tappedBalanceButton() {
        headerRightActionHandler?(.balanceButtonTapped)
    }

    @objc private func tappedCashbackActiveButton() {
        headerRightActionHandler?(.cashbackActiveButtonTapped)
    }
}
