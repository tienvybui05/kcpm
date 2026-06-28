Feature("Register Form - Họ tên");

const fillValidData = (I) => {
  const timestamp = Date.now();

  I.fillField("Email", `test${timestamp}@gmail.com`);
  I.fillField("Sdt", `09${timestamp.toString().slice(-8)}`);
  I.fillField("Mat_Khau", "Abc@123");
  I.fillField("Xac_Nhan_Mat_Khau", "Abc@123");
  I.fillField("Ngay_sinh", "2000-01-01");
  I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
  I.fillField("Bang_Lai_Xe", `A1`);
};

//
// hoTen - min- = 0
//
Scenario("[hoTen - min-=0] Đăng ký với họ tên rỗng", ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Ho_Ten", "");

  I.click("Đăng ký tài xế");

  I.see("Họ tên không được để trống");
});

//
// hoTen - min = 1
//
Scenario("[hoTen - min=1] Đăng ký với họ tên 1 ký tự", ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Ho_Ten", "T");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

//
// hoTen - min+ = 2
//
Scenario("[hoTen - min+=2] Đăng ký với họ tên 2 ký tự", ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Ho_Ten", "AB");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

//
// hoTen - nominal
//
Scenario("[hoTen - nominal] Đăng ký với họ tên hợp lệ", ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Ho_Ten", "Nguyen Van A");

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

//
// hoTen - max- = 49
//
Scenario("[hoTen - max-=49] Đăng ký với họ tên 49 ký tự", ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Ho_Ten", "A".repeat(49));

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

//
// hoTen - max = 50
//
Scenario("[hoTen - max=50] Đăng ký với họ tên 50 ký tự", ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Ho_Ten", "A".repeat(50));

  I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

//
// hoTen - max+ = 51
//
Scenario("[hoTen - max+=51] Đăng ký với họ tên 51 ký tự", ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Ho_Ten", "A".repeat(51));

  I.click("Đăng ký tài xế");

  I.see("Họ tên phải từ 1-50 ký tự");
});

//
// hoTen - invalid number
//
Scenario("[hoTen - invalid] Đăng ký với họ tên chứa số", ({ I }) => {
  I.amOnPage("/register");

  fillValidData(I);

  I.fillField("Ho_Ten", "Nguyen Van A1");

  I.click("Đăng ký tài xế");

  I.see("Họ tên chỉ được chứa chữ cái");
});

//
// hoTen - invalid special character
//
Scenario(
  "[hoTen - invalid] Đăng ký với họ tên chứa ký tự đặc biệt",
  ({ I }) => {
    I.amOnPage("/register");

    fillValidData(I);

    I.fillField("Ho_Ten", "Nguyen@Van");

    I.click("Đăng ký tài xế");

    I.see("Họ tên chỉ được chứa chữ cái");
  },
);
