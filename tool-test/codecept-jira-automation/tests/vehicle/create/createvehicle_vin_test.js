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

    // KÌ VỌNG: Hệ thống chặn lại và báo lỗi trường Số VIN
    I.see("Vui lòng nhập số VIN");
});