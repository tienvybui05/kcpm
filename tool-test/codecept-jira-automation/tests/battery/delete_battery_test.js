// delete_battery_test.js
// @group Battery
// @feature Delete Battery
// @description Kiểm thử chức năng xóa pin (8 test cases)

const assert = require('assert');

Feature('Delete Battery - 8 Test Cases');

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
   HELPER: Tạo 1 pin mới (để xóa sau)
========================= */
async function createBattery(I, modelSuffix = '') {
    const model = 'BAT_DEL_' + Date.now() + (modelSuffix || '');
    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.click('Thêm pin mới');
    I.waitForElement('input[name="loaiPin"]', 10);

    I.fillField('input[name="loaiPin"]', model);
    I.fillField('input[name="dungLuong"]', '10');
    I.fillField('input[name="newSucKhoe"]', '90');
    I.selectOption('select[name="newTinhTrang"]', 'đầy');
    await selectFirstStation(I);
    I.click('Xác nhận');
    I.waitForText('Thêm pin mới thành công', 15);
    I.wait(1);
    return model;
}

/* =========================
   HELPER: Click vào nút Xóa trên card có chứa model (mở modal)
========================= */
async function clickDeleteButtonForModel(I, model) {
    const xpath = `//div[contains(@class,'card') and contains(.,'${model}')]//*[contains(@class,'action') and .//*[@data-icon='trash']]`;
    I.click(xpath);
    I.waitForElement('[class*="overlay"]', 10);
}

/* =========================
   ĐĂNG NHẬP TRƯỚC MỖI SCENARIO
========================= */
Before(async ({ I }) => {
    await loginAsAdmin(I);
});

/* ============================================================
   TEST CASES
============================================================ */

// DEL-01: Xóa pin thành công (Happy Path)
Scenario('DEL-01_Delete_Success', async ({ I }) => {
    const model = await createBattery(I, '_DEL01');

    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.waitForText(model, 10);
    await clickDeleteButtonForModel(I, model);

    I.click('button[class*="deleteBtn"]');
    I.wait(2);
    I.dontSee(model);
});

// DEL-02: Nhấn Hủy → không xóa
Scenario('DEL-02_Cancel_NoDelete', async ({ I }) => {
    const model = await createBattery(I, '_DEL02');

    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.waitForText(model, 10);
    await clickDeleteButtonForModel(I, model);

    I.click('button[class*="cancelBtn"]');
    I.wait(1);
    I.dontSeeElement('[class*="overlay"]');
    I.see(model);
});

// DEL-05: API trả 404 → hiện alert "Không thể xóa pin!"
Scenario('DEL-05_API_404', async ({ I }) => {
    const model = await createBattery(I, '_DEL05');

    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.waitForText(model, 10);
    await clickDeleteButtonForModel(I, model);

    // Mock API trả về 404
    await I.executeScript(() => {
        const originalFetch = window.fetch;
        window.fetch = function(url, options) {
            if (url.includes('/api/battery-service/pins/') && options.method === 'DELETE') {
                return Promise.resolve({
                    ok: false,
                    status: 404,
                    json: () => Promise.resolve({}),
                });
            }
            return originalFetch(url, options);
        };
    });

    I.click('button[class*="deleteBtn"]');
    const alertText = await I.grabPopupText();
    assert.equal(alertText, 'Không thể xóa pin!');
    I.acceptPopup();
});

// DEL-06: API trả 500 → hiện alert "Không thể xóa pin!"
Scenario('DEL-06_API_500', async ({ I }) => {
    const model = await createBattery(I, '_DEL06');

    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.waitForText(model, 10);
    await clickDeleteButtonForModel(I, model);

    await I.executeScript(() => {
        const originalFetch = window.fetch;
        window.fetch = function(url, options) {
            if (url.includes('/api/battery-service/pins/') && options.method === 'DELETE') {
                return Promise.resolve({
                    ok: false,
                    status: 500,
                    json: () => Promise.resolve({}),
                });
            }
            return originalFetch(url, options);
        };
    });

    I.click('button[class*="deleteBtn"]');
    const alertText = await I.grabPopupText();
    assert.equal(alertText, 'Không thể xóa pin!');
    I.acceptPopup();
});

// DEL-07: Mất kết nối server → hiện alert "Lỗi kết nối server!"
Scenario('DEL-07_NetworkError', async ({ I }) => {
    const model = await createBattery(I, '_DEL07');

    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.waitForText(model, 10);
    await clickDeleteButtonForModel(I, model);

    await I.executeScript(() => {
        const originalFetch = window.fetch;
        window.fetch = function(url, options) {
            if (url.includes('/api/battery-service/pins/') && options.method === 'DELETE') {
                return Promise.reject(new Error('Network error'));
            }
            return originalFetch(url, options);
        };
    });

    I.click('button[class*="deleteBtn"]');
    const alertText = await I.grabPopupText();
    assert.equal(alertText, 'Lỗi kết nối server!');
    I.acceptPopup();
});

