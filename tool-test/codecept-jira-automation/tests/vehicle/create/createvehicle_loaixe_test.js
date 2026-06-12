const {
    loginOnce,
    openAddVehicleModal,
    fillNominalVehicleForm,
} = require("./vehicleHelper");

Feature("Create Vehicle - Loai Xe");

Before(async ({ I }) => {
    await loginOnce(I);
});

Scenario("Loại xe để rỗng - Kiểm tra chữ thông báo lỗi của ô nhập loại xe", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { loaiXe: true }); // Không tự động điền loại xe

    I.click("#loaiXe");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');

    I.pressKey("Tab"); // Rời ô để kích hoạt validation chữ đỏ
    I.wait(1);

    // KÌ VỌNG: Kiểm tra chữ cảnh báo lỗi xuất hiện trên UI
    I.see("Không được loại xe để trống");
});