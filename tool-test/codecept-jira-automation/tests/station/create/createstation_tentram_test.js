const {
  generateString,
  generateUniqueString,
  waitAndSeePopup,
  fillFieldFast,
  loginOnce,
  openAddStationModal,
  fillNominalFormForTenTram,
} = require("./stationHelper");

Feature("Thêm Trạm - Test Thuộc Tính tenTram");

BeforeSuite(async ({ I }) => {
  await loginOnce(I);
});

Before(async ({ I }) => {
  await openAddStationModal(I);
});

Scenario(
  "TC_CREATESTATION - [tenTram - Min-=0] Tạo trạm với tenTram = Rỗng",
  async ({ I }) => {
    await fillNominalFormForTenTram(I);
    await fillFieldFast(I, 'input[name="tenTram"]', "");

    I.click("Thêm Trạm", "form");

    I.seeElement('input[name="tenTram"]:invalid');
  },
);

Scenario(
  "TC_CREATESTATION - [tenTram - Min=1] Tạo trạm với tenTram = 1 ký tự",
  async ({ I }) => {
    await fillNominalFormForTenTram(I);
    await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(1));

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [tenTram - Min+=2] Tạo trạm với tenTram = 2 ký tự",
  async ({ I }) => {
    await fillNominalFormForTenTram(I);
    await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(2));

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [tenTram - Max-=149] Tạo trạm với tenTram = 149 ký tự",
  async ({ I }) => {
    await fillNominalFormForTenTram(I);
    await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(149));

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [tenTram - Max=150] Tạo trạm với tenTram = 150 ký tự",
  async ({ I }) => {
    await fillNominalFormForTenTram(I);
    await fillFieldFast(I, 'input[name="tenTram"]', generateUniqueString(150));

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [tenTram - Max+=151] Tạo trạm với tenTram = 151 ký tự",
  async ({ I }) => {
    await fillNominalFormForTenTram(I);
    await fillFieldFast(I, 'input[name="tenTram"]', generateString(151));

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Tên trạm lố 150 kí tự.");
  },
);

Scenario(
  "TC_CREATESTATION - [tenTram - Error=Trùng lặp] Tạo trạm với tenTram đã tồn tại",
  async ({ I }) => {
    const duplicateName = "TRAM_DUP_" + Date.now();

    // Lần 1
    await fillNominalFormForTenTram(I);
    await fillFieldFast(I, 'input[name="tenTram"]', duplicateName);
    I.click("Thêm Trạm", "form");
    waitAndSeePopup(I, "✅ Thêm trạm thành công!");

    // Lần 2
    I.click("Thêm Trạm");
    I.waitForText("Thêm Trạm Mới", 5);

    await fillNominalFormForTenTram(I);
    await fillFieldFast(I, 'input[name="tenTram"]', duplicateName);
    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Tên trạm trùng");
  },
);