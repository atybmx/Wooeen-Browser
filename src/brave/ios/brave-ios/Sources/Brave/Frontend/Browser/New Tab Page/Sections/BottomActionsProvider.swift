// Copyright 2020 The Brave Authors. All rights reserved.
// This Source Code Form is subject to the terms of the Mozilla Public
// License, v. 2.0. If a copy of the MPL was not distributed with this
// file, You can obtain one at https://mozilla.org/MPL/2.0/.

import BraveShields
import BraveStrings
import BraveUI
import Foundation
import Preferences
import Shared
import Wooeen
import UIKit

class BottomActionsProvider: NSObject, NTPSectionProvider {
  
  var openCouponsPressed: () -> Void
  var openDiscountsPressed: () -> Void
  var openEarnPressed: () -> Void
  var openAdvertisersPressed: () -> Void
  var openMyDataPressed: () -> Void
  var openSupportPressed: () -> Void

  init(
    openCouponsPressed: @escaping () -> Void,
    openDiscountsPressed: @escaping () -> Void,
    openEarnPressed: @escaping () -> Void,
    openAdvertisersPressed: @escaping () -> Void,
    openMyDataPressed: @escaping () -> Void,
    openSupportPressed: @escaping () -> Void
  ) {
    self.openCouponsPressed = openCouponsPressed
    self.openDiscountsPressed = openDiscountsPressed
    self.openEarnPressed = openEarnPressed
    self.openAdvertisersPressed = openAdvertisersPressed
    self.openMyDataPressed = openMyDataPressed
    self.openSupportPressed = openSupportPressed
  }

  @objc private func tappedButton(_ gestureRecognizer: UIGestureRecognizer) {
    guard let cell = gestureRecognizer.view as? BottomActionsView else {
      return
    }

    cell.isHighlighted = true

    Task.delayed(bySeconds: 0.1) { @MainActor in
      cell.isHighlighted = false
      self.openCouponsPressed()
    }
  }

  func collectionView(
    _ collectionView: UICollectionView,
    numberOfItemsInSection section: Int
  ) -> Int {
    return 1
  }

  func registerCells(to collectionView: UICollectionView) {
    collectionView.register(NewTabCenteredCollectionViewCell<BottomActionsView>.self)
  }

