let authStorage = {};

function generateVin() {
    return "VIN" + Date.now();
}

function generateBienSo() {
    return "UI" + Date.now().toString().slice(-6);
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

function fillNominalVehicleForm(I) {
    I.fillField("#vin", generateVin());
    I.fillField("#bienSo", generateBienSo());
    I.fillField("#loaiXe", "Xe máy điện");

    I.fillField("#loaiPin", "Lithium-ion");
    I.fillField("#dungLuongPin", "100");
    I.fillField("#sucKhoePin", "95");
}

module.exports = {
    loginOnce,
    openAddVehicleModal,
    fillNominalVehicleForm,
    generateVin,
    generateBienSo,
};