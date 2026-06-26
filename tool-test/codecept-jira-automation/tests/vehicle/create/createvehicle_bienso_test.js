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
    I.see("Biển số không được để trống");
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
    I.see("Biển số không hợp lệ");
});

// =========================================================================
// CÁC TEST CASE KIỂM TRA GIÁ TRỊ BIÊN VÀ NGOÀI BIÊN (RBVA) CHO ĐỘ DÀI BIỂN SỐ (7 - 12 ký tự)
// =========================================================================

Scenario("TC_RBVA_005_bienSo_Min_Minus=6 - Nhập biển số 6 ký tự (Không hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { bienSo: true });

    I.click("#bienSo");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi 6 ký tự
    I.fillField("#bienSo", "51A-123");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Xuất hiện thông báo lỗi do chưa đủ 7 ký tự
    I.see("Biển số phải từ 7 - 12 ký tự");
});

Scenario("TC_RBVA_006_bienSo_Min=7 - Nhập biển số 7 ký tự (Hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { bienSo: true });

    I.click("#bienSo");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi 7 ký tự
    I.fillField("#bienSo", "51A-1234");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Không xuất hiện thông báo lỗi
    I.dontSee("Biển số phải từ 7 - 12 ký tự");
});

Scenario("TC_RBVA_007_bienSo_Min_Plus=8 - Nhập biển số 8 ký tự (Hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { bienSo: true });

    I.click("#bienSo");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi 8 ký tự
    I.fillField("#bienSo", "51A-1234");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Không xuất hiện thông báo lỗi
    I.dontSee("Biển số phải từ 7 - 12 ký tự");
});

Scenario("TC_RBVA_008_bienSo_Max_Minus=11 - Nhập biển số 11 ký tự (Hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { bienSo: true });

    I.click("#bienSo");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi 11 ký tự
    I.fillField("#bienSo", "51A-123.456");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Không xuất hiện thông báo lỗi
    I.dontSee("Biển số phải từ 7 - 12 ký tự");
});

Scenario("TC_RBVA_009_bienSo_Max=12 - Nhập biển số 12 ký tự (Hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { bienSo: true });

    I.click("#bienSo");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi 12 ký tự
    I.fillField("#bienSo", "51A-123.4567");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Không xuất hiện thông báo lỗi
    I.dontSee("Biển số phải từ 7 - 12 ký tự");
});

Scenario("TC_RBVA_010_bienSo_Max_Plus=13 - Nhập biển số 13 ký tự (Không hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { bienSo: true });

    I.click("#bienSo");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi 13 ký tự
    I.fillField("#bienSo", "51A-123.45678");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Xuất hiện thông báo lỗi do vượt quá 12 ký tự
    I.see("Biển số phải từ 7 - 12 ký tự");
});