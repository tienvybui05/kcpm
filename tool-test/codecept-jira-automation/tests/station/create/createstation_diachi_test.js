const {
  generateString,
  waitAndSeePopup,
  fillFieldFast,
  loginOnce,
  openAddStationModal,
  fillNominalFormForDiaChi,
} = require("./stationHelper");

Feature("Thêm Trạm - Test Thuộc Tính diaChi");

BeforeSuite(async ({ I }) => {
  await loginOnce(I);
});

Before(async ({ I }) => {
  await openAddStationModal(I);
});

Scenario("TC_CREATESTATION - Địa chỉ hợp lệ min 1 ký tự", ({ I }) => {
  fillNominalFormForDiaChi(I);
  I.fillField('input[name="diaChi"]', generateString(1));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Địa chỉ hợp lệ min+ 2 ký tự", ({ I }) => {
  fillNominalFormForDiaChi(I);
  I.fillField('input[name="diaChi"]', generateString(2));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Địa chỉ hợp lệ nom 125 ký tự", async ({ I }) => {
  fillNominalFormForDiaChi(I);
  await fillFieldFast(I, 'input[name="diaChi"]', generateString(125));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Địa chỉ hợp lệ max- 249 ký tự", async ({ I }) => {
  fillNominalFormForDiaChi(I);
  await fillFieldFast(I, 'input[name="diaChi"]', generateString(249));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Địa chỉ hợp lệ max 250 ký tự", async ({ I }) => {
  fillNominalFormForDiaChi(I);
  await fillFieldFast(I, 'input[name="diaChi"]', generateString(250));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Không nhập địa chỉ", ({ I }) => {
  fillNominalFormForDiaChi(I);
  I.clearField('input[name="diaChi"]');

  I.click("Thêm Trạm", "form");

  I.seeElement('input[name="diaChi"]:invalid');
});

Scenario(
  "TC_CREATESTATION - Địa chỉ vượt quá giới hạn 250 ký tự",
  async ({ I }) => {
    fillNominalFormForDiaChi(I);
    await fillFieldFast(I, 'input[name="diaChi"]', generateString(251));

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Địa chỉ lố 250 kí tự.");
  },
);