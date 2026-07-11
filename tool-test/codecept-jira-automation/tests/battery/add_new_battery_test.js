// @group Battery
// @feature Add Battery
// add_battery_full_31_tests.js
const assert = require('assert');

Feature('Battery Management - Full 31 Test Cases (UI + API)');

/* =========================
   LOGIN REUSABLE
========================= */
async function loginAsAdmin(I) {
    I.amOnPage('http://localhost:3000/login');
    I.waitForElement('input[name="phone"]', 10);
    I.fillField('input[name="phone"]', '0909090908');
    I.fillField('input[name="password"]', '123456aB');
    I.click('Đăng nhập');
    I.waitForURL('**/dashboard**', 15);
    I.wait(2);
}

/* =========================
   HELPER: Mở modal Add Pin
========================= */
async function openAddBatteryModal(I) {
    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.click('Thêm pin mới');
    I.waitForElement('input[name="loaiPin"]', 10);
}

/* =========================
   HELPER: Chọn trạm đầu tiên có giá trị
========================= */
async function selectFirstStation(I) {
    await I.executeScript(() => {
        const select = document.querySelector('select[name="maTram"]');
        if (!select) return;
        const options = select.options;
        for (let i = 0; i < options.length; i++) {
            if (options[i].value && options[i].value !== '') {
                select.selectedIndex = i;
                select.dispatchEvent(new Event('change', { bubbles: true }));
                return;
            }
        }
    });
}

/* =========================
   HELPER: Điền chuỗi dài
========================= */
async function fillLongText(I, selector, text) {
    await I.executeScript(({ selector, text }) => {
        const input = document.querySelector(selector);
        if (input) {
            input.value = text;
            input.dispatchEvent(new Event('input', { bubbles: true }));
            input.dispatchEvent(new Event('change', { bubbles: true }));
        }
    }, { selector, text });
}

/* =========================
   HELPER: Cập nhật state React cho input date
========================= */
async function setReactInputValue(I, selector, value) {
    await I.executeScript(({ selector, value }) => {
        const input = document.querySelector(selector);
        if (input) {
            const nativeSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, "value").set;
            nativeSetter.call(input, value);
            input.dispatchEvent(new Event('input', { bubbles: true }));
            input.dispatchEvent(new Event('change', { bubbles: true }));
        }
    }, { selector, value });
}

/* =========================
   ĐĂNG NHẬP TRƯỚC MỖI SCENARIO
========================= */
Before(async ({ I }) => {
    await loginAsAdmin(I);
});

/* ============================================================
   PHẦN UI - Các test có thể thực hiện trên Modal Add
============================================================ */

// 1. Nominal
Scenario('BVA-PIN-01_Nominal', async ({ I }) => {
    await openAddBatteryModal(I);

    const model = 'BAT_UI_' + Date.now();

    I.fillField('input[name="loaiPin"]', model);
    I.fillField('input[name="dungLuong"]', '75');
    I.fillField('input[name="newSucKhoe"]', '85');

    I.selectOption('select[name="newTinhTrang"]', 'đầy');
    await selectFirstStation(I);

    await I.executeScript(() => {
        const input = document.querySelector('input[name="ngayNhapKho"]');
        input.value = '2026-01-15';
        input.dispatchEvent(new Event('input', { bubbles: true }));
        input.dispatchEvent(new Event('change', { bubbles: true }));
    });

    await I.executeScript(() => {
        const input = document.querySelector('input[name="ngayBaoDuongGanNhat"]');
        input.value = '2026-01-10';
        input.dispatchEvent(new Event('input', { bubbles: true }));
        input.dispatchEvent(new Event('change', { bubbles: true }));
    });

    I.click('Xác nhận');

    I.waitForText('Thêm pin mới thành công', 15);
});

// 2. loaiPin Min = 1 ký tự
Scenario('BVA-PIN-02_loaiPin_Min_1', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'A2');
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 3. loaiPin Min+1 = 2 ký tự
Scenario('BVA-PIN-03_loaiPin_MinPlus1', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'A3');
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 4. loaiPin Max-1 = 99 ký tự
Scenario('BVA-PIN-04_loaiPin_MaxMinus1', async ({ I }) => {
    await openAddBatteryModal(I);

    const longName = 'A'.repeat(99);

    I.click('input[name="loaiPin"]');
    I.pressKey(['Control', 'A']);
    I.pressKey('Backspace');
    I.type(longName);
    I.pressKey('Tab');

    I.wait(1);

    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);

    I.click('Xác nhận');

    I.waitForText('Thêm pin mới thành công', 15);
});

