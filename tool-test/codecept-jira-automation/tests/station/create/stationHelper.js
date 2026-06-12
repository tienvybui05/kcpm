let authStorage = {};

function generateString(length) {
  return "A".repeat(length);
}

function generatePhone() {
  return "09" + Math.floor(10000000 + Math.random() * 90000000);
}

function generateUniqueString(length) {
  const base = (
    "TRAM" +
    Date.now() +
    Math.floor(Math.random() * 100000)
  ).toUpperCase();

  if (length <= base.length) {
    return base.slice(0, length);
  }

  return base + "A".repeat(length - base.length);
}

function waitAndSeePopup(I, message) {
  I.wait(1.5);
  I.seeInPopup(message);
  I.acceptPopup();
}

async function fillFieldFast(I, selector, value) {
  await I.executeScript(
    ({ selector, value }) => {
      const input = document.querySelector(selector);

      if (!input) {
        throw new Error(`Không tìm thấy input: ${selector}`);
      }

      const nativeInputValueSetter = Object.getOwnPropertyDescriptor(
        window.HTMLInputElement.prototype,
        "value",
      ).set;

      nativeInputValueSetter.call(input, value);

      input.dispatchEvent(new Event("input", { bubbles: true }));
      input.dispatchEvent(new Event("change", { bubbles: true }));
    },
    { selector, value },
  );
}

async function loginOnce(I) {
  I.amOnPage("http://localhost:3000/login");

  I.fillField('input[name="phone"]', "0703735248");
  I.fillField('input[name="password"]', "123456");
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

async function openAddStationModal(I) {
  I.amOnPage("http://localhost:3000/dashboard/stations");

  const currentUrl = await I.grabCurrentUrl();

  if (currentUrl.includes("/login")) {
    await I.executeScript((storage) => {
      localStorage.setItem("token", storage.token);
      localStorage.setItem("userRole", storage.userRole);
      localStorage.setItem("userEmail", storage.userEmail);
      localStorage.setItem("userId", storage.userId);
      localStorage.setItem("hoTen", storage.hoTen);
    }, authStorage);

    I.amOnPage("http://localhost:3000/dashboard/stations");
  }

  I.waitForText("Quản Lý Trạm", 15);
  I.click("Thêm Trạm");
  I.waitForText("Thêm Trạm Mới", 5);
}

function fillNominalFormForTenTram(I) {
  I.fillField('input[name="diaChi"]', "123 Đường Trung Tâm, Quận 1");
  I.fillField('input[name="kinhDo"]', "106.6821");
  I.fillField('input[name="viDo"]', "10.7626");
  I.fillField('input[name="soLuongPinToiDa"]', "50");
  I.fillField('input[name="soDT"]', generatePhone());
  I.selectOption('select[name="trangThai"]', "Hoạt động");
}

function fillNominalFormForDiaChi(I) {
  I.fillField('input[name="tenTram"]', generateUniqueString(30));
  I.fillField('input[name="kinhDo"]', "106.6821");
  I.fillField('input[name="viDo"]', "10.7626");
  I.fillField('input[name="soLuongPinToiDa"]', "50");
  I.fillField('input[name="soDT"]', generatePhone());
  I.selectOption('select[name="trangThai"]', "Hoạt động");
}

function fillNominalFormForKinhDo(I) {
  I.fillField('input[name="tenTram"]', generateUniqueString(30));
  I.fillField('input[name="diaChi"]', "123 Đường Trung Tâm, Quận 1");
  I.fillField('input[name="viDo"]', "10.7626");
  I.fillField('input[name="soLuongPinToiDa"]', "50");
  I.fillField('input[name="soDT"]', generatePhone());
  I.selectOption('select[name="trangThai"]', "Hoạt động");
}

function fillNominalFormForViDo(I) {
  I.fillField('input[name="tenTram"]', generateUniqueString(30));
  I.fillField('input[name="diaChi"]', "123 Đường Trung Tâm, Quận 1");
  I.fillField('input[name="kinhDo"]', "106.6821");
  I.fillField('input[name="soLuongPinToiDa"]', "50");
  I.fillField('input[name="soDT"]', generatePhone());
  I.selectOption('select[name="trangThai"]', "Hoạt động");
}

function fillNominalFormForSoDT(I) {
  I.fillField('input[name="tenTram"]', generateUniqueString(30));
  I.fillField('input[name="diaChi"]', "123 Đường Trung Tâm, Quận 1");
  I.fillField('input[name="kinhDo"]', "106.6821");
  I.fillField('input[name="viDo"]', "10.7626");
  I.fillField('input[name="soLuongPinToiDa"]', "50");
  I.selectOption('select[name="trangThai"]', "Hoạt động");
}

function fillNominalFormForTrangThai(I) {
  I.fillField('input[name="tenTram"]', generateUniqueString(30));
  I.fillField('input[name="diaChi"]', "123 Đường Trung Tâm, Quận 1");
  I.fillField('input[name="kinhDo"]', "106.6821");
  I.fillField('input[name="viDo"]', "10.7626");
  I.fillField('input[name="soLuongPinToiDa"]', "50");
  I.fillField('input[name="soDT"]', generatePhone());
}

module.exports = {
  generateString,
  generatePhone,
  generateUniqueString,
  waitAndSeePopup,
  fillFieldFast,
  loginOnce,
  openAddStationModal,
  fillNominalFormForTenTram,
  fillNominalFormForDiaChi,
  fillNominalFormForKinhDo,
  fillNominalFormForViDo,
  fillNominalFormForSoDT,
  fillNominalFormForTrangThai,
};