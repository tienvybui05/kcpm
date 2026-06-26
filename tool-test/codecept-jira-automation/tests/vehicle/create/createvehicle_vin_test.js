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
    I.see("Mã VIN không được để trống");
});

// =========================================================================
// CÁC TEST CASE KIỂM TRA GIÁ TRỊ BIÊN VÀ NGOÀI BIÊN (RBVA) CHO ĐỘ DÀI MÃ VIN (CHÍNH XÁC 17 KÝ TỰ)
// =========================================================================

Scenario("TC_RBVA_002_vin_Min_Minus=16 - Nhập mã VIN 16 ký tự (Không hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { vin: true });

    I.click("#vin");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi 16 ký tự (thiếu 1 ký tự so với chuẩn 17)
    I.fillField("#vin", "1HGCR2F8XHA00000");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Xuất hiện thông báo lỗi do sai độ dài chuẩn
    I.see("Mã VIN phải có đúng 17 ký tự");
});

Scenario("TC_RBVA_003_vin_Min_Max=17 - Nhập mã VIN 17 ký tự (Hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { vin: true });

    I.click("#vin");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi đúng mốc 17 ký tự chuẩn quốc tế
    I.fillField("#vin", "1HGCR2F8XHA000001");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Đúng định dạng và độ dài, không hiển thị lỗi validation
    I.dontSee("Mã VIN phải có đúng 17 ký tự");
});

Scenario("TC_RBVA_004_vin_Max_Plus=18 - Nhập mã VIN 18 ký tự (Không hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { vin: true });

    I.click("#vin");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi 18 ký tự (vượt quá 1 ký tự so với chuẩn 17)
    I.fillField("#vin", "1HGCR2F8XHA0000012");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Vượt quá số ký tự quy định, hệ thống phải báo lỗi
    I.see("Mã VIN phải có đúng 17 ký tự");
});