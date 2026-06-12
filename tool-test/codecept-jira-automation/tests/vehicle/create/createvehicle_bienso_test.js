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

    I.click("Tạo Xe & Pin");
});