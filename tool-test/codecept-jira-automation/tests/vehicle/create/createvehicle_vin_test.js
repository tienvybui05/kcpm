const {
    loginOnce,
    openAddVehicleModal,
    fillNominalVehicleForm,
} = require("./vehicleHelper");

Feature("Create Vehicle - VIN");

Before(async ({ I }) => {
    await loginOnce(I);
});

Scenario("Mã VIN để rỗng - Kiểm tra chữ thông báo lỗi hiển thị của ô nhập VIN", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { vin: true }); // Không tự động điền mã VIN

    I.click("#vin");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Kiểm tra chữ cảnh báo lỗi xuất hiện trên UI
    I.see("Không được để mã VIN trống");
});