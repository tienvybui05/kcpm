const {
    loginOnce,
    openAddVehicleModal,
    fillNominalVehicleForm,
} = require("./vehicleHelper");

Feature("Create Vehicle - Bien So");

Before(async ({ I }) => {
    await loginOnce(I);
});

Scenario("Biển số để rỗng - Kiểm tra chữ thông báo lỗi hiển thị của ô nhập biển số", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { bienSo: true }); // Báo helper không tự động điền biển số

    // Click vào ô biển số, xóa sạch dữ liệu
    I.click("#bienSo");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');

    // Nhấn Tab
    I.pressKey("Tab");
    I.wait(1); // Chờ 1 giây để hiệu ứng chữ cảnh báo đỏ của Frontend xuất hiện

    // KÌ VỌNG: Kiểm tra chữ cảnh báo hiển thị trên màn hình
    I.see("Không được để biển số trống");
});

Scenario("Biển số sai định dạng Regex - Kiểm tra chữ thông báo lỗi hiển thị", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { bienSo: true });

    // Click vào ô biển số, xóa sạch dữ liệu cũ và điền chuỗi sai định dạng
    I.click("#bienSo");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    I.fillField("#bienSo", "BIEN-SO-SAI");

    // Nhấn Tab để kích hoạt bộ kiểm tra định dạng Regex của Frontend
    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Kiểm tra chữ cảnh báo sai định dạng xuất hiện trên UI
    I.see("Không hợp lệ");
});