const {
  waitAndSeePopup,
  fillFieldFast,
  loginOnce,
  openAddStationModal,
  fillNominalFormForViDo,
} = require("./stationHelper");

Feature("Thêm Trạm - Test Thuộc Tính viDo");

BeforeSuite(async ({ I }) => {
  await loginOnce(I);
});

Before(async ({ I }) => {
  await openAddStationModal(I);
});

Scenario("TC_CREATESTATION - Vĩ độ hợp lệ min -90.0", ({ I }) => {
  fillNominalFormForViDo(I);
  I.fillField('input[name="viDo"]', "-90.0");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Vĩ độ hợp lệ min+ -89.9999", ({ I }) => {
  fillNominalFormForViDo(I);
  I.fillField('input[name="viDo"]', "-89.9999");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Vĩ độ hợp lệ nom 0.0", ({ I }) => {
  fillNominalFormForViDo(I);
  I.fillField('input[name="viDo"]', "0.0");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Vĩ độ hợp lệ max- 89.9999", ({ I }) => {
  fillNominalFormForViDo(I);
  I.fillField('input[name="viDo"]', "89.9999");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Vĩ độ hợp lệ max 90.0", ({ I }) => {
  fillNominalFormForViDo(I);
  I.fillField('input[name="viDo"]', "90.0");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Không nhập vĩ độ", ({ I }) => {
  fillNominalFormForViDo(I);
  I.clearField('input[name="viDo"]');

  I.click("Thêm Trạm", "form");

  I.seeElement('input[name="viDo"]:invalid');
});

Scenario("TC_CREATESTATION - Vĩ độ nhỏ hơn -90", ({ I }) => {
  fillNominalFormForViDo(I);
  I.fillField('input[name="viDo"]', "-90.0001");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Vĩ độ nhỏ hơn -90.");
});

Scenario("TC_CREATESTATION - Vĩ độ lớn hơn 90", ({ I }) => {
  fillNominalFormForViDo(I);
  I.fillField('input[name="viDo"]', "90.0001");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Vĩ độ lớn hơn 90.");
});

Scenario("TC_CREATESTATION - Vĩ độ sai kiểu dữ liệu", async ({ I }) => {
  fillNominalFormForViDo(I);

  await fillFieldFast(I, 'input[name="viDo"]', "abc");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Vĩ độ sai kiểu dữ liệu.");
});