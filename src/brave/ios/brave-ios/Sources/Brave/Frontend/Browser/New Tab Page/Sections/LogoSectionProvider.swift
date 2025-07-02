import BraveUI
import CoreData
import Foundation
import Shared
import UIKit
import Wooeen

class LogoButton: SpringButton {
    
    private let topStackView = UIStackView().then {
      $0.axis = .vertical
      //$0.alignment = .center
      $0.distribution = .equalSpacing
      $0.directionalLayoutMargins = .init(.init(top: 0, leading: 0, bottom: 0, trailing: 0))
    }

  override init(frame: CGRect) {
    super.init(frame: frame)
      
    let logoImage = UIImage(sharedNamed: "wooeen_default.logo")!.withRenderingMode(.alwaysOriginal)
    let logoImageView = UIImageView(image: logoImage)
    logoImageView.contentMode = .scaleAspectFit
      
    let logoName = UILabel().then {
        $0.text = Strings.Wooeen.woeSearch
        $0.textColor = .black
        $0.font = UIFontMetrics.default.scaledFont(for: FrontUtils.getPoppinsRegular(size: 15)!)
        $0.textAlignment = .center
    }
      
    topStackView.addStackViewItems(.view(logoImageView), .view(logoName))
      
    addSubview(topStackView)
    
    topStackView.snp.makeConstraints {
      $0.edges.equalToSuperview()
      $0.width.equalTo(640)
    }
    logoImageView.snp.makeConstraints {
      //$0.edges.equalToSuperview()
      $0.height.equalTo(80)
    }
    logoName.snp.makeConstraints {
      //$0.edges.equalToSuperview()
      $0.height.equalTo(30)
    }
    
      
  }
}

class LogoSectionProvider: NSObject, NTPObservableSectionProvider {
  let action: () -> Void
  var sectionDidChange: (() -> Void)?

  private typealias LogoCell = NewTabCenteredCollectionViewCell<
    LogoButton
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
    collectionView.register(LogoCell.self)
  }

  func collectionView(
    _ collectionView: UICollectionView,
    cellForItemAt indexPath: IndexPath
  ) -> UICollectionViewCell {
    let cell = collectionView.dequeueReusableCell(for: indexPath) as LogoCell
    cell.view.addTarget(self, action: #selector(tappedButton), for: .touchUpInside)
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
        return UIEdgeInsets(top: 8, left: 16, bottom: 20, right: 16)
  }
    
    func collectionView(
        _ collectionView: UICollectionView,
        didEndDisplaying cell: UICollectionViewCell,
        forItemAt indexPath: IndexPath) {
    }
    
}

extension LogoSectionProvider: NSFetchedResultsControllerDelegate {
  func controllerDidChangeContent(_ controller: NSFetchedResultsController<NSFetchRequestResult>) {
    DispatchQueue.main.async {
      self.sectionDidChange?()
    }
  }
}
