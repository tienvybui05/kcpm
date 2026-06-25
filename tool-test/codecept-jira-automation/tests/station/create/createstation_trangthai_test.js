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

Scenario(
  "TC_CREATESTATION - [trangThai - Valid=Có dữ liệu] Tạo trạm với trangThai = Hoạt động",
  async ({ I }) => {
    await fillNominalFormForTrangThai(I);
    I.selectOption('select[name="trangThai"]', "Hoạt động");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [trangThai - Valid=Có dữ liệu] Tạo trạm với trangThai = Bảo trì",
  async ({ I }) => {
    await fillNominalFormForTrangThai(I);
    I.selectOption('select[name="trangThai"]', "Bảo trì");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [trangThai - Valid=Có dữ liệu] Tạo trạm với trangThai = Tạm dừng",
  async ({ I }) => {
    await fillNominalFormForTrangThai(I);
    I.selectOption('select[name="trangThai"]', "Tạm dừng");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);