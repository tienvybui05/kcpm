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

    // KÌ VỌNG: Nút "Tạo Xe & Pin" phải bị khóa (disabled) khi loại xe trống
    I.seeElement('//button[contains(., "Tạo Xe & Pin") and @disabled]');
});