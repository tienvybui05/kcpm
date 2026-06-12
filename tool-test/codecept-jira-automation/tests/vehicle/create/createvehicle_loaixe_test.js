const {
    loginOnce,
    openAddVehicleModal,
    fillNominalVehicleForm,
} = require("./vehicleHelper");

Feature("Create Vehicle - Loai Xe");

Before(async ({ I }) => {
    await loginOnce(I);
});

Scenario("Loại xe rỗng", async ({ I }) => {
    await openAddVehicleModal(I);

    fillNominalVehicleForm(I);

    I.fillField("#loaiXe", "");

    I.click("Tạo Xe & Pin");

    // KÌ VỌNG: Hệ thống chặn lại và báo lỗi trường Loại xe
    I.see("Vui lòng nhập loại xe");
});