  func collectionView(
    _ collectionView: UICollectionView,
    cellForItemAt indexPath: IndexPath
  ) -> UICollectionViewCell {
    let cell =
      collectionView.dequeueReusableCell(for: indexPath)
      as NewTabCenteredCollectionViewCell<BottomActionsView>

    let tap = UITapGestureRecognizer(target: self, action: #selector(tappedButton(_:)))

    cell.view.do {
      $0.addGestureRecognizer(tap)

      $0.openCouponsPressed = { [weak self] in
        self?.openCouponsPressed()
      }

      $0.openDiscountsPressed = { [weak self] in
        self?.openDiscountsPressed()
      }
        
      $0.openEarnPressed = { [weak self] in
        self?.openEarnPressed()
      }
        
      $0.openAdvertisersPressed = { [weak self] in
        self?.openAdvertisersPressed()
      }
        
      $0.openMyDataPressed = { [weak self] in
        self?.openMyDataPressed()
      }
        
      $0.openSupportPressed = { [weak self] in
        self?.openSupportPressed()
      }
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
    
    if UIDevice.current.userInterfaceIdiom == .pad {
        size.height = 150
    }
      
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
    var mt = (Int(collectionView.bounds.height) - sz) / 2
      
    if mt < 0 {
        mt = 20
    }
    return UIEdgeInsets(top: CGFloat(mt), left: 0, bottom: 0, right: 0)//margins
  }
}

class BottomActionsView: SpringButton {
  var openCouponsPressed: (() -> Void)?
  var openDiscountsPressed: (() -> Void)?
  var openEarnPressed: (() -> Void)?
  var openAdvertisersPressed: (() -> Void)?
  var openMyDataPressed: (() -> Void)?
  var openSupportPressed: (() -> Void)?

  private lazy var couponsView: StatView = {
    let statView = StatView(frame: CGRect.zero)
    statView.title = Strings.Wooeen.woeHomeActionCoupons.capitalized
    statView.color = FrontUtils.rgbaToUIColor(red: 62, green: 63, blue: 65, alpha: 1)
    statView.image = "shoppingmode"
    return statView
  }()

  private lazy var discountsView: StatView = {
    let statView = StatView(frame: .zero)
    statView.title = Strings.Wooeen.woeHomeActionDiscounts.capitalized
    statView.color = FrontUtils.rgbaToUIColor(red: 62, green: 63, blue: 65, alpha: 1)
      statView.image = "local_mall"
    return statView
  }()

  private lazy var earnView: StatView = {
    let statView = StatView(frame: .zero)
    statView.title = Strings.Wooeen.woeHomeActionEarn.capitalized
    statView.color = FrontUtils.rgbaToUIColor(red: 62, green: 63, blue: 65, alpha: 1)
      statView.image = "attach_money"
    return statView
  }()
    
  private lazy var advertisersView: StatView = {
    let statView = StatView(frame: .zero)
    statView.title = Strings.Wooeen.woeHomeActionAdvertisers.capitalized
    statView.color = FrontUtils.rgbaToUIColor(red: 62, green: 63, blue: 65, alpha: 1)
      statView.image = "store"
    return statView
  }()
    
    private lazy var myDataView: StatView = {
      let statView = StatView(frame: .zero)
      statView.title = Strings.Wooeen.woeHomeActionMyData.capitalized
      statView.color = FrontUtils.rgbaToUIColor(red: 62, green: 63, blue: 65, alpha: 1)
      statView.image = "person"
      return statView
    }()
    
    private lazy var supportView: StatView = {
      let statView = StatView(frame: .zero)
      statView.title = Strings.Wooeen.woeHomeActionSupport.capitalized
      statView.color = FrontUtils.rgbaToUIColor(red: 62, green: 63, blue: 65, alpha: 1)
      statView.image = "support_agent"
      return statView
    }()

  private let statsStackView = UIStackView().then {
    $0.distribution = .fillEqually
    $0.spacing = 8
  }

  private let contentStackView = UIStackView().then {
    $0.axis = .vertical
    $0.spacing = 8
    $0.isLayoutMarginsRelativeArrangement = true
    $0.directionalLayoutMargins = .init(.init(top: 8, leading: 8, bottom: 8, trailing: 8))
    if UIDevice.current.userInterfaceIdiom == .pad {
        $0.directionalLayoutMargins = .init(.init(top: 20, leading: 8, bottom: 20, trailing: 8))
    }
    $0.backgroundColor = FrontUtils.rgbaToUIColor(red: 255, green: 255, blue: 255, alpha: 1)
  }

  private let backgroundView = UIView()

  override init(frame: CGRect) {
    super.init(frame: .zero)

    statsStackView.addStackViewItems(
      .view(discountsView),
      .view(couponsView),
      .view(advertisersView),
      .view(supportView)
    )
    contentStackView.addStackViewItems(.view(statsStackView))
    addSubview(contentStackView)

    update()

    contentStackView.snp.makeConstraints {
      $0.edges.equalToSuperview()
      $0.width.equalTo(640)
    }
    
    discountsView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(tappedOpenDiscountsPressed)))
    advertisersView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(tappedOpenAdvertisersPressed)))
    couponsView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(tappedOpenCouponsPressed)))
    supportView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(tappedOpenSupportPressed)))
  }
  
    @objc private func tappedOpenAdvertisersPressed() {
        self.openAdvertisersPressed?()
    }
    
  @objc private func tappedOpenCouponsPressed() {
      self.openCouponsPressed?()
  }
    
    @objc private func tappedOpenDiscountsPressed() {
        self.openDiscountsPressed?()
    }
    
    @objc private func tappedOpenSupportPressed() {
        self.openSupportPressed?()
    }

  required init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  deinit {
    NotificationCenter.default.removeObserver(self)
  }

  @objc private func update() {
      if UserDAO.getUser() != nil{
          statsStackView.isHidden = false
      }else{
          statsStackView.isHidden = true
      }
  }
}

private class StatView: UIView {
  var color: UIColor = .braveLabel {
    didSet {
      titleLabel.textColor = color
    }
  }

  var image: String = "" {
    didSet {
        let imageFile = UIImage(sharedNamed: "\(image)")!
            .withRenderingMode(.alwaysTemplate)
            
        //chage color icon
        //.withTintColor(FrontUtils.rgbaToUIColor(red: 62, green: 63, blue: 65, alpha: 1), renderingMode: .alwaysOriginal)
        
        imageView.image = imageFile
    }
  }

  var title: String = "" {
    didSet {
      titleLabel.text = "\(title)"
    }
  }
    
    fileprivate var imageView: UIImageView = {
        let imageView = UIImageView()
        imageView.contentMode = .scaleAspectFit
        return imageView
    }()

  fileprivate var titleLabel: UILabel = {
    let label = UILabel()
    label.textAlignment = .center
    label.numberOfLines = 0
    label.adjustsFontSizeToFitWidth = true
    label.font = UIFont.systemFont(ofSize: 11, weight: UIFont.Weight.regular)
    return label
  }()

  override init(frame: CGRect) {
    super.init(frame: frame)

    let stackView = UIStackView()
    stackView.axis = .vertical
    stackView.alignment = .center
    stackView.spacing = 5
    stackView.addStackViewItems(.view(imageView), .view(titleLabel))

    addSubview(stackView)

    stackView.snp.makeConstraints {
      $0.edges.equalToSuperview()
    }
      imageView.snp.makeConstraints {
      //$0.edges.equalToSuperview()
      $0.height.equalTo(25)
      $0.width.equalTo(25)
      if UIDevice.current.userInterfaceIdiom == .pad {
          $0.height.equalTo(35)
          $0.width.equalTo(35)
      }
    }
  }

  required init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }
}
