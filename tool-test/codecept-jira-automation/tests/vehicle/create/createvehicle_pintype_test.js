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

    I.click("Tạo Xe & Pin");
});