// DEL-08: Double click nút Xóa
Scenario('DEL-08_DoubleClick_Delete', async ({ I }) => {
    const model = await createBattery(I, '_DEL08');

    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.waitForText(model, 10);
    await clickDeleteButtonForModel(I, model);

    I.doubleClick('button[class*="deleteBtn"]');
    I.wait(2);
    I.dontSee(model);
});

// DEL-09: Trong lúc loading nút Xóa bị disable (chỉ kiểm tra UI)
Scenario('DEL-09_Loading_Disabled', async ({ I }) => {
    const model = await createBattery(I, '_DEL09');

    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.waitForText(model, 10);
    await clickDeleteButtonForModel(I, model);

    // Làm chậm response
    await I.executeScript(() => {
        const originalFetch = window.fetch;
        window.fetch = function(url, options) {
            if (url.includes('/api/battery-service/pins/') && options.method === 'DELETE') {
                return new Promise((resolve) => {
                    setTimeout(() => {
                        resolve({
                            ok: true,
                            json: () => Promise.resolve({}),
                        });
                    }, 3000);
                });
            }
            return originalFetch(url, options);
        };
    });

    I.click('button[class*="deleteBtn"]');
    I.seeAttributesOnElements('button[class*="deleteBtn"]', { disabled: true });
    I.see('Đang xóa...', 'button[class*="deleteBtn"]');
    I.wait(4);
});

// DEL-10: Xóa xong danh sách được refresh
Scenario('DEL-10_Refresh_After_Delete', async ({ I }) => {
    const model = await createBattery(I, '_DEL10');

    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.waitForText(model, 10);
    await clickDeleteButtonForModel(I, model);

    I.click('button[class*="deleteBtn"]');
    I.wait(3);
    I.dontSee(model);
});

// TC-11: Xóa pin khi token hết hạn (API trả 401)
// ⚠️ Lỗi tiềm ẩn: Ứng dụng không xử lý 401, chỉ hiện alert "Không thể xóa pin!"
// và không chuyển hướng về login, gây trải nghiệm xấu.
Scenario('BUG-11_Delete_Unauthorized_401', async ({ I }) => {
    const model = await createBattery(I, '_BUG11');

    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.waitForText(model, 10);
    await clickDeleteButtonForModel(I, model);

    // Mock DELETE trả về 401 Unauthorized
    await I.executeScript(() => {
        const originalFetch = window.fetch;
        window.fetch = function(url, options) {
            if (url.includes('/api/battery-service/pins/') && options.method === 'DELETE') {
                return Promise.resolve({
                    ok: false,
                    status: 401,
                    json: () => Promise.resolve({ message: 'Token hết hạn' }),
                });
            }
            return originalFetch(url, options);
        };
    });

    I.click('button[class*="deleteBtn"]');
    // Kiểm tra alert hiện ra (dù backend có thể trả về message khác)
    const alertText = await I.grabPopupText();
    // Hiện tại, component luôn hiển thị "Không thể xóa pin!" cho mọi lỗi 4xx/5xx
    assert.equal(alertText, 'Không thể xóa pin!');
    I.acceptPopup();

    // 💡 Điểm yếu: Nếu là 401, nên chuyển hướng đến login hoặc thông báo rõ ràng hơn.
    // Nếu không, người dùng vẫn ở trang và có thể thử lại với token hết hạn.
    // Bạn có thể kiểm tra thêm xem có chuyển hướng không.
    // I.dontSeeCurrentUrlEquals('http://localhost:3000/login'); // (nếu không có chuyển hướng)
});

// TC-12: Xóa pin đang được sử dụng (API trả 409 Conflict)
// ⚠️ Lỗi tiềm ẩn: Backend có thể trả về thông báo chi tiết (ví dụ: "Pin đang được sử dụng"),
// nhưng frontend lại hiển thị thông báo chung "Không thể xóa pin!" – che giấu nguyên nhân thực sự.
Scenario('BUG-12_Delete_Conflict_409', async ({ I }) => {
    const model = await createBattery(I, '_BUG12');

    I.amOnPage('http://localhost:3000/dashboard/batteries');
    I.waitForText('Danh sách Pin', 10);
    I.waitForText(model, 10);
    await clickDeleteButtonForModel(I, model);

    await I.executeScript(() => {
        const originalFetch = window.fetch;
        window.fetch = function(url, options) {
            if (url.includes('/api/battery-service/pins/') && options.method === 'DELETE') {
                return Promise.resolve({
                    ok: false,
                    status: 409,
                    json: () => Promise.resolve({ message: 'Pin đang được sử dụng, không thể xóa' }),
                });
            }
            return originalFetch(url, options);
        };
    });

    I.click('button[class*="deleteBtn"]');
    const alertText = await I.grabPopupText();
    // Lại là thông báo chung "Không thể xóa pin!" – không hiển thị lý do cụ thể
    assert.equal(alertText, 'Không thể xóa pin!');
    I.acceptPopup();

    // 💡 Điểm yếu: Người dùng không biết tại sao xóa thất bại, dẫn đến bối rối.
    // Nên parse response body và hiển thị message chi tiết (nếu có).
});