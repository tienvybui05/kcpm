Feature('register');

Scenario('Đăng ký thành công', async ({ I }) => {
  I.amOnPage('http://localhost:3000/register');

  I.fillField('Ho_Ten', 'Nguyen Van A');
  I.fillField('Email', `test${Date.now()}@gmail.com`);
  I.fillField('Sdt', '0987654321');
  I.selectOption('Gioi_tinh', 'Nam');

  I.fillField('Mat_Khau', '123456');
  I.fillField('Xac_Nhan_Mat_Khau', '123456');

  I.fillField('Ngay_sinh', '2000-01-01');
  I.fillField('Dia_Chi', 'TP HCM');
  I.fillField('Bang_Lai_Xe', 'B2-123456');

  I.click('Đăng ký tài xế');

  // Chờ xử lý
  I.wait(3);

  // Kiểm tra chuyển sang trang login
  I.seeInCurrentUrl('/login');
});
Scenario('Đăng ký thất bại khi mật khẩu không khớp', async ({ I }) => {
  I.amOnPage('http://localhost:3000/register');

  I.fillField('Ho_Ten', 'Nguyen Van A');
  I.fillField('Email', 'abc@gmail.com');
  I.fillField('Sdt', '0987654321');

  I.fillField('Mat_Khau', '123456');
  I.fillField('Xac_Nhan_Mat_Khau', '654321');

  I.fillField('Ngay_sinh', '2000-01-01');
  I.fillField('Bang_Lai_Xe', 'B2-123456');

  I.click('Đăng ký tài xế');

  I.see('Mật khẩu và xác nhận mật khẩu không khớp!');
});
Scenario('Đăng ký thất bại khi chưa đủ 18 tuổi', async ({ I }) => {
  I.amOnPage('http://localhost:3000/register');

  I.fillField('Ho_Ten', 'Nguyen Van A');
  I.fillField('Email', 'abc@gmail.com');
  I.fillField('Sdt', '0987654321');

  I.fillField('Mat_Khau', '123456');
  I.fillField('Xac_Nhan_Mat_Khau', '123456');

  I.fillField('Ngay_sinh', '2012-01-01');
  I.fillField('Bang_Lai_Xe', 'B2-123456');

  I.click('Đăng ký tài xế');

  I.see('Bạn phải đủ 18 tuổi trở lên để đăng ký lái xe!');
});
Scenario('Đăng ký thất bại khi mật khẩu dưới 6 ký tự', async ({ I }) => {
  I.amOnPage('http://localhost:3000/register');

  I.fillField('Ho_Ten', 'Nguyen Van A');
  I.fillField('Email', 'abc@gmail.com');
  I.fillField('Sdt', '0987654321');

  I.fillField('Mat_Khau', '123');
  I.fillField('Xac_Nhan_Mat_Khau', '123');

  I.fillField('Ngay_sinh', '2000-01-01');
  I.fillField('Bang_Lai_Xe', 'B2-123456');

  I.click('Đăng ký tài xế');

  I.see('Mật khẩu phải có ít nhất 6 ký tự!');
});