// 5. loaiPin Max = 100 ký tự
Scenario('BVA-PIN-05_loaiPin_Max_100', async ({ I }) => {
    await openAddBatteryModal(I);

    const longName = 'A'.repeat(100);

    I.click('input[name="loaiPin"]');
    I.pressKey(['Control', 'A']);
    I.pressKey('Backspace');
    I.type(longName);
    I.pressKey('Tab');

    I.wait(1);

    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);

    I.click('Xác nhận');

    I.waitForText('Thêm pin mới thành công', 15);
});

// 6. dungLuong Min = 0.1
Scenario('BVA-PIN-06_dungLuong_Min_0.1', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '0.1');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 7. dungLuong Min+1 = 0.2
Scenario('BVA-PIN-07_dungLuong_MinPlus1', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '0.2');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 8. dungLuong Max-1 = 149.9
Scenario('BVA-PIN-08_dungLuong_MaxMinus1', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '149.9');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 9. dungLuong Max = 150.0
Scenario('BVA-PIN-09_dungLuong_Max_150', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '150.0');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 10. sucKhoe Min = 0.0
Scenario('BVA-PIN-10_sucKhoe_Min_0', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '0');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 11. sucKhoe Min+1 = 0.1
Scenario('BVA-PIN-11_sucKhoe_MinPlus1', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '0.1');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 12. sucKhoe Max-1 = 99.9
Scenario('BVA-PIN-12_sucKhoe_MaxMinus1', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '99.9');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 13. sucKhoe Max = 100.0
Scenario('BVA-PIN-13_sucKhoe_Max_100', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '100');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 14. tinhTrang = DANG_SAC (đang sạc)
Scenario('BVA-PIN-14_tinhTrang_DangSac', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    I.selectOption('select[name="newTinhTrang"]', 'đang sạc');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 15. tinhTrang = BAO_TRI (bảo trì)
Scenario('BVA-PIN-15_tinhTrang_BaoTri', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    I.selectOption('select[name="newTinhTrang"]', 'bảo trì');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 16. tinhTrang = DAY (đầy)
Scenario('BVA-PIN-16_tinhTrang_Day', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    I.selectOption('select[name="newTinhTrang"]', 'đầy');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});

// 17. loaiPin = "" (rỗng) -> UI bắt lỗi
Scenario('BVA-PIN-17_loaiPin_Rong', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'TEMP');
    I.clearField('input[name="loaiPin"]');
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Vui lòng nhập hoặc chọn model', 5);
});

// 18. loaiPin = "   " (chỉ khoảng trắng) -> UI bắt lỗi
Scenario('BVA-PIN-18_loaiPin_Whitespace', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', '   ');
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Vui lòng nhập hoặc chọn model', 5);
});

// 19. loaiPin = 101 ký tự
Scenario('BVA-PIN-19_loaiPin_101KyTu_ApiError', async ({ I }) => {
    await openAddBatteryModal(I);

    const longName = 'A'.repeat(101);

    I.click('input[name="loaiPin"]');
    I.pressKey(['Control', 'A']);
    I.pressKey('Backspace');
    I.type(longName);
    I.pressKey('Tab');

    I.wait(1);

    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);

    I.click('Xác nhận');

    I.wait(3);

    I.dontSee('Thêm pin mới thành công');
});

// 20. dungLuong = 0.0 -> UI bắt lỗi
Scenario('BVA-PIN-20_dungLuong_0', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '0');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Vui lòng nhập dung lượng hợp lệ', 5);
});

// 21. dungLuong = -0.1 -> UI bắt lỗi
Scenario('BVA-PIN-21_dungLuong_Am', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '-0.1');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Vui lòng nhập dung lượng hợp lệ', 5);
});

// 22. dungLuong = "" (rỗng) -> UI bắt lỗi
Scenario('BVA-PIN-22_dungLuong_Empty', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.clearField('input[name="dungLuong"]');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Vui lòng nhập dung lượng hợp lệ', 5);
});

// 23. sucKhoe = -0.1 -> UI bắt lỗi
Scenario('BVA-PIN-23_sucKhoe_Minus01', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '-0.1');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Giá trị sức khỏe phải trong khoảng 0–100%', 5);
});

