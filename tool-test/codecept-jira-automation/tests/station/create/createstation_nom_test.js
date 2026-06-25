const {
    waitAndSeePopup,
    loginOnce,
    openAddStationModal,
    fillNominalForm, // Gọi hàm chuẩn mới tạo
} = require("./stationHelper");

Feature("Thêm Trạm - Test Nominal (Happy Path)");

BeforeSuite(async ({ I }) => {
    await loginOnce(I);
});

Before(async ({ I }) => {
    await openAddStationModal(I);
});

Scenario("TC_CREATESTATION - Tạo trạm thành công với tất cả thông tin hợp lệ", async ({ I }) => {
    // Điền tất cả giá trị chuẩn, không can thiệp lỗi
    await fillNominalForm(I);

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
});