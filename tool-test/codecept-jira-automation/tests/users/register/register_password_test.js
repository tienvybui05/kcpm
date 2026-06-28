Feature("Register Form - Mật khẩu");

const fillValidData = (I) => {
const timestamp = Date.now();

I.fillField("Ho_Ten", "Nguyen Van A");
I.fillField("Email", `test${timestamp}@gmail.com`);
I.fillField("Sdt", `09${timestamp.toString().slice(-8)}`);
I.fillField("Ngay_sinh", "2000-01-01");
I.fillField("Dia_Chi", "123 Đường ABC, Quận 1, TP.HCM");
I.fillField("Bang_Lai_Xe", "A1");
};

// matKhau - min-=5
Scenario("[matKhau - min-=5] Đăng ký với mật khẩu 5 ký tự", async ({ I }) => {
I.amOnPage("/register");

fillValidData(I);

I.fillField("Mat_Khau", "Abc12");
I.fillField("Xac_Nhan_Mat_Khau", "Abc12");

I.click("Đăng ký tài xế");

I.see("Mật khẩu phải từ 6-20 ký tự");
});

// matKhau - min=6
Scenario("[matKhau - min=6] Đăng ký với mật khẩu 6 ký tự", async ({ I }) => {
I.amOnPage("/register");

fillValidData(I);

I.fillField("Mat_Khau", "Abc123");
I.fillField("Xac_Nhan_Mat_Khau", "Abc123");

I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// matKhau - min+=7
Scenario("[matKhau - min+=7] Đăng ký với mật khẩu 7 ký tự", async ({ I }) => {
I.amOnPage("/register");

fillValidData(I);

I.fillField("Mat_Khau", "Abc@123");
I.fillField("Xac_Nhan_Mat_Khau", "Abc@123");

I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// matKhau - max-=19
Scenario("[matKhau - max-=19] Đăng ký với mật khẩu 19 ký tự", async ({ I }) => {
I.amOnPage("/register");

fillValidData(I);

const password = "Abcdef@12345678901"; // 19 ký tự

I.fillField("Mat_Khau", password);
I.fillField("Xac_Nhan_Mat_Khau", password);

I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// matKhau - max=20
Scenario("[matKhau - max=20] Đăng ký với mật khẩu 20 ký tự", async ({ I }) => {
I.amOnPage("/register");

fillValidData(I);

const password = "Abcdef@123456789012"; // 20 ký tự

I.fillField("Mat_Khau", password);
I.fillField("Xac_Nhan_Mat_Khau", password);

I.click("Đăng ký tài xế");

  I.waitForURL(/\/login$/, 30);
});

// matKhau - max+=21
Scenario("[matKhau - max+=21] Đăng ký với mật khẩu 21 ký tự", async ({ I }) => {
I.amOnPage("/register");

fillValidData(I);

const password = "Abcdef@1234567890123"; // 21 ký tự

I.fillField("Mat_Khau", password);
I.fillField("Xac_Nhan_Mat_Khau", password);

I.click("Đăng ký tài xế");

I.see("Mật khẩu phải từ 6-20 ký tự");
});

// matKhau - invalid
Scenario(
"[matKhau - invalid] Đăng ký với mật khẩu không có chữ hoa",
async ({ I }) => {
I.amOnPage("/register");


fillValidData(I);

I.fillField("Mat_Khau", "abc@123");
I.fillField("Xac_Nhan_Mat_Khau", "abc@123");

I.click("Đăng ký tài xế");

I.see("Mật khẩu phải chứa ít nhất 1 chữ hoa");


}
);

// matKhau - invalid
Scenario(
"[matKhau - invalid] Đăng ký với mật khẩu không có chữ thường",
async ({ I }) => {
I.amOnPage("/register");

fillValidData(I);

I.fillField("Mat_Khau", "ABC@123");
I.fillField("Xac_Nhan_Mat_Khau", "ABC@123");

I.click("Đăng ký tài xế");

I.see("Mật khẩu phải chứa ít nhất 1 chữ thường");

}
);

// matKhau - invalid
Scenario(
"[matKhau - invalid] Đăng ký với mật khẩu không có số",
async ({ I }) => {
I.amOnPage("/register");

fillValidData(I);

I.fillField("Mat_Khau", "Abc@def");
I.fillField("Xac_Nhan_Mat_Khau", "Abc@def");

I.click("Đăng ký tài xế");

I.see("Mật khẩu phải chứa ít nhất 1 chữ số");

}
);