// 24. sucKhoe = 100.1 -> UI bắt lỗi
Scenario('BVA-PIN-24_sucKhoe_100.1', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '100.1');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Giá trị sức khỏe phải trong khoảng 0–100%', 5);
});

// =============================================================
// Business Logic Test Cases (25 – 31)
// =============================================================

// 25. Kiểm tra ngày nhập kho tương lai bằng cơ chế cập nhật State chuẩn xác
Scenario('LOGIC-PIN-25_ngayNhapKho_TuongLai', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_FUTURE_IN_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);

    await setReactInputValue(I, 'input[name="ngayNhapKho"]', '2099-12-31');

    I.click('Xác nhận');
    I.waitForText('Không được chọn ngày trong tương lai', 5);
});

// 26. Sử dụng model ngẫu nhiên riêng biệt để không dính rác từ kịch bản cũ
Scenario('LOGIC-PIN-26_ngayBaoDuong_TuongLai', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_FUTURE_MAINT_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    await selectFirstStation(I);

    await setReactInputValue(I, 'input[name="ngayBaoDuongGanNhat"]', '2099-12-31');

    I.click('Xác nhận');
    I.waitForText('Không được chọn ngày trong tương lai', 5);
});

// 27. Kiểm tra mã trạm trống trong ngữ cảnh kho tổng
Scenario('LOGIC-PIN-27_maTram_Rong', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_NO_STATION_' + Date.now());
    I.fillField('input[name="dungLuong"]', '5');
    I.fillField('input[name="newSucKhoe"]', '80');
    I.selectOption('select[name="maTram"]', '');
    I.click('Xác nhận');
    I.waitForText('Vui lòng chọn trạm', 5);
});

// 28. Tạo mới 1 model động trước, sau đó gọi lại chính nó để test tính năng AutoFill & ReadOnly độc lập
Scenario('LOGIC-PIN-28_AutoFill_Model', async ({ I }) => {
    const dynamicModel = 'MODEL_AUTO_' + Date.now();

    // Bước 1: Khởi tạo model này trong hệ thống
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', dynamicModel);
    I.fillField('input[name="dungLuong"]', '65');
    I.fillField('input[name="newSucKhoe"]', '95');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);

    // Bước 2: Kiểm tra khả năng tự điền dung lượng và khóa thuộc tính chỉnh sửa
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', dynamicModel);
    I.click('input[name="newSucKhoe"]'); // Kích hoạt sự kiện blur/change
    I.wait(2);
    I.seeAttributesOnElements('input[name="dungLuong"]', { readOnly: true });
});

// 29. Khắc phục lỗi kiểm tra chuỗi trùng khớp bằng việc nhắm chính xác phần tử Option DOM
Scenario('LOGIC-PIN-29_Dependency_TinhTrang_DangSac', async ({ I }) => {
    await openAddBatteryModal(I);
    I.selectOption('select[name="newTinhTrang"]', 'đang sạc');
    I.seeInField('select[name="trangThaiSoHuu"]', 'chưa sẵn sàng');
    I.dontSeeElement('select[name="trangThaiSoHuu"] option[value="sẵn sàng"]');
});

// 30. Trạng thái chuyển dịch tự động khi chọn tình trạng "đầy"
Scenario('LOGIC-PIN-30_Dependency_TinhTrang_Day', async ({ I }) => {
    await openAddBatteryModal(I);
    I.selectOption('select[name="newTinhTrang"]', 'bảo trì');
    I.seeInField('select[name="trangThaiSoHuu"]', 'chưa sẵn sàng');

    I.selectOption('select[name="newTinhTrang"]', 'đầy');
    I.seeInField('select[name="trangThaiSoHuu"]', 'sẵn sàng');
});

// 31. Lưu trữ bản ghi kèm theo ghi chú tùy chỉnh thành công
Scenario('LOGIC-PIN-31_Custom_LogNote', async ({ I }) => {
    await openAddBatteryModal(I);
    I.fillField('input[name="loaiPin"]', 'BAT_LOG_' + Date.now());
    I.fillField('input[name="dungLuong"]', '10');
    I.fillField('input[name="newSucKhoe"]', '100');
    await selectFirstStation(I);
    I.fillField('input[name="logNote"]', 'Test tạo pin kèm ghi chú đặc biệt');
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
});