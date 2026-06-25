const {
  waitAndSeePopup,
  fillFieldFast,
  loginOnce,
  openAddStationModal,
  fillNominalFormForSoDT,
} = require("./stationHelper");

Feature("Thêm Trạm - Test Thuộc Tính soDT");

BeforeSuite(async ({ I }) => {
  await loginOnce(I);
});

Before(async ({ I }) => {
  await openAddStationModal(I);
});

Scenario(
  "TC_CREATESTATION - [soDT - Min-=9] Tạo trạm với soDT = 9 ký tự",
  async ({ I }) => {
    await fillNominalFormForSoDT(I);
    await fillFieldFast(I, 'input[name="soDT"]', "091234567");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Số điện thoại nhỏ hơn 10 ký tự.");
  },
);

Scenario(
  "TC_CREATESTATION - [soDT - Min=10] Tạo trạm với soDT = 10 ký tự",
  async ({ I }) => {
    await fillNominalFormForSoDT(I);
    await fillFieldFast(I, 'input[name="soDT"]', "0912345678");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [soDT - Max=11] Tạo trạm với soDT = 11 ký tự",
  async ({ I }) => {
    await fillNominalFormForSoDT(I);
    await fillFieldFast(I, 'input[name="soDT"]', "09123456789");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [soDT - Max+=12] Tạo trạm với soDT = 12 ký tự",
  async ({ I }) => {
    await fillNominalFormForSoDT(I);
    await fillFieldFast(I, 'input[name="soDT"]', "091234567890");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Số điện thoại lớn hơn 11 ký tự.");
  },
);

Scenario(
  "TC_CREATESTATION - [soDT - Error=Sai định dạng] Tạo trạm với soDT chứa chữ",
  async ({ I }) => {
    await fillNominalFormForSoDT(I);
    await fillFieldFast(I, 'input[name="soDT"]', "09123abc78");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Số điện thoại chứa chữ cái.");
  },
);

Scenario(
  "TC_CREATESTATION - [soDT - Error=Null] Tạo trạm với soDT = Null",
  async ({ I }) => {
    await fillNominalFormForSoDT(I);
    await fillFieldFast(I, 'input[name="soDT"]', "");

    I.click("Thêm Trạm", "form");

    I.seeElement('input[name="soDT"]:invalid');
  },
);
