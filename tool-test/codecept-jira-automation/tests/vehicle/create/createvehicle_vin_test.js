const {
    loginOnce,
    openAddVehicleModal,
    fillNominalVehicleForm,
} = require("./vehicleHelper");

Feature("Create Vehicle - VIN");

Before(async ({ I }) => {
    await loginOnce(I);
});

Scenario("VIN rỗng", async ({ I }) => {
    await openAddVehicleModal(I);

    fillNominalVehicleForm(I);

    I.fillField("#vin", "");

    // KÌ VỌNG: Nút "Tạo Xe & Pin" phải bị khóa (disabled) khi số VIN trống
    I.seeElement('//button[contains(., "Tạo Xe & Pin") and @disabled]');
});