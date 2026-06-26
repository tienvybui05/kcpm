const {
  waitAndSeePopup,
  fillFieldFast,
  loginOnce,
  openAddStationModal,
  fillNominalFormForKinhDo,
} = require("./stationHelper");

Feature("Thêm Trạm - Test Thuộc Tính kinhDo");

BeforeSuite(async ({ I }) => {
  await loginOnce(I);
});

Before(async ({ I }) => {
  await openAddStationModal(I);
});

Scenario(
  "TC_CREATESTATION - [kinhDo - Min-=-180.1] Tạo trạm với kinhDo = -180.1",
  async ({ I }) => {
    await fillNominalFormForKinhDo(I);
    await fillFieldFast(I, 'input[name="kinhDo"]', "-180.1");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Kinh độ nhỏ hơn -180.");
  },
);

Scenario(
  "TC_CREATESTATION - [kinhDo - Min=-180.0] Tạo trạm với kinhDo = -180.0",
  async ({ I }) => {
    await fillNominalFormForKinhDo(I);
    await fillFieldFast(I, 'input[name="kinhDo"]', "-180.0");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [kinhDo - Min+=-179.9999] Tạo trạm với kinhDo = -179.9999",
  async ({ I }) => {
    await fillNominalFormForKinhDo(I);
    await fillFieldFast(I, 'input[name="kinhDo"]', "-179.9999");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [kinhDo - Max-=179.9999] Tạo trạm với kinhDo = 179.9999",
  async ({ I }) => {
    await fillNominalFormForKinhDo(I);
    await fillFieldFast(I, 'input[name="kinhDo"]', "179.9999");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [kinhDo - Max=180.0] Tạo trạm với kinhDo = 180.0",
  async ({ I }) => {
    await fillNominalFormForKinhDo(I);
    await fillFieldFast(I, 'input[name="kinhDo"]', "180.0");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [kinhDo - Max+=180.1] Tạo trạm với kinhDo = 180.1",
  async ({ I }) => {
    await fillNominalFormForKinhDo(I);
    await fillFieldFast(I, 'input[name="kinhDo"]', "180.1");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Kinh độ lớn hơn 180.");
  },
);

Scenario(
  "TC_CREATESTATION - [kinhDo - Error=Sai kiểu] Tạo trạm với kinhDo chứa chữ",
  async ({ I }) => {
    await fillNominalFormForKinhDo(I);
    await fillFieldFast(I, 'input[name="kinhDo"]', "abc");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Kinh độ sai kiểu dữ liệu.");
  },
);

Scenario(
  "TC_CREATESTATION - [kinhDo - Error=Null] Tạo trạm với kinhDo = Rỗng",
  async ({ I }) => {
    await fillNominalFormForKinhDo(I);
    await fillFieldFast(I, 'input[name="kinhDo"]', "");

    I.click("Thêm Trạm", "form");

    I.seeElement('input[name="kinhDo"]:invalid');
  },
);