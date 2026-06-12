const {
    loginOnce,
    openAddVehicleModal,
    fillNominalVehicleForm,
} = require("./vehicleHelper");

Feature("Create Vehicle - Pin Type");

Before(async ({ I }) => {
    await loginOnce(I);
});

Scenario("Không nhập loại pin", async ({ I }) => {
    await openAddVehicleModal(I);

    fillNominalVehicleForm(I);

    I.fillField("#loaiPin", "");

    // KÌ VỌNG: Nút "Tạo Xe & Pin" phải bị khóa (disabled) khi loại pin trống
    I.seeElement('//button[contains(., "Tạo Xe & Pin") and @disabled]');
});