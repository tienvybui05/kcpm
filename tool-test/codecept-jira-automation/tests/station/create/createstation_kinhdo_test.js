const {
  waitAndSeePopup,
  fillFieldFast,
  loginOnce,
  openAddStationModal,
  fillNominalFormForKinhDo,
} = require("./stationHelper");

Feature("Thêm Trạm - Test Thuộc Tính kinhDo");

BeforeSuite(async ({ I }) => {
  await loginOnce(I);
});

Before(async ({ I }) => {
  await openAddStationModal(I);
});

Scenario("TC_CREATESTATION - Kinh độ hợp lệ min -180.0", ({ I }) => {
  fillNominalFormForKinhDo(I);
  I.fillField('input[name="kinhDo"]', "-180.0");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Kinh độ hợp lệ min+ -179.9999", ({ I }) => {
  fillNominalFormForKinhDo(I);
  I.fillField('input[name="kinhDo"]', "-179.9999");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Kinh độ hợp lệ nom 0.0", ({ I }) => {
  fillNominalFormForKinhDo(I);
  I.fillField('input[name="kinhDo"]', "0.0");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Kinh độ hợp lệ max- 179.9999", ({ I }) => {
  fillNominalFormForKinhDo(I);
  I.fillField('input[name="kinhDo"]', "179.9999");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Kinh độ hợp lệ max 180.0", ({ I }) => {
  fillNominalFormForKinhDo(I);
  I.fillField('input[name="kinhDo"]', "180.0");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Không nhập kinh độ", ({ I }) => {
  fillNominalFormForKinhDo(I);
  I.clearField('input[name="kinhDo"]');

  I.click("Thêm Trạm", "form");

  I.seeElement('input[name="kinhDo"]:invalid');
});

Scenario("TC_CREATESTATION - Kinh độ nhỏ hơn -180", ({ I }) => {
  fillNominalFormForKinhDo(I);
  I.fillField('input[name="kinhDo"]', "-180.0001");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Kinh độ nhỏ hơn -180.");
});

Scenario("TC_CREATESTATION - Kinh độ lớn hơn 180", ({ I }) => {
  fillNominalFormForKinhDo(I);
  I.fillField('input[name="kinhDo"]', "180.0001");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Kinh độ lớn hơn 180.");
});

Scenario("TC_CREATESTATION - Kinh độ sai kiểu dữ liệu", async ({ I }) => {
  fillNominalFormForKinhDo(I);

  await fillFieldFast(I, 'input[name="kinhDo"]', "abc");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Kinh độ sai kiểu dữ liệu.");
});
