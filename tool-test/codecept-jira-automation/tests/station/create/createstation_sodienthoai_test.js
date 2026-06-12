const {
  waitAndSeePopup,
  loginOnce,
  openAddStationModal,
  fillNominalFormForSoDT,
} = require("./stationHelper");

Feature("Thêm Trạm - Test Thuộc Tính soDT");

BeforeSuite(async ({ I }) => {
  await loginOnce(I);
});

Before(async ({ I }) => {
  await openAddStationModal(I);
});

Scenario("TC_CREATESTATION - Số điện thoại hợp lệ min 10 ký tự", ({ I }) => {
  fillNominalFormForSoDT(I);
  I.fillField('input[name="soDT"]', "0912345678");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Số điện thoại hợp lệ max 11 ký tự", ({ I }) => {
  fillNominalFormForSoDT(I);
  I.fillField('input[name="soDT"]', "09123456789");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Không nhập số điện thoại", ({ I }) => {
  fillNominalFormForSoDT(I);
  I.clearField('input[name="soDT"]');

  I.click("Thêm Trạm", "form");

  I.seeElement('input[name="soDT"]:invalid');
});

Scenario("TC_CREATESTATION - Số điện thoại nhỏ hơn 10 ký tự", ({ I }) => {
  fillNominalFormForSoDT(I);
  I.fillField('input[name="soDT"]', "091234567");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Số điện thoại nhỏ hơn 10 ký tự.");
});

Scenario("TC_CREATESTATION - Số điện thoại lớn hơn 11 ký tự", ({ I }) => {
  fillNominalFormForSoDT(I);
  I.fillField('input[name="soDT"]', "091234567890");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Số điện thoại lớn hơn 11 ký tự.");
});

Scenario("TC_CREATESTATION - Số điện thoại chứa chữ cái", ({ I }) => {
  fillNominalFormForSoDT(I);
  I.fillField('input[name="soDT"]', "09123abc78");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Số điện thoại chứa chữ cái.");
});

Scenario("TC_CREATESTATION - Số điện thoại chứa ký tự đặc biệt", ({ I }) => {
  fillNominalFormForSoDT(I);
  I.fillField('input[name="soDT"]', "09123@5678");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Số điện thoại chứa ký tự đặc biệt.");
});