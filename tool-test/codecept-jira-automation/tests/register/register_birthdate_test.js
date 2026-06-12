Feature("Register Form - Ngày sinh");

const fillValidData = (I) => {
  const timestamp = Date.now();

  I.fillField("Ho_Ten", "Nguyen Van A");
  I.fillField("Email", `test${timestamp}@gmail.com`);
  I.fillField("Sdt", `09${String(timestamp).slice(-8)}`);
  I.fillField("Mat_Khau", "abc123");
  I.fillField("Xac_Nhan_Mat_Khau", "abc123");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
  I.fillField("Bang_Lai_Xe", `A1-${String(timestamp).slice(-6)}`);
};

const setBirthDate = (I, date) => {
  I.usePlaywrightTo("set birth date", async ({ page }) => {
    await page.locator('input[name="Ngay_sinh"]').fill(date);
  });
};

// ================= INVALID =================

Scenario("TC_REGISTER - Dưới 18 tuổi", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);
  setBirthDate(I, "2010-01-01");

  I.click("Đăng ký tài xế");

  I.see("Bạn phải đủ 18 tuổi trở lên để đăng ký lái xe!");
});

// ================= BOUNDARY MIN- =================

Scenario("TC_REGISTER - Boundary Min- (17 tuổi)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  const year = new Date().getFullYear() - 17;
  setBirthDate(I, `${year}-01-01`);

  I.click("Đăng ký tài xế");

  I.see("Bạn phải đủ 18 tuổi trở lên để đăng ký lái xe!");
});

// ================= BOUNDARY MIN =================

Scenario("TC_REGISTER - Boundary Min (18 tuổi)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  const year = new Date().getFullYear() - 18;
  setBirthDate(I, `${year}-01-01`);

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// ================= NOMINAL =================

Scenario("TC_REGISTER - Nominal (25 tuổi)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  const year = new Date().getFullYear() - 25;
  setBirthDate(I, `${year}-01-01`);

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// ================= BOUNDARY MAX- =================

Scenario("TC_REGISTER - Boundary Max- (60 tuổi)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  const year = new Date().getFullYear() - 60;
  setBirthDate(I, `${year}-01-01`);

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// ================= BOUNDARY MAX =================

Scenario("TC_REGISTER - Boundary Max (90 tuổi)", async ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  const year = new Date().getFullYear() - 90;
  setBirthDate(I, `${year}-01-01`);

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});
