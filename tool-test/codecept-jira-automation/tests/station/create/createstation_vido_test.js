const {
  waitAndSeePopup,
  fillFieldFast,
  loginOnce,
  openAddStationModal,
  fillNominalFormForViDo,
} = require("./stationHelper");

Feature("Thêm Trạm - Test Thuộc Tính viDo");

BeforeSuite(async ({ I }) => {
  await loginOnce(I);
});

Before(async ({ I }) => {
  await openAddStationModal(I);
});

Scenario(
  "TC_CREATESTATION - [viDo - Min-=-90.1] Tạo trạm với viDo = -90.1",
  async ({ I }) => {
    await fillNominalFormForViDo(I);
    await fillFieldFast(I, 'input[name="viDo"]', "-90.1");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Vĩ độ nhỏ hơn -90.");
  },
);

Scenario(
  "TC_CREATESTATION - [viDo - Min=-90.0] Tạo trạm với viDo = -90.0",
  async ({ I }) => {
    await fillNominalFormForViDo(I);
    await fillFieldFast(I, 'input[name="viDo"]', "-90.0");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [viDo - Min+=-89.9999] Tạo trạm với viDo = -89.9999",
  async ({ I }) => {
    await fillNominalFormForViDo(I);
    await fillFieldFast(I, 'input[name="viDo"]', "-89.9999");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [viDo - Max-=89.9999] Tạo trạm với viDo = 89.9999",
  async ({ I }) => {
    await fillNominalFormForViDo(I);
    await fillFieldFast(I, 'input[name="viDo"]', "89.9999");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [viDo - Max=90.0] Tạo trạm với viDo = 90.0",
  async ({ I }) => {
    await fillNominalFormForViDo(I);
    await fillFieldFast(I, 'input[name="viDo"]', "90.0");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "✅ Thêm trạm thành công!");
  },
);

Scenario(
  "TC_CREATESTATION - [viDo - Max+=90.1] Tạo trạm với viDo = 90.1",
  async ({ I }) => {
    await fillNominalFormForViDo(I);
    await fillFieldFast(I, 'input[name="viDo"]', "90.1");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Vĩ độ lớn hơn 90.");
  },
);

Scenario(
  "TC_CREATESTATION - [viDo - Error=Sai kiểu] Tạo trạm với viDo không phải số",
  async ({ I }) => {
    await fillNominalFormForViDo(I);
    await fillFieldFast(I, 'input[name="viDo"]', "abc");

    I.click("Thêm Trạm", "form");

    waitAndSeePopup(I, "❌ Vĩ độ sai kiểu dữ liệu.");
  },
);

Scenario(
  "TC_CREATESTATION - [viDo - Error=Null] Tạo trạm với viDo = Null",
  async ({ I }) => {
    await fillNominalFormForViDo(I);
    await fillFieldFast(I, 'input[name="viDo"]', "");

    I.click("Thêm Trạm", "form");

    I.seeElement('input[name="viDo"]:invalid');
  },
);
