const {
    loginOnce,
    openAddVehicleModal,
    fillNominalVehicleForm,
} = require("./vehicleHelper");

Feature("Create Vehicle - Bien So");

Before(async ({ I }) => {
    await loginOnce(I);
});

Scenario("Biển số rỗng", async ({ I }) => {
    await openAddVehicleModal(I);

    fillNominalVehicleForm(I);

    I.fillField("#bienSo", "");

    // KÌ VỌNG: Nút "Tạo Xe & Pin" phải bị khóa (disabled), không cho người dùng bấm
    I.seeElement('//button[contains(., "Tạo Xe & Pin") and @disabled]');
});