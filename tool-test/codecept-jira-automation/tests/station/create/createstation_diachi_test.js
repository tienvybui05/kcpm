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

Scenario("TC_CREATESTATION - [diaChi - Min-=0] Tạo trạm với diaChi = Rỗng", async ({ I }) => {
  await fillNominalFormForDiaChi(I);
  await fillFieldFast(I, 'input[name="diaChi"]', "");

  I.click("Thêm Trạm", "form");

  I.seeElement('input[name="diaChi"]:invalid');
});

Scenario("TC_CREATESTATION - [diaChi - Min=1] Tạo trạm với diaChi = 1 ký tự", async ({ I }) => {
  await fillNominalFormForDiaChi(I);
  await fillFieldFast(I, 'input[name="diaChi"]', generateString(1));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - [diaChi - Min+=2] Tạo trạm với diaChi = 2 ký tự", async ({ I }) => {
  await fillNominalFormForDiaChi(I);
  await fillFieldFast(I, 'input[name="diaChi"]', generateString(2));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - [diaChi - Max-=249] Tạo trạm với diaChi = 249 ký tự", async ({ I }) => {
  await fillNominalFormForDiaChi(I);
  await fillFieldFast(I, 'input[name="diaChi"]', generateString(249));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - [diaChi - Max=250] Tạo trạm với diaChi = 250 ký tự", async ({ I }) => {
  await fillNominalFormForDiaChi(I);
  await fillFieldFast(I, 'input[name="diaChi"]', generateString(250));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - [diaChi - Max+=251] Tạo trạm với diaChi = 251 ký tự", async ({ I }) => {
  await fillNominalFormForDiaChi(I);
  await fillFieldFast(I, 'input[name="diaChi"]', generateString(251));

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "❌ Địa chỉ lố 250 kí tự.");
});

Scenario("TC_CREATESTATION - [diaChi - Error=Null] Tạo trạm với diaChi = Null", async ({ I }) => {
  await fillNominalFormForDiaChi(I);
  // Giả lập UI cho null bằng cách không điền gì (chuỗi rỗng)
  await fillFieldFast(I, 'input[name="diaChi"]', "");

  I.click("Thêm Trạm", "form");

  I.seeElement('input[name="diaChi"]:invalid');
});