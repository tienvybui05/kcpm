const {
  generateString,
  generateUniqueString,
  waitAndSeePopup,
  fillFieldFast,
  loginOnce,
  openAddStationModal,
  fillNominalFormForTenTram,
} = require("./stationHelper");

Feature("Thêm Trạm - Test Thuộc Tính tenTram");

BeforeSuite(async ({ I }) => {
  await loginOnce(I);
});

Before(async ({ I }) => {
  await openAddStationModal(I);
});

Scenario("TC_CREATESTATION - Tên trạm hợp lệ min 1 ký tự", ({ I }) => {
  fillNominalFormForTenTram(I);
  I.fillField('input[name="tenTram"]', generateUniqueString(1));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Tên trạm hợp lệ min+ 2 ký tự", ({ I }) => {
  fillNominalFormForTenTram(I);
  I.fillField('input[name="tenTram"]', generateUniqueString(2));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Tên trạm hợp lệ nom 75 ký tự", async ({ I }) => {
  fillNominalFormForTenTram(I);
  await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(75));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Tên trạm hợp lệ max- 149 ký tự", async ({ I }) => {
  fillNominalFormForTenTram(I);
  await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(149));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Tên trạm hợp lệ max 150 ký tự", async ({ I }) => {
  fillNominalFormForTenTram(I);
  await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(150));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Không nhập tên trạm", ({ I }) => {
  fillNominalFormForTenTram(I);
  I.clearField('input[name="tenTram"]');

  I.click("Thêm Trạm", "form");

  I.seeElement('input[name="tenTram"]:invalid');
});

Scenario(
  "TC_CREATESTATION - Tên trạm vượt quá giới hạn 150 ký tự",
  async ({ I }) => {
    fillNominalFormForTenTram(I);
    await fillFieldFast(I, 'input[name="tenTram"]', generateString(151));

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Tên trạm lố 150 kí tự.");
  },
);

Scenario("TC_CREATESTATION - Tên trạm đã tồn tại trong hệ thống", ({ I }) => {
  const duplicateName = "TRAM_DUP_" + Date.now();

  fillNominalFormForTenTram(I);
  I.fillField('input[name="tenTram"]', duplicateName);

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");

  I.click("Thêm Trạm");
  I.waitForText("Thêm Trạm Mới", 5);

  fillNominalFormForTenTram(I);
  I.fillField('input[name="tenTram"]', duplicateName);

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Tên trạm trùng");
});
