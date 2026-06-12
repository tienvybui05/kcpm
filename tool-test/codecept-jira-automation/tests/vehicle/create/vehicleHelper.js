let authStorage = {};

// Sinh số VIN ngẫu nhiên
function generateVin() {
    return "VIN" + Date.now();
}

// Sinh biển số ĐÚNG CHUẨN REGEX BACKEND (Ví dụ: 29A-12345)
function generateBienSo() {
    const microtime = Date.now().toString();
    const haiSoDau = microtime.slice(-2);   // Lấy 2 số ngẫu nhiên cuối
    const namSoCuoi = microtime.slice(-5);  // Lấy 5 số ngẫu nhiên cuối
    return `${haiSoDau}A-${namSoCuoi}`;
}

async function loginOnce(I) {
    I.amOnPage("http://localhost:3000/login");
    I.fillField('input[name="phone"]', "0396076125");
    I.fillField('input[name="password"]', "0396076125duy@");
    I.click('button[type="submit"]');
    I.wait(3);
    I.waitForFunction(() => {
        return localStorage.getItem("token") !== null;
    }, 10);

    authStorage = await I.executeScript(() => {
        return {
            token: localStorage.getItem("token"),
            userRole: localStorage.getItem("userRole"),
            userEmail: localStorage.getItem("userEmail"),
            userId: localStorage.getItem("userId"),
            hoTen: localStorage.getItem("hoTen"),
        };
    });
}

async function openAddVehicleModal(I) {
    I.amOnPage("http://localhost:3000/dashboard/information");
    const currentUrl = await I.grabCurrentUrl();
    if (currentUrl.includes("/login")) {
        await I.executeScript((storage) => {
            localStorage.setItem("token", storage.token);
            localStorage.setItem("userRole", storage.userRole);
            localStorage.setItem("userEmail", storage.userEmail);
            localStorage.setItem("userId", storage.userId);
            localStorage.setItem("hoTen", storage.hoTen);
        }, authStorage);
        I.amOnPage("http://localhost:3000/dashboard/information");
    }
    I.waitForText("Xe Của Tôi", 15);
    I.click("Thêm xe mới + Pin");
    I.waitForText("Thêm Xe Mới & Tạo Pin", 10);
}

// Hàm điền dữ liệu mẫu chuẩn - ĐÃ LOẠI BỎ CÁC TRƯỜNG VỀ PIN
function fillNominalVehicleForm(I, customFields = {}) {
    if (!customFields.hasOwnProperty('vin')) I.fillField("#vin", generateVin());
    if (!customFields.hasOwnProperty('bienSo')) I.fillField("#bienSo", generateBienSo());
    if (!customFields.hasOwnProperty('loaiXe')) I.fillField("#loaiXe", "Xe máy điện");
}

module.exports = {
    loginOnce,
    openAddVehicleModal,
    fillNominalVehicleForm,
    generateVin,
    generateBienSo,
};