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
    I.see("Loại xe không được để trống");
});

// =========================================================================
// CÁC TEST CASE KIỂM TRA GIÁ TRỊ BIÊN VÀ NGOÀI BIÊN (RBVA) CHO ĐỘ DÀI LOẠI XE (2 - 50 ký tự)
// =========================================================================

Scenario("TC_RBVA_011_loaiXe_Min_Minus=1 - Nhập loại xe 1 ký tự (Không hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { loaiXe: true });

    I.click("#loaiXe");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi 1 ký tự
    I.fillField("#loaiXe", "V");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Xuất hiện thông báo lỗi do chưa đủ độ dài tối thiểu là 2
    I.see("Loại xe phải từ 2 - 50 ký tự");
});

Scenario("TC_RBVA_012_loaiXe_Min=2 - Nhập loại xe 2 ký tự (Hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { loaiXe: true });

    I.click("#loaiXe");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi 2 ký tự
    I.fillField("#loaiXe", "VF");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Đạt chuẩn độ dài tối thiểu, không có lỗi xuất hiện
    I.dontSee("Loại xe phải từ 2 - 50 ký tự");
});

Scenario("TC_RBVA_013_loaiXe_Min_Plus=3 - Nhập loại xe 3 ký tự (Hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { loaiXe: true });

    I.click("#loaiXe");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Chuỗi 3 ký tự
    I.fillField("#loaiXe", "VF8");

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Không xuất hiện thông báo lỗi
    I.dontSee("Loại xe phải từ 2 - 50 ký tự");
});

Scenario("TC_RBVA_014_loaiXe_Max_Minus=49 - Nhập loại xe 49 ký tự (Hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { loaiXe: true });

    I.click("#loaiXe");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Tạo nhanh chuỗi có độ dài chuẩn 49 ký tự
    I.fillField("#loaiXe", "A".repeat(49));

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: không có lỗi
    I.dontSee("Loại xe phải từ 2 - 50 ký tự");
});

Scenario("TC_RBVA_015_loaiXe_Max=50 - Nhập loại xe 50 ký tự (Hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { loaiXe: true });

    I.click("#loaiXe");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Tạo nhanh chuỗi chạm mốc tối đa đúng 50 ký tự
    I.fillField("#loaiXe", "A".repeat(50));

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Đạt mốc biên tối đa, không xuất hiện lỗi
    I.dontSee("Loại xe phải từ 2 - 50 ký tự");
});

Scenario("TC_RBVA_016_loaiXe_Max_Plus=51 - Nhập loại xe 51 ký tự (Không hợp lệ)", async ({ I }) => {
    await openAddVehicleModal(I);
    fillNominalVehicleForm(I, { loaiXe: true });

    I.click("#loaiXe");
    I.pressKey(['Control', 'a']);
    I.pressKey('Backspace');
    // Tạo chuỗi vượt ngưỡng tối đa: 51 ký tự
    I.fillField("#loaiXe", "A".repeat(51));

    I.pressKey("Tab");
    I.wait(1);

    // KÌ VỌNG: Vượt quá biên 50 ký tự, hệ thống phải văng lỗi validation
    I.see("Loại xe phải từ 2 - 50 ký tự");
});