const {
  waitAndSeePopup,
  loginOnce,
  openAddStationModal,
  fillNominalFormForTrangThai,
} = require("./stationHelper");

Feature("Thêm Trạm - Test Thuộc Tính trangThai");

BeforeSuite(async ({ I }) => {
  await loginOnce(I);
});

Before(async ({ I }) => {
  await openAddStationModal(I);
});

Scenario("TC_CREATESTATION - Trạng thái hợp lệ Hoạt động", ({ I }) => {
  fillNominalFormForTrangThai(I);
  I.selectOption('select[name="trangThai"]', "Hoạt động");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Trạng thái hợp lệ Bảo trì", ({ I }) => {
  fillNominalFormForTrangThai(I);
  I.selectOption('select[name="trangThai"]', "Bảo trì");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});

Scenario("TC_CREATESTATION - Trạng thái hợp lệ Tạm dừng", ({ I }) => {
  fillNominalFormForTrangThai(I);
  I.selectOption('select[name="trangThai"]', "Tạm dừng");

  I.click("Thêm Trạm", "form");

  waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});