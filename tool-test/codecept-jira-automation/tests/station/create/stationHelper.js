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

// KHÔI PHỤC: Đã trả lại thời gian chờ 1.5s cho popup như cũ
function waitAndSeePopup(I, message) {
  I.wait(1.5);
  I.seeInPopup(message);
  I.acceptPopup();
}

// GIỮ LẠI: Hàm này giúp copy/paste 1 lèo chữ thay vì gõ từng phím
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

// KHÔI PHỤC: Trả lại I.wait(3) ở hàm Login như cũ để tránh bị trôi quá nhanh
async function loginOnce(I) {
  I.amOnPage("/login");
  I.wait(3);
  I.saveScreenshot("man_hinh_jenkins_nhin_thay.png");

  I.fillField('input[name="phone"]', "0703735248");
  I.fillField('input[name="password"]', "0703735248Ngoc@");
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

// KHÔI PHỤC: Trả lại cách load trang gốc của bạn
async function openAddStationModal(I) {
  I.amOnPage("/dashboard/stations");

  const currentUrl = await I.grabCurrentUrl();

  if (currentUrl.includes("/login")) {
    await I.executeScript((storage) => {
      localStorage.setItem("token", storage.token);
      localStorage.setItem("userRole", storage.userRole);
      localStorage.setItem("userEmail", storage.userEmail);
      localStorage.setItem("userId", storage.userId);
      localStorage.setItem("hoTen", storage.hoTen);
    }, authStorage);

    I.amOnPage("/dashboard/stations");
  }

  I.waitForText("Quản Lý Trạm", 15);
  I.click("Thêm Trạm");
  I.waitForText("Thêm Trạm Mới", 5);
}

// GIỮ LẠI: Vẫn dùng async/await với fillFieldFast để các trường điền 1 lèo
async function fillNominalFormForTenTram(I) {
  await fillFieldFast(I, 'input[name="diaChi"]', "123 Đường Trung Tâm, Quận 1");
  await fillFieldFast(I, 'input[name="kinhDo"]', "106.6821");
  await fillFieldFast(I, 'input[name="viDo"]', "10.7626");
  await fillFieldFast(I, 'input[name="soLuongPinToiDa"]', "50");
  await fillFieldFast(I, 'input[name="soDT"]', generatePhone());
  I.selectOption('select[name="trangThai"]', "Hoạt động");
}

async function fillNominalFormForDiaChi(I) {
  await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(30));
  await fillFieldFast(I, 'input[name="kinhDo"]', "106.6821");
  await fillFieldFast(I, 'input[name="viDo"]', "10.7626");
  await fillFieldFast(I, 'input[name="soLuongPinToiDa"]', "50");
  await fillFieldFast(I, 'input[name="soDT"]', generatePhone());
  I.selectOption('select[name="trangThai"]', "Hoạt động");
}

async function fillNominalFormForKinhDo(I) {
  await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(30));
  await fillFieldFast(I, 'input[name="diaChi"]', "123 Đường Trung Tâm, Quận 1");
  await fillFieldFast(I, 'input[name="viDo"]', "10.7626");
  await fillFieldFast(I, 'input[name="soLuongPinToiDa"]', "50");
  await fillFieldFast(I, 'input[name="soDT"]', generatePhone());
  I.selectOption('select[name="trangThai"]', "Hoạt động");
}

async function fillNominalFormForViDo(I) {
  await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(30));
  await fillFieldFast(I, 'input[name="diaChi"]', "123 Đường Trung Tâm, Quận 1");
  await fillFieldFast(I, 'input[name="kinhDo"]', "106.6821");
  await fillFieldFast(I, 'input[name="soLuongPinToiDa"]', "50");
  await fillFieldFast(I, 'input[name="soDT"]', generatePhone());
  I.selectOption('select[name="trangThai"]', "Hoạt động");
}

async function fillNominalFormForSoDT(I) {
  await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(30));
  await fillFieldFast(I, 'input[name="diaChi"]', "123 Đường Trung Tâm, Quận 1");
  await fillFieldFast(I, 'input[name="kinhDo"]', "106.6821");
  await fillFieldFast(I, 'input[name="viDo"]', "10.7626");
  await fillFieldFast(I, 'input[name="soLuongPinToiDa"]', "50");
  I.selectOption('select[name="trangThai"]', "Hoạt động");
}

async function fillNominalFormForTrangThai(I) {
  await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(30));
  await fillFieldFast(I, 'input[name="diaChi"]', "123 Đường Trung Tâm, Quận 1");
  await fillFieldFast(I, 'input[name="kinhDo"]', "106.6821");
  await fillFieldFast(I, 'input[name="viDo"]', "10.7626");
  await fillFieldFast(I, 'input[name="soLuongPinToiDa"]', "50");
  await fillFieldFast(I, 'input[name="soDT"]', generatePhone());
}

async function fillNominalForm(I) {
  await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(30));
  await fillFieldFast(I, 'input[name="diaChi"]', "123 Đường Trung Tâm, Quận 1");
  await fillFieldFast(I, 'input[name="kinhDo"]', "106.6821");
  await fillFieldFast(I, 'input[name="viDo"]', "10.7626");
  await fillFieldFast(I, 'input[name="soLuongPinToiDa"]', "50");
  await fillFieldFast(I, 'input[name="soDT"]', generatePhone());
  I.selectOption('select[name="trangThai"]', "Hoạt động");
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
  fillNominalForm,
};
