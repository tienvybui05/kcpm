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

    // ---  KÌ VỌNG  ---
    // Ví dụ hiển thị message cảnh báo của HTML5 hoặc của Form validation
    I.see("Vui lòng nhập biển số xe");
    // Hoặc kiểm tra class lỗi xuất hiện trên input
    I.seeElement("#bienSo.is-invalid");
});