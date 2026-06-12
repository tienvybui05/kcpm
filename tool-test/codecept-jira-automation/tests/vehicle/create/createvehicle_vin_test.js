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

    I.click("Tạo Xe & Pin");